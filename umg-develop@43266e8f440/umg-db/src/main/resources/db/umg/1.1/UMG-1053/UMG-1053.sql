ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS` DROP COLUMN MANDATORY;
ALTER TABLE `SYNDICATE_DATA_QUERY_INPUTS_AUDIT` DROP COLUMN MANDATORY;
ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS` DROP COLUMN MANDATORY;
ALTER TABLE `SYNDICATE_DATA_QUERY_OUTPUTS_AUDIT` DROP COLUMN MANDATORY;

alter table MAPPING_INPUT add TENANT_INTF_SYS_DEFINITION BLOB;
alter table MAPPING_INPUT_AUDIT add TENANT_INTF_SYS_DEFINITION BLOB;

