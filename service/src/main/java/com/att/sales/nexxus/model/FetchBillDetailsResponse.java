package com.att.sales.nexxus.model;
import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import com.att.sales.framework.model.ServiceResponse;
import lombok.Getter;
import lombok.Setter;

/**
 * @author AVSV3C744
 *
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class FetchBillDetailsResponse extends ServiceResponse{
	private static final long serialVersionUID = 1L;
	private List<FetchBillDetails> billMonths;
	private List<FetchBillDetails> beginBillMonths;
}