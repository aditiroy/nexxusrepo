package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxReqRefNumMapping;

public interface NxReqRefNumMappingRepository extends JpaRepository<NxReqRefNumMapping, String> {
	List<NxReqRefNumMapping> findByNexxusRefNum(@Param("nexxusRefNum") String nexxusRefNum);
}
