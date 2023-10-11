package com.att.sales.nexxus.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.att.sales.framework.exception.SalesBusinessException;
import com.att.sales.framework.model.Message;
import com.att.sales.framework.model.ServiceResponse;
import com.att.sales.framework.model.Status;
import com.att.sales.framework.model.constants.HttpErrorCodes;
import com.att.sales.framework.service.BaseServiceImpl;
import com.att.sales.nexxus.constant.CommonConstants;
import com.att.sales.nexxus.constant.MessageConstants;
import com.att.sales.nexxus.dao.model.LittleProductDataEntity;
import com.att.sales.nexxus.dao.model.TopProductDataEntity;
import com.att.sales.nexxus.dao.repository.LittleProductRepo;
import com.att.sales.nexxus.dao.repository.TopProductRepo;
import com.att.sales.nexxus.model.LittleProduct;
import com.att.sales.nexxus.model.RetrieveAdminDataRequest;
import com.att.sales.nexxus.model.RetrieveAdminDataResponse;
import com.att.sales.nexxus.model.TopProduct;
import com.att.sales.nexxus.reteriveicb.model.GetTopProductsResponse;

/**
 * The Class NxAdminService.
 *
 * @author RudreshWaladaunki
 */
@Service
@Transactional
public class NxAdminService extends BaseServiceImpl {

	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(NxAdminService.class);

	/** The little product repo. */
	@Autowired
	private LittleProductRepo littleProductRepo;

	/** The em. */
	@PersistenceContext
	private EntityManager em;

	/** The top product repo. */
	@Autowired
	private TopProductRepo topProductRepo;

	/**
	 * Gets the admin data.
	 *
	 * @param request the request
	 * @return the admin data
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse getAdminData(RetrieveAdminDataRequest request) throws SalesBusinessException {
		RetrieveAdminDataResponse adminDataResponse = new RetrieveAdminDataResponse();

		if (null != request.getAction() && request.getAction().equalsIgnoreCase("retrieveAdminData")) {
			List<TopProduct> topProductList = new ArrayList<>();
			List<TopProductDataEntity> topProductDataEntities = null;//topProductRepo.findAll(new Sort(Sort.Direction.ASC, "topProductName"));

			for (TopProductDataEntity topProductData : topProductDataEntities) {
				TopProduct topProduct = new TopProduct();
				topProduct.setTopProductId(topProductData.getTopProductId());
				topProduct.setProduct(topProductData.getProduct());
				topProduct.setDescription(topProductData.getTopProductName());
				List<LittleProduct> littleProducts = new ArrayList<>();
				List<LittleProductDataEntity> littleProductDataEntities = littleProductRepo
						.findByTopProductData(topProductData);
				for (LittleProductDataEntity littleProductDataEntity : littleProductDataEntities) {
					LittleProduct littleProduct = new LittleProduct();
					littleProduct.setLittleProductId(littleProductDataEntity.getLittleProductId());
					littleProduct.setDescription(littleProductDataEntity.getLittleProductName());
					littleProduct.setActiveYn(littleProductDataEntity.getActiveYn());
					littleProduct.setLittleId(littleProductDataEntity.getLittleId());
					littleProducts.add(littleProduct);
				}
				topProduct.setLittleProductList(littleProducts);
				topProductList.add(topProduct);
			}

			adminDataResponse.setTopProductList(topProductList);
			setSuccessResponse(adminDataResponse);
		} else {
			throw new SalesBusinessException();
		}

		return adminDataResponse;
	}

	/**
	 * Fetch all top products.
	 *
	 * @param queryParams the query params
	 * @return the service response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse fetchAllTopProducts(LinkedHashMap<String, Object> queryParams)
			throws SalesBusinessException {
		try {
			GetTopProductsResponse resp = new GetTopProductsResponse();
			List<TopProductDataEntity> topProductDataEntities = null;// = topProductRepo.findAll(new Sort(Sort.Direction.ASC, "topProductName"));
			resp.setTopProductList(topProductDataEntities);
			setSuccessResponse(resp);
			return resp;
		} catch (Exception e) {
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}

	/**
	 * Admin update product info.
	 *
	 * @param queryParams the query params
	 * @return the service response
	 * @throws SalesBusinessException the sales business exception
	 */
	public ServiceResponse adminUpdateProductInfo(LinkedHashMap<String, Object> queryParams)
			throws SalesBusinessException {
		try {
			ServiceResponse resp = new ServiceResponse();

			String productType = queryParams.get("productType").toString();
			if ("TOP".equalsIgnoreCase(productType)) {
				Long srcTopProductId = Long.parseLong(queryParams.get("srcTopProductId").toString());
				Long destTopProductId = Long.parseLong(queryParams.get("destTopProductId").toString());
				String destTopProductDesc = queryParams.get("destTopProductDesc").toString();
				this.copyRecursiveTopProductInfoWithNewTopProductId(srcTopProductId, destTopProductId,
						destTopProductDesc, resp);
			} else {				
                Long topProductId = Long.parseLong(queryParams.get("topProductId").toString());
				Long srcLittleProductId = Long.parseLong(queryParams.get("srcLittleProductId").toString());
				Long destLittleProductId = Long.parseLong(queryParams.get("destLittleProductId").toString());
				String destLittleProductDesc = queryParams.get("destLittleProductDesc").toString();
				Query q =  em.createNamedQuery("updateLittleProductAdminData");
				q.setParameter("newLittleProductId", destLittleProductId);
				q.setParameter("littleProductName", destLittleProductDesc);
				q.setParameter("topProductId", topProductId);
				q.setParameter("oldLittleProductId", srcLittleProductId);
				q.executeUpdate();
				logger.info("Updated the little product changes successfully in DB with new id as {} and desc as {}", destLittleProductId, destLittleProductDesc);
				setSuccessResponse(resp);
			}

			return resp;
		} catch (Exception e) {
			throw new SalesBusinessException(MessageConstants.PROCESS_ERROR_CODE);
		}
	}

