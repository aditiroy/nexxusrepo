package com.att.sales.nexxus.inr;
import static org.junit.Assert.assertNotNull;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.dao.repository.InrXmlToJsonRuleDao;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.dao.repository.NxUdfMappingDao;
import com.att.sales.nexxus.dao.repository.SalesMsDao;
import com.att.sales.nexxus.dao.repository.SalesMsProdcompUdfAttrValRepository;
import com.att.sales.nexxus.service.NexxusAIService;
import com.att.sales.nexxus.service.NxMyPriceRepositoryServce;
import com.att.sales.nexxus.util.UnmockableWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class InrFactoryTest {
	@Mock
	private ObjectMapper mapper;
	@Mock
	private InrXmlToJsonRuleDao inrXmlToJsonRuleDao;
	@Mock
	private EntityManager em;
	@Spy
	@InjectMocks
	private InrFactory inrFactory;
	@Mock
	private NxLookupDataRepository nxLookupDataRepository;
	@Mock
	private UnmockableWrapper unmockableWrapper;
	@Mock
	private NexxusAIService nexxusAIService;
	@Mock
	private NxUdfMappingDao nxUdfMappingDao;
	@Mock
	private SalesMsProdcompUdfAttrValRepository salesMsProdcompUdfAttrValRepository;
	@Mock
	private SalesMsDao salesMsDao;
	@Mock
	private NxMyPriceRepositoryServce nxMyPriceRepositoryServce;
	
	@Test
	public void test() throws SalesBusinessException {
		ReflectionTestUtils.setField(inrFactory, "p8dLocalPath", "/");
		ReflectionTestUtils.setField(inrFactory, "templatePath", "/");
		
		assertNotNull(inrFactory.getInrJsonToIntermediateJson(null));
		assertNotNull(inrFactory.getOutputJsonGenerator(null, null));
		assertNotNull(inrFactory.getInrPreviewGeneratorV1(null, 0));
		assertNotNull(inrFactory.getAseDppRequestToSnsdRequest(null));
		assertNotNull(inrFactory.getAseDppRequestToSnsdSoldCancelRequest(null));
		assertNotNull(inrFactory.getAseDppRequestUdfTranslation(null));
		assertNotNull(inrFactory.getInrInventoryJsonFlatten(null, null));
		assertNotNull(inrFactory.getInrInventoryJsonToIntermediateJson(null, null));
		assertNotNull(inrFactory.getCopyOutputToIntermediateJson(null, null));
		assertNotNull(inrFactory.getInrIntermediateJsonUpdate(null, null, null, null, null, null, null));
	}
}
