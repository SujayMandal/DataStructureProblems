INSERT INTO SYSTEM_KEY(ID,SYSTEM_KEY,KEY_TYPE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('05a68810-e6f9-11e3-a68a-82687f4fc15c','DRIVER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be454a-e6f9-11e3-a68a-82687f4fc15c','URL','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be4a2c-e6f9-11e3-a68a-82687f4fc15c','SCHEMA','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be4fe0-e6f9-11e3-a68a-82687f4fc15c','USER','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('05be5350-e6f9-11e3-a68a-82687f4fc15c','PASSWORD','DATABASE','SYSTEM',1401344421,'SYSTEM',1401344421),
('74246ec4-fac2-11e3-801d-b2227cce2b54','URL','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421),
('742471a8-fac2-11e3-801d-b2227cce2b54','SOME','TENANT','SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO TENANT(ID,NAME,DESCRIPTION,CODE,TENANT_TYPE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('d6fae55c-e6fc-11e3-aab5-82687f4fc15c','Ocwen','Ocwen Financial Corporation','ocwen','both','SYSTEM',1401344421,'SYSTEM',1401344421),
('d6fae8d6-e6fc-11e3-aab5-82687f4fc15c','Equator','Equator','equator','both','SYSTEM',1401344421,'SYSTEM',1401344421),
('d6faeb06-e6fc-11e3-aab5-82687f4fc15c','Altisource','Altisource','altisource','both','SYSTEM',1401344421,'SYSTEM',1401344421),
('b8d75c34-e730-11e3-b953-82687f4fc15c','localhost','localhost','localhost','both','SYSTEM',1401344421,'SYSTEM',1401344421),
('42161aa6-e7d3-11e3-b99a-82687f4fc15c','blr-lt-f3xrvs1','blr-lt-f3xrvs1','blr-lt-f3xrvs1','both','SYSTEM',1401344421,'SYSTEM',1401344421);;


SELECT @tenant_ocwen:=(SELECT ID FROM TENANT WHERE CODE='ocwen');
SELECT @tenant_equator:=(SELECT ID FROM TENANT WHERE CODE='equator');
SELECT @tenant_altisource:=(SELECT ID FROM TENANT WHERE CODE='altisource');
SELECT @tenant_localhost:=(SELECT ID FROM TENANT WHERE CODE='localhost');
SELECT @tenant_blrltf3xrvs1:=(SELECT ID FROM TENANT WHERE CODE='blr-lt-f3xrvs1');

SELECT @system_key_db_driver:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='DRIVER');
SELECT @system_key_db_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='URL');
SELECT @system_key_db_schema:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='SCHEMA');
SELECT @system_key_db_user:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='USER');
SELECT @system_key_db_password:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='DATABASE' AND SYSTEM_KEY='PASSWORD');
SELECT @system_key_tenant_url:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='URL');
SELECT @system_key_tenant_some:=(SELECT ID FROM SYSTEM_KEY WHERE KEY_TYPE='TENANT' AND SYSTEM_KEY='SOME');

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('d60de758-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_driver,'com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dea46-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_url,'jdbc:mysql://localhost:3306/ocwen','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dec58-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_schema,'ocwen','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60dee42-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_user,'root','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df036-e6fb-11e3-aab5-82687f4fc15c',@tenant_ocwen,@system_key_db_password,'','SYSTEM',1401344421,'SYSTEM',1401344421),
('9f28f270-fac2-11e3-801d-b2227cce2b54',@tenant_ocwen,@system_key_tenant_url,'http://ocwen:6060/','SYSTEM',1401344421,'SYSTEM',1401344421),
('9f28f55e-fac2-11e3-801d-b2227cce2b54',@tenant_ocwen,@system_key_tenant_some,'test','SYSTEM',1401344421,'SYSTEM',1401344421);


INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('d60df22a-e6fb-11e3-aab5-82687f4fc15c',@tenant_equator,@system_key_db_driver,'com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df40a-e6fb-11e3-aab5-82687f4fc15c',@tenant_equator,@system_key_db_url,'jdbc:mysql://localhost:3306/equator','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df5fe-e6fb-11e3-aab5-82687f4fc15c',@tenant_equator,@system_key_db_schema,'equator','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df7e8-e6fb-11e3-aab5-82687f4fc15c',@tenant_equator,@system_key_db_user,'root','SYSTEM',1401344421,'SYSTEM',1401344421),
('d60df9d2-e6fb-11e3-aab5-82687f4fc15c',@tenant_equator,@system_key_db_password,'','SYSTEM',1401344421,'SYSTEM',1401344421),
('d04559c0-fac2-11e3-801d-b2227cce2b54',@tenant_equator,@system_key_tenant_url,'http://equator:6060/','SYSTEM',1401344421,'SYSTEM',1401344421),
('d0455d30-fac2-11e3-801d-b2227cce2b54',@tenant_equator,@system_key_tenant_some,'test','SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('1ff13bfe-e6fc-11e3-aab5-82687f4fc15c',@tenant_altisource,@system_key_db_driver,'com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421),
('1ff13ed8-e6fc-11e3-aab5-82687f4fc15c',@tenant_altisource,@system_key_db_url,'jdbc:mysql://localhost:3306/altisource','SYSTEM',1401344421,'SYSTEM',1401344421),
('1ff140e0-e6fc-11e3-aab5-82687f4fc15c',@tenant_altisource,@system_key_db_schema,'altisource','SYSTEM',1401344421,'SYSTEM',1401344421),
('1ff142ca-e6fc-11e3-aab5-82687f4fc15c',@tenant_altisource,@system_key_db_user,'root','SYSTEM',1401344421,'SYSTEM',1401344421),
('1ff144b4-e6fc-11e3-aab5-82687f4fc15c',@tenant_altisource,@system_key_db_password,'','SYSTEM',1401344421,'SYSTEM',1401344421),
('08ce4cde-fac3-11e3-801d-b2227cce2b54',@tenant_altisource,@system_key_tenant_url,'http://altisource:6060/','SYSTEM',1401344421,'SYSTEM',1401344421),
('08ce50b2-fac3-11e3-801d-b2227cce2b54',@tenant_altisource,@system_key_tenant_some,'test','SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('d4f48c66-e730-11e3-b953-82687f4fc15c',@tenant_localhost,@system_key_db_driver,'com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421),
('d4f48f5e-e730-11e3-b953-82687f4fc15c',@tenant_localhost,@system_key_db_url,'jdbc:mysql://localhost:3306/localhost','SYSTEM',1401344421,'SYSTEM',1401344421),
('d4f49166-e730-11e3-b953-82687f4fc15c',@tenant_localhost,@system_key_db_schema,'localhost','SYSTEM',1401344421,'SYSTEM',1401344421),
('d4f49350-e730-11e3-b953-82687f4fc15c',@tenant_localhost,@system_key_db_user,'root','SYSTEM',1401344421,'SYSTEM',1401344421),
('d4f4953a-e730-11e3-b953-82687f4fc15c',@tenant_localhost,@system_key_db_password,'','SYSTEM',1401344421,'SYSTEM',1401344421),
('1a099724-fac3-11e3-801d-b2227cce2b54',@tenant_localhost,@system_key_tenant_url,'http://localhost:6060/','SYSTEM',1401344421,'SYSTEM',1401344421),
('1a099a62-fac3-11e3-801d-b2227cce2b54',@tenant_localhost,@system_key_tenant_some,'test','SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO TENANT_CONFIG(ID,TENANT_ID,SYSTEM_KEY_ID,CONFIG_VALUE,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('421610ba-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,@system_key_db_driver,'com.mysql.jdbc.Driver','SYSTEM',1401344421,'SYSTEM',1401344421),
('421614c0-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,@system_key_db_url,'jdbc:mysql://localhost:3306/blr-lt-f3xrvs1','SYSTEM',1401344421,'SYSTEM',1401344421),
('421612cc-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,@system_key_db_schema,'blr-lt-f3xrvs1','SYSTEM',1401344421,'SYSTEM',1401344421),
('421616be-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,@system_key_db_user,'root','SYSTEM',1401344421,'SYSTEM',1401344421),
('421618b2-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,@system_key_db_password,'','SYSTEM',1401344421,'SYSTEM',1401344421),
('4dffa65e-fac3-11e3-801d-b2227cce2b54',@tenant_blrltf3xrvs1,@system_key_tenant_url,'http://blr-lt-f3xrvs1:6060/','SYSTEM',1401344421,'SYSTEM',1401344421),
('4dffa956-fac3-11e3-801d-b2227cce2b54',@tenant_blrltf3xrvs1,@system_key_tenant_some,'test','SYSTEM',1401344421,'SYSTEM',1401344421);

INSERT INTO ADDRESS(ID,TENANT_ID,ADDRESS_1,ADDRESS_2,CITY,STATE,ZIP,COUNTRY,CREATED_BY,CREATED_ON,LAST_UPDATED_BY,LAST_UPDATED_ON)
VALUES
('4bce84e2-e6fd-11e3-aab5-82687f4fc15c',@tenant_ocwen,'ocwen','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421),
('4bce87da-e6fd-11e3-aab5-82687f4fc15c',@tenant_equator,'equator','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421),
('4bce89e2-e6fd-11e3-aab5-82687f4fc15c',@tenant_altisource,'altisource','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421),
('fd18ad80-e730-11e3-b953-82687f4fc15c',@tenant_localhost,'localhost','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421),
('42160da4-e7d3-11e3-b99a-82687f4fc15c',@tenant_blrltf3xrvs1,'blr-lt-f3xrvs1','address2','city','state','zip','country','SYSTEM',1401344421,'SYSTEM',1401344421);

COMMIT;