	/**
	 * This method invokes the stored procedure to copy top product details under new top product id.
	 *
	 * @param srcTopProductId the src top product id
	 * @param destTopProductId the dest top product id
	 * @param destTopProductDesc the dest top product desc
	 * @param resp the resp
	 */
	private void copyRecursiveTopProductInfoWithNewTopProductId(Long srcTopProductId, Long destTopProductId,
			String destTopProductDesc, ServiceResponse resp) {
		StoredProcedureQuery storedProcedure = em.createStoredProcedureQuery(CommonConstants.PROC_ADD_NEW_TOP_PRODUCT);
		storedProcedure.registerStoredProcedureParameter("src_top_product_id", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("dest_top_product_id", Long.class, ParameterMode.IN)
				.registerStoredProcedureParameter("top_product_desc", String.class, ParameterMode.IN)
				.registerStoredProcedureParameter("p_out_status", String.class, ParameterMode.OUT)
				.registerStoredProcedureParameter("p_out_desc", String.class, ParameterMode.OUT);

		storedProcedure.setParameter("src_top_product_id", srcTopProductId);
		storedProcedure.setParameter("dest_top_product_id", destTopProductId);
		storedProcedure.setParameter("top_product_desc", destTopProductDesc);

		storedProcedure.execute();
		Object resultStatus = storedProcedure.getOutputParameterValue("p_out_status");
		Object resultDesc = storedProcedure.getOutputParameterValue("p_out_desc");

		String strResultStatus = resultStatus != null ? resultStatus.toString() : "";
		String strResultDesc = resultDesc != null ? resultDesc.toString() : "REQUEST_COMPLETED_SUCCESSFULLY";

		Status s = new Status();
		List<Message> msgList = new ArrayList<>();
		Message msg = new Message();
		msg.setDescription(strResultDesc);
		msg.setDetailedDescription(strResultDesc);
		msgList.add(msg);

		if ("SUCCESS".equalsIgnoreCase(strResultStatus)) {
			msg.setCode("M00000");
			s.setCode(HttpErrorCodes.STATUS_OK.toString());			
		} else {
			msg.setCode("M00003");
			s.setCode("500");
		}

		s.setMessages(msgList);
		resp.setStatus(s);
	}
}
