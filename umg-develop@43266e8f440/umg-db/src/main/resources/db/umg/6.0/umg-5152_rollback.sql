use localhost;

ALTER TABLE UMG_VERSION DROP COLUMN MODEL_TYPE;

ALTER TABLE UMG_VERSION_AUDIT DROP COLUMN MODEL_TYPE;

commit;