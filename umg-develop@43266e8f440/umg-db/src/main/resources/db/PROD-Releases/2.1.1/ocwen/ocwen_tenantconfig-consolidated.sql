use umg_admin;
INSERT INTO TENANT(ID,NAME,DESCRIPTION,CODE,TENANT_TYPE,AUTH_TOKEN,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('1e67984e-f212-4fd2-8cbe-92ecae5d2a5d','Ocwen','Ocwen Financial Corporation','Ocwen','both','cBt/PQa4YTajb4dO+LNurq/oWcQn+bu8ew5QHwUcdJKr0yFBCy3rqrYiaNG/KBLg','SYSTEM',1401344421,'SYSTEM',1401344421);
-- Tenant auth code --> 1e67984e-f212-4fd2-8cbe-92ecae5d2a5d

SELECT @tenant_ocwen:=(SELECT ID FROM TENANT WHERE CODE='Ocwen');

SELECT @system_key_db_driver:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='DRIVER');
SELECT @system_key_db_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='URL');
SELECT @system_key_db_schema:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='SCHEMA');
SELECT @system_key_db_user:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='USER');
SELECT @system_key_db_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='PASSWORD');
SELECT @system_key_tenant_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='URL');
SELECT @system_key_tenant_some:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='SOME');


SELECT @system_key_db_driver_val:='com.mysql.jdbc.Driver';
SELECT @system_key_db_url_val:='jdbc:mysql://10.52.90.142:3306/Ocwen';
SELECT @system_key_db_schema_val:='Ocwen';
SELECT @system_key_db_user_val:='umgprod';
SELECT @system_key_db_password_val:='Prod#2014';
SELECT @system_key_tenant_url_val:='http://10.52.90.133';
SELECT @system_key_tenant_some_val:='test';

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('d60de758-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_driver,@system_key_db_driver_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dea46-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_url,@system_key_db_url_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dec58-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_schema,@system_key_db_schema_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dee42-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_user,@system_key_db_user_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df036-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_password,@system_key_db_password_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('9f28f270-fac2-11e3-801d-b2227cce2b54',@tenant_ocwen,@system_key_tenant_url,@system_key_tenant_url_val,'SYSTEM',1401344421,'SYSTEM',1401344421),
('9f28f55e-fac2-11e3-801d-b2227cce2b54',@tenant_ocwen,@system_key_tenant_some,@system_key_tenant_some_val,'SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO ADDRESS(ID,TENANT_ID,ADDRESS_1,ADDRESS_2,CITY,STATE,ZIP,COUNTRY,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('4bce84e2-e6fd-11e3-aab5-82687f4fc15c',@tenant_ocwen,'Ocwen','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421);

COMMIT;
