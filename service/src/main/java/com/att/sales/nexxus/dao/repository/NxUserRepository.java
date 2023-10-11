package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxProfiles;
import com.att.sales.nexxus.dao.model.NxUser;

@Repository
@Transactional
public interface NxUserRepository extends JpaRepository<NxUser, String>{

	@Query(value= "select profile_name from nx_profiles npf join  nx_user nxusr on npf.profile_id = nxusr.profile_id "
			+ "where nxusr.user_att_id= :attuid and npf.active='Y' and  nxusr.active='Y' and npf.profile_name in ('General Access', 'Admin Access')",nativeQuery = true)
	List<String> findProfileNameByAttID(@Param("attuid") String attuid);
	
	List<NxUser> findByNxProfiles(NxProfiles nxProfiles);

	NxUser findByUserAttId(String userAttId);

}
