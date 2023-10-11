package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.att.sales.nexxus.dao.model.NexxusPreviewInrUIModel;

/**
 * The Class NxPreviewInrJsonDao.
 */
@Component
public class NxPreviewInrJsonDao {
	
	
	/** The em. */
	@PersistenceContext
	private EntityManager em;
	
	/**
	 * Gets the intermediate json.
	 *
	 * @param nXReqId the n X req id
	 * @return the intermediate json
	 */
	public List<NexxusPreviewInrUIModel> getIntermediateJson(Long nXReqId)
	
	{
		
		TypedQuery<NexxusPreviewInrUIModel> q=null;
		q = em.createNamedQuery("getNexxusPreviewInrByNxReqId", NexxusPreviewInrUIModel.class);
		q.setParameter("nXReqId", nXReqId);
		List<NexxusPreviewInrUIModel> resultantlist = q.getResultList();
		
		return resultantlist;
		
		
	}

}
