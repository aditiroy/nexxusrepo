package com.att.sales.nexxus.dao.repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.constant.MyPriceConstants;
import com.google.common.collect.Lists;

import edu.emory.mathcs.backport.java.util.Arrays;

@Repository
public class UsrpDao {
	private static Logger logger = LoggerFactory.getLogger(UsrpDao.class);
	private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedjdbcTemplate;
	int chunkSize = 1000;
	public static final Comparator<Map<String, Object>> EPLSWAN_CMP = Comparator
			.comparingInt(UsrpDao::eplsWanKeyFieldsWeightedCount);
	
	@Autowired 
    public void setDataSource(@Qualifier("usrpDS") DataSource usrpDS) {
        this.jdbcTemplate = new JdbcTemplate(usrpDS); 
        this.namedjdbcTemplate = new NamedParameterJdbcTemplate(usrpDS); 

    }

//	public List<Map<String,Object>> query(String mcn) {
//		return jdbcTemplate.queryForList("select * from v_avpn_inv where service_mcn = ? order by circuitid", mcn);
//	}
	
	public List<Map<String,Object>> queryWithSize(String mcn, String viewName, String product) {
		logger.info("query usrp for mcn {}, viewName {}, product {}", mcn, viewName, product);
		String addtionalFilter = "";
		if (MyPriceConstants.ADI.equalsIgnoreCase(product)) {
			addtionalFilter = " and svc_desc = 'MIS/ADI' ";
		} else if (MyPriceConstants.ADIG.equalsIgnoreCase(product)) {
			addtionalFilter = " and svc_desc = 'GMIS/ADIG' ";
		}
		String mcnColumnName = "service_mcn";
		if (MyPriceConstants.EPLSWAN.equals(product)) {
			mcnColumnName = "mcn";
		}
		String sql = String.format("select top 1000 * from %s where %s = ? %s order by circuitid", viewName, mcnColumnName, addtionalFilter);
		String sqlSubsequent = String.format("select top 1000 * from %s where %s = ? and circuitid >= ? %s order by circuitid", viewName, mcnColumnName, addtionalFilter);
		Set<Map<String,Object>> res = new HashSet<>();
		boolean hasMore = false;
		List<Map<String, Object>> queryForList = jdbcTemplate.queryForList(sql, mcn);
		String lastCircuitId = "";
		do {
			for (Map<String,Object> row : queryForList) {
				if (!res.contains(row)) {
					res.add(row);
					lastCircuitId = String.valueOf(row.get("circuitid"));
				}
			}
			if (queryForList.size() < 1000) {
				hasMore = false;
			} else {
				hasMore = true;
				queryForList = jdbcTemplate.queryForList(sqlSubsequent, mcn, lastCircuitId);
			}
		} while (hasMore);
		
		if (MyPriceConstants.EPLSWAN.equals(product)) {
			Map<String, Optional<Map<String, Object>>> collect = res.stream().collect(
					Collectors.groupingBy(row -> String.valueOf(row.get("circuitid")), Collectors.maxBy(EPLSWAN_CMP)));
			List<Map<String, Object>> collect2 = collect.values().stream().map(Optional::get)
					.collect(Collectors.toList());
			return collect2;
		} else {
			return new ArrayList<>(res);
		}
	}
	
//	public List<Map<String,Object>> query(String viewName, String mcn, String circuitId) {
//		String sqlString = String.format("select * from %s where service_mcn = ? and circuitid = ?", viewName);
//		return jdbcTemplate.queryForList(sqlString, mcn, circuitId);
//	}
	
	public List<Map<String,Object>> query(String sql, Object... args) {
		logger.info("query usrp in sql {}", sql);
		logger.info("with args {}", Arrays.toString(args));
		return jdbcTemplate.queryForList(sql, args);
	}
	
