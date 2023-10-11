package com.att.sales.nexxus.myprice.transaction.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.att.sales.framework.model.ServiceResponse;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class ConfigureSolnAndProductResponse extends ServiceResponse {

	private static final long serialVersionUID = -7769957676246939569L;

	private List<TransactionLine> transactionLines = new ArrayList<>();

	public void addTransactionLines(TransactionLine transactionLine) {
		transactionLines.add(transactionLine);
	}
}