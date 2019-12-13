

UPDATE `COMMAND` SET `DESCRIPTION`='Classification Mismatch' WHERE `NAME`='week0RRClassification';
UPDATE `COMMAND` SET `DESCRIPTION`='Duplicate Asset' WHERE `NAME`='week0DuplicateFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='SS Investor' WHERE `NAME`='week0InvestorCodeFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Unsupported Asset Value' WHERE `NAME`='week0AssetValueFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Failed Data Fetch' WHERE `NAME`='week0RRRtngAggregator';
UPDATE `COMMAND` SET `DESCRIPTION`='Unsupported Property Type' WHERE `NAME`='week0PropertyTypeFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Model Failure' WHERE `NAME`='week0RAInputPayload';

UPDATE `COMMAND` SET `DESCRIPTION`='Data Fetch Failure' WHERE `NAME`='weekNFetchData';
UPDATE `COMMAND` SET `DESCRIPTION`='Active Listing' WHERE `NAME`='weekNActiveListingsFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Past 12 Cycles' WHERE `NAME`='weekNPast12CyclesFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Odd Listing' WHERE `NAME`='weekNOddListingsFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Benchmark' WHERE `NAME`='weekNAssignmentFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Unsupported State/Zip' WHERE `NAME`='weekNZipStateFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='SS & PMI' WHERE `NAME`='weekNSSPmiFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='SOP' WHERE `NAME`='weekNSOPFilter';
UPDATE `COMMAND` SET `DESCRIPTION`='Model Failure' WHERE `NAME`='weekNRAIntegrarion';
