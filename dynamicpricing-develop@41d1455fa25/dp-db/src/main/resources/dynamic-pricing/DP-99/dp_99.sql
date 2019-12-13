

DELETE FROM `RA_TNT_APP_PARAMS` WHERE `ATTR_KEY` = 'RTNG_QUERY';

SELECT ID from `RA_TNT_APPS` WHERE LOWER(code) = 'dpa' INTO @APP_ID;
INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CREATED_BY`, `CREATED_ON`) VALUES (UUID(), @APP_ID, 'RTNG_QUERY', 'SELECT Loan_Number,Property_Id,Vendor_Order_Nbr,Order_Ingestion_Date,Order_Created_Date,Product_Type,Vendor_Order_Status,Investor_Code,Investor_Name,Vendor_Fulfilled_Date,Vendor_Address1,Vendor_Address2,Vendor_City,Vendor_State,Vendor_Zip,Property_Address1,Property_Address2,Property_City,Property_State,Property_Zip,Current_Review_High,Current_Review_Low,Review_Mid_Value,Property_Type,As_Is_Low,As_Is_High,Property_Condition,Site_Size,GLA,Room_Count,Bathroom_Count,Bedroom_Count,Total_Room_Count,Age,Design,Repair_Low,Repair_High,Suggest_Low,Suggest_High,Suggest_Repair_Low,Suggest_Repair_High,Repair_Amount1,Repair_Amount2,Repair_Amount3,Repair_Amount4,Repair_Amount5,Repair_Amount6,Repair_Amount7,Repair_Amount8,Repair_Amount_Total,Is_WSTRAT,Repaired_Costs,Order_Approved_Date,Review_Repair_Low,Review_Repair_High,Review_Repair_Mid,Action_Comments,FAIR_MARKET_VALUE FROM RPT_DYNMC_PRCNG where Loan_Number = ? and Review_Mid_Value is not NULL order by Order_Created_Date desc', 'SYSTEM', NOW());

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = null WHERE `ATTR_KEY` = 'nrz.pricemode.input';
UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE` = null WHERE `ATTR_KEY` = 'ocn.pricemode.input';
