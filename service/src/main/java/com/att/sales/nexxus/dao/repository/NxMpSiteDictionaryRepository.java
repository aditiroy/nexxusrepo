package com.att.sales.nexxus.dao.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxMpSiteDictionary;

/**
 * @author KumariMuktta
 *
 */
@Repository
@Transactional
public interface NxMpSiteDictionaryRepository extends JpaRepository<NxMpSiteDictionary, Long> {

	NxMpSiteDictionary findByNxTxnIdAndSiteRefId(Long nxTxnId, Long siteRefId);
	
	@Query("select data from NxMpSiteDictionary data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId")
	NxMpSiteDictionary findByNxTxnId(@Param("nxTxnId") Long nxTxnId);

}
