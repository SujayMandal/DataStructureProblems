USE `Ocwen`;

ALTER TABLE `BATCH_TRANSACTION` DROP COLUMN `EXECUTION_ENVIRONMENT`, DROP COLUMN `MODELLING_ENVIRONMENT`;

COMMIT;