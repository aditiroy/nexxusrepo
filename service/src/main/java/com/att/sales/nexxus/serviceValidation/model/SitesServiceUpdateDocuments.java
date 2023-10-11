package com.att.sales.nexxus.serviceValidation.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author KumariMuktta
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
public class SitesServiceUpdateDocuments implements Serializable {

	private static final long serialVersionUID = -2987126509835079977L;

	private String integrationServiceSiteDict1;

}
