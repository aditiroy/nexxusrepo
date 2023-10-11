package com.att.sales.nexxus.fallout.service;

/**
 * @author ruchi
 * 
 */
import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.fallout.model.FalloutDetailsRequest;
import com.att.sales.nexxus.fallout.model.FalloutDetailsResponse;

/**
 * The Interface FalloutDetails.
 */
public interface FalloutDetails {
    
    /**
     * Nexxus request actions.
     *
     * @param request the request
     * @return the fallout details response
     * @throws SalesBusinessException the sales business exception
     */
    public ServiceResponse nexxusRequestActions(FalloutDetailsRequest request) throws SalesBusinessException;
}
