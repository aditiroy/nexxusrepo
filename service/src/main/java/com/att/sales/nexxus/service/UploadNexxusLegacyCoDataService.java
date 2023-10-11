package com.att.sales.nexxus.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.aft.dme2.internal.google.common.base.Objects;
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.model.LegacyCoDetails;
import com.att.sales.nexxus.dao.model.LegacyCoPercentage;
import com.att.sales.nexxus.dao.model.NxDesignAudit;
import com.att.sales.nexxus.dao.model.LegacyCoPercentage.Pk;
import com.att.sales.nexxus.dao.repository.LegacyCoDetailsDao;
import com.att.sales.nexxus.dao.repository.LegacyCoPercentageDao;
import com.att.sales.nexxus.dao.repository.NxDesignAuditRepository;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.template.model.NxTemplateUploadRequest;
import com.att.sales.nexxus.util.ExceptionUtil;
import com.github.pjfanning.xlsx.StreamingReader;

@Component
public class UploadNexxusLegacyCoDataService {
	private static Logger logger = LoggerFactory.getLogger(UploadNexxusLegacyCoDataService.class);
	
	private static final String ERROR_PROCESS_ERROR = "M00003";
	public static final String STATUS_DCC_ADD = "dcc_add";
	public static final String STATUS_DCC_MODIFY = "dcc_modify";

	private static final Set<String> VTHM_COMPANYCODE_SET;
	static {
		Set<String> stringSet = new HashSet<>(
				Arrays.asList("5181", "5191", "5192", "5182", "5183", "5184", "5193", "5194", "5185"));
		VTHM_COMPANYCODE_SET = Collections.unmodifiableSet(stringSet);
	}
	
	private static final Set<String> STATE_FILTER;
	static {
		Set<String> stringSet = new HashSet<>(Arrays.asList("AL", "FL", "GA", "KY", "LA", "MS", "NC", "SC", "TN"));
		STATE_FILTER = Collections.unmodifiableSet(stringSet);
	}

	@Autowired
	private LegacyCoDetailsDao legacyCoDetailsDao;

	@Autowired
	private LegacyCoPercentageDao legacyCoPercentageDao;

	@Autowired
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;

	@Autowired
	private NxLookupDataRepository nxLookupDataRepository;
	
	@Value("${p8.local.destPath}")
	private String p8dLocalPath;

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private NxDesignAuditRepository nxDesignAuditRepository;
	
	
	@Transactional(rollbackOn = Exception.class)
	public void updateNexxusLegacyCoData(NxTemplateUploadRequest request)
			throws SalesBusinessException {
		logger.info("enter updateNexxusLegacyCoData method");
		Date date = new Date();
		try {
			if ("QENXXNP.txt".equals(request.getFileName())) {
				processQENXXNP(request, date);
			} else if ("TISDN.txt".equals(request.getFileName())) {
				processTISDN(request, date);
			} else if (request.getFileName().endsWith("zip")) {
				processNECA(request, date);
			}
			saveDesignAudit(request.getFileName(), date, "SUCCESS", "waiting for sync");
		} catch (IOException e) {
			logger.error("Exception", e);
			saveDesignAudit(request.getFileName(), date, "FAILURE", ExceptionUtil.toString(e));
			throw new SalesBusinessException(ERROR_PROCESS_ERROR);
		}
	}

	protected void saveDesignAudit(String fileName, Date date, String status, String data) {
		NxDesignAudit nxDesignAudit = new NxDesignAudit();		
		nxDesignAudit.setTransaction("DCC_LEGACY_FILE");
		nxDesignAudit.setCreatedDate(date);
		nxDesignAudit.setModifedDate(date);
		nxDesignAudit.setNxSubRefId(fileName);
		nxDesignAudit.setStatus(status);
		nxDesignAudit.setData(data);
		nxDesignAuditRepository.save(nxDesignAudit);
	}

