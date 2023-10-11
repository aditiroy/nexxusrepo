/*
 * package com.att.sales.nexxus.service.routes;
 * 
 * import org.apache.camel.LoggingLevel; import
 * org.apache.camel.builder.RouteBuilder; import
 * org.springframework.stereotype.Component;
 * 
 *//**
	 * The Class NexxusRoute.
	 */
/*
 * @Component public class NexxusRoute extends RouteBuilder {
 * 
 *//**
	 * Let's configure the Camel routing rules using Java code...
	 *
	 * @throws Exception the exception
	 *//*
		 * 
		 * public void configure() throws Exception {
		 * 
		 * onException(Exception.class).log(LoggingLevel.INFO,
		 * "Exception=${exception.message}").handled(true)
		 * .to("bean:transactionCAETExceptionHandler?method=defaultHandler")
		 * .to("velocity:file:{{ajsc.vm.path}}/error.vm");
		 * 
		 * from("cxfrs:bean:nexxusServer?bindingStyle=SimpleConsumer&synchronous=true")
		 * 
		 * .to("bean:appprefilter?method=filter").choice().when().simple(
		 * "${header.error_response}")
		 * .to("velocity:file:{{ajsc.vm.path}}/error.vm").otherwise().choice().when()
		 * .simple("${header.operationName} == 'transformTestData'")
		 * .to("bean:nexxusBean?method=transformTestData")
		 * .when().simple("${header.operationName} == 'retreiveICBPSP'")
		 * .to("bean:nexxusBean?method=retreiveICBPSP").when()
		 * .simple("${header.operationName} == 'putUploadASENexxusFile'")
		 * .to("bean:nexxusBean?method=putUploadASENexxusFile").when()
		 * .simple("${header.operationName} == 'putProductDataLoad'")
		 * .to("bean:nexxusBean?method=putProductDataLoad")
		 * .when().simple("${header.operationName} == 'transformPricingData'")
		 * .to("bean:nexxusBean?method=transformPricingData")
		 * .when().simple("${header.operationName} == 'fetchNexxusSolutionsByUserId'")
		 * .to("bean:nexxusBean?method=fetchNexxusSolutionsByUserId")
		 * .when().simple("${header.operationName} == 'retrieveSalesOrderInfo'")
		 * .to("bean:nexxusBean?method=retrieveSalesOrderInfo")
		 * .when().simple("${header.operationName} == 'getBillingManagePriceData'")
		 * .to("bean:nexxusBean?method=getBillingManagePriceData")
		 * .when().simple("${header.operationName} == 'nexxusOutputDownload'")
		 * .to("bean:nexxusBean?method=nexxusOutputDownload")
		 * .when().simple("${header.operationName} == 'mailNotification'")
		 * .to("bean:nexxusBean?method=mailNotification")
		 * .when().simple("${header.operationName} == 'nexxusOutputZipFileDownload'")
		 * .to("bean:nexxusBean?method=nexxusOutputZipFileDownload")
		 * .when().simple("${header.operationName} == 'getInternalTest'")
		 * .to("bean:nexxusBean?method=getInternalTest")
		 * .when().simple("${header.operationName} == 'retreiveUserDetails'")
		 * .to("bean:nexxusBean?method=retreiveUserDetails")
		 * .when().simple("${header.operationName} == 'consumerDetail'")
		 * .to("bean:nexxusBean?method=consumerDetail")
		 * .when().simple("${header.operationName} == 'getNxOutputFileId'")
		 * .to("bean:nexxusBean?method=getNxOutputFileId")
		 * .when().simple("${header.operationName} == 'uploadNxTemplateFile'")
		 * .to("bean:nexxusBean?method=uploadNxTemplateFile")
		 * .when().simple("${header.operationName} == 'putProductRatePlanDataLoad'")
		 * .to("bean:nexxusBean?method=putProductRatePlanDataLoad")
		 * .when().simple("${header.operationName} == 'retrieveAdminData'")
		 * .to("bean:nexxusBean?method=retrieveAdminData")
		 * .when().simple("${header.operationName} == 'fetchAllTopProducts'")
		 * .to("bean:nexxusBean?method=fetchAllTopProducts")
		 * .when().simple("${header.operationName} == 'adminUpdateProductInfo'")
		 * .to("bean:nexxusBean?method=adminUpdateProductInfo")
		 * .when().simple("${header.operationName} == 'nexxusRequestActions'")
		 * .to("bean:nexxusBean?method=nexxusRequestActions")
		 * .when().simple("${header.operationName} == 'uploadNexxusDataFile'")
		 * .to("bean:nexxusBean?method=uploadNexxusDataFile")
		 * .when().simple("${header.operationName} == 'transmitDesignData'")
		 * .to("bean:nexxusBean?method=transmitDesignData")
		 * .when().simple("${header.operationName} == 'prepareAndSendMailForPEDRequest'"
		 * ) .to("bean:nexxusBean?method=prepareAndSendMailForPEDRequest")
		 * .when().simple("${header.operationName} == 'getnXPEDStatus'")
		 * .to("bean:nexxusBean?method=getnXPEDStatus")
		 * .when().simple("${header.operationName} == 'rateLetterStatus'")
		 * .to("bean:nexxusBean?method=rateLetterStatus")
		 * .when().simple("${header.operationName} == 'publishValidatedAddressesStatus'"
		 * ) .to("bean:nexxusBean?method=publishValidatedAddressesStatus")
		 * .when().simple("${header.operationName} == 'serviceValidation'")
		 * .to("bean:nexxusBean?method=serviceValidation")
		 * .when().simple("${header.operationName} == 'createTransaction'")
		 * .to("bean:nexxusBean?method=createTransaction")
		 * .when().simple("${header.operationName} == 'getTransaction'")
		 * .to("bean:nexxusBean?method=getTransaction")
		 * .when().simple("${header.operationName} == 'getTransactionLine'")
		 * .to("bean:nexxusBean?method=getTransactionLine")
		 * .when().simple("${header.operationName} == 'updateTransactionPricingRequest'"
		 * ) .to("bean:nexxusBean?method=updateTransactionPricingRequest")
		 * .when().simple("${header.operationName} == 'updateTransactionSiteUpload'")
		 * .to("bean:nexxusBean?method=updateTransactionSiteUpload")
		 * .when().simple("${header.operationName} == 'removeTransactionLine'")
		 * .to("bean:nexxusBean?method=removeTransactionLine")
		 * .when().simple("${header.operationName} == 'configureSolnAndProduct'")
		 * .to("bean:nexxusBean?method=configureSolnAndProduct")
		 * .when().simple("${header.operationName} == 'updateTransactionOverride'")
		 * .to("bean:nexxusBean?method=updateTransactionOverride")
		 * .when().simple("${header.operationName} == 'copyTransaction'")
		 * .to("bean:nexxusBean?method=copyTransaction")
		 * .when().simple("${header.operationName} == 'getCustomPricing'")
		 * .to("bean:nexxusBean?method=getCustomPricing")
		 * .when().simple("${header.operationName} == 'getCustomPricingSalesOne'")
		 * .to("bean:nexxusBean?method=getCustomPricingSalesOne")
		 * .when().simple("${header.operationName} == 'updateTransactionQualifyService'"
		 * ) .to("bean:nexxusBean?method=updateTransactionQualifyService")
		 * .when().simple("${header.operationName} == 'updateTransactionPriceScore'")
		 * .to("bean:nexxusBean?method=updateTransactionPriceScore") .when().
		 * simple("${header.operationName} == 'updateTransactionSubmitToApproval'")
		 * .to("bean:nexxusBean?method=updateTransactionSubmitToApproval")
		 * .when().simple("${header.operationName} == 'generateRateLetter'")
		 * .to("bean:nexxusBean?method=generateRateLetter")
		 * .when().simple("${header.operationName} == 'aseodReqRates'")
		 * .to("bean:nexxusBean?method=aseodReqRates")
		 * .when().simple("${header.operationName} == 'bulkUploadEthTokens'")
		 * .to("bean:nexxusBean?method=bulkUploadEthTokens")
		 * .when().simple("${header.operationName} == 'manBulkUploadToEDF'")
		 * .to("bean:nexxusBean?method=manBulkUploadToEDF")
		 * .when().simple("${header.operationName} == 'downloadFailedTokenFile'")
		 * .to("bean:nexxusBean?method=downloadFailedTokenFile")
		 * .when().simple("${header.operationName} == 'getEncodedBinaryFile'")
		 * .to("bean:nexxusBean?method=getEncodedBinaryFile")
		 * .when().simple("${header.operationName} == 'refreshCache'")
		 * .to("bean:nexxusBean?method=refreshCache")
		 * .when().simple("${header.operationName} == 'addUser'")
		 * .to("bean:nexxusBean?method=addUser")
		 * .when().simple("${header.operationName} == 'checkAccess'")
		 * .to("bean:nexxusBean?method=checkAccess")
		 * .when().simple("${header.operationName} == 'getContractInventory'")
		 * .to("bean:nexxusBean?method=getContractInventory")
		 * .when().simple("${header.operationName} == 'nexxusSolutionAction'")
		 * .to("bean:nexxusBean?method=nexxusSolutionAction")
		 * .when().simple("${header.operationName} == 'submitFeedback'")
		 * .to("bean:nexxusBean?method=submitFeedback")
		 * .when().simple("${header.operationName} == 'bulkUploadInrData'")
		 * .to("bean:nexxusBean?method=bulkUploadInrData")
		 * .when().simple("${header.operationName} == 'fetchNewEnhancements'")
		 * .to("bean:nexxusBean?method=fetchNewEnhancements")
		 * .when().simple("${header.operationName} == 'fetchBillDetails'")
		 * .to("bean:nexxusBean?method=fetchBillDetails")
		 * 
		 * .when().simple("${header.operationName} == 'uploadMyPriceLegacyCoData'")
		 * .to("bean:nexxusBean?method=uploadMyPriceLegacyCoData")
		 * .when().simple("${header.operationName} == 'syncMyPriceLegacyCoData'")
		 * .to("bean:nexxusBean?method=syncMyPriceLegacyCoData")
		 * .when().simple("${header.operationName} == 'datafeed'")
		 * .to("bean:nexxusBean?method=datafeed"); 
		 * 
		 		.when().simple("${header.operationName} == 'retrieveBillingCharges'")
		 		.to("bean:nexxusBean?method=retrieveBillingCharges")
		        .when().simple("${header.operationName} == 'usrpDesign'")
		        .to("bean:nexxusBean?method=usrpDesign");
	}}
		 * 
		 * 
		 * 
		 * }
		 */


