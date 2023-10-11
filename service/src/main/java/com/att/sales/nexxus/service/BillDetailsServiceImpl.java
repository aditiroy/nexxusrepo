package com.att.sales.nexxus.service;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.common.StringConstants;
import com.att.sales.nexxus.dao.model.NxLookupData;
import com.att.sales.nexxus.dao.repository.NxLookupDataRepository;
import com.att.sales.nexxus.model.FetchBillDetails;
import com.att.sales.nexxus.model.FetchBillDetailsResponse;


/**
 * @author AVSV3C744
 *
 */
@Service
@Transactional
public class BillDetailsServiceImpl extends BaseServiceImpl implements BillDetailsService {
	
	@Autowired
	public NxLookupDataRepository nxLookupDataRepository;

	private static Logger logger = LoggerFactory.getLogger(BillDetailsServiceImpl.class);

	@Override
	public ServiceResponse fetchBillDetails() {

		FetchBillDetailsResponse response = new FetchBillDetailsResponse();
		List<NxLookupData> nxLookUpDatas = new ArrayList<NxLookupData>();
		List<String> dataSetName = new ArrayList<String>();
		dataSetName.add(StringConstants.BILL_MONTH);
		dataSetName.add(StringConstants.BEGIN_BILL_MONTH);
		nxLookUpDatas = nxLookupDataRepository.fetchByDatasetNameAndActive(dataSetName, StringConstants.Y);

		List<FetchBillDetails> billMonthsList = new ArrayList<>();
		List<FetchBillDetails> beginBillMonthsList = new ArrayList<>();
		for (NxLookupData lookupData : nxLookUpDatas) {
			FetchBillDetails billMonth = new FetchBillDetails();
			billMonth.setEdfInput(lookupData.getItemId());
			billMonth.setUiMonthYear(lookupData.getDescription());
			if(lookupData.getDatasetName().equals(StringConstants.BEGIN_BILL_MONTH)){
				beginBillMonthsList.add(billMonth);
			}else{
				billMonthsList.add(billMonth);		
			}
		}
		response.setBillMonths(billMonthsList);
		response.setBeginBillMonths(beginBillMonthsList);
		logger.info("response:"+response);
		setSuccessResponse(response);
		return response;
	}
  }
