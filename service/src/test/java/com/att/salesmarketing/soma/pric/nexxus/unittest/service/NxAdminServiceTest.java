package com.att.salesmarketing.soma.pric.nexxus.unittest.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.hibernate.annotations.Sort;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceMetaData;
import com.att.sales.nexxus.common.CommonConstants;
import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.TopProductDataEntity;
import com.att.sales.nexxus.dao.repository.LittleProductRepo;
import com.att.sales.nexxus.dao.repository.TopProductRepo;
import com.att.sales.nexxus.model.RetrieveAdminDataRequest;
import com.att.sales.nexxus.service.NxAdminService;


@SuppressWarnings("deprecation")
@ExtendWith(MockitoExtension.class)
public class NxAdminServiceTest {

	
	@InjectMocks
	NxAdminService nxAdminService;
	
	@Mock
	private LittleProductRepo littleProductRepo;

	@Mock
	private TopProductRepo topProductRepo;
	
	@Mock
	private EntityManager em;

	@Mock
	private StoredProcedureQuery storedProcedure;
	
	@Mock
	private Query q ;
	
	@BeforeEach
	public static void init() {
		Map<String, Object> map = new HashMap<>();
		map.put(ServiceMetaData.OFFER, "2");
		map.put(ServiceMetaData.VERSION, "v2");
		map.put(ServiceMetaData.METHOD, "post");
		map.put(ServiceMetaData.URI, "hghg");
		map.put(ServiceMetaData.REST_REQUEST_START_TIME, System.currentTimeMillis());
		map.put(ServiceMetaData.SERVICE_FILTER, "AVPN");
		map.put(ServiceMetaData.SERVICEID, "SERVICEID");
		map.put(CommonConstants.FILENAME, "FILE_1");
		
		ServiceMetaData.add(map);

	}

	@SuppressWarnings("deprecation")
	@Test
	public void testGetAdminData() throws SalesBusinessException {
		RetrieveAdminDataRequest request  = new RetrieveAdminDataRequest();
		request.setAction("retrieveAdminData");
		List<TopProductDataEntity> topProductDataEntities = new ArrayList<>();
		TopProductDataEntity dataEntity = new TopProductDataEntity();
		dataEntity.setProduct("product");
		topProductDataEntities.add(dataEntity);
		List<LittleProductDataEntity> littleProductDataEntities =new ArrayList<>();
		LittleProductDataEntity dataEntity2 = new LittleProductDataEntity();
		dataEntity2.setLittleProductId(12345L);
		dataEntity2.setLittleProductName("littleProductName");
		littleProductDataEntities.add(dataEntity2);
		Mockito.when(topProductRepo.findAll((org.springframework.data.domain.Sort) any(Sort.class)))
				.thenReturn(topProductDataEntities);
		Mockito.when(littleProductRepo
				.findByTopProductData(Mockito.any())).thenReturn(littleProductDataEntities);
		nxAdminService.getAdminData(request);
	}
	

	@Test
	public void testGetAdminDataException() throws SalesBusinessException {
		RetrieveAdminDataRequest request  = new RetrieveAdminDataRequest();
		request.setAction("ret");
		try {
			nxAdminService.getAdminData(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
					}
	}
	
	@Test
	public void testfetchAllTopProducts() throws SalesBusinessException {
		List<TopProductDataEntity> topProductDataEntities = new ArrayList<>();
		TopProductDataEntity dataEntity = new TopProductDataEntity();
		dataEntity.setProduct("product");
		topProductDataEntities.add(dataEntity);
		Mockito.when(topProductRepo.findAll((org.springframework.data.domain.Sort) any(Sort.class))).thenReturn(topProductDataEntities);
		nxAdminService.fetchAllTopProducts(new LinkedHashMap<String, Object>());
	}
	
	@Test
	public void testfetchAllTopProductsException() throws SalesBusinessException {
		doThrow(Exception.class).when(topProductRepo).findAll((org.springframework.data.domain.Sort) any(Sort.class));
		try {
			nxAdminService.fetchAllTopProducts(new LinkedHashMap<String, Object>());
		}catch (Exception e) {
		}
	}
	
	@Test
	public void testadminUpdateProductInfo() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new  LinkedHashMap<>();
		queryParams.put("productType","TOP");
		queryParams.put("srcTopProductId",1L);
		queryParams.put("destTopProductId",1L);
		queryParams.put("destTopProductDesc","destTopProductDesc");
		when(em.createStoredProcedureQuery(Mockito.anyString())).thenReturn(storedProcedure);
		when(storedProcedure.registerStoredProcedureParameter(any(),any(),any())).thenReturn(storedProcedure);
		nxAdminService.adminUpdateProductInfo(queryParams);
		
		queryParams.put("productType","LITTLE");
		queryParams.put("topProductId",1L);
		queryParams.put("srcLittleProductId",1L);
		queryParams.put("destLittleProductId",1L);
		queryParams.put("destLittleProductDesc", "destLittleProductDesc");
		when(em.createNamedQuery(Mockito.anyString())).thenReturn(q);
		nxAdminService.adminUpdateProductInfo(queryParams);
	}
	
	@Test
	public void testadminUpdateProductInfoException() throws SalesBusinessException {
		LinkedHashMap<String, Object> queryParams = new  LinkedHashMap<>();
		queryParams.put("productType","LITTLE");
		doThrow(Exception.class).when(em).createNamedQuery(anyString());
		try {
			nxAdminService.adminUpdateProductInfo(queryParams);
		}catch(Exception e) {}
	}

}
