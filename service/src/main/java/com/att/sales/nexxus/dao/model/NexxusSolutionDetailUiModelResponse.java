package com.att.sales.nexxus.dao.model;

import java.util.ArrayList;

import com.att.sales.framework.model.ServiceResponse;

/**
 * The Class NexxusSolutionDetailUiModelResponse.
 */
public class NexxusSolutionDetailUiModelResponse extends ServiceResponse {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The number of solutions. */
	private Integer numberOfSolutions;

	/** The nexxus solutions detail. */
	private Object nexxusSolutionsDetail = new ArrayList<>();

	/**
	 * Gets the number of solutions.
	 *
	 * @return the number of solutions
	 */
	public Integer getNumberOfSolutions() {
		return numberOfSolutions;
	}

	/**
	 * Sets the number of solutions.
	 *
	 * @param numberOfSolutions the new number of solutions
	 */
	public void setNumberOfSolutions(Integer numberOfSolutions) {
		this.numberOfSolutions = numberOfSolutions;
	}

	/**
	 * Gets the nexxus solutions detail.
	 *
	 * @return the nexxus solutions detail
	 */
	public Object getNexxusSolutionsDetail() {
		return nexxusSolutionsDetail;
	}

	/**
	 * Sets the nexxus solutions detail.
	 *
	 * @param nexxusDetails the new nexxus solutions detail
	 */
	public void setNexxusSolutionsDetail(Object nexxusDetails) {
		this.nexxusSolutionsDetail = nexxusDetails;
	}
}
