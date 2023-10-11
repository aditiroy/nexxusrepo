package com.att.sales.nexxus.dao.model;

import java.io.Serializable;

import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

/**
 * The Class NamedQueryHolderPreviewInr.
 *
 * @author rk967c
 */
/*@NamedNativeQueries({
		
		Preview INR Query
		
			@NamedNativeQuery(name = "getNexxusPreviewInrByNxReqId", query = "select c.CUSTOMER_NAME, c.OPTY_ID, c.DUNS_NUMBER, b.PRODUCT_CD, a.INTERMEDIATE_JSON, a. OUTPUT_JSON"
					+ "from NX_OUTPUT_FILE a, NX_REQUEST_DETAILS b, NX_SOLUTION_DETAILS c"
					+"where a.NX_REQ_ID = b.NX_REQ_ID and b.NX_SOLUTION_ID = c.NX_SOLUTION_ID and b.NX_REQ_ID=:nXReqId", resultSetMapping = "sqlToNexxusPreviewInrUIModelMapping") 
					
		 })*/


//PREVIEW INR

/*@SqlResultSetMappings({ @SqlResultSetMapping(name = "sqlToNexxusPreviewInrUIModelMapping", classes = {
		@ConstructorResult(targetClass = NexxusPreviewInrUIModel.class, columns = {
				@ColumnResult(name = "CUSTOMER_NAME", type = String.class),
				@ColumnResult(name = "OPTY_ID", type = String.class),
				@ColumnResult(name = "DUNS_NUMBER", type = String.class),
				@ColumnResult(name = "PRODUCT_CD", type = String.class),
				@ColumnResult(name = "INTERMEDIATE_JSON", type = String.class),
				@ColumnResult(name = "OUTPUT_JSON", type = String.class)}) }) })*/

@NamedNativeQuery(name = "getNexxusPreviewInrByNxReqId", query = "select c.CUSTOMER_NAME, c.OPTY_ID, c.DUNS_NUMBER, b.PRODUCT_CD, a.INTERMEDIATE_JSON, a. OUTPUT_JSON"
		+ " from NX_OUTPUT_FILE a, NX_REQUEST_DETAILS b, NX_SOLUTION_DETAILS c"
		+" where a.NX_REQ_ID = b.NX_REQ_ID and b.NX_SOLUTION_ID = c.NX_SOLUTION_ID and b.NX_REQ_ID=:nXReqId", resultSetMapping = "sqlToNexxusPreviewInrUIModelMapping") 

@SqlResultSetMappings({ @SqlResultSetMapping(name = "sqlToNexxusPreviewInrUIModelMapping", classes = {
		@ConstructorResult(targetClass = NexxusPreviewInrUIModel.class, columns = {
				@ColumnResult(name = "CUSTOMER_NAME", type = String.class),
				@ColumnResult(name = "OPTY_ID", type = String.class),
				@ColumnResult(name = "DUNS_NUMBER", type = String.class),
				@ColumnResult(name = "PRODUCT_CD", type = String.class),
				@ColumnResult(name = "INTERMEDIATE_JSON", type = String.class),
				@ColumnResult(name = "OUTPUT_JSON", type = String.class)}) }) })
@Entity
public class NamedQueryHolderPreviewInr implements Serializable {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The id. */
	@Id
	private Integer id;
}
