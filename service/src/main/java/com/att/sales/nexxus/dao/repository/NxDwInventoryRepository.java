package com.att.sales.nexxus.dao.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.NxDwInventory;

@Repository
@Transactional
public interface NxDwInventoryRepository extends JpaRepository<NxDwInventory, Long>{

}
