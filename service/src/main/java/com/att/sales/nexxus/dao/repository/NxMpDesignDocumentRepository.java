package com.att.sales.nexxus.dao.repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxMpDesignDocument;

/**
 * @author KumariMuktta
 *
 */
@Repository
@Transactional
public interface NxMpDesignDocumentRepository extends JpaRepository<NxMpDesignDocument, Long> {

	@Query(value = "select count(*) from NX_MP_DESIGN_DOCUMENT WHERE NX_TXN_ID = :nxTxnId AND MP_SOLUTION_ID = :mpDocumentNumber", nativeQuery = true)
	Long findCountByNxTxnIdAndMpSolutionId(@Param("nxTxnId") Long nxTxnId, @Param("mpDocumentNumber") Long mpDocumentNumber);

	@Query(value = "select designDocument from NxMpDesignDocument designDocument where nxTxnId = :nxTxnId AND mpSolutionId = :mpSolutionId")
	NxMpDesignDocument findByNxTxnIdAndMpSolutionId(@Param("nxTxnId") Long nxTxnId, @Param("mpSolutionId") String mpSolutionId);

	NxMpDesignDocument findByUsocId(String usocId);
	
	@Query(value = "select designDocument from NxMpDesignDocument designDocument where nxTxnId = :nxTxnId AND nxDesignId = :nxDesignId AND usocId = :usocId AND mpPartNumber = :componentId")
	NxMpDesignDocument findByTxnDesignUsocComponentIds(@Param("nxTxnId") Long nxTxnId,@Param("nxDesignId") Long nxDesignId,@Param("usocId") String usocId,@Param("componentId") String componentId);
	
	@Query(value = "select designDocument from NxMpDesignDocument designDocument where nxTxnId = :nxTxnId AND nxDesignId = :nxDesignId AND usocId = :usocId")
	NxMpDesignDocument findByTxnDesignUsocIds(@Param("nxTxnId") Long nxTxnId,@Param("nxDesignId") Long nxDesignId,@Param("usocId") String usocId);

	@Query(value = "select * from NX_MP_DESIGN_DOCUMENT WHERE NX_TXN_ID = :nxTxnId AND MP_PRODUCT_LINE_ID = :mpDocumentNumber AND MP_SOLUTION_ID = :mpSolutionId", nativeQuery = true)
	List<NxMpDesignDocument> findByNxTxnIdAndMpProdLineId(@Param("nxTxnId") Long nxTxnId, @Param("mpDocumentNumber") Long mpDocumentNumber, @Param("mpSolutionId") String mpSolutionId);
	
	List<NxMpDesignDocument> findByMpProductLineIdAndMpDocumentNumberAndNxTxnId(@Param("mpProductLineId")String mpProductLineId,
			@Param("mpDocumentNumber")Long mpDocumentNumber,@Param("nxTxnId")Long nxTxnId);
	
	List<NxMpDesignDocument> findByMpProductLineIdAndMpDocumentNumberAndNxTxnIdAndMpPartNumber(@Param("mpProductLineId")String mpProductLineId,
			@Param("mpDocumentNumber")Long mpDocumentNumber,@Param("nxTxnId")Long nxTxnId,@Param("mpPartNumber")String componentId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update NxMpDesignDocument set mpDocumentNumber =:mpDocumentNumber,usocId=:usocId,modifiedDate=:modifiedDate "
			+ "where mpSolutionId=:mpSolutionId and mpProductLineId =:mpProductLineId and nxTxnId=:nxTxnId")
	int updateDesignBySolIdAndProductId(@Param("mpDocumentNumber")Long mpDocumentNumber,@Param("usocId")String usocId,@Param("modifiedDate") Date modifiedDate,
			@Param("mpSolutionId")String mpSolutionId,@Param("mpProductLineId") String mpProductLineId,@Param("nxTxnId") Long nxTxnId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update NxMpDesignDocument set mpDocumentNumber =:mpDocumentNumber,usocId=:usocId,mpPartNumber=:mpPartNumber,modifiedDate=:modifiedDate "
			+ "where mpSolutionId=:mpSolutionId and mpProductLineId =:mpProductLineId and nxTxnId=:nxTxnId")
	int updateDesignBySolIdAndProductIdFmo(@Param("mpDocumentNumber")Long mpDocumentNumber,@Param("usocId")String usocId,@Param("mpPartNumber")String mpPartNumber,
			@Param("modifiedDate") Date modifiedDate,@Param("mpSolutionId")String mpSolutionId,@Param("mpProductLineId") String mpProductLineId,@Param("nxTxnId") Long nxTxnId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "update NxMpDesignDocument set mpDocumentNumber =:mpDocumentNumber,usocId=:usocId,mpPartNumber=:mpPartNumber,modifiedDate=:modifiedDate"
			+ " where mpSolutionId=:mpSolutionId and mpProductLineId =:mpProductLineId and nxTxnId=:nxTxnId")
	int updateDesignBySolIdAndProductIdForAde(@Param("mpDocumentNumber")Long mpDocumentNumber,@Param("usocId")String usocId,@Param("mpPartNumber") String componentId,
			@Param("modifiedDate") Date modifiedDate,@Param("mpSolutionId")String mpSolutionId,@Param("mpProductLineId") String mpProductLineId,@Param("nxTxnId") Long nxTxnId);
	
