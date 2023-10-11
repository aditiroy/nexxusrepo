package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxAdminUserModel;

/**
 * @author Shrinath
 *
 */
public interface NxAdminUserRepository extends JpaRepository<NxAdminUserModel, Long> {
	
	@Query(value = "select nau from NxAdminUserModel nau where attuid = :attuid and activeYn = 'Y'")
	List<NxAdminUserModel> findByAttUid(@Param("attuid") String userId);
	
	@Query(value = "select nau from NxAdminUserModel nau where attuid = :attuid")
	List<NxAdminUserModel> findByAttuidOnly(@Param("attuid") String userId);
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value="UPDATE NX_ADMIN_USER SET ACTIVE_YN = :activeYN WHERE ID = :rowId", nativeQuery=true)
	int updateActiveYn(@Param("activeYN") String activeYN, @Param("rowId") Long rowId);
	
}
