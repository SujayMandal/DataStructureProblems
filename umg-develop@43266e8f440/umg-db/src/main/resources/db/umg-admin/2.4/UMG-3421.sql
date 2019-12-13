use admin;

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('3528F744-40A1-4779-89C6-9B5595E9D089', 'validateRManifestFile', 'Validating of Manifest file', 1, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);

INSERT INTO `COMMAND` (`ID`, `NAME`, `DESCRIPTION`, `EXECUTION_SEQUENCE`, `PROCESS`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES ('3528F744-40A1-4779-89C6-9B5595E9D019', 'convertExcelToXml', 'Conversion of Excel to Xml', 4, 'CREATE', 'SYSTEM', 12354856456, NULL, NULL);

UPDATE COMMAND SET EXECUTION_SEQUENCE=5 WHERE NAME='validateModelIOXml';
UPDATE COMMAND SET EXECUTION_SEQUENCE=6 WHERE NAME='symanticCheckModelIOXml';
UPDATE COMMAND SET EXECUTION_SEQUENCE=7 WHERE NAME='createModel';
UPDATE COMMAND SET EXECUTION_SEQUENCE=8 WHERE NAME='createMapping';
UPDATE COMMAND SET EXECUTION_SEQUENCE=9 WHERE NAME='createVersion';
UPDATE COMMAND SET EXECUTION_SEQUENCE=10 WHERE NAME='generateTestInput';
UPDATE COMMAND SET EXECUTION_SEQUENCE=11 WHERE NAME='testVersion';
UPDATE COMMAND SET EXECUTION_SEQUENCE=2 WHERE NAME='validateLibraryChecksum';
UPDATE COMMAND SET EXECUTION_SEQUENCE=3 WHERE NAME='createModelLibrary';