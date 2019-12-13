ALTER TABLE tenant_config ADD  `ROLE` CHAR(20) NULL DEFAULT NULL;

CREATE TABLE SYSTEM_PARAMETER(
			ID CHAR(36),
			SYS_KEY VARCHAR(100) ,
			SYS_VALUE VARCHAR(100),
			IS_ACTIVE CHAR(1),
			CREATED_BY VARCHAR(100),
			CREATED_ON BIGINT(20),
			LAST_UPDATED_BY VARCHAR(100),
			LAST_UPDATED_ON BIGINT(20),
			PRIMARY KEY(ID)
);
CREATE TABLE SYSTEM_PARAMETER_AUDIT(
			ID CHAR(36),
			SYS_KEY VARCHAR(100) ,
			SYS_VALUE VARCHAR(100),
			IS_ACTIVE CHAR(1),
			CREATED_BY VARCHAR(100),
			CREATED_ON BIGINT(20),
			LAST_UPDATED_BY VARCHAR(100),
			LAST_UPDATED_ON BIGINT(20),
			REV INT(11),
			REVTYPE INT(11),
			PRIMARY KEY(ID,REV)
);