package com.att.sales.nexxus.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxUserLoginDetails;

@Repository
public interface NxUserLoginDetailsRepository extends JpaRepository<NxUserLoginDetails, Long> {

}
