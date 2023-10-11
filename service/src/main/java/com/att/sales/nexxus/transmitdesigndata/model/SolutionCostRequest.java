/**
 * 
 */
package com.att.sales.nexxus.transmitdesigndata.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

/**
 * @author aa316k
 *
 */
 @JsonIgnoreProperties(ignoreUnknown = true)
 @JsonInclude(JsonInclude.Include.NON_NULL)
 @JsonTypeName("SolutionCostRequest")                                                                                         
 @JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT ,use = JsonTypeInfo.Id.NAME)
 public class SolutionCostRequest {
	 
	    private Long solutionId;
	    
	    private String slcIndicator;

		public Long getSolutionId() {
			return solutionId;
		}

		public void setSolutionId(Long solutionId) {
			this.solutionId = solutionId;
		}

		public String getSlcIndicator() {
			return slcIndicator;
		}

		public void setSlcIndicator(String slcIndicator) {
			this.slcIndicator = slcIndicator;
		}

}
