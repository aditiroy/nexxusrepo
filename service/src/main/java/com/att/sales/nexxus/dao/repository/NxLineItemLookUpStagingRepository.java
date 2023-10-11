package com.att.sales.nexxus.dao.repository;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.constant.DataUploadConstants;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpDataModel;
import com.att.sales.nexxus.dao.model.NxLineItemLookUpStagingModel;

/**
 * The Class NxLineItemLookUpStagingRepository.
 */
@Repository(value="nxLineItemLookUpStagingRepository")
public class NxLineItemLookUpStagingRepository {
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(NxLineItemLookUpStagingRepository.class);
	
	/** The em. */
	@PersistenceContext
	private EntityManager em;
	
	/** The batch size. */
	@Value("${hibernate.jdbc.batch_size}")
	private int batchSize;
	
	/** The nx line item lookup data repo. */
	@Autowired
	private NxLineItemLookupDataRepository nxLineItemLookupDataRepo;
	
	
	
	/**
	 * Bulk save.
	 *
	 * @param entities the entities
	 * @return the linked list
	 */
	public List<NxLineItemLookUpStagingModel> bulkSave(List<NxLineItemLookUpStagingModel> entities) {
		log.info("Inside bulkSave method  {}","");
		  LinkedList<NxLineItemLookUpStagingModel> savedEntities = new LinkedList<>();
		  int i = 0;
		  for (NxLineItemLookUpStagingModel t : entities) {
		    savedEntities.add(persistOrMergeStagingData(t));
		    i++;
		    if (i % batchSize == 0) {
		      // Flush a batch of inserts and release memory.
		      em.flush();
		      em.clear();
		    }
		  }
		  return savedEntities;
	}
	
	
	/**
	 * Persist or merge.
	 *
	 * @param t the t
	 * @return the nx line item look up staging model
	 */
	public NxLineItemLookUpStagingModel persistOrMergeStagingData(NxLineItemLookUpStagingModel t) {
		//code required 
		  if (t.getNxItemId()==null) {
		    em.persist(t);
		    return t;
		  } else {
		    return em.merge(t);
		  }
	}
	
	
	
