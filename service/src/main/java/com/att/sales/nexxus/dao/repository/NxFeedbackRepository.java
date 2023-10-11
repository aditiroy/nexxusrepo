package com.att.sales.nexxus.dao.repository;

import org.springframework.transaction.annotation.Transactional;

import com.att.sales.nexxus.dao.model.NxFeedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface NxFeedbackRepository extends JpaRepository<NxFeedback, String>{

}
