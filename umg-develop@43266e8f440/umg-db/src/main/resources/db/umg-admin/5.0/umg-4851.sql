use umg_admin;


INSERT INTO SYSTEM_KEY (`ID`, `SYSTEM_KEY`, `KEY_TYPE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('05be454a-e6f9-11e3-a68a-82687f4fcx15', 'maxIdleTime', 'DATABASE', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');


INSERT INTO TENANT_CONFIG (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('fac86e54-3dce-4996-ac90-61c9b04b1d7X', '849d65e4-3b49-11e5-a151-feff819cdc9f', '05be454a-e6f9-11e3-a68a-82687f4fcx15', '28200', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');

INSERT INTO TENANT_CONFIG (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('fac86e54-3dce-4996-ac90-61c9b04b1d7Y', '84fe0bdd-5045-4625-9904-ad102f52ab78', '05be454a-e6f9-11e3-a68a-82687f4fcx15', '28200', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');

INSERT INTO TENANT_CONFIG (`ID`, `TENANT_ID`, `SYSTEM_KEY_ID`, `CONFIG_VALUE`, `CREATED_BY`, `CREATED_ON`, `LAST_UPDATED_BY`, `LAST_UPDATED_ON`) 
VALUES ('fac86e54-3dce-4996-ac90-61c9b04b1d7X', 'd6fae55c-e6fc-11e3-aab5-82687f4fc15c', '05be454a-e6f9-11e3-a68a-82687f4fcx15', '28200', 'SYSTEM', '1401344421', 'SYSTEM', '1401344421');


COMMIT;