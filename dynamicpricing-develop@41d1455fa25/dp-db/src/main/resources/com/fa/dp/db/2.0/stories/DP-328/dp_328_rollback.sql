

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='select fc_inventory_flag, nrz_acquisition_dt from shahmayu.reo_portfolio_master where prop_temp = ? and nrz_acquisition_dt is not null and fc_inventory_flag in (1,2)' WHERE  `ATTR_KEY`='RR_CLASSIFICATION_QUERY';