package com.att.sales.nexxus.service;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;


import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.att.sales.nexxus.dao.model.NxMpDeal;
import com.att.sales.nexxus.dao.repository.NxMpDealRepository;

@ExtendWith(MockitoExtension.class)
public class UpdateInrDealFailedServiceTest {
	
	@InjectMocks
	private UpdateInrDealFailedService updataInrDealFailedService;
	@Mock
	private NxMpDealRepository nxMpDealRepository;
	@Mock
	private MailServiceImpl mailServiceImpl;
	
	@Test
	public void updateMpDealFailedStatusTest() {
		ReflectionTestUtils.setField(updataInrDealFailedService, "enableScheduler", "Y");
		List<NxMpDeal> nxMpDeals = new ArrayList<NxMpDeal>();
		NxMpDeal deal = new NxMpDeal();
		deal.setDealStatus("CREATED");
		nxMpDeals.add(deal);
		when(nxMpDealRepository.getNxMpDeals(any())).thenReturn(nxMpDeals);
		when(nxMpDealRepository.save(any(NxMpDeal.class))).thenReturn(deal);
		doNothing().when(mailServiceImpl).prepareMyPriceDealSubmissionRequest(any());
		updataInrDealFailedService.updateMpDealFailedStatus();
	}


}
