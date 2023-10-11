package com.att.sales.nexxus.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * The Class DME2TestClient.
 *
 * @author Lijo Manickathan John( Sales)
 * 
 * 
 *         DME2 Test Client which calls GET,POST,PUT,PATCH and DELETE requests
 *         The latitude and longitude configurations need to be passed for the
 *         ACTIVE cluster. When ACTIVE CLUSTER goes down, the request will route
 *         to the DR cluster having the same route offer. As long as we specify
 *         the right latitude and longitude of ACTIVE cluster in DME2 request
 *         ,all requests will go to the ACTIVE cluster and not to DR cluster
 */
@Disabled
@RunWith(Parameterized.class)
public class DME2TestClient {

	Logger log = LoggerFactory.getLogger(DME2TestClient.class);

	@Parameters
	public static List<Object[]> data() {
		return Arrays.asList(new Object[1][0]);
	}

	   
	   @Test
		public void testForGetforvpn() throws Exception {

			String env = "AFTUAT";
		
			String latitude = "1.0"; // Production DataCenter Attributes. Change to the respective active environment
											// details.
			String longitude = "1.0"; /// Production DataCenter Attributes. Change to the active respective
												/// environment details.

			
			String uri="http://DME2RESOLVE/service=com.att.salesmarketing.soma.prop.DiscountAndPromotionSession/version=1.0.0/envContext=TEST/routeOffer=D2A";
			String subContext="/restservices/discountpromo/v1/discountpromoservice/discountpromodetails";
			
			//String subContext="/restservices/discountpromo/v1/discountpromoservice/discountpromodetails";
			String method = "POST";
			String userId = "m12568@pricerd.att.com"; //MechID
			String password = "Apple2019@";//Password
			Map requestHeaders = new HashMap();
			requestHeaders.put("offer", "AVPN");
			requestHeaders.put("Content-Type", "application/json");
			
		
			
			Map queryParameters = new HashMap();
			
		/*String requestPayLoad="{ \r\n" + 
					"	\"processCPECompRequest\": \r\n" + 
					"{ \r\n" + 
					"    \"bundleCode\": \"AVPN\", \r\n" + 
					"	\"solutionType\": \"NS\", \r\n" + 
					"	\"externalKey\":\"121\", \r\n" + 
					"	\"billerId\":\"UB\", \r\n" + 
					"	\"userId\": \"an081j\", \r\n" + 
					"	\"siteList\":[ \r\n" + 
					"		{ \r\n" + 
					"	    \"siteId\": \"1234567\", \r\n" + 
					"		\"portId\": \"5674783\", \r\n" + 
					"		\"routerDetails\": [ \r\n" + 
					"			{ \r\n" + 
					"			\"component\": [ \r\n" + 
					"				{ \r\n" + 
					"				\"componentId\": \"700\", \r\n" + 
					"				\"componentCodeType\": \"30\", \r\n" + 
					"				\"designDetails\": [ \r\n" + 
					" \r\n" + 
					"				        { \r\n" + 
					"						\"udfId\": \"20030\", \r\n" + 
					"						\"udfAttributeId\":[\"30595\"] \r\n" + 
					"						}, \r\n" + 
					" \r\n" + 
					"						{ \r\n" + 
					"						\"udfId\": \"20028\", \r\n" + 
					"						\"udfAttributeId\":[\"30044\"] \r\n" + 
					"						}, \r\n" + 
					"							{ \r\n" + 
					"						\"udfId\": \"20029\", \r\n" + 
					"						\"udfAttributeId\":[\"30429\"] \r\n" + 
					"						}, \r\n" + 
					"						{ \r\n" + 
					"						\"udfId\": \"20032\", \r\n" + 
					"						\"udfAttributeId\":[\"30501\"] \r\n" + 
					"						} \r\n" + 
					" \r\n" + 
					"				] \r\n" + 
					"			} \r\n" + 
					"			] \r\n" + 
					"			} \r\n" + 
					"			] \r\n" + 
					"			} \r\n" + 
					"		] \r\n" + 
					"	 } \r\n" + 
					"}";*/
			
			/*String requestPayLoad="{\r\n" + 
					"\"determinants\":[{\r\n" + 
					"\"activity\":\"Retrieve\",\r\n" + 
					"\"component\":[\"DiscountData\",\"SocList\", \"PSList\"]\r\n" + 
					"}],\r\n" + 
					"\"discountData\":{\r\n" + 
					"\"externalKey\":\"6194440\",\r\n" + 
					"\"priceScenarioId\":\"6147026\",\r\n" + 
					"\"actionType\":\"apldisc\",\r\n" + 
					"\"solutionType \":\"\",\r\n" + 
					"\"socUpdate\":\"\",\r\n" + 
					"\"erateInd\":\"\",\r\n" + 
					"\"countryDisc\":\"\",\r\n" + 
					"\"currentOrAll\":\"\",\r\n" + 
					"\"promotionCode\":\"\",\r\n" + 
					"\"segment\":\"\",\r\n" + 
					"\"roleId\":\"40\",\r\n" + 
					"\"strata\":\"\",\r\n" + 
					"\"isbBilled\":\"\",\r\n" + 
					"\"msPSIId\":\"\",\r\n" + 
					"\"billerId\":\"\",\r\n" + 
					"\"siteDetails\":[{\r\n" + 
					"\"siteId\":\"\",\r\n" + 
					"\"state\":\"\",\r\n" + 
					"\"npanxx\":\"\",\r\n" + 
					"\"zip\":\"\",\r\n" + 
					"\"cilli\":\"\",\r\n" + 
					"\"accessRegion\":\"\",\r\n" + 
					"\"udfDetails\":[{\r\n" + 
					"\"udfId\":\"\",\r\n" + 
					"\"udfValue\":\"\",\r\n" + 
					"\"udfAttributes\":{\r\n" + 
					"\"udfAttributeId\":\"\",\r\n" + 
					"\"udfAttributeValue\":\"\",\r\n" + 
					"\"udfAttributeText\":\"\",\r\n" + 
					"\"udfAttributeNumber\":\"\"\r\n" + 
					"}\r\n" + 
					"}]\r\n" + 
					"}],\r\n" + 
					"\"pricePlanDetails\":[{\r\n" + 
					"\"externalPricePlanId\":\"1229\",\r\n" + 
					"\"msPSIId\":\"9137\",\r\n" + 
					"\"msSPCId\":\"\",\r\n" + 
					"\"marc\":\"120000\",\r\n" + 
					"\"term\":\"12\",\r\n" + 
					"\"ratePlanId\":\"646\",\r\n" + 
					"\"discAuthLvl\":\"20\",\r\n" + 
					"\"priceCtlgId\":\"\",\r\n" + 
					"\"gbpYN\":\"\",\r\n" + 
					"\"discountableYN\":\"\",\r\n" + 
					"\"fromFeatureInd\":\"\",\r\n" + 
					"\"pricePlanId\":\"\",\r\n" + 
					"\"offerId\":\"\",\r\n" + 
					"\"offerIdExternal \":\"\",\r\n" + 
					"\"offerValue\":\"AVPN\",\r\n" + 
					"\"bundleCode\":\"\",\r\n" + 
					"\"regionCD\":\"MOW_AVPN\",\r\n" + 
					"\"segment\":\"\",\r\n" + 
					"\"minApplicableTerm\":\"\",\r\n" + 
					"\"rateLetterExpDate\":\"\",\r\n" + 
					"\"accessPriceExpDate\":\"\",\r\n" + 
					"\"termsConditionsYN\":\"\",\r\n" + 
					"\"termsConditionsNote\":\"\",\r\n" + 
					"\"stareCompareYN\":\"\",\r\n" + 
					"\"pricingSourceId\":\"\",\r\n" + 
					"\"accessSourceId\":\"\",\r\n" + 
					"\"gcpContractId\":\"\",\r\n" + 
					"\"contractNegoAction\":\"\",\r\n" + 
					"\"customPricePlanYN\":\"\",\r\n" + 
					"\"customerSignedYN\":\"\",\r\n" + 
					"\"regionId\":\"\",\r\n" + 
					"\"extendedTermId\":\"\",\r\n" + 
					"\"contractImpactingYN\":\"\",\r\n" + 
					"\"salesExecPricingLevel\":\"\",\r\n" + 
					"\"targetCurrency\":\"\",\r\n" + 
					"\"fxVersionId\":\"\",\r\n" + 
					"\"fxVersionDate\":\"\",\r\n" + 
					"\"expiredSOCAppovYN\":\"\",\r\n" + 
					"\"currencyVerApproved\":\"\",\r\n" + 
					"\"mdgfAttachedYN\":\"\",\r\n" + 
					"\"isbBilled\":\"\",\r\n" + 
					"\"icbDiscAppliedbyGCSYN\":\"\",\r\n" + 
					"\"salesPricingLevel\":\"\",\r\n" + 
					"\"country\":\"GB\",\r\n" + 
					"\"promoAppliedYN\":\"\",\r\n" + 
					"\"abnSavePromo\":\"\",\r\n" + 
					"\"hasAggregateBilling\":\"\",\r\n" + 
					"\"hasRevenueDiscount\":\"\",\r\n" + 
					"\"coterminousIndicator\":\"\",\r\n" + 
					"\"authorityLevel\":\"\",\r\n" + 
					"\"serviceCoverageType\":\"\",\r\n" + 
					"\"bundledServiceName\":\"\",\r\n" + 
					"\"bundledMarcYN\":\"\",\r\n" + 
					"\"coterminousExpDate\":\"\",\r\n" + 
					"\"isCountryAgnosticYN\":\"\",\r\n" + 
					"\"overrideYN\":\"\",\r\n" + 
					"\"siteDetailsRefs\":{\r\n" + 
					"\"siteId\":\"\"\r\n" + 
					"},\r\n" + 
					"\"beidDetails\":[{\r\n" + 
					"\"beid\":\"930\",\r\n" + 
					"\"discountId\":\"56162\",\r\n" + 
					"\"discountRefId\":\"7104\",\r\n" + 
					"\"discPercenatge\":\"\",\r\n" + 
					"\"zone\":\"\"\r\n" + 
					"}],\r\n" + 
					"\"offers\":[{\r\n" + 
					"\"offerId\":\"\",\r\n" + 
					"\"offerType\":\"\",\r\n" + 
					"\"country\":\"\",\r\n" + 
					"\"regionCD\":\"\",\r\n" + 
					"\"ratePlanId\":\"\",\r\n" + 
					"\"ratePlanVersion\":\"\",\r\n" + 
					"\"pricePlanVersion\":\"\",\r\n" + 
					"\"category\":\"7104\",\r\n" + 
					"\"subCategory\":\"7104\",\r\n" + 
					"\"connection\":\"\",\r\n" + 
					"\"discFeatDesc\":\"MPLS Port Activation Charge\",\r\n" + 
					"\"startDate\":\"\",\r\n" + 
					"\"endDate\":\"\",\r\n" + 
					"\"intialDisc\":\"0\",\r\n" + 
					"\"maxDisc\":\"\",\r\n" + 
					"\"additionalPromoGrpCode\":\"\",\r\n" + 
					"\"regionId\":\"MOW_AVPN\",\r\n" + 
					"\"isActive\":\"Y\",\r\n" + 
					"\"discAuthLevel\":\"\",\r\n" + 
					"\"frmDsgn\":\"\",\r\n" + 
					"\"adtlFtrYN\":\"\",\r\n" + 
					"\"discPrctg\":\"9\",\r\n" + 
					"\"icbMxDiscPrctg\":\"\",\r\n" + 
					"\"statusid\":\"\",\r\n" + 
					"\"psiMandtryYN\":\"\",\r\n" + 
					"\"addtnlPromoGrpCd\":\"\",\r\n" + 
					"\"icbApldDiscPerc\":\"\",\r\n" + 
					"\"isbYN\":\"\",\r\n" + 
					"\"bgnRng\":\"\",\r\n" + 
					"\"endRng\":\"\",\r\n" + 
					"\"discCat\":\"\",\r\n" + 
					"\"discRngId\":\"\",\r\n" + 
					"\"ratestableind\":\"\",\r\n" + 
					"\"gscApproSplansCtrct\":\"\",\r\n" + 
					"\"discGrpId\":\"7104\",\r\n" + 
					"\"isCstmDisc\":\"\",\r\n" + 
					"\"grpName\":\"MPLS Port\",\r\n" + 
					"\"srvcTyp\":\"\",\r\n" + 
					"\"ucTotlFtrDisc\":\"\",\r\n" + 
					"\"catSbcatDiscId\":\"\",\r\n" + 
					"\"discountType\":\"Regular Discount\",\r\n" + 
					"\"categoryDsc\":\"Non-Recurring Charges\",\r\n" + 
					"\"subCategoryDsc\":\"\",\r\n" + 
					"\"gbpYN\":\"\",\r\n" + 
					"\"recurrenceDesc\":\"NRC\",\r\n" + 
					"\"enumeratedValues\":\"\",\r\n" + 
					"\"recordType\":\"\",\r\n" + 
					"\"maxSpecialDiscPrctg\":\"\",\r\n" + 
					"\"icbDesiredDiscPerc\":\"\",\r\n" + 
					"\"fromInventoryYN\":\"\",\r\n" + 
					"\"nonDiscYN\":\"\",\r\n" + 
					"\"accessType\":\"\",\r\n" + 
					"\"externalDiscId\":\"322\"\r\n" + 
					"}]\r\n" + 
					"}],\r\n" + 
					"\"subGroupDetails\":[{\r\n" + 
					"\"subGroupName\":\"\",\r\n" + 
					"\"subGroupId\":\"\",\r\n" + 
					"\"userId\":\"10113\",\r\n" + 
					"\"catId\":\"\",\r\n" + 
					"\"crtDate\":\"\",\r\n" + 
					"\"versionNum\":\"\",\r\n" + 
					"\"activeYN\":\"\"\r\n" + 
					"}],\r\n" + 
					"\"getSocList\":{\r\n" + 
					"\"transactionId\":\"\",\r\n" + 
					"\"productCd\":\"4\",\r\n" + 
					"\"offerId\":\"4\",\r\n" + 
					"\"effectiveDate\":\"\",\r\n" + 
					"\"active \":\"\",\r\n" + 
					"\"erateInd\":\"\"\r\n" + 
					"}\r\n" + 
					"}\r\n" + 
					"}";
	*/		
			
			//requestPayLoad="{\"determinants\":[{\"activity\":\"Save\",\"component\":[\"Design\",\"Price\"]}],\"solution\":{\"userId\":\"ec006e\",\"externalKey\":6196381,\"productCd\":\"AVPN\",\"priceScenarioId\":99999999785,\"solutionType\":\"NS\",\"erateInd\":\"N\",\"targetCurrency\":\"USD\",\"ssaYN\":\"\",\"strata\":\"BNS\",\"term\":12,\"promotionCode\":\"\",\"abnYn\":\"\",\"offers\":[{\"offerId\":4,\"offerType\":\"Standalone\",\"bundleCode\":\"AVPN\",\"site\":[{\"siteId\":7298611,\"address1\":\"\",\"city\":\"\",\"state\":\"CA\",\"postalCode\":\"\",\"telephoneCode\":\"\",\"accessActivityType\":\"3\",\"term\":12,\"tdmDesignandPrice\":{},\"designSiteOfferPort\":[{\"portId\":4047,\"component\":[{\"componentCodeId\":10,\"componentCodeType\":\"Connection\",\"componentId\":4047,\"designDetails\":[{\"udfAttributeId\":[30595],\"udfId\":20030}]},{\"componentCodeId\":30,\"componentCodeType\":\"Port\",\"designDetails\":[{\"udfAttributeId\":[30345],\"udfId\":20023},{\"udfAttributeId\":[80629],\"udfId\":20164},{\"udfAttributeId\":[30279],\"udfId\":20035},{\"udfAttributeId\":[30168],\"udfId\":20011},{\"udfAttributeId\":[30349],\"udfId\":20028},{\"udfAttributeId\":[30367],\"udfId\":20036},{\"udfAttributeId\":[30155],\"udfId\":20034},{\"udfAttributeId\":[30172],\"udfId\":20000},{\"udfAttributeId\":[30009],\"udfId\":20019},{\"udfAttributeText\":[\"345212\"],\"udfId\":20043}]},{\"componentCodeId\":60,\"componentCodeType\":\"Access\",\"designDetails\":[{\"udfAttributeId\":[30155],\"udfId\":20003},{\"udfAttributeId\":[80629],\"udfId\":20164},{\"udfAttributeId\":[30375],\"udfId\":20002},{\"udfAttributeId\":[30167],\"udfId\":20009}]},{\"componentCodeId\":140,\"componentCodeType\":\"DiversityGroup\",\"designDetails\":[{\"udfAttributeId\":[30009],\"udfId\":20019}]}]}]}]}]}}";
		
			//String requestPayLoad=null;
			
			/*if(requestPayLoad==null) {
				requestPayLoad="";
			}*/
			
			/*String requestPayLoad="{ \r\n" + 
					"    \"externalKey\": 6196748, \r\n" + 
					"    \"offerId\": 4, \r\n" + 
					"    \"userId\": \"ec006e\" \r\n" + 
					"}";*/
			
			
			String requestPayLoad="{ \r\n" + 
					"  \"externalKey\": 6197930, \r\n" + 
					"  \"actionType\": \"pageLd\", \r\n" + 
					"  \"roleId\": \"1\", \r\n" + 
					"  \"pricePlanDetails\": [ \r\n" + 
					"    { \r\n" + 
					"      \"offers\": [ \r\n" + 
					"        { \r\n" + 
					"          \"offerValue\": \"AVPN\", \r\n" + 
					"          \"country\": \"US\" \r\n" + 
					"        } \r\n" + 
					"      ] \r\n" + 
					"    } \r\n" + 
					"  ], \r\n" + 
					"  \"subGroupDetails\": [ \r\n" + 
					"    { \r\n" + 
					"      \"userId\": \"136968\" \r\n" + 
					"    } \r\n" + 
					"  ] \r\n" + 
					"}";
		
			
			
			//String uri="http://DME2RESOLVE/service=com.att.salesmarketing.soma.pric./version=1.0.0/envContext=TEST/routeOffer=D2A";
			//String subContext="/restservices/designOrhestration/v1/designOrch/createPort";
			
			//requestHeaders.put( "Content-Length", Integer.toString( requestPayLoad.length() ) );
			//String requestPayLoad="{\"priceScenarioId\":\"337\",\"externalKey\":\"6194440\",\"solutionType\":\"NS\",\"priceDeterminant\":\"TO\",\"offers\":[{\"offerId\":\"4\"}]}";
			//callDME2(env,latitude,longitude,uri,requestPayLoad,subContext,userId,password);
			
			
		
			DME2TestUtility util = new DME2TestUtility();
			String response = util.processRequest(env, latitude, longitude, uri, subContext, method, queryParameters,
				requestHeaders, requestPayLoad, userId, password,false);
			log.info("The response is %s" + response.toString());
			
		}
	   
