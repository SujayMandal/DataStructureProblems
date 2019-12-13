use umg_admin;

SET SQL_SAFE_UPDATES = 0;

UPDATE POOL SET `MODELET_COUNT`=2 WHERE ID=15;

delete from POOL_USAGE_ORDER where pool_id in (7, 8);
delete from POOL_CRITERIA_DEF_MAPPING where pool_id in (7, 8);
delete from POOL where id in (7, 8);

commit;