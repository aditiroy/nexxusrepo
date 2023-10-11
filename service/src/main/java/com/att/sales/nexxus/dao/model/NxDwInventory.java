package com.att.sales.nexxus.dao.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NX_DW_INVENTORY")
@Getter
@Setter

public class NxDwInventory implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "sequence_nx_dw_inventory", sequenceName = "SEQ_NX_DW_INVENTORY_ID", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequence_nx_dw_inventory")
	@Column(name = "ID")
	private Long id;
	
	@Column(name="PRODUCT")
	private String product;
	
	@Column(name="BILL_MONTH")
	private String billMonth;
	
	@Column(name="PARENT_ACCT_ID")
	private String parentAcctId;
	
	@Column(name="ACCT_ID")
	private String acctId;
	
	@Column(name="BILLER_CODE")
	private String billerCode;
	
	@Column(name="BILLER_NAME")
	private String billerName;
	
	@Column(name="MCN_BASE")
	private String mcnBase;
	
	@Column(name="MCN_SUFFIX")
	private String mcnSuffix;
	
	@Column(name="SALES_OFFICE_CODE")
	private String salesOfficeCode;
	
	@Column(name="GRC")
	private String grc;
	
	@Column(name="SVID")
	private String svid;
	
	@Column(name="L3")
	private String l3;
	
	@Column(name="CIRCUIT_NUMBER")
	private String circuitNumber;
	
	@Column(name="PORT_NUMBER")
	private String portNumber;
	
	@Column(name="ROUTER_NUMBER")
	private String routerNumber;
	
	@Column(name="ACCOUNT_NAME")
	private String accountName;
	
	@Column(name="OFFER_ID")
	private String offerId;
	
	@Column(name="TERM_AGREEMENT")
	private String termAgreement;
	
	@Column(name="PBI")
	private String pbi;
	
	@Column(name="BILLING_ELEMENT_ID")
	private String billingElementId;
	
	@Column(name="USOC")
	private String usoc;
	
	@Column(name="PCOS_CODE")
	private String pcosCode;
	
	@Column(name="BILLING_ELEMENT_CODE")
	private String billingElementCode;
	
	@Column(name="COMPONENT_ID")
	private String componentId;
	
	@Column(name="COMPONENT_DESCRIPTION")
	private String componentDescription;
	
	@Column(name="INVOICE_PRODUCT_FAMILY_CD")
	private String invoiceProductFamilyCd;
	
	@Column(name="INVOICE_PRODUCT_FAMILY_DESC")
	private String invoiceProductFamilyDesc;
	
	@Column(name="INVOICE_PRODUCT_TYPE_CD")
	private String invoiceProductTypeCd;
	
	@Column(name="INVOICE_PRODUCT_TYPE_DESC")
	private String invoicePoductTypedesc;
	
	@Column(name="INVOICE_PRODUCT_CHARGE_CD")
	private String invoiceProductChargeCd;
	
	@Column(name="BILLING_TRANSACTION_CD")
	private String billingTransactionCd;
	
	@Column(name="REVENUE_TYPE_CD")
	private String revenueTypeCd;
	
	@Column(name="REVENUE_TYPE_DESC")
	private String revenueTypedesc;
	
	@Column(name="CURRENT_MONTH_BILL_DATE")
	private String currentMonthBillDate;
	
	@Column(name="INSTALL_DATE")
	private String installDate;
	
	@Column(name="CANCEL_DATE")
	private String cancelDate;
	
	@Column(name="SERVICE_ORDER_DATE")
	private String serviceOrderDate;
	
	@Column(name="SERVICE_ORDER_COMPLETION_DATE")
	private String serviceOrderCompletionDate;
	
	@Column(name="LAST_BILL_DATE")
	private String lastBillDate;
	
	@Column(name="BILLING_THRU_DATE")
	private String billingThruDate;
	
	@Column(name="BIT_START_DATE")
	private String bitStartDate;
	
	@Column(name="BIT_END_DATE")
	private String bitEndDate;
	
	@Column(name="PRORATION_INDICATOR")
	private String prorationIndicator;
	
	@Column(name="PRORATION_UNIT")
	private String prorationUnit;
	
	@Column(name="RATING_CURRENCY")
	private String ratingCurrency;
	
	@Column(name="CURRENCY_CODE")
	private String currencycode;
	
	@Column(name="TYPE_OF_CHARGE")
	private String typeOfCharge;
	
	@Column(name="LAST_PAYMENT_AMT")
	private String lastPaymentamt;
	
	@Column(name="PREVIOUS_MON_AMT")
	private String previousMonAmt;
	
	@Column(name="POST_DISCOUNT_AMT")
	private String postDiscountAmt;
	
	@Column(name="PRE_DISCOUNT_AMT")
	private String preDiscountAmt;
	
	@Column(name="UNIT_RATE_AMOUNT")
	private String unitRateAmount;
	
	@Column(name="DISCOUNT_AMOUNT")
	private String discountamount;
	
	@Column(name="BIT_CT")
	private String bitCt;
	
	@Column(name="INVENTORY_CT")
	private BigDecimal inventoryct;
	
	@Column(name="RATE_ID")
	private String rateId;
	
	@Column(name="COUNTRY_CODE")
	private String countrycode;
	
	@Column(name="SERVICE_TYPE")
	private String serviceType;
	
	@Column(name="CHARGE_AMOUNT")
	private BigDecimal chargeamount;
	
	@Column(name="CLASS_OF_SERVICE")
	private String classOfService;
	
	@Column(name="FINANCIAL_REV_TYPE_CODE")
	private String financialRevTypeCode;
	
	@Column(name="TRANSACTION_TYPE_CODE")
	private String transactionTypeCode;
	
	@Column(name="CHARGE_TYPE_CODE")
	private String chargeTypeCode;
	
	@Column(name="FILENAME")
	private String filename;
	
	@Column(name="FILE_TYPE")
	private String fileType;
	
	@Column(name="CREATED_DATE")
	private Date createdDate;

	@Column(name="IS_QUALIFY")
	private String isQualify;

	@Column(name="CLEAN_CIRCUIT_NUMBER")
	private String cleanCircuitNumber;

	@Column(name="SUB_PRODUCT")
	private String subProduct;

}
