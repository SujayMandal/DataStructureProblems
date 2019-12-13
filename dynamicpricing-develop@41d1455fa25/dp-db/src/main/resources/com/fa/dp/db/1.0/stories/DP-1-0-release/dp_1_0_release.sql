UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='NRZweekN'  WHERE  `ATTR_KEY`='nrz.weekNVacant.model.name';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='1'  WHERE  `ATTR_KEY`='nrz.weekNVacant.major.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`=''  WHERE  `ATTR_KEY`='nrz.weekNVacant.minor.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='RjGR6wKXC9OcIRmoV4FxTAnTDP2sGI7B7b1V7HgqDB+Ui7dDuiBGmGuFVThl5yaB'  WHERE  `ATTR_KEY`='nrz.weekN.dp.auth.token';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='nrz'  WHERE  `ATTR_KEY`='nrz.weekNVacant.tenant.code';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='1'  WHERE  `ATTR_KEY`='ocn.weekNVacant.major.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`=''  WHERE  `ATTR_KEY`='ocn.weekNVacant.minor.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='k+oRDQ7tYuD5oyq6HubRAbFrReewznGAzQFfQwPxZdMarkkvK5mO5sobLwRsW4H4'  WHERE  `ATTR_KEY`='ocn.weekN.dp.auth.token';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='https://avm.collateralanalytics.com/RequestHandler.aspx'  WHERE  `ATTR_KEY`='ca.url';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='altlabxmluser2'  WHERE  `ATTR_KEY`='ca.username';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='$AlsLb#209'  WHERE  `ATTR_KEY`='ca.password';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='nrz'  WHERE  `ATTR_KEY`='nrz.week0Vacant.tenant.code';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`=''  WHERE  `ATTR_KEY`='nrz.week0Vacant.minor.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='24'  WHERE  `ATTR_KEY`='ocn.week0Vacant.major.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`=''  WHERE  `ATTR_KEY`='ocn.week0Vacant.minor.version';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='k+oRDQ7tYuD5oyq6HubRAbFrReewznGAzQFfQwPxZdMarkkvK5mO5sobLwRsW4H4'  WHERE  `ATTR_KEY`='ocn.dp.auth.token';

UPDATE `RA_TNT_APP_PARAMS` SET `ATTR_VALUE`='RjGR6wKXC9OcIRmoV4FxTAnTDP2sGI7B7b1V7HgqDB+Ui7dDuiBGmGuFVThl5yaB'  WHERE  `ATTR_KEY`='nrz.dp.auth.token';

SELECT ID from RA_TNT_APPS WHERE LOWER(code) = 'dpa' INTO @APP_ID;

INSERT INTO `RA_TNT_APP_PARAMS` (`ID`, `RA_TNT_APP_ID`, `ATTR_KEY`, `ATTR_VALUE`, `CLASSIFICATION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES
	(UUID(), @APP_ID, 'ra.invocation.timeout', '30', NULL, 'SYSTEM', NOW(), NULL, NULL);

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='/usr/local/tomcat/sanpath'  WHERE  `SYS_KEY`='san.path';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='https://avm.collateralanalytics.com/RequestHandler.aspx'  WHERE  `SYS_KEY`='ca.url';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='altlabxmluser2'  WHERE  `SYS_KEY`='ca.username';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='$AlsLb#209'  WHERE  `SYS_KEY`='ca.password';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='https://realanalytics.modeloncloud.com/umg-admin'  WHERE  `SYS_KEY`='ra.admin.base.url';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='https://realanalytics.modeloncloud.com/umg-admin'  WHERE  `SYS_KEY`='ra.admin.url';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='https://realanalytics.modeloncloud.com/umg-runtime'  WHERE  `SYS_KEY`='ra.runtime.url';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='GKlFUr5VdZxQv4Bi8EeVuSJysNywaUUuJdk7qupQxl2aR9bPn6fNT3IiJfm1ZkOw'  WHERE  `SYS_KEY`='ocn.dp.auth.token';

UPDATE `RA_TNT_SYSTEM_PARAMETERS` SET `SYS_VALUE`='k+oRDQ7tYuD5oyq6HubRAbFrReewznGAzQFfQwPxZdMarkkvK5mO5sobLwRsW4H4'  WHERE  `SYS_KEY`='nrz.dp.auth.token';

INSERT INTO `RA_TNT_SYSTEM_PARAMETERS` (`ID`, `SYS_KEY`, `SYS_VALUE`, `DESCRIPTION`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES
	(UUID(), 'ra.invocation.timeout', '30', 'Time out value for RA invocation.', 'SYSTEM', NOW(), NULL, NULL);
	
INSERT INTO `PMI_INSURANCE_COMPANY_FILES` (`ID`, `UPLOADED_FILE_NAME`, `ACTIVE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES
	(UUID(), 'Insurance Companies.xlsx', 1, 'SYSTEM', NOW(), 'SYSTEM', NOW());

SELECT ID from PMI_INSURANCE_COMPANY_FILES WHERE UPLOADED_FILE_NAME='Insurance Companies.xlsx' INTO @FILE_ID;	
	
INSERT INTO `PMI_INSURANCE_COMPANIES` (`ID`, `PMI_INSURANCE_COMPANY_FILE_ID`, `COMPANY_CODE`, `INSURANCE_COMPANY`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) VALUES
	(UUID(), @FILE_ID, 'UGIC', 'United Guaranty', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'TGIC', 'TGIC', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'RMIC', 'RMIC', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'IMPAC', 'IMPAC', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'ARCHMI', 'ARCHMI', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'RGI', 'Radian Guaranty', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'MGIC', 'MGIC', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'UGRIC', 'UGRIC', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'TGJC', 'Triad Guaranty', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'PMI', 'PMI Mortgage Insurance', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'ESSENT', 'ESSENT', 'SYSTEM', NOW(), 'SYSTEM', NOW()),
	(UUID(), @FILE_ID, 'GE', 'Genworth Mortgage Insurance', 'SYSTEM', NOW(), 'SYSTEM', NOW());