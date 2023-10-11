package com.att.sales.nexxus.dao.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.att.sales.nexxus.dao.model.NxLookupData;

/**
 * The Interface NxLookupDataRepository.
 */
public interface NxLookupDataRepository extends JpaRepository<NxLookupData, String> {
	
	/**
	 * Find top by dataset name and item id.
	 *
	 * @param datasetName the dataset name
	 * @param itemId the item id
	 * @return the nx lookup data
	 */
	NxLookupData findTopByDatasetNameAndItemId(String datasetName, String itemId);
	
	
	/**
	 * Find top by dataset name and description.
	 *
	 * @param datasetName the dataset name
	 * @param description the description
	 * @return the nx lookup data
	 */
	NxLookupData findTopByDatasetNameAndDescription(String datasetName, String description);
	
	
	/**
	 * Find by dataset name.
	 *
	 * @param datasetName the dataset name
	 * @return the list
	 */
	List<NxLookupData> findByDatasetName(String datasetName);
	
	List<NxLookupData> findByDatasetNameAndItemIdAndDescription(String datasetName,String itemId,String description);
	
	List<NxLookupData> findByDatasetNameAndItemId(String dataSetName,String itemId);
	 
	/**
	 * Gets the new existing migration rules.
	 *
	 * @param datasetName the dataset name
	 * @return the new existing migration rules
	 */
	@Query(value="from NxLookupData nl where nl.datasetName=:datasetName and itemId=:itemId and nl.active='Y' order by nl.sortOrder ")
	List<NxLookupData> getNewExistingMigrationRules(@Param("datasetName") String datasetName,@Param("itemId") String itemId);
	
	@Query(value="from NxLookupData where datasetName=:datasetName and description=:description ")
	NxLookupData findByDatasetNameAndDescription(@Param("datasetName") String datasetName,@Param("description") String description);
	
	@Query(value="from NxLookupData where datasetName=:datasetName and description like %:description% ")
	NxLookupData findHridCondByDatasetNameAndDescription(@Param("datasetName") String datasetName,@Param("description") String description);
	
	@Query(value="from NxLookupData where datasetName='SERVICE_GROUP' and description IN (:description) ")
	NxLookupData findServiceGroupByDescription(@Param("description") List<String> description);

	@Query(value="from NxLookupData where datasetName='SERVICE_ACCESS_GROUP' and description=:description ")
	NxLookupData findServiceAccessGroupByDescription(@Param("description") String description);

	@Query(value="from NxLookupData nl where description=:description")
	NxLookupData findByDescription(@Param("description") String description);
	
	@Query(value="from NxLookupData nl where itemId=:itemId and nl.datasetName in (:datasetName)")
	List<NxLookupData> findByItemIdAndDatasetName(@Param("itemId") String itemId, @Param("datasetName") List<String> datasetName);
	
	@Query(value="from NxLookupData nl where description=:description and nl.datasetName in (:datasetName)")
	NxLookupData findByDescriptionAndDatasetName(@Param("description") String description, @Param("datasetName") List<String> datasetName);

	@Query(value = "select dataset_name from nx_lookup_data where dataset_name in (:datasetName) and item_id = :itemId",nativeQuery=true)
	String findDatasetNameByItemIdAndDatasetName(@Param("itemId") String itemId, @Param("datasetName") List<String> datasetName);
	
	NxLookupData findByItemIdAndDatasetNameAndCriteria(@Param("itemId") String itemId, @Param("datasetName") String datasetName, @Param("criteria") String criteria);
	
	@Query(value = "from NxLookupData where itemId in (:itemId) and datasetName = :datasetName and criteria =:criteria and description=:description")
	List<NxLookupData> findByItemIdsAndDatasetAndCriteriaAndDesc(@Param("itemId") Set<String> itemId, @Param("datasetName") String datasetName, @Param("criteria") String criteria, @Param("description") String description);
	
	@Query(value = "from NxLookupData where itemId = :itemId and datasetName = :datasetName and criteria =:criteria and description=:description")
	List<NxLookupData> findByItemIdAndDatasetAndCriteriaAndDesc(@Param("itemId") String itemId, @Param("datasetName") String datasetName, @Param("criteria") String criteria, @Param("description") String description);
	
	@Query(value="from NxLookupData nl where nl.datasetName=:datasetName and nl.active='Y' order by nl.sortOrder ")
	List<NxLookupData> getOrderForMacd(@Param("datasetName") String datasetName);
	
	List<NxLookupData> findByDatasetNameAndItemIdAndActive(String datasetName,String itemId,String active);
	
	default Map<String, String> getDescDataFromLookup(String datasetName) {
		HashMap<String, String> result = new HashMap<String, String>();
		List<NxLookupData> nxLookupLst = findByDatasetName(datasetName);
		Optional.ofNullable(nxLookupLst).map(List::stream).orElse(Stream.empty()).filter(Objects::nonNull)
				.forEach(data -> {
					if (StringUtils.isNotEmpty(data.getDescription())) {
						result.put(data.getItemId(), data.getDescription());
					}
				});
		return result;
	}	
	/**
	 * Find by dataset name.
	 *
	 * @param datasetName the dataset name
	 * @param active the active
	 * @return the list
	 */

	List<NxLookupData> findByDatasetNameAndActive(String dataSetName, String active);
	
	@Query(value = "select * from nx_lookup_data where dataset_name in (:dataSetName) and active = :active order by id desc",nativeQuery=true)
	List<NxLookupData> fetchByDatasetNameAndActive(@Param("dataSetName")List<String> dataSetName, @Param("active")String active);

	
	@Query(value = "select listagg(DESCRIPTION,',')within group (order by DESCRIPTION) as description from nx_lookup_data where DATASET_NAME=:dataSetName",nativeQuery=true)
	List<String> fetchDescriptionByDataSetName(@Param("dataSetName")String dataSetName);

}
