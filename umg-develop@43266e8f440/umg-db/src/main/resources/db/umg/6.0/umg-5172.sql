use localhost;

ALTER TABLE BATCH_TRANSACTION ADD COLUMN TRANSACTION_MODE VARCHAR(10) NOT NULL DEFAULT 'Batch';

commit;
