ALTER TABLE syndicate_data_query_inputs ADD COLUMN DATATYPE_FORMAT VARCHAR(50) NULL DEFAULT NULL COMMENT 'The format of the type if applicable' AFTER SAMPLE_VALUE ;
ALTER TABLE syndicate_data_query_inputs_audit ADD COLUMN DATATYPE_FORMAT VARCHAR(50) NULL DEFAULT NULL COMMENT 'The format of the type if applicable' AFTER SAMPLE_VALUE;
ALTER TABLE syndicate_data_query_outputs ADD COLUMN DATATYPE_FORMAT VARCHAR(50) NULL DEFAULT NULL COMMENT 'The format of the type if applicable' AFTER DATA_TYPE;
ALTER TABLE syndicate_data_query_outputs_audit ADD COLUMN DATATYPE_FORMAT VARCHAR(50) NULL DEFAULT NULL COMMENT 'The format of the type if applicable' AFTER DATA_TYPE;
