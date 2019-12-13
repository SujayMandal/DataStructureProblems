use umg_admin;

DROP TABLE IF EXISTS SYSTEM_MODELETS;

CREATE TABLE `SYSTEM_MODELETS` (
                `HOST_NAME` CHAR(36) NOT NULL,
                `PORT` INT(10) NOT NULL,
                `ENVIRONMENT` VARCHAR(20) NOT NULL,
                `MEMBER_HOST` VARCHAR(36) NOT NULL,
                PRIMARY KEY (`HOST_NAME`, `PORT`)
)
COMMENT='Stores all modelet configurations'
COLLATE='latin1_swedish_ci'
ENGINE=InnoDB;
