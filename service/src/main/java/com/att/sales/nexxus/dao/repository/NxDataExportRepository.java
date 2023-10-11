package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxDataExport;;
/**
 * The Interface NxSsdfSpeedMappingRepository.
 */
@Repository
@Transactional
public interface NxDataExportRepository extends JpaRepository<NxDataExport, Long>{
	
	@Query(value= "select * from NX_DATA_EXPORT n where n.COMPONENT= :component and n.ACTIVE = :active order by n.order_seq ASC",nativeQuery=true)
	List<NxDataExport> getNxDataExport(@Param("component") String component, @Param("active") String active);
	
	

}

