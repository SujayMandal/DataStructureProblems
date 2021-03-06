-- `MODEL_DEFINITION`

ALTER TABLE `MODEL_DEFINITION`
	ADD COLUMN `Temp_IO_Def` MEDIUMBLOB NULL AFTER `IO_DEFINITION`;
	
update MODEL_DEFINITION set Temp_IO_Def=IO_DEFINITION ;

ALTER TABLE `MODEL_DEFINITION`
	DROP COLUMN `IO_DEFINITION`;
	
ALTER TABLE `MODEL_DEFINITION`
	CHANGE COLUMN `Temp_IO_Def` `IO_DEFINITION` MEDIUMBLOB NOT NULL COMMENT 'The actual input output defintion for the model.' AFTER `IO_TYPE`;
	

-- `MODEL_DEFINITION_AUDIT`

ALTER TABLE `MODEL_DEFINITION_AUDIT`
	ADD COLUMN `Temp_IO_Def` MEDIUMBLOB NULL AFTER `IO_DEFINITION`;
	
update MODEL_DEFINITION_AUDIT set Temp_IO_Def=IO_DEFINITION ;

ALTER TABLE `MODEL_DEFINITION_AUDIT`
	DROP COLUMN `IO_DEFINITION`;
	
ALTER TABLE `MODEL_DEFINITION_AUDIT`
	CHANGE COLUMN `Temp_IO_Def` `IO_DEFINITION` MEDIUMBLOB NOT NULL COMMENT 'The actual input output defintion for the model.' AFTER `IO_TYPE`;
	