/**
 * 
 */
package com.att.sales.nexxus.dao.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.att.sales.nexxus.dao.model.FailedBeidDetails;

/**
 * The Interface FailedBeidDetailsRepository.
 *
 * @author RudreshWaladaunki
 */
@Repository
public interface FailedBeidDetailsRepository extends JpaRepository<FailedBeidDetails, Long> {

}
