

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='SELECT LOANNUMBER, REVIEWDT,APPRTYP,ASISLOWMKTVAL,ASISMIDMKTVAL,CONDITIONCDE,LIVINGAREA,TOTREPAIRAMT FROM STAGE5.ST_MORTAPRQ WHERE LOANNUMBER =? AND PROPNBR = ? ORDER BY REVIEWDT DESC' WHERE  `ATTR_KEY`='STAGE5_QUERY';


	
