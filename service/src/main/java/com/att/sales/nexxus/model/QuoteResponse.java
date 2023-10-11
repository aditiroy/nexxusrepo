package com.att.sales.nexxus.model;


import java.io.Serializable;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class QuoteResponse.
 *
 * @author km017g
 */
@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class QuoteResponse  implements Serializable{
	
	/** The quote details. */
	private GUIResponse quoteDetails;//not need
	
	/** The site id. */
	@JsonInclude(Include.NON_DEFAULT)
	 private Long siteId;
	 
 	/** The url. */
 	private String url;
	 
 	/** The city URL. */
 	private String cityURL;
	 
 	/** The customer name. */
 	private String customerName;
	 
 	/** The cust addr 1. */
 	private String custAddr1;
	 
 	/** The cust addr 2. */
 	private String custAddr2;
	 
 	/** The cust city. */
 	private String custCity;
	 
 	/** The cust postalcode. */
 	private String custPostalcode;
	 
 	/** The cust state. */
 	private String custState;
	 
 	/** The cust country. */
 	private String custCountry;
	 
 	/** The cust telephone. */
 	private String custTelephone;
	 
 	/** The token id. */
 	private String tokenId;
	 
 	/** The availability. */
 	private String availability;
	 
 	/** The serial number. */
 	private String serialNumber;
	 
 	/** The monthly price local. */
 	private String monthlyPriceLocal;
 	
 	private String oneTimePriceLocal;
	 
 	/** The pnt. */
 	private String pnt;
	 
 	/** The access arrangement. */
 	private String accessArrangement;
	 
 	/** The ethernet IOC. */
 	private String ethernetIOC;
	 
 	/** The service guide eligible indicator. */
 	private String serviceGuideEligibleIndicator;
	 
 	/** The zone. */
 	private String zone;
	 
 	/** The discount percentage. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer discountPercentage;
	 
 	/** The quote request date. */
 	private String quoteRequestDate;
	 
 	/** The circuit quantity. */
 	private String circuitQuantity;
	 
 	/** The access supplier list. */
 	private List<AccessSupplierList> accessSupplierList;
	 
 	/** The electrical interface. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer electricalInterface;
	 
 	/** The physical connector. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer physicalConnector;
	 
 	/** The impedance. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer impedance;
	 
 	/** The physical interface. */
 	private String physicalInterface;
	 
 	/** The service guide published date. */
 	private String serviceGuidePublishedDate;
	 
 	/** The access bandwidth. */
 	private Integer accessBandwidth;

 	private Integer bandwidth;
 	
 	/** The speed. */
 	private String speed;
	 
 	/** The service. */
 	private String service;
	 
 	/** The tail technology. */
 	@JsonInclude(Include.NON_DEFAULT)
	 private Integer tailTechnology;
	 
 	/** The on net check. */
 	private String onNetCheck;
	 
 	/** The access arch. */
 	private String accessArch;
	 
 	/** The cmtu. */
 	//@JsonInclude(Include.NON_DEFAULT)
	// private Integer cmtu;
	 
 	/** The port level cos. */
 	private String portLevelCos;
	 
 	/** The meet point indicator. */
 	private String meetPointIndicator;
	 
 	/** The meet point V coordinate. */
 	private String meetPointVCoordinate;
	 
 	/** The meet point H coordinate. */
 	private String meetPointHCoordinate;
	 
 	/** The ipag cilli. */
 	private String ipagCilli;
	 
 	/** The connection type. */
 	private String connectionType;
	 
 	/** The cos. */
 	private String cos;
	 
 	/** The diversity id. */
 	private String diversityId;
	 
 	/** The diversity options. */
 	private String diversityOptions;
	 
 	/** The diversity grouping. */
 	private String diversityGrouping;
	 
 	/** The diversity order type. */
 	private String diversityOrderType;
	 
 	/** The diversity vendor type. */
 	private String diversityVendorType;
	 
 	/** The diversity change order. */
 	private String diversityChangeOrder;
	 
 	/** The swclli. */
 	private String swclli;
	 
 	/** The design details. */
 	private List<DesignDetails> designDetails ;
	 
 	/** The sub grouploc details. */
 	private List<Subgrouplocdetails> subGrouplocDetails;
	 
 	/** The cq transfer data list. */
 	private List<CQTransferDataList> cqTransferDataList;
	 
 	/** The status. */
 	private Status status;
 	
 	private String clli;
 	
 	private String nodeName;
 	
 	private String localCurrency;
 	
 	private String alternateCurrency;
 	
 	private String supplierName;
  
  
    private String quoteStatus;
 	
 	private String  coverageIndicator;
 	
	 
	 public String getQuoteStatus() {
		return quoteStatus;
	}

	public String getCoverageIndicator() {
		return coverageIndicator;
	}

	public void setCoverageIndicator(String coverageIndicator) {
		this.coverageIndicator = coverageIndicator;
	}

	public void setQuoteStatus(String quoteStatus) {
		this.quoteStatus = quoteStatus;
	}
 	
	 
	 /**
 	 * Gets the status.
 	 *
 	 * @return the status
 	 */
 	public Status getStatus() {
		return status;
	}
	
	/**
	 * Sets the status.
	 *
	 * @param status the new status
	 */
	public void setStatus(Status status) {
		this.status = status;
	}
	
	/** The access bandwidth list. */
	//Newly Added Fields (sb808b, yp353m)
	 private transient List<AccessBandwidth> accessBandwidthList;
	 
	/**
	 * Gets the cq transfer data list.
	 *
	 * @return the cq transfer data list
	 */
	public List<CQTransferDataList> getCqTransferDataList() {
		return cqTransferDataList;
	}
	
	/**
	 * Sets the cq transfer data list.
	 *
	 * @param cqTransferDataList the new cq transfer data list
	 */
	public void setCqTransferDataList(List<CQTransferDataList> cqTransferDataList) {
		this.cqTransferDataList = cqTransferDataList;
	}
	
	
	/**
	 * Gets the monthly price local.
	 *
	 * @return the monthlyPriceLocal
	 */
	public String getMonthlyPriceLocal() {
		return monthlyPriceLocal;
	}
	
	/**
	 * Sets the monthly price local.
	 *
	 * @param monthlyPriceLocal the monthlyPriceLocal to set
	 */
	public void setMonthlyPriceLocal(String monthlyPriceLocal) {
		this.monthlyPriceLocal = monthlyPriceLocal;
	}
	
	public String getOneTimePriceLocal() {
		return oneTimePriceLocal;
	}

	public void setOneTimePriceLocal(String oneTimePriceLocal) {
		this.oneTimePriceLocal = oneTimePriceLocal;
	}

	/**
	 * Gets the site id.
	 *
	 * @return the site id
	 */
	public Long getSiteId() {
		return siteId;
	}
	
	/**
	 * Sets the site id.
	 *
	 * @param siteId the new site id
	 */
	public void setSiteId(Long siteId) {
		this.siteId = siteId;
	}
	
	/**
	 * Gets the discount percentage.
	 *
	 * @return the discount percentage
	 */
	public Integer getDiscountPercentage() {
		return discountPercentage;
	}
	
	/**
	 * Gets the serial number.
	 *
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	
	/**
	 * Sets the serial number.
	 *
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	
	/**
	 * Sets the discount percentage.
	 *
	 * @param discountPercentage the new discount percentage
	 */
	public void setDiscountPercentage(Integer discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	/**
	 * Gets the electrical interface.
	 *
	 * @return the electrical interface
	 */
	public Integer getElectricalInterface() {
		return electricalInterface;
	}
	
	/**
	 * Sets the electrical interface.
	 *
	 * @param electricalInterface the new electrical interface
	 */
	public void setElectricalInterface(Integer electricalInterface) {
		this.electricalInterface = electricalInterface;
	}
	
	/**
	 * Gets the physical connector.
	 *
	 * @return the physical connector
	 */
	public Integer getPhysicalConnector() {
		return physicalConnector;
	}
	
	/**
	 * Sets the physical connector.
	 *
	 * @param physicalConnector the new physical connector
	 */
	public void setPhysicalConnector(Integer physicalConnector) {
		this.physicalConnector = physicalConnector;
	}
	
	/**
	 * Gets the impedance.
	 *
	 * @return the impedance
	 */
	public Integer getImpedance() {
		return impedance;
	}
	
	/**
	 * Sets the impedance.
	 *
	 * @param impedance the new impedance
	 */
	public void setImpedance(Integer impedance) {
		this.impedance = impedance;
	}
	
	/**
	 * Gets the access bandwidth.
	 *
	 * @return the access bandwidth
	 */
	public Integer getAccessBandwidth() {
		return accessBandwidth;
	}
	
	/**
	 * Sets the access bandwidth.
	 *
	 * @param accessBandwidth the new access bandwidth
	 */
	public void setAccessBandwidth(Integer accessBandwidth) {
		this.accessBandwidth = accessBandwidth;
	}
	
	/**
	 * Gets the tail technology.
	 *
	 * @return the tail technology
	 */
	public Integer getTailTechnology() {
		return tailTechnology;
	}
	
	/**
	 * Sets the tail technology.
	 *
	 * @param tailTechnology the new tail technology
	 */
	public void setTailTechnology(Integer tailTechnology) {
		this.tailTechnology = tailTechnology;
	}
	
	/**
	 * Gets the cmtu.
	 *
	 * @return the cmtu
	 */
	//public Integer getCmtu() {
		//return cmtu;
//	}
	
	/**
	 * Sets the cmtu.
	 *
	 * @param cmtu the new cmtu
	 */
	/*public void setCmtu(Integer cmtu) {
		this.cmtu = cmtu;
	}*/
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Sets the url.
	 *
	 * @param url the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}
	
	/**
	 * Gets the city URL.
	 *
	 * @return the city URL
	 */
	public String getCityURL() {
		return cityURL;
	}
	
	/**
	 * Sets the city URL.
	 *
	 * @param cityURL the new city URL
	 */
	public void setCityURL(String cityURL) {
		this.cityURL = cityURL;
	}
	
	/**
	 * Gets the customer name.
	 *
	 * @return the customer name
	 */
	public String getCustomerName() {
		return customerName;
	}
	
	/**
	 * Sets the customer name.
	 *
	 * @param customerName the new customer name
	 */
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	/**
	 * Gets the cust addr 1.
	 *
	 * @return the cust addr 1
	 */
	public String getCustAddr1() {
		return custAddr1;
	}
	
	/**
	 * Sets the cust addr 1.
	 *
	 * @param custAddr1 the new cust addr 1
	 */
	public void setCustAddr1(String custAddr1) {
		this.custAddr1 = custAddr1;
	}
	
	/**
	 * Gets the cust addr 2.
	 *
	 * @return the cust addr 2
	 */
	public String getCustAddr2() {
		return custAddr2;
	}
	
	/**
	 * Sets the cust addr 2.
	 *
	 * @param custAddr2 the new cust addr 2
	 */
	public void setCustAddr2(String custAddr2) {
		this.custAddr2 = custAddr2;
	}
	
	/**
	 * Gets the cust city.
	 *
	 * @return the cust city
	 */
	public String getCustCity() {
		return custCity;
	}
	
	/**
	 * Sets the cust city.
	 *
	 * @param custCity the new cust city
	 */
	public void setCustCity(String custCity) {
		this.custCity = custCity;
	}
	
	/**
	 * Gets the cust postalcode.
	 *
	 * @return the cust postalcode
	 */
	public String getCustPostalcode() {
		return custPostalcode;
	}
	
	/**
	 * Sets the cust postalcode.
	 *
	 * @param custPostalcode the new cust postalcode
	 */
	public void setCustPostalcode(String custPostalcode) {
		this.custPostalcode = custPostalcode;
	}
	
	/**
	 * Gets the cust state.
	 *
	 * @return the cust state
	 */
	public String getCustState() {
		return custState;
	}
	
	/**
	 * Sets the cust state.
	 *
	 * @param custState the new cust state
	 */
	public void setCustState(String custState) {
		this.custState = custState;
	}
	
	/**
	 * Gets the cust country.
	 *
	 * @return the cust country
	 */
	public String getCustCountry() {
		return custCountry;
	}
	
	/**
	 * Sets the cust country.
	 *
	 * @param custCountry the new cust country
	 */
	public void setCustCountry(String custCountry) {
		this.custCountry = custCountry;
	}
	
	/**
	 * Gets the cust telephone.
	 *
	 * @return the cust telephone
	 */
	public String getCustTelephone() {
		return custTelephone;
	}
	
	/**
	 * Sets the cust telephone.
	 *
	 * @param custTelephone the new cust telephone
	 */
	public void setCustTelephone(String custTelephone) {
		this.custTelephone = custTelephone;
	}
	
	/**
	 * Gets the token id.
	 *
	 * @return the token id
	 */
	public String getTokenId() {
		return tokenId;
	}
	
	/**
	 * Sets the token id.
	 *
	 * @param tokenId the new token id
	 */
	public void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	
	/**
	 * Gets the availability.
	 *
	 * @return the availability
	 */
	public String getAvailability() {
		return availability;
	}
	
	/**
	 * Sets the availability.
	 *
	 * @param availability the new availability
	 */
	public void setAvailability(String availability) {
		this.availability = availability;
	}
	
	/**
	 * Gets the pnt.
	 *
	 * @return the pnt
	 */
	public String getPnt() {
		return pnt;
	}
	
	/**
	 * Sets the pnt.
	 *
	 * @param pnt the new pnt
	 */
	public void setPnt(String pnt) {
		this.pnt = pnt;
	}
	
	/**
	 * Gets the access arrangement.
	 *
	 * @return the access arrangement
	 */
	public String getAccessArrangement() {
		return accessArrangement;
	}
	
	/**
	 * Sets the access arrangement.
	 *
	 * @param accessArrangement the new access arrangement
	 */
	public void setAccessArrangement(String accessArrangement) {
		this.accessArrangement = accessArrangement;
	}
	
	/**
	 * Gets the ethernet IOC.
	 *
	 * @return the ethernet IOC
	 */
	public String getEthernetIOC() {
		return ethernetIOC;
	}
	
	/**
	 * Sets the ethernet IOC.
	 *
	 * @param ethernetIOC the new ethernet IOC
	 */
	public void setEthernetIOC(String ethernetIOC) {
		this.ethernetIOC = ethernetIOC;
	}
	
	/**
	 * Gets the service guide eligible indicator.
	 *
	 * @return the service guide eligible indicator
	 */
	public String getServiceGuideEligibleIndicator() {
		return serviceGuideEligibleIndicator;
	}
	
	/**
	 * Sets the service guide eligible indicator.
	 *
	 * @param serviceGuideEligibleIndicator the new service guide eligible indicator
	 */
	public void setServiceGuideEligibleIndicator(String serviceGuideEligibleIndicator) {
		this.serviceGuideEligibleIndicator = serviceGuideEligibleIndicator;
	}
	
	/**
	 * Gets the zone.
	 *
	 * @return the zone
	 */
	public String getZone() {
		return zone;
	}
	
	/**
	 * Sets the zone.
	 *
	 * @param zone the new zone
	 */
	public void setZone(String zone) {
		this.zone = zone;
	}
	
	/**
	 * Gets the quote request date.
	 *
	 * @return the quote request date
	 */
	public String getQuoteRequestDate() {
		return quoteRequestDate;
	}
	
	/**
	 * Sets the quote request date.
	 *
	 * @param quoteRequestDate the new quote request date
	 */
	public void setQuoteRequestDate(String quoteRequestDate) {
		this.quoteRequestDate = quoteRequestDate;
	}
	
	/**
	 * Gets the circuit quantity.
	 *
	 * @return the circuit quantity
	 */
	public String getCircuitQuantity() {
		return circuitQuantity;
	}
	
	/**
	 * Sets the circuit quantity.
	 *
	 * @param circuitQuantity the new circuit quantity
	 */
	public void setCircuitQuantity(String circuitQuantity) {
		this.circuitQuantity = circuitQuantity;
	}
	
	/**
	 * Gets the access supplier list.
	 *
	 * @return the access supplier list
	 */
	public List<AccessSupplierList> getAccessSupplierList() {
		return accessSupplierList;
	}
	
	/**
	 * Sets the access supplier list.
	 *
	 * @param accessSupplierList the new access supplier list
	 */
	public void setAccessSupplierList(List<AccessSupplierList> accessSupplierList) {
		this.accessSupplierList = accessSupplierList;
	}
   
   /**
    * Gets the quote details.
    *
    * @return the quote details
    */
   public GUIResponse getQuoteDetails() {
		return quoteDetails;
	}
	
	/**
	 * Sets the quote details.
	 *
	 * @param quoteDetails the new quote details
	 */
	public void setQuoteDetails(GUIResponse quoteDetails) {
		this.quoteDetails = quoteDetails;
	}
	
	/**
	 * Gets the physical interface.
	 *
	 * @return the physical interface
	 */
	public String getPhysicalInterface() {
		return physicalInterface;
	}
	
	/**
	 * Sets the physical interface.
	 *
	 * @param physicalInterface the new physical interface
	 */
	public void setPhysicalInterface(String physicalInterface) {
		this.physicalInterface = physicalInterface;
	}
	
	/**
	 * Gets the service guide published date.
	 *
	 * @return the service guide published date
	 */
	public String getServiceGuidePublishedDate() {
		return serviceGuidePublishedDate;
	}
	
	/**
	 * Sets the service guide published date.
	 *
	 * @param serviceGuidePublishedDate the new service guide published date
	 */
	public void setServiceGuidePublishedDate(String serviceGuidePublishedDate) {
		this.serviceGuidePublishedDate = serviceGuidePublishedDate;
	}
	
	/**
	 * Gets the speed.
	 *
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}
	
	/**
	 * Sets the speed.
	 *
	 * @param speed the new speed
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	
	/**
	 * Gets the service.
	 *
	 * @return the service
	 */
	public String getService() {
		return service;
	}
	
	/**
	 * Sets the service.
	 *
	 * @param service the new service
	 */
	public void setService(String service) {
		this.service = service;
	}
	
	/**
	 * Gets the on net check.
	 *
	 * @return the on net check
	 */
	public String getOnNetCheck() {
		return onNetCheck;
	}
	
	/**
	 * Sets the on net check.
	 *
	 * @param onNetCheck the new on net check
	 */
	public void setOnNetCheck(String onNetCheck) {
		this.onNetCheck = onNetCheck;
	}
	
	/**
	 * Gets the access arch.
	 *
	 * @return the access arch
	 */
	public String getAccessArch() {
		return accessArch;
	}
	
	/**
	 * Sets the access arch.
	 *
	 * @param accessArch the new access arch
	 */
	public void setAccessArch(String accessArch) {
		this.accessArch = accessArch;
	}
	
	/**
	 * Gets the port level cos.
	 *
	 * @return the port level cos
	 */
	public String getPortLevelCos() {
		return portLevelCos;
	}
	
	/**
	 * Sets the port level cos.
	 *
	 * @param portLevelCos the new port level cos
	 */
	public void setPortLevelCos(String portLevelCos) {
		this.portLevelCos = portLevelCos;
	}
	
	/**
	 * Gets the meet point indicator.
	 *
	 * @return the meet point indicator
	 */
	public String getMeetPointIndicator() {
		return meetPointIndicator;
	}
	
	/**
	 * Sets the meet point indicator.
	 *
	 * @param meetPointIndicator the new meet point indicator
	 */
	public void setMeetPointIndicator(String meetPointIndicator) {
		this.meetPointIndicator = meetPointIndicator;
	}
	
	/**
	 * Gets the meet point V coordinate.
	 *
	 * @return the meet point V coordinate
	 */
	public String getMeetPointVCoordinate() {
		return meetPointVCoordinate;
	}
	
	/**
	 * Sets the meet point V coordinate.
	 *
	 * @param meetPointVCoordinate the new meet point V coordinate
	 */
	public void setMeetPointVCoordinate(String meetPointVCoordinate) {
		this.meetPointVCoordinate = meetPointVCoordinate;
	}
	
	/**
	 * Gets the meet point H coordinate.
	 *
	 * @return the meet point H coordinate
	 */
	public String getMeetPointHCoordinate() {
		return meetPointHCoordinate;
	}
	
	/**
	 * Sets the meet point H coordinate.
	 *
	 * @param meetPointHCoordinate the new meet point H coordinate
	 */
	public void setMeetPointHCoordinate(String meetPointHCoordinate) {
		this.meetPointHCoordinate = meetPointHCoordinate;
	}
	
	/**
	 * Gets the ipag cilli.
	 *
	 * @return the ipag cilli
	 */
	public String getIpagCilli() {
		return ipagCilli;
	}
	
	/**
	 * Sets the ipag cilli.
	 *
	 * @param ipagCilli the new ipag cilli
	 */
	public void setIpagCilli(String ipagCilli) {
		this.ipagCilli = ipagCilli;
	}
	
	/**
	 * Gets the connection type.
	 *
	 * @return the connection type
	 */
	public String getConnectionType() {
		return connectionType;
	}
	
	/**
	 * Sets the connection type.
	 *
	 * @param connectionType the new connection type
	 */
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	
	/**
	 * Gets the cos.
	 *
	 * @return the cos
	 */
	public String getCos() {
		return cos;
	}
	
	/**
	 * Sets the cos.
	 *
	 * @param cos the new cos
	 */
	public void setCos(String cos) {
		this.cos = cos;
	}
	
	/**
	 * Gets the diversity id.
	 *
	 * @return the diversity id
	 */
	public String getDiversityId() {
		return diversityId;
	}
	
	/**
	 * Sets the diversity id.
	 *
	 * @param diversityId the new diversity id
	 */
	public void setDiversityId(String diversityId) {
		this.diversityId = diversityId;
	}
	
	/**
	 * Gets the diversity options.
	 *
	 * @return the diversity options
	 */
	public String getDiversityOptions() {
		return diversityOptions;
	}
	
	/**
	 * Sets the diversity options.
	 *
	 * @param diversityOptions the new diversity options
	 */
	public void setDiversityOptions(String diversityOptions) {
		this.diversityOptions = diversityOptions;
	}
	
	/**
	 * Gets the diversity grouping.
	 *
	 * @return the diversity grouping
	 */
	public String getDiversityGrouping() {
		return diversityGrouping;
	}
	
	/**
	 * Sets the diversity grouping.
	 *
	 * @param diversityGrouping the new diversity grouping
	 */
	public void setDiversityGrouping(String diversityGrouping) {
		this.diversityGrouping = diversityGrouping;
	}
	
	/**
	 * Gets the diversity order type.
	 *
	 * @return the diversity order type
	 */
	public String getDiversityOrderType() {
		return diversityOrderType;
	}
	
	/**
	 * Sets the diversity order type.
	 *
	 * @param diversityOrderType the new diversity order type
	 */
	public void setDiversityOrderType(String diversityOrderType) {
		this.diversityOrderType = diversityOrderType;
	}
	
	/**
	 * Gets the diversity vendor type.
	 *
	 * @return the diversity vendor type
	 */
	public String getDiversityVendorType() {
		return diversityVendorType;
	}
	
	/**
	 * Sets the diversity vendor type.
	 *
	 * @param diversityVendorType the new diversity vendor type
	 */
	public void setDiversityVendorType(String diversityVendorType) {
		this.diversityVendorType = diversityVendorType;
	}
	
	/**
	 * Gets the diversity change order.
	 *
	 * @return the diversity change order
	 */
	public String getDiversityChangeOrder() {
		return diversityChangeOrder;
	}
	
	/**
	 * Sets the diversity change order.
	 *
	 * @param diversityChangeOrder the new diversity change order
	 */
	public void setDiversityChangeOrder(String diversityChangeOrder) {
		this.diversityChangeOrder = diversityChangeOrder;
	}
	
	/**
	 * Gets the swclli.
	 *
	 * @return the swclli
	 */
	public String getSwclli() {
		return swclli;
	}
	
	/**
	 * Sets the swclli.
	 *
	 * @param swclli the new swclli
	 */
	public void setSwclli(String swclli) {
		this.swclli = swclli;
	}
	
	/**
	 * Gets the design details.
	 *
	 * @return the design details
	 */
	public List<DesignDetails> getDesignDetails() {
		return designDetails;
	}
	
	/**
	 * Sets the design details.
	 *
	 * @param designDetails the new design details
	 */
	public void setDesignDetails(List<DesignDetails> designDetails) {
		this.designDetails = designDetails;
	}
	
	/**
	 * Gets the sub grouploc details.
	 *
	 * @return the sub grouploc details
	 */
	public List<Subgrouplocdetails> getSubGrouplocDetails() {
		return subGrouplocDetails;
	}
	
	/**
	 * Sets the sub grouploc details.
	 *
	 * @param subGrouplocDetails the new sub grouploc details
	 */
	public void setSubGrouplocDetails(List<Subgrouplocdetails> subGrouplocDetails) {
		this.subGrouplocDetails = subGrouplocDetails;
	}

	public Integer getBandwidth() {
		return bandwidth;
	}

	public void setBandwidth(Integer bandwidth) {
		this.bandwidth = bandwidth;
	}

	public String getClli() {
		return clli;
	}

	public void setClli(String clli) {
		this.clli = clli;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getLocalCurrency() {
		return localCurrency;
	}

	public void setLocalCurrency(String localCurrency) {
		this.localCurrency = localCurrency;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getAlternateCurrency() {
		return alternateCurrency;
	}

	public void setAlternateCurrency(String alternateCurrency) {
		this.alternateCurrency = alternateCurrency;
	}

	@Override
	public String toString() {
		return "QuoteResponse [quoteDetails=" + quoteDetails + ", siteId=" + siteId + ", url=" + url + ", cityURL="
				+ cityURL + ", customerName=" + customerName + ", custAddr1=" + custAddr1 + ", custAddr2=" + custAddr2
				+ ", custCity=" + custCity + ", custPostalcode=" + custPostalcode + ", custState=" + custState
				+ ", custCountry=" + custCountry + ", custTelephone=" + custTelephone + ", tokenId=" + tokenId
				+ ", availability=" + availability + ", serialNumber=" + serialNumber + ", monthlyPriceLocal="
				+ monthlyPriceLocal + ", oneTimePriceLocal=" + oneTimePriceLocal + ", pnt=" + pnt
				+ ", accessArrangement=" + accessArrangement + ", ethernetIOC=" + ethernetIOC
				+ ", serviceGuideEligibleIndicator=" + serviceGuideEligibleIndicator + ", zone=" + zone
				+ ", discountPercentage=" + discountPercentage + ", quoteRequestDate=" + quoteRequestDate
				+ ", circuitQuantity=" + circuitQuantity + ", accessSupplierList=" + accessSupplierList
				+ ", electricalInterface=" + electricalInterface + ", physicalConnector=" + physicalConnector
				+ ", impedance=" + impedance + ", physicalInterface=" + physicalInterface
				+ ", serviceGuidePublishedDate=" + serviceGuidePublishedDate + ", accessBandwidth=" + accessBandwidth
				+ ", bandwidth=" + bandwidth + ", speed=" + speed + ", service=" + service + ", tailTechnology="
				+ tailTechnology + ", onNetCheck=" + onNetCheck + ", accessArch=" + accessArch 
				+ ", portLevelCos=" + portLevelCos + ", meetPointIndicator=" + meetPointIndicator
				+ ", meetPointVCoordinate=" + meetPointVCoordinate + ", meetPointHCoordinate=" + meetPointHCoordinate
				+ ", ipagCilli=" + ipagCilli + ", connectionType=" + connectionType + ", cos=" + cos + ", diversityId="
				+ diversityId + ", diversityOptions=" + diversityOptions + ", diversityGrouping=" + diversityGrouping
				+ ", diversityOrderType=" + diversityOrderType + ", diversityVendorType=" + diversityVendorType
				+ ", diversityChangeOrder=" + diversityChangeOrder + ", swclli=" + swclli + ", designDetails="
				+ designDetails + ", subGrouplocDetails=" + subGrouplocDetails + ", cqTransferDataList="
				+ cqTransferDataList + ", status=" + status + ", clli=" + clli + ", nodeName=" + nodeName
				+ ", localCurrency=" + localCurrency + ", alternateCurrency=" + alternateCurrency + ", supplierName="
				+ supplierName + "]";
	}

	
	

}