	/**
	 * Gets the stagging data by little and top prod id.
	 *
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @return the stagging data by little and top prod id
	 */
	@SuppressWarnings("unchecked")
	public List<NxLineItemLookUpStagingModel> getStaggingDataByLittleAndTopProdId(Long littleProdId,Long topProdId){
		List<NxLineItemLookUpStagingModel> resultLst=null;
		String hqlQuery="from NxLineItemLookUpStagingModel where littleProdId=:littleProdId "
				+ "and topProdId=:topProdId and active='S' order by nxItemId  ";
		Query query=em.createQuery(hqlQuery);
		query.setParameter(DataUploadConstants.LITTLE_PROD_ID, littleProdId);
		query.setParameter(DataUploadConstants.TOP_PROD_ID, topProdId);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	/**
	 * Bulk delete.
	 *
	 * @param entities the entities
	 */
	public void bulkDelete(List<NxLineItemLookUpStagingModel> entities) {
		  int i = 0;
		  for (NxLineItemLookUpStagingModel t : entities) {
		  em.remove(em.contains(t) ? t : em.merge(t));
		
		    i++;
		    if (i % batchSize == 0) {
		      em.flush();
		      em.clear();
		    }
		  }
	}
	
	
	/**
	 * Delete record By ids.
	 *
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 */
	public void deleteRecordByIds(Long littleProdId,Long topProdId) {
		String hqlQuery="delete from NxLineItemLookUpStagingModel where littleProdId=:littleProdId and topProdId=:topProdId ";
		Query query=em.createQuery(hqlQuery);
		query.setParameter(DataUploadConstants.LITTLE_PROD_ID, littleProdId);
		query.setParameter(DataUploadConstants.TOP_PROD_ID, topProdId);
		query.executeUpdate();
	}
	
	/**
	 * Gets the id by little and top prod id.
	 *
	 * @param littleProdId the little prod id
	 * @param topProdId the top prod id
	 * @return the id by little and top prod id
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getIdByLittleAndTopProdId(Long littleProdId,Long topProdId) {
		List<Long> resultLst=null;
		String hqlQuery="select ns.nxItemId from NxLineItemLookUpStagingModel ns where "
				+ "ns.littleProdId=:littleProdId and ns.topProdId=:topProdId order by nxItemId  ";
		Query query=em.createQuery(hqlQuery);
		query.setParameter(DataUploadConstants.LITTLE_PROD_ID, littleProdId);
		query.setParameter(DataUploadConstants.TOP_PROD_ID, topProdId);
		resultLst = query.getResultList();
		return resultLst;
	}
	
	
	/**
	 * Activate line item data.
	 *
	 * @param inputmap the inputmap
	 * @return the list
	 */
	public List<NxLineItemLookUpDataModel> activateLineItemData(Map<String,Object> inputmap) {
		Long littleProdId=null!=inputmap.get(DataUploadConstants.LITTLE_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.LITTLE_PROD_ID).toString()):null;
		Long topProdId=null!=inputmap.get(DataUploadConstants.TOP_PROD_ID)?
				Long.valueOf(inputmap.get(DataUploadConstants.TOP_PROD_ID).toString()):null;
		List<NxLineItemLookUpStagingModel> resultLst=this.getStaggingDataByLittleAndTopProdId(littleProdId, topProdId);
		LinkedList<NxLineItemLookUpDataModel> savedEntities = new LinkedList<>();
		Set<String> unMatchedSecKeys=new HashSet<>();
		Optional.ofNullable(resultLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
		.forEach(data -> {
			Long dataCount=nxLineItemLookupDataRepo.getDataCountByLineItemIdSecKeyLittleProdIdAndTopProdId(data.getLineItemId(),
					data.getSecondaryKey(), littleProdId, topProdId, data.getFlowType());
			if(dataCount<=0) {
				List<NxLineItemLookUpDataModel> lookupDataLst=nxLineItemLookupDataRepo.findBySecondaryKeyLittleProdIdAndTopProdId(
						littleProdId,topProdId, data.getSecondaryKey(),data.getFlowType());
				if(CollectionUtils.isNotEmpty(lookupDataLst)) {
					final AtomicInteger indexHolder = new AtomicInteger(0);
					 lookupDataLst.stream().filter(Objects::nonNull).forEach(t -> {
						 try {
							NxLineItemLookUpDataModel newObj=(NxLineItemLookUpDataModel)BeanUtils.cloneBean(t);
							if(!data.getLineItemId().equals(t.getLineItemId())) {
								newObj.setNxItemId(null);
								newObj.setLineItemId(data.getLineItemId());
								em.persist(newObj);
								savedEntities.add(newObj);
								final int i=indexHolder.getAndIncrement();
							    if (i % batchSize == 0) {
							      em.flush();
							      em.clear();
							    }
							    data.setActive(DataUploadConstants.NO);
							    this.persistOrMergeStagingData(data);
							}
							
						} catch (IllegalAccessException | InstantiationException | InvocationTargetException
								| NoSuchMethodException e) {
							log.error("Exception in Activating data", e);
						}
						 
					});
				}else {
					unMatchedSecKeys.add(data.getSecondaryKey());
				}
			}else {
				 data.setActive(DataUploadConstants.NO);
				 this.persistOrMergeStagingData(data);
			}
			
		});
		if(CollectionUtils.isNotEmpty(unMatchedSecKeys)) {
			unMatchedSecKeys.removeIf(Objects::isNull);
			StringUtils.join(unMatchedSecKeys, ',');
			inputmap.put(DataUploadConstants.UNMATCHED_SECONDARY_KES, unMatchedSecKeys);
		}else {
			inputmap.put(DataUploadConstants.STATUS, DataUploadConstants.SUCCESS);
		}
		
		return savedEntities;
	}

}