	  /* public void callDME2(String env,String latitude,String longitude,String clientUri,String payload,String subContext,String username,String password) throws Exception {
		   Properties props = new Properties();
		   InputStream ins = new ByteArrayInputStream( payload.getBytes() );
		  
		   HashMap<String, String> hm = new HashMap<String, String>();
		   hm.put("AFT_DME2_EP_READ_TIMEOUT_MS", "50000");

		   hm.put("AFT_DME2_ROUNDTRIP_TIMEOUT_MS", "240000");

		   hm.put("AFT_DME2_EP_CONN_TIMEOUT", "50000");
		   
		   System.setProperty("AFT_ENVIRONMENT", env);
			System.setProperty("AFT_LATITUDE", latitude);
			System.setProperty("AFT_LONGITUDE", longitude);
			
		   try {
			DME2Manager mgr = new DME2Manager("JettyClient", props);
			DME2Client client = new DME2Client(mgr, new URI(clientUri), 60000);
			
			client.setHeaders(hm);
			DME2StreamPayload inPayload = new DME2StreamPayload(ins);
		
			client.setDME2Payload(inPayload);
			client.addHeader("Content-Type", "application/json");
			client.addHeader("Transfer-encoding", "chunked");
			client.setSubContext(subContext);
			client.setCredentials(username, password);
			 String response = client.sendAndWait(60000);
			 if(response != null) {
			    
				 log.info("The response is %s" + response.toString());
		   	}	
			 
			
		} catch (DME2Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
		  
	   
		
		   
		   
	   }*/
	 
}
