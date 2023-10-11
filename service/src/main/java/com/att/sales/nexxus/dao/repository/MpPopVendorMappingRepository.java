package com.att.sales.nexxus.dao.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.MpPopVendorMapping;

public interface MpPopVendorMappingRepository extends JpaRepository<MpPopVendorMapping, Long> {

	@Query(value="from MpPopVendorMapping mp where mp.popclli=:popclli and mp.vendorName=:vendorName  order by mp.id ")
	List<MpPopVendorMapping> findByPopclliAndVendorName(@Param("popclli") String popclli,@Param("vendorName") String vendorName);

}
