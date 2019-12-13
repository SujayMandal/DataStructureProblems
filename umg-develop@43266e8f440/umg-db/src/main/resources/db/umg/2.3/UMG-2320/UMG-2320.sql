#New table to store cancel status of usage search request

DROP TABLE IF EXISTS USAGE_SEARCH_REQUEST_CANCEL;

CREATE TABLE USAGE_SEARCH_REQUEST_CANCEL (
                ID CHARACTER(36) NOT NULL COMMENT 'Unique Identifier',
                TENANT_ID CHAR(36) NOT NULL COMMENT 'Tenant code for the record',
                IS_USAGE_SEARCH_CANCEL TINYINT(4) NOT NULL COMMENT 'Flag to indicate whether request has been cancelled or not',
                CREATED_BY CHAR(36) NOT NULL, 
                CREATED_ON BIGINT NOT NULL,
                LAST_UPDATED_BY CHAR(36) NULL, 
                LAST_UPDATED_ON BIGINT NULL,
                PRIMARY KEY (ID)
)
COMMENT='contains cancellation status for all usage search requests'
DEFAULT CHARSET=utf8
COLLATE='utf8_bin'
ENGINE=InnoDB;

commit;


# index on run_as_of_date and model name
create index umg_runtime_transaction_index_1 using btree on umg_runtime_transaction (RUN_AS_OF_DATE , VERSION_NAME);
commit;