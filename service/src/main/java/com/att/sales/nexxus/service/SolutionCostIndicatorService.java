/**
 * 
 */
package com.att.sales.nexxus.service;

import com.att.sales.nexxus.reteriveicb.model.SolutionCostIndicatorResponse;
import com.att.sales.nexxus.transmitdesigndata.model.SolutionCostRequest;

/**
 * @author aa316k
 *
 */
public interface SolutionCostIndicatorService {
	
	public SolutionCostIndicatorResponse solutionCostIndicator(SolutionCostRequest request);

}