	public List<Map<String,Object>> queryAvpnCircuitByPortNumber(List<String> portNumberList) {
		/*In query has limitation of 1000 entries*/
		 List<Long> p = portNumberList.stream().filter(str -> str!=null && !str.isEmpty()).map(s -> Long.parseLong(s))
                 .collect(Collectors.toList());
		List<List<Long>> smallerPortNumberList = Lists.partition(p, chunkSize);	
		Map<String,Object> queryParam= new HashMap<String, Object>();
		List<Map<String, Object>> result=new ArrayList<>();
		String sql = "select * from v_avpn_inv where icore_site_id IN (:portNumber)";
		for(List<Long> l: smallerPortNumberList) {
			queryParam.put("portNumber", l);
			List<Map<String, Object>> r=namedjdbcTemplate.queryForList(sql, queryParam);
			result.addAll(r);
		}
		

		return result;
	}
	
	
	public String getProductTypeBasedCircuitAndPortNumber(String avpncircuitId,String avpnclsSerial,String adigcircuitId,
			String adigclsSerial,String portNumber) {
	String res=null;
		String sql="select top 1 * from v_adig_inv where circuitid = ? and cls_serial = ? and svc_desc = 'MIS/ADI' union select top 1 * from v_adig_inv where billing_sap_id = ? and svc_desc = 'MIS/ADI' union select top 1 * from v_adig_inv where ipd_serv_acc_pt_id = convert(int, ?) and svc_desc = 'MIS/ADI' union select top 1 * from v_adig_inv where ml_ipd_serv_acc_pt_id = convert(int, ?) and svc_desc = 'MIS/ADI'";
		Object[] adiPassArgs = new Object[5];
		adiPassArgs[0]=adigcircuitId;
		adiPassArgs[1]=adigclsSerial;
		adiPassArgs[2]=portNumber;
		adiPassArgs[3]=portNumber;
		adiPassArgs[4]=portNumber;
		List<Map<String, Object>> adiQuery=jdbcTemplate.queryForList(sql, adiPassArgs);
		if (!adiQuery.isEmpty()) {
			return "ADI";
		}else {
			String avpnsql="select top 1 * from v_avpn_inv where (circuitid = ? and cls_serial = ?) union select top 1 * from v_avpn_inv where icore_site_id = convert(int,?)";
			Object[] avpnPassArgs = new Object[3];
			avpnPassArgs[0]=avpncircuitId;
			avpnPassArgs[1]=avpnclsSerial;
			avpnPassArgs[2]=portNumber;
			List<Map<String, Object>> avpnQuery=jdbcTemplate.queryForList(avpnsql, avpnPassArgs);
			if (!avpnQuery.isEmpty()) {
				return "AVPN";
			}
		}
	
	return res;
	}
	
	public String getProductTypeBasedCircuit(String avpncircuitId,String avpnclsSerial,String adigcircuitId,String adigclsSerial) {
	String res=null;
		String sql="select top 1 * from v_adig_inv where circuitid = ? and cls_serial = ? and svc_desc = 'MIS/ADI'";
		Object[] adiPassArgs = new Object[2];
		adiPassArgs[0]=adigcircuitId;
		adiPassArgs[1]=adigclsSerial;
		List<Map<String, Object>> adiQuery=jdbcTemplate.queryForList(sql, adiPassArgs);
		if (!adiQuery.isEmpty()) {
			return "ADI";
		}else {
			String avpnsql="select top 1 * from v_avpn_inv where circuitid = ? and cls_serial = ?";
			Object[] avpnPassArgs = new Object[2];
			avpnPassArgs[0]=avpncircuitId;
			avpnPassArgs[1]=avpnclsSerial;
			List<Map<String, Object>> avpnQuery=jdbcTemplate.queryForList(avpnsql, avpnPassArgs);
			if (!avpnQuery.isEmpty()) {
				return "AVPN";
			}
		}
	
	return res;
	}
	
	public String getProductTypeBasedPortNumber(String portNumber) {
		String res=null;
			String sql="select top 1 * from v_adig_inv where billing_sap_id = ? and svc_desc = 'MIS/ADI' union select top 1 * from v_adig_inv where ipd_serv_acc_pt_id = convert(int, ?) and svc_desc = 'MIS/ADI' union select top 1 * from v_adig_inv where ml_ipd_serv_acc_pt_id = convert(int, ?) and svc_desc = 'MIS/ADI'";
			Object[] adiPassArgs = new Object[3];
			adiPassArgs[0]=portNumber;
			adiPassArgs[1]=portNumber;
			adiPassArgs[2]=portNumber;

			List<Map<String, Object>> adiQuery=jdbcTemplate.queryForList(sql, adiPassArgs);
			if (!adiQuery.isEmpty()) {
				return "ADI";
			}else {
				String avpnsql="select top 1 * from v_avpn_inv where icore_site_id = convert(int,?)";
				Object[] avpnPassArgs = new Object[1];
				avpnPassArgs[0]=portNumber;
				List<Map<String, Object>> avpnQuery=jdbcTemplate.queryForList(avpnsql, avpnPassArgs);
				if (!avpnQuery.isEmpty()) {
					return "AVPN";
				}
			}
		
		return res;
		}
	
	public static int eplsWanKeyFieldsWeightedCount(Map<String, Object> row) {
		int res = 0;
		String[] lineItemLookupAndSubmitToMpFields = {"bitrate", "ioc_type", "z_end_pop_clli", "a_end_pop_clli", "a_end_swc_clli", "z_end_swc_clli", "z_end_access_vendor", "a_end_access_vendor"};
		String[] designJsonFields = {"circuitid", "z_end_paa", "a_end_paa", "z_end_loc_country", "a_end_loc_country", "z_end_loc_state", "a_end_loc_state", "z_end_loc_str1", "a_end_loc_str1", "z_end_loc_zip", "a_end_loc_zip", "z_end_loc_city", "a_end_loc_city", "z_end_cpe_interface_type", "a_end_cpe_interface_type", "z_end_access_type", "a_end_access_type"};
		for (String key : lineItemLookupAndSubmitToMpFields) {
			Object value = row.get(key);
			if (value != null && !String.valueOf(value).trim().isEmpty()) {
				res += (designJsonFields.length + 1);
			}
		}
		for (String key : designJsonFields) {
			Object value = row.get(key);
			if (value != null && !String.valueOf(value).trim().isEmpty()) {
				res++;
			}
		}
		return res;
	}
}
