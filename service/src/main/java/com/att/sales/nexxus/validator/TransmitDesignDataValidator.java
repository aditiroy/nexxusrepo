package com.att.sales.nexxus.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.constant.TDDConstants;
import com.att.sales.nexxus.transmitdesigndata.model.CircuitDetails;
import com.att.sales.nexxus.transmitdesigndata.model.EndpointDetails;
import com.att.sales.nexxus.transmitdesigndata.model.PortDetails;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionStatus;
import com.att.sales.nexxus.transmitdesigndata.model.TransmitDesignDataRequest;

/**
 * The Class TransmitDesignDataValidator.
 */
public class TransmitDesignDataValidator {
	
	/**
	 * Instantiates a new transmit design data validator.
	 */
	private TransmitDesignDataValidator() {}
	
	/**
	 * Validate upload request.
	 *
	 * @param request the request
	 * @throws SalesBusinessException the sales business exception
	 */
	public static void validateUploadRequest(TransmitDesignDataRequest request) throws SalesBusinessException  {
		List<String> msg = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(request.getSolutionStatus())) {
			request.getSolutionStatus().stream().filter(Objects::nonNull).forEach( solutionData -> {
				if(StringUtils.isEmpty(solutionData.getOpportunityId())) {
					msg.add(MessageConstants.OPTY_ID_NOT_FOUND);
				}
				if(solutionData.getSolutionId()==null) {
					msg.add(MessageConstants.SOLUTION_ID_NOT_FOUND);
				}
				if(CollectionUtils.isNotEmpty(solutionData.getCircuitDetails())) {
					//validateCircuitDetails(solutionData.getCircuitDetails(), msg,solutionData);
				}
				if(CollectionUtils.isNotEmpty(solutionData.getPortDetails())) {
					validatePortDetails(msg, solutionData.getPortDetails(),solutionData);
				}
			});
		}
		if(CollectionUtils.isNotEmpty(msg)) {
			throw new SalesBusinessException(msg);
		}
	}
	
	
	public static void validateCircuitDetails(List<CircuitDetails> circuitDetails,List<String> msg,SolutionStatus solutionData) {
		circuitDetails. stream().filter(Objects::nonNull).forEach( circuitData -> {
			if(StringUtils.isEmpty(circuitData.getAsrItemId())) {
				msg.add(MessageConstants.ASR_ITEM_ID_NOT_FOUND);
			}
			if(circuitData.getConfirmedInterval()==null) {
				msg.add(MessageConstants.CONFIRMED_INTERVAL_NOT_FOUND);
			}
			if(circuitData.getEstimatedInterval()==null) {
				msg.add(MessageConstants.ESTIMATED_INTERVAL_NOT_FOUND);
			}
			if(((solutionData.getStatusCode().equals("CL") 
					|| solutionData.getStatusCode().equals("SC"))
					&& solutionData.getResponseType().equals(TDDConstants.CIRCUIT)) 
					&& StringUtils.isEmpty(circuitData.getCircuitCancellationReason())) {
				msg.add(MessageConstants.CIRCUIT_CANCEL_REASON_NOT_FOUND);
			}
			if(CollectionUtils.isNotEmpty(circuitData.getEndpointDetails())) {
				validateEndPointDetails(msg, circuitData.getEndpointDetails());
			}
		});
	}

	protected static void validateEndPointDetails(List<String> msg, List<EndpointDetails> endpointDetails) {
		endpointDetails. stream().filter(Objects::nonNull).forEach( endPointData -> {
			if(StringUtils.isEmpty(endPointData.getEndpointType())) {
				msg.add(MessageConstants.END_POINT_TYPE_NOT_FOUND);
			}
			if(StringUtils.isEmpty(endPointData.getEdgelessDesignIndicator())) {
				msg.add(MessageConstants.EDGELESS_DESIGN_IND_NOT_FOUND);
			}
			if(StringUtils.isEmpty(endPointData.getLocationCLLI())) {
				msg.add(MessageConstants.LOCATION_CLLI_NOT_FOUND);
			}
			if(StringUtils.isEmpty(endPointData.getAlternateSWCCLLI())) {
				msg.add(MessageConstants.ALTERNATE_SWC_CLLI_NOT_FOUND);
			}
			
		});
	}
	
	public static void validatePortDetails(List<String> msg,List<PortDetails> portDetails,SolutionStatus solutionData) {
		portDetails. stream().filter(Objects::nonNull).forEach( portData -> {
			if(StringUtils.isEmpty(portData.getAsrItemId())) {
				msg.add(MessageConstants.ASR_ITEM_ID_NOT_FOUND);
			}
			if(!isFailledInd(portData) && StringUtils.isEmpty(portData.getStatusCode())) {
				msg.add(MessageConstants.STATUS_CODE_NOT_FOUND);
			}
		});
	}
	
	protected static Boolean isFailledInd(PortDetails portData) {
		if(StringUtils.isNotEmpty(portData.getFailureInd()) &&"Y".equalsIgnoreCase(portData.getFailureInd())) {
			return true;
		}
		return false;
	}

	

}
