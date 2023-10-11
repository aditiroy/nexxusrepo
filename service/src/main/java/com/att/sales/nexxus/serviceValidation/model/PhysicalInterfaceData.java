package com.att.sales.nexxus.serviceValidation.model;


import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhysicalInterfaceData {
	private String physicalInterfaceType;
	private List<String> physicalInterfaceName;
	private List<String> price;
}
