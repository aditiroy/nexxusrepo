package com.att.sales.nexxus.dao.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxProfiles;

@Repository
@Transactional
public interface NxProfilesRepository extends JpaRepository<NxProfiles, Long>{
	
	public List<NxProfiles> findAll();
	
	NxProfiles findByProfileNameAndActive(String profileName, String active);
	
}