	protected void processNECA(NxTemplateUploadRequest request, Date date) throws IOException {
		String savedZipFolder = "uploadMP_" + Thread.currentThread().getName() + "_" +  System.currentTimeMillis();
		String savedZipFileName = savedZipFolder + ".zip";
		Path savedZipFilePath = Paths.get(p8dLocalPath).resolve(savedZipFileName);
		Files.copy(request.getInputStream(), savedZipFilePath);
		Path savedZipFolderPath = Paths.get(p8dLocalPath).resolve(savedZipFolder);
		Files.createDirectory(savedZipFolderPath);
		ZipFile savedZipFile = new ZipFile(savedZipFilePath.toFile());
		savedZipFile.stream().forEach(zipEntry -> {
			try {
				Files.copy(savedZipFile.getInputStream(zipEntry), savedZipFolderPath.resolve(zipEntry.getName()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		String necaZipFileName = savedZipFile.stream().map(ZipEntry::getName)
				.filter(fileName -> fileName.endsWith("zip")).findFirst().orElse(null);
		savedZipFile.close();
		Path necaZipFilePath = savedZipFolderPath.resolve(necaZipFileName);
		ZipFile necaZipFile = new ZipFile(necaZipFilePath.toFile());
		necaZipFile.stream().filter(zipEntry -> !zipEntry.isDirectory() && zipEntry.getName().endsWith("zip"))
				.forEach(zipEntry -> {
					try {
						Files.copy(necaZipFile.getInputStream(zipEntry), savedZipFolderPath
								.resolve(zipEntry.getName().substring(zipEntry.getName().lastIndexOf('/') + 1)));
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
		necaZipFile.close();
		Files.delete(necaZipFilePath);
		Path ccaListData = Files.list(savedZipFolderPath).filter(path -> path.getFileName().toString().endsWith("xlsx"))
				.findFirst().orElse(null);
		Map<String, CcaListData> ccaListDataMap = loadCcaListData(ccaListData);
		Map<String, String> reliefMap = nxLookupDataRepository.getDescDataFromLookup("LEGACY_CO_DETAILS_RELIEF");
		for (Path path : Files.newDirectoryStream(savedZipFolderPath)) {
			String fileName = path.getFileName().toString();
			if (fileName.endsWith("zip")) {
				int fileNum = Integer.parseInt(fileName.substring(2, 2 + 3));
				if ((12 <= fileNum && fileNum <= 64) || (400 <= fileNum && fileNum <= 401)) {
					logger.info("processing file {}", fileName);
					processVHTM(path, ccaListDataMap, reliefMap, date);
				} else if ((65 <= fileNum && fileNum <= 204) || (500 <= fileNum && fileNum <= 503)) {
					logger.info("processing file {}", fileName);
					processBPTE(path, date);
				}
			}
		}
		processCcaList(ccaListDataMap, date);
	}

	protected void processCcaList(Map<String, CcaListData> ccaListDataMap, Date date) {
		Map<String, String> map = legacyCoDetailsDao.getOcnToCompanyNameMap();
		for (Entry<String, String> entry : map.entrySet()) {
			if (ccaListDataMap.containsKey(entry.getKey())) {
				CcaListData ccaListData = ccaListDataMap.get(entry.getKey());
				if (ccaListData.getAsecName() != null && !Objects.equal(ccaListData.getAsecName(), entry.getValue())) {
					legacyCoDetailsDao.updateCompanyName(ccaListData.getAsecName(), entry.getKey(), date);
				}
			}
		}

		map = legacyCoDetailsDao.getOcnToCategoryMap();
		for (Entry<String, String> entry : map.entrySet()) {
			if (ccaListDataMap.containsKey(entry.getKey())) {
				CcaListData ccaListData = ccaListDataMap.get(entry.getKey());
				if (ccaListData.getCategory() != null && !Objects.equal(ccaListData.getCategory(), entry.getValue())) {
					legacyCoDetailsDao.updateCategory(ccaListData.getCategory(), entry.getKey(), date);
				}
			}
		}

		map = legacyCoPercentageDao.getOcnToCompanyNameMap();
		for (Entry<String, String> entry : map.entrySet()) {
			if (ccaListDataMap.containsKey(entry.getKey())) {
				CcaListData ccaListData = ccaListDataMap.get(entry.getKey());
				if (ccaListData.getAsecName() != null && !Objects.equal(ccaListData.getAsecName(), entry.getValue())) {
					legacyCoPercentageDao.updateCompanyName(ccaListData.getAsecName(), entry.getKey(), date);
				}
			}
		}
	}

	/*
	 * read VHTM File(499 lines): sample line: ---
	 * "07-01-2004","000011627","ABBEVILLE                 ","ABVLALXADS0","07752",
	 * "01993","9788","478","" --- schema: --- [VHTM] Filetype=VARYING Delimiter="
	 * Separator=, CharSet=ascii Field1=EffFromDt,CHAR,10,0,0
	 * Field2=RecordID,CHAR,9,0,10 Field3=Locality,CHAR,26,0,19
	 * Field4=CLLI,CHAR,11,0,45 Field5=VerticalCoordinate,CHAR,5,0,56
	 * Field6=HorizontalCoordinate,CHAR,5,0,61 Field7=CompanyCode,CHAR,4,0,66
	 * Field8=LataNumber,CHAR,3,0,70 Field9=TariffRevisionCode,CHAR,3,0,73 --- read
	 * VHTO File (6854 lines): sample line: --- "07-01-2004","000011627","AA" ---
	 * schema: --- [VHTO] Filetype=VARYING Delimiter=" Separator=, CharSet=ascii
	 * Field1=EffFromDt,CHAR,10,0,0 Field2=RecordID,CHAR,9,0,10
	 * Field3=Capability,CHAR,2,0,19 ---
	 */
	protected void processVHTM(Path path, Map<String, CcaListData> ccaListDataMap, Map<String, String> reliefMap,
			Date date) throws ZipException, IOException {
		try (ZipFile zipFile = new ZipFile(path.toFile())) {
			ZipEntry vhto = zipFile.stream().filter(zipEntry -> zipEntry.getName().startsWith("VHTO")).findFirst()
					.orElse(null);
			ZipEntry vhtm = zipFile.stream().filter(zipEntry -> zipEntry.getName().startsWith("VHTM")).findFirst()
					.orElse(null);
			try (BufferedReader vhtoIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(vhto)));
					BufferedReader vhtmIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(vhtm)))) {
				Map<String, String> vhtoCapabilityMap = new HashMap<>();
				List<LegacyCoDetails> legacyCoDetailsSavedList = new ArrayList<>();
				Set<String> newAddedIds = new HashSet<>();
				String line;
				while ((line = vhtoIn.readLine()) != null) {
					List<String> vhtoTokens = parseLine(line);
					String vhtoCapability = vhtoTokens.get(2);
					if ("PA".equals(vhtoCapability) || "PB".equals(vhtoCapability) || "PC".equals(vhtoCapability)) {
						vhtoCapabilityMap.put(vhtoTokens.get(1), vhtoCapability);
					}
				}
				while ((line = vhtmIn.readLine()) != null) {
					List<String> vhtmTokens = parseLine(line);
					String vhtm_tariffRevisionode = vhtmTokens.get(8);
					String vhtm_clli = vhtmTokens.get(3);
					String state = vhtm_clli.substring(4, 4 + 2);
					if (!STATE_FILTER.contains(state)) {
						continue;
					}
					if (!"D".equals(vhtm_tariffRevisionode)) {
						String vhtm_companyCode = vhtmTokens.get(6);
						LegacyCoDetails legacyCoDetails = legacyCoDetailsDao.findOne(vhtm_clli);
						if (legacyCoDetails != null) {
							if (VTHM_COMPANYCODE_SET.contains(vhtm_companyCode)
									&& !VTHM_COMPANYCODE_SET.contains(legacyCoDetails.getOcn())) {
								String vthm_verticalCoordinate = vhtmTokens.get(4);
								String vthm_horizontalCoordinate = vhtmTokens.get(5);
								String vhtm_recordId = vhtmTokens.get(1);
								legacyCoDetails.setOcn(vhtm_companyCode);
								legacyCoDetails.setModifiedDate(date);
								legacyCoDetails.setSwcvcoordinate(vthm_verticalCoordinate);
								legacyCoDetails.setSwchcoordinate(vthm_horizontalCoordinate);
								if (!STATUS_DCC_ADD.equals(legacyCoDetails.getStatus())) {
									legacyCoDetails.setStatus(STATUS_DCC_MODIFY);
								}
								if (vhtoCapabilityMap.containsKey(vhtm_recordId)) {
									legacyCoDetails.setFeatureCode(vhtoCapabilityMap.get(vhtm_recordId));
									if ("PA".equals(legacyCoDetails.getFeatureCode())) {
										legacyCoDetails.setZone("1");
									} else if ("PB".equals(legacyCoDetails.getFeatureCode())) {
										legacyCoDetails.setZone("2");
									} else {
										legacyCoDetails.setZone("3");
									}
								}
							}
						} else if (!newAddedIds.contains(vhtm_clli)) {
							String vthm_verticalCoordinate = vhtmTokens.get(4);
							String vthm_horizontalCoordinate = vhtmTokens.get(5);
							String vhtm_recordId = vhtmTokens.get(1);
							legacyCoDetails = new LegacyCoDetails();
							legacyCoDetails.setCreatedDate(date);
							legacyCoDetails.setStatus(STATUS_DCC_ADD);
							legacyCoDetails.setSwcclli(vhtm_clli);
							legacyCoDetails.setCoclli(vhtm_clli.substring(0, 0 + 8));
							legacyCoDetails.setSwitchclli(vhtm_clli.substring(8, 8 + 3));
							legacyCoDetails.setState(state);
							legacyCoDetails.setSwcvcoordinate(vthm_verticalCoordinate);
							legacyCoDetails.setSwchcoordinate(vthm_horizontalCoordinate);
							legacyCoDetails.setOcn(vhtm_companyCode);
							String isRelief = reliefMap.get(vhtm_clli.substring(0, 0 + 8));
							boolean isCLEC = ccaListDataMap.containsKey(legacyCoDetails.getOcn());
							if (isCLEC && VTHM_COMPANYCODE_SET.contains(legacyCoDetails.getOcn())) {
								if ("L".equals(isRelief)) {
									legacyCoDetails.setMsaReliefInd("1");
								} else if ("F".equals(isRelief)) {
									legacyCoDetails.setMsaReliefInd("2");
								} else if (isRelief == null) {
									legacyCoDetails.setMsaReliefInd("0");
								}
							}
							if (vhtoCapabilityMap.containsKey(vhtm_recordId)) {
								legacyCoDetails.setFeatureCode(vhtoCapabilityMap.get(vhtm_recordId));
								if ("PA".equals(legacyCoDetails.getFeatureCode())) {
									legacyCoDetails.setZone("1");
								} else if ("PB".equals(legacyCoDetails.getFeatureCode())) {
									legacyCoDetails.setZone("2");
								} else {
									legacyCoDetails.setZone("3");
								}
							}
							legacyCoDetailsSavedList.add(legacyCoDetails);
							newAddedIds.add(vhtm_clli);
						}
					}
				}
				legacyCoDetailsDao.bulkSave(legacyCoDetailsSavedList);
			}
		}
	}

	protected List<String> parseLine(String line) {
		ArrayList<String> res = new ArrayList<>();
		boolean inQuote = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			switch (c) {
			case '"':
				inQuote = !inQuote;
				break;
			case ',':
				if (!inQuote) {
					res.add(sb.toString().trim());
					sb.setLength(0);
				} else {
					sb.append(c);
				}
				break;
			default:
				sb.append(c);
				break;
			}
		}
		res.add(sb.toString().trim());
		return res;
	}

	/*
	 * read BPTE File(4517 lines): sample line: ---
	 * "08-01-2002","F","ALABASTER                 ","ALBSALMA","5181","074","END",
	 * "ALL","","ALICEVILLE                ","ACVLALXA","9789","026","END" ---
	 * schema: --- [BPTE] Filetype=VARYING Delimiter=" Separator=, CharSet=ascii
	 * Field1=EffFromDt,CHAR,10,0 Field2=Flag,CHAR,1,0,10
	 * Field3=LocalityA,CHAR,26,0,11 Field4=CLLIA,CHAR,8,0,37
	 * Field5=CompanyA,CHAR,4,0,45 Field6=BPA,CHAR,3,0,49 Field7=OIA,CHAR,3,0,52
	 * Field8=Service,CHAR,3,0,55 Field9=TRC,CHAR,3,0,58
	 * Field10=LocalityB,CHAR,26,0,61 Field11=CLLIB,CHAR,8,0,87
	 * Field12=CompanyB,CHAR,4,0,95 Field13=BPB,CHAR,3,0,99 Field14=OIB,CHAR,3,0,102
	 * --- BPTE.CLLIA BPTE.CLLIB BPTE.CompanyA BPTE.CompanyB BPTE.BPA(convert to
	 * int, then convert back to string) BPTE.BPB(convert to int, then convert back
	 * to string) BPTE.Service BPTE.Flag BPTE.TRC
	 * 
	 * read BPTI File(250 lines): sample line: ---
	 * "01-01-2010","ALBRALXA","NTSLALXA","ALL","ALBERTA                   "
	 * ,"NOTASULGA                 ","5181","068","9789","9789" --- schema: ---
	 * [BPTI] Filetype=VARYING Delimiter=" Separator=, CharSet=ascii
	 * Field1=EffFromDt,CHAR,10,0,0 Field2=CLLIA,CHAR,8,0,10
	 * Field3=CLLIB,CHAR,8,0,18 Field4=Service,CHAR,3,0,26
	 * Field5=LocalityA,CHAR,26,0,29 Field6=LocalityB,CHAR,26,0,55
	 * Field7=Company,CHAR,4,0,81 Field8=BP,CHAR,3,0,85 --- BPTI.CLLIA BPTI.CLLIB
	 * BPTI.Company BPTI.BP(convert to int, then convert back to string)
	 * BPTI.Service
	 */
	protected void processBPTE(Path path, Date date) throws ZipException, IOException {
		try (ZipFile zipFile = new ZipFile(path.toFile())) {
			ZipEntry bpti = zipFile.stream().filter(zipEntry -> zipEntry.getName().startsWith("BPTI")).findFirst()
					.orElse(null);
			ZipEntry bpte = zipFile.stream().filter(zipEntry -> zipEntry.getName().startsWith("BPTE")).findFirst()
					.orElse(null);
			Set<Pk> existingLegacyCoPercentagePks = new HashSet<>();
			try (BufferedReader bptiIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(bpti)));
					BufferedReader bpteIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(bpte)))) {
				List<Pk> pksInFile = new ArrayList<>();
				String line;
				while ((line = bptiIn.readLine()) != null) {
					List<String> bptiTokens = parseLine(line);
					String bpti_cllia = bptiTokens.get(1);
					String bpti_cllib = bptiTokens.get(2);
					String bpti_company = com.att.sales.util.StringUtils.trimLeadingZeros(bptiTokens.get(6));
					String bpti_bp;
					try {
						bpti_bp = String.valueOf(Integer.parseInt(bptiTokens.get(7)));
					} catch (NumberFormatException e) {
						continue;
					}
					pksInFile.add(new Pk(bpti_cllia, bpti_cllib, bpti_company, bpti_bp));
				}
				while ((line = bpteIn.readLine()) != null) {
					List<String> bpteTokens = parseLine(line);
					String bpte_cllia = bpteTokens.get(3);
					String bpte_cllib = bpteTokens.get(10);
					if (!bpte_cllia.equals(bpte_cllib)) {
						String bpte_bpa;
						String bpte_bpb;
						try {
							bpte_bpa = String.valueOf(Integer.parseInt(bpteTokens.get(5)));
							bpte_bpb = String.valueOf(Integer.parseInt(bpteTokens.get(12)));
						} catch (NumberFormatException e) {
							continue;
						}
						String bpte_companyA = com.att.sales.util.StringUtils.trimLeadingZeros(bpteTokens.get(4));
						String bpte_companyB = com.att.sales.util.StringUtils.trimLeadingZeros(bpteTokens.get(11));
						pksInFile.add(new Pk(bpte_cllia, bpte_cllib, bpte_companyA, bpte_bpa));
						pksInFile.add(new Pk(bpte_cllia, bpte_cllib, bpte_companyB, bpte_bpb));
					}
				}
				existingLegacyCoPercentagePks = legacyCoPercentageDao.findPkByPkIn(pksInFile);
			}
			try (BufferedReader bptiIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(bpti)));
					BufferedReader bpteIn = new BufferedReader(new InputStreamReader(zipFile.getInputStream(bpte)))) {
				Map<String, Bpti> bptiMap = new HashMap<>();
				List<LegacyCoPercentage> legacyCoPercentageSavedList = new ArrayList<>();
				Set<Pk> newAddedIds = new HashSet<>();
				String line;
				while ((line = bptiIn.readLine()) != null) {
					List<String> bptiTokens = parseLine(line);
					String bpti_cllia = bptiTokens.get(1);
					String bpti_cllib = bptiTokens.get(2);
					String bpti_company = com.att.sales.util.StringUtils.trimLeadingZeros(bptiTokens.get(6));
					String bpti_bp;
					try {
						bpti_bp = String.valueOf(Integer.parseInt(bptiTokens.get(7)));
					} catch (NumberFormatException e) {
						continue;
					}
					String bpti_service = bptiTokens.get(3);
					Bpti bptiRecord = new Bpti(bpti_cllia, bpti_cllib, bpti_company, bpti_bp, bpti_service);
					bptiMap.put(bpti_cllia + "_" + bpti_cllib + "_" + bpti_service, bptiRecord);
				}
				while ((line = bpteIn.readLine()) != null) {
					List<String> bpteTokens = parseLine(line);
					String bpte_cllia = bpteTokens.get(3);
					String bpte_cllib = bpteTokens.get(10);
					if (!bpte_cllia.equals(bpte_cllib)) {
						String bpte_bpa;
						String bpte_bpb;
						try {
							bpte_bpa = String.valueOf(Integer.parseInt(bpteTokens.get(5)));
							bpte_bpb = String.valueOf(Integer.parseInt(bpteTokens.get(12)));
						} catch (NumberFormatException e) {
							continue;
						}
						String bpte_companyA = com.att.sales.util.StringUtils.trimLeadingZeros(bpteTokens.get(4));
						String bpte_companyB = com.att.sales.util.StringUtils.trimLeadingZeros(bpteTokens.get(11));
						Pk pk = new Pk(bpte_cllia, bpte_cllib, bpte_companyA, bpte_bpa);
						int percentTotal = Integer.parseInt(bpte_bpa);
						if (!existingLegacyCoPercentagePks.contains(pk) && !newAddedIds.contains(pk)) {
							LegacyCoPercentage legacyCoPercentage = new LegacyCoPercentage();
							legacyCoPercentage.setPk(pk);
							legacyCoPercentage.setCreatedDate(date);
							legacyCoPercentage.setStatus(STATUS_DCC_ADD);
							legacyCoPercentageSavedList.add(legacyCoPercentage);
							newAddedIds.add(pk);
						}

						pk = new Pk(bpte_cllia, bpte_cllib, bpte_companyB, bpte_bpb);
						percentTotal += Integer.parseInt(bpte_bpb);
						if (!existingLegacyCoPercentagePks.contains(pk) && !newAddedIds.contains(pk)) {
							LegacyCoPercentage legacyCoPercentage = new LegacyCoPercentage();
							legacyCoPercentage.setPk(pk);
							legacyCoPercentage.setCreatedDate(date);
							legacyCoPercentage.setStatus(STATUS_DCC_ADD);
							legacyCoPercentageSavedList.add(legacyCoPercentage);
							newAddedIds.add(pk);
						}

						if (percentTotal != 100) {
							String bpte_flag = bpteTokens.get(1);
							String bpte_trc = bpteTokens.get(8);
							if ("T".equals(bpte_flag) && "C".equals(bpte_trc)) {
								String bpte_service = bpteTokens.get(7);
								String bptiMapKey = bpte_cllia + "_" + bpte_cllib + "_" + bpte_service;
								if (bptiMap.containsKey(bptiMapKey)) {
									Bpti bptiRecord = bptiMap.get(bptiMapKey);
									pk = new Pk(bptiRecord.getCllia(), bptiRecord.getCllib(), bptiRecord.getCompany(),
											bptiRecord.getBp());
									if (!existingLegacyCoPercentagePks.contains(pk) && !newAddedIds.contains(pk)) {
										LegacyCoPercentage legacyCoPercentage = new LegacyCoPercentage();
										legacyCoPercentage.setPk(pk);
										legacyCoPercentage.setCreatedDate(date);
										legacyCoPercentage.setStatus(STATUS_DCC_ADD);
										legacyCoPercentageSavedList.add(legacyCoPercentage);
										newAddedIds.add(pk);
									}
								}
							}
						}
					}
				}
				legacyCoPercentageDao.bulkSave(legacyCoPercentageSavedList);
			}
		}
	}

	/*
	 * read CCA List May 2021.xlsx (17572 lines) only need "CCA List" sheet, if
	 * CCA_List.ASEC Name <> "", read data: CCA_List.ASEC, column index 3
	 * CCA_List.ASEC Name, column index 8 CCA_List.Category, column index 0
	 */
	protected Map<String, CcaListData> loadCcaListData(Path ccaListData) throws IOException {
		Map<String, CcaListData> res = new HashMap<>();
		try (InputStream in = Files.newInputStream(ccaListData);
				Workbook workbook = StreamingReader.builder().rowCacheSize(100).bufferSize(4096).open(in)) {
			Sheet sheet = workbook.getSheetAt(1);
			java.util.Iterator<Row> rows = sheet.rowIterator();
			rows.next();
			while (rows.hasNext()) {
				Row r = rows.next();
				String asec = r.getCell(3).getStringCellValue().trim();
				if (!asec.isEmpty()) {
					String asecName = r.getCell(8).getStringCellValue().trim();
					String category = r.getCell(0).getStringCellValue().trim();
					CcaListData data = new CcaListData(asec, asecName.isEmpty() ? null : asecName,
							category.isEmpty() ? null : category);
					res.put(asec, data);
				}
			}
		}
		return res;
	}

	/*
	 * switch type mismatch switch type in file [1AES, 5ES, 5EXM, 5ORM, 5RSM, D1/2,
	 * D10, D100, D500, DCO, EWSD, NS5, NSD, RCU, RLCM, RLS, RLS4, RLU, RNS, RSC,
	 * SRSC] switch type in mapping [1AES, 5ES, 5EXM, 5ORM, 5RSM, D1/2, D10, D100,
	 * D200, D500, DCO, EWSD, NS5, RCU, RLCM, RLS, RLS4, RLU, RNS, RSC, SRSC]
	 * 
	 */

	/*
	 * sample line --- ABRDMSESRS9ABEROSMSMS482M1ABERDEEN 300 N MERIDIAN ST ABERDEEN
	 * MS39730SCB 5EXM5E16.2 N NI-2 N NOT ALLOWED NOT ALLOWED CLMBMSMADS0NI-2 25
	 * 7261 64 0 --- schema --- [TISDN] Filetype=FIXED CharSet=ascii Field1 = CLLI,
	 * CHAR, 11,0,0 Field2 = EXCH_ABBREV, CHAR, 4,0,11 Field3 = MSA_ABBREV, CHAR,
	 * 6,0,15 Field4 = LCODE_NET, CHAR, 3,0,21 Field5 = LCODE_MKT, CHAR, 2,0,24
	 * Field6 = SWITCH_NAME, CHAR, 20,0,26 Field7 = STREET, CHAR, 30,0,46 Field8 =
	 * CITY, CHAR, 20,0,76 Field9 = STATE, CHAR, 2,0,96 Field10 = ZIP, CHAR, 5,0,98
	 * Field11 = CO, CHAR, 4,0,103 Field12 = SWTYPE, CHAR, 4,0,107 Field13 = GENISS,
	 * CHAR, 12,0,111 Field14 = PACKET_IND, CHAR, 1,0,123 Field15 = PACKET_DATE,
	 * CHAR, 8,0,124 Field16 = BRI_EQ, CHAR, 6,0,132 Field17 = PRI_EQ, CHAR, 6,0,138
	 * Field18 = PROJ_ID, CHAR, 12,0,144 Field19 = EVT_ID1, CHAR, 6,0,156 Field20 =
	 * EVT_SVC_DATE1, CHAR, 8,0,162 Field21 = FUTBRI1, CHAR, 4,0,170 Field22 =
	 * FUTPRI1, CHAR, 4,0,174 Field23 = PRIM_BA_SO, CHAR, 11,0,178 Field24 =
	 * PRIM_BA_SOFT, CHAR, 6,0,189 Field25 = PRIM_BA_MILE, CHAR, 10,0,195 Field26 =
	 * PRIM_PA_SO, CHAR, 11,0,205 Field27 = PRIM_PA_SOFT, CHAR, 6,0,216 Field28 =
	 * PRIM_PA_MILE, CHAR, 10,0,222 Field29 = SEC_BA_SO, CHAR, 11,0,232 Field30 =
	 * SEC_BA_SOFT, CHAR, 6,0,243 Field31 = SEC_BA_MILE, CHAR, 10,0,249 Field32 =
	 * SEC_PA_SO, CHAR, 11,0,259 Field33 = SEC_PA_SOFT, CHAR, 6,0,270 Field34 =
	 * SEC_PA_MILE, CHAR, 10,0,276 Field35 = BF_SO, CHAR, 11,0,286 Field36 =
	 * BF_SOFT, CHAR, 6,0,297 Field37 = BF_MILE, CHAR, 10,0,303 Field38 = PF_SO,
	 * CHAR, 11,0,313 Field39 = PF_SOFT, CHAR, 6,0,324 Field40 = PF_MILE, CHAR,
	 * 10,0,330 Field41 = PRIM_HOST, CHAR, 11,0,340 Field42 = PRIM_HOST_SOFT, CHAR,
	 * 6,0,351 Field43 = PRIM_HOST_MILE, CHAR, 10,0,357 Field44 = ACCESS_TOTAL,
	 * CHAR, 1,0,367 Field45 = ACCESS_BRI, CHAR, 1,0,368 Field46 = ACCESS_PRI, CHAR,
	 * 1,0,369 --- TISDN.CLLI = Field1 = CLLI, CHAR, 11,0,0 TISDN.SWTYPE = Field12 =
	 * SWTYPE, CHAR, 4,0,107 TISDN.PRI_EQ = Field17 = PRI_EQ, CHAR, 6,0,138
	 * TISDN.PRIM_PA_SO = Field26 = PRIM_PA_SO, CHAR, 11,0,205 TISDN.PF_SO = Field38
	 * = PF_SO, CHAR, 11,0,313 TISDN.PRIM_HOST = Field41 = PRIM_HOST, CHAR, 11,0,340
	 */
	protected void processTISDN(NxTemplateUploadRequest request, Date date) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			Map<String, String> switchTypeMap = nxMyPriceRepositoryServce
					.getDescDataFromLookup("LEGACY_CO_DETAILS_SWITCHTYPE");
			Map<String, String> centrexCapableMap = nxMyPriceRepositoryServce
					.getDescDataFromLookup("LEGACY_CO_DETAILS_CENTREX_CAPABLE");
			String line = null;
			while ((line = in.readLine()) != null) {
				String state = line.substring(96, 96 + 2);
				if (!STATE_FILTER.contains(state)) {
					continue;
				}
				String tisdn_clli = line.substring(0, 0 + 11);
				LegacyCoDetails legacyCoDetails = legacyCoDetailsDao.findOne(tisdn_clli);
				if (legacyCoDetails != null) {
					String tisdn_swtype = line.substring(107, 107 + 4).trim();
					String tisdn_pri_eq = line.substring(138, 138 + 6).trim();
					String tisdn_prim_pa_so = line.substring(205, 205 + 11);
					String tisdn_pf_so = line.substring(313, 313 + 11);
					String tisdn_prim_host = line.substring(340, 340 + 11);
					if (switchTypeMap.containsKey(tisdn_swtype)) {
						legacyCoDetails.setSwitchtype(switchTypeMap.get(tisdn_swtype));
					}
					if (centrexCapableMap.containsKey(tisdn_swtype)) {
						legacyCoDetails.setCentrexCapable(centrexCapableMap.get(tisdn_swtype));
					}
					String priCapable = null;
					if (!"N".equals(tisdn_pri_eq)) {
						if (StringUtils.isNotBlank(tisdn_prim_pa_so)) {
							priCapable = "A";
						} else {
							priCapable = "E";
						}
					} else {
						if (StringUtils.isNotBlank(tisdn_prim_pa_so)) {
							priCapable = "A";
						} else if (StringUtils.isNotBlank(tisdn_pf_so)) {
							priCapable = "F";
						} else if (StringUtils.isNotBlank(tisdn_prim_host)) {
							priCapable = "R";
						}
					}
					legacyCoDetails.setIsdnprihosttype(priCapable);
					legacyCoDetails.setModifiedDate(date);
					legacyCoDetails.setStatus(STATUS_DCC_MODIFY);
				}
			}
		}
	}

	/*
	 * sample line --- 2052060 BRHMALENRPA2012071600000999 000ASRSCSN09U
	 * 2021-05-09009419 D2057810000BIRMINGHAM --- QENXXNP.COCLLI = Start from 9th
	 * character fetch 8 characters; 'BRHMALEN' QENXXNP.SwitchCLLI = Start from 17th
	 * character fetch 3 characters; 'RPA' QENXXNP.SWCCLLI = COCLLI + SwitchCLLI;
	 * 'BRHMALENRPA' QENXXNP.STATE = Start from 13th character fetch 2 characters
	 */
	protected void processQENXXNP(NxTemplateUploadRequest request, Date date) throws IOException {
		try (BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
			List<LegacyCoDetails> legacyCoDetailsSavedList = new ArrayList<>();
			Set<String> newAddedIds = new HashSet<>();
			List<String> ids = legacyCoDetailsDao.findAllSwcclliSorted();
			String line = null;
			while ((line = in.readLine()) != null) {
				String qenxxnp_state = line.substring(12, 12 + 2);
				if (!STATE_FILTER.contains(qenxxnp_state)) {
					continue;
				}
				String qenxxnp_swcclli = line.substring(8, 8 + 11);
				if (Collections.binarySearch(ids, qenxxnp_swcclli) < 0 && !newAddedIds.contains(qenxxnp_swcclli)) {
					String qenxxnp_coclli = line.substring(8, 8 + 8);
					String qenxxnp_switchclli = line.substring(16, 16 + 3);
					LegacyCoDetails legacyCoDetails = new LegacyCoDetails();
					legacyCoDetails.setCreatedDate(date);
					legacyCoDetails.setStatus(STATUS_DCC_ADD);
					legacyCoDetails.setSwcclli(qenxxnp_swcclli);
					legacyCoDetails.setCoclli(qenxxnp_coclli);
					legacyCoDetails.setSwitchclli(qenxxnp_switchclli);
					legacyCoDetails.setState(qenxxnp_state);
					legacyCoDetailsSavedList.add(legacyCoDetails);
					newAddedIds.add(qenxxnp_swcclli);
				}
			}
			legacyCoDetailsDao.bulkSave(legacyCoDetailsSavedList);
		}
	}
	
	static class CcaListData {
		private String asec;
		private String asecName;
		private String category;

		public CcaListData(String asec, String asecName, String category) {
			super();
			this.asec = asec;
			this.asecName = asecName;
			this.category = category;
		}

		public String getAsec() {
			return asec;
		}

		public String getAsecName() {
			return asecName;
		}

		public String getCategory() {
			return category;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("CcaListData [asec=").append(asec).append(", asecName=").append(asecName)
					.append(", category=").append(category).append("]");
			return builder.toString();
		}

	}

	static class Bpti {
		private String cllia;
		private String cllib;
		private String company;
		private String bp;
		private String service;

		public Bpti(String cllia, String cllib, String company, String bp, String service) {
			super();
			this.cllia = cllia;
			this.cllib = cllib;
			this.company = company;
			this.bp = bp;
			this.service = service;
		}

		public String getCllia() {
			return cllia;
		}

		public String getCllib() {
			return cllib;
		}

		public String getCompany() {
			return company;
		}

		public String getBp() {
			return bp;
		}

		public String getService() {
			return service;
		}
	}
}
