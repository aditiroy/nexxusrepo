package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxTDMKeyIdMapping;

/**
 * @author ar896d
 *
 */
@Repository
@Transactional

public interface NxTDMKeyIdMappingRepo extends JpaRepository<NxTDMKeyIdMapping, Long>{

	List<NxTDMKeyIdMapping> findByNxGrpId(Long nxGrpId );
	
	List<NxTDMKeyIdMapping> findByNxGrpIdAndNxKeyId(Long nxGrpId, String nxKeyId);
	
	List<NxTDMKeyIdMapping> findByNxGrpIdAndTdmNxKeyId(Long nxGrpId, String tdmNxKeyId);

}