	@Query(value = "select distinct(MP_SOLUTION_ID) from nx_mp_design_document where MP_PRODUCT_LINE_ID=:mpProductLineId and nx_txn_id=:nxTxnId", nativeQuery = true)
	String getSolutionIdByProductId(@Param("mpProductLineId")String mpProductLineId,@Param("nxTxnId")Long nxTxnId);
	
	@Query(value = "select * from NX_MP_DESIGN_DOCUMENT where MP_SOLUTION_ID=:mpSolutionId and MP_PRODUCT_LINE_ID=:mpProductLineId "
			+ "and NX_TXN_ID=:nxTxnId and MP_DOCUMENT_NUMBER is null" , nativeQuery = true)
	List<NxMpDesignDocument> checkDesignForUpdate(@Param("mpSolutionId")String mpSolutionId,@Param("mpProductLineId")String mpProductLineId,@Param("nxTxnId")Long nxTxnId);
	
	@Query(value = "select * from NX_MP_DESIGN_DOCUMENT where MP_SOLUTION_ID=:mpSolutionId and NX_TXN_ID=:nxTxnId and MP_PRODUCT_LINE_ID is null" , nativeQuery = true)
	List<NxMpDesignDocument> checkProductForUpdate(@Param("mpSolutionId")String mpSolutionId,@Param("nxTxnId")Long nxTxnId);
	
	@org.springframework.transaction.annotation.Transactional
	@Modifying
	@Query(value = "update NxMpDesignDocument set activeYN ='N', modifiedDate=:modifiedDate where nxTxnId = :nxTxnId and nxDesignId = :nxDesignId")
	void updateActiveYNByTxnId(@Param("modifiedDate") Date modifiedDate, @Param("nxTxnId") Long nxTxnId, @Param("nxDesignId") Long nxDesignId);
	
	List<NxMpDesignDocument>  findByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId);
	
	List<NxMpDesignDocument>  getMpProductLineIdByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId);

	@Query("select data from NxMpDesignDocument data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId")
	List<NxMpDesignDocument> findByNxTxnId(@Param("nxTxnId") Long nxTxnId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	long deleteByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId);
	
	@Modifying(clearAutomatically = true)
	@Query(value = "update NxMpDesignDocument set mpProductLineId =:mpProductLineId,modifiedDate=:modifiedDate "
			+ "where mpSolutionId=:mpSolutionId and nxTxnId=:nxTxnId and activeYN='Y'")
	int updateDesignSolIdAndProductResponse(@Param("mpProductLineId")String mpProductLineId,@Param("modifiedDate") Date modifiedDate,
			@Param("mpSolutionId")String mpSolutionId,@Param("nxTxnId") Long nxTxnId);
	@Query("select data from NxMpDesignDocument data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId and data.nxDesignId=:nxDesignId")
	List<NxMpDesignDocument> getDataByNxTxnIdAndNxDesignId(@Param("nxTxnId") Long nxTxnId,@Param("nxDesignId") Long nxDesignId);
	
	List<NxMpDesignDocument>  findByNxTxnIdAndNxDesignIdAndMpPartNumber(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId, @Param("mpPartNumber") String portId);
	
	List<NxMpDesignDocument> findByNxTxnIdAndNxDesignIdIn(Long nxTxnId, List<Long> nxDesignIds);
	
	@Query("select data from NxMpDesignDocument data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId and data.nxDesignId=:nxDesignId and data.usocId IN (:usocId)")
	List<NxMpDesignDocument>  findByNxTxnIdAndNxDesignIdAndUsocIds(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") Long nxDesignId, @Param("usocId") Set<String> usocId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "delete from NxMpDesignDocument where nxTxnId = :nxTxnId and nxDesignId in :nxDesignId")
	void deleteDataByNxTxnIdAndNxDesignId(@Param("nxTxnId")Long nxTxnId, @Param("nxDesignId") List<Long> nxDesignId);
	
	@Query("select distinct nxDesignId from NxMpDesignDocument data where data.activeYN = 'Y' and data.nxTxnId =:nxTxnId and data.mpSolutionId IN (:mpSolutionId)")
	List<Long> findNxDesignIdByNxTxnIdAndMpSolutionId(@Param("nxTxnId")Long nxTxnId, @Param("mpSolutionId") List<String> mpSolutionId);
	

}
