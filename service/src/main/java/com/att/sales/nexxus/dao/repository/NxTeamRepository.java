package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.solution.NxSolutionDetail;
import com.att.sales.nexxus.dao.model.solution.NxTeam;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

/**
 * The Interface NxTeamRepository.
 *
 * @author RudreshWaladaunki
 */
@Repository
@Transactional
public interface NxTeamRepository extends JpaRepository<NxTeam, Long> {
	
	/**
	 * Find by attuid and nx solution detail.
	 *
	 * @param attuid the attuid
	 * @param nxSolutionDetail the nx solution detail
	 * @return the list
	 */
	List<NxTeam> findByAttuidAndNxSolutionDetail(@Param("attuid") String attuid, @Param("nxSolutionDetail") NxSolutionDetail nxSolutionDetail);
	
	/**
	 * Gets the nx team.
	 *
	 * @param nxSolutionId the nx solution id
	 * @param attuid the attuid
	 * @return the nx team
	 */
	@Query(value="select * from NX_TEAM nxt where nxt.ATTUID = :attuid and nxt.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true)
	NxTeam getNxTeam( @Param("nxSolutionId")Long nxSolutionId, @Param("attuid") String attuid);
	
	/**
	 * Find by nx solution id.
	 *
	 * @param nxSolutionId the nx solution id
	 * @return the list
	 */
	@Query(value="select * from NX_TEAM nxt where nxt.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true) 
	List<NxTeam> findByNxSolutionId( @Param("nxSolutionId")Long nxSolutionId);


	@Transactional
	@Modifying
	@Query(value = "delete from NX_TEAM nxt where nxt.NX_SOLUTION_ID = :nxSolutionId",nativeQuery = true)
	int deleteNxTeams( @Param("nxSolutionId")Long nxSolutionId);

}
