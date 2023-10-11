package com.att.sales.nexxus.dao.repository;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxMpPriceDetails;

/**
 * 
 * @author Laxman Honawad
 *
 */
@Repository
@Transactional
public interface NxMpPriceDetailsRepository extends JpaRepository<NxMpPriceDetails, Long> {

	NxMpPriceDetails findByMpDocumentNumberAndNxTxnIdAndFrequency(Long mpDocumentNumber,Long nxTxnId, String frequency);
	
	@Query(value="SELECT np FROM NxMpPriceDetails np WHERE  np.mpDocumentNumber = :mpDocumentNumber AND np.nxTxnId = :nxTxnId")
	List<NxMpPriceDetails> findAllMpDocumentNumberAndNxTxnId(@Param("mpDocumentNumber") Long mpDocumentNumber,@Param("nxTxnId") Long nxTxnId);
	
	List<NxMpPriceDetails> findByNxTxnId(Long nxTxnId);

	@Query(value="SELECT np FROM NxMpPriceDetails np WHERE np.nxTxnId IN (:nxTxnId)")
	List<NxMpPriceDetails> findByMultipleTxnIds(@Param("nxTxnId") Set<Long> nxTxnId);
	
	NxMpPriceDetails findByNxPriceDetailsIdAndNxTxnId(Long nxPriceDetailsId, Long nxTxnId);
	
	List<NxMpPriceDetails> findByNxDesignIdAndNxTxnId(@Param("nxDesignId") Long nxDesignId, @Param("nxTxnId") Long nxTxnId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	long deleteByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	long deleteByNxTxnId(@Param("nxTxnId")Long nxTxnId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from NxMpPriceDetails where nxTxnId = :nxTxnId and nxDesignId in :nxDesignId")
	void deleteDataByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") List<Long> nxDesignId);
	
}
