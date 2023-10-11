package com.att.sales.nexxus.reteriveicb.model;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
public class ResponseDetials {
	 private String type;
	 private String title;
	 @JsonProperty("faultcode")
	 private String faultCode;
	 @JsonProperty("faultstring")
	 private String faultString;
	 @JsonProperty("error_description")
	 private String errorDescription;
	 @JsonProperty("error_uri")
	 private String errorUri;
	 private String error;
	 private String log;

}
