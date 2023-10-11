create or replace PROCEDURE NX_ADD_NEW_TOP_PRODUCT (
							 src_top_product_id  IN NUMBER,
							 dest_top_product_id  IN NUMBER,
  							 top_product_desc IN VARCHAR2,
  							 p_out_status OUT VARCHAR2,
							 p_out_desc OUT VARCHAR2) 
    IS
    
		l_top_product_id NUMBER;
		l_count NUMBER;
        l_little_product_id NUMBER;
    BEGIN
		select count(TOP_PRODUCT_ID)into l_count  from TOP_PRODUCT_DATA where TOP_PRODUCT_ID=dest_top_product_id;
		IF (l_count = 0) THEN
			insert into TOP_PRODUCT_DATA(TOP_ID, TOP_PRODUCT_ID, PRODUCT, TOP_PRODUCT_NAME) 
            select SEQ_NX_TOP_PRODUCT.nextval, dest_top_product_id, PRODUCT, top_product_desc from TOP_PRODUCT_DATA where TOP_PRODUCT_ID=src_top_product_id;
            
            insert into NX_OUTPUT_PRODUCT_MAPPING (TOP_PROD_ID, TAB_NAME) select dest_top_product_id, TAB_NAME from NX_OUTPUT_PRODUCT_MAPPING where TOP_PROD_ID=src_top_product_id;
            
            for cur_little_product_data in (select a.* from LITTLE_PRODUCT_DATA a where a.TOP_PRODUCT_ID=src_top_product_id)
            LOOP
                select SEQ_NX_LITTLE_PRODUCT.nextval into l_little_product_id from DUAL;
                insert into LITTLE_PRODUCT_DATA (LITTLE_ID, LITTLE_PRODUCT_ID, TOP_PRODUCT_ID, LITTLE_PRODUCT_NAME, ACTIVE_YN)
                values (l_little_product_id, cur_little_product_data.LITTLE_PRODUCT_ID, dest_top_product_id, 
                        cur_little_product_data.LITTLE_PRODUCT_NAME, cur_little_product_data.ACTIVE_YN);
                DBMS_OUTPUT.PUT_LINE('NEW LITTLE PRODUCT ID: ' || l_little_product_id);
                
                DBMS_OUTPUT.PUT_LINE('MAPPING WITH LOOKUP DATA LP ID: ' || cur_little_product_data.LITTLE_ID);
                for cur_lookup_data_mapping in (select a.* from LOOKUP_DATA_MAPPING a where a.LITTLE_ID=cur_little_product_data.LITTLE_ID)
                LOOP
                    insert into LOOKUP_DATA_MAPPING (ID, LITTLE_ID, TABLE_COLUMN_NAME, INPUT_CELL, REQUIRED_YN, DEFAULT_YN, DEFAULT_VALUE, FLOW_TYPE, POJO_ATTRIBUTE)
                    values (SEQ_LOOKUP_DATA_MAPPING.nextval,l_little_product_id, cur_lookup_data_mapping.TABLE_COLUMN_NAME, cur_lookup_data_mapping.INPUT_CELL, cur_lookup_data_mapping.REQUIRED_YN, 
                            cur_lookup_data_mapping.DEFAULT_YN, cur_lookup_data_mapping.DEFAULT_VALUE, cur_lookup_data_mapping.FLOW_TYPE, cur_lookup_data_mapping.POJO_ATTRIBUTE);
                END LOOP;
            END LOOP;
            
            p_out_status := 'SUCCESS';
		ELSE
			p_out_status := 'ERROR';
			p_out_desc := 'The record with top product id already exists';			
		END IF;
   EXCEPTION
	   WHEN OTHERS THEN
       	   raise_application_error(-20002, SQLCODE || ' - ' || SQLERRM);
END NX_ADD_NEW_TOP_PRODUCT;



-- TEST SCRIPT --

-- set serveroutput on;
-- declare
--     p1 varchar2(100);
--     p2 varchar2(100);
-- begin
--     NX_ADD_NEW_TOP_PRODUCT(1049, 3333, 'sachin test', p1, p2);
--     DBMS_OUTPUT.PUT_LINE('p1=' || p1);
--     DBMS_OUTPUT.PUT_LINE('p2=' || p2);
-- END;