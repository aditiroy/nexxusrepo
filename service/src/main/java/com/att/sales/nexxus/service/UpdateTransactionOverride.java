package com.att.sales.nexxus.service;

import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.nexxus.model.UpdateTransactionOverrideRequest;

public interface UpdateTransactionOverride {

	ServiceResponse updateTransactionOverride(UpdateTransactionOverrideRequest request);
}
