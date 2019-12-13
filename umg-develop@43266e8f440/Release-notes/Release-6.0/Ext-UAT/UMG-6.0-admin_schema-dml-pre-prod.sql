use umg_admin;


SET SQL_SAFE_UPDATES = 0;

update POOL set PRIORITY = 101 where ID = 15;
update POOL set PRIORITY = 102 where ID = 100;
update POOL set PRIORITY = 103 where ID = 101;
update POOL set PRIORITY = 104 where ID = 2;
update POOL set PRIORITY = 105 where ID = 3;
update POOL set PRIORITY = 106 where ID = 7;
update POOL set PRIORITY = 107 where ID = 8;
update POOL set PRIORITY = 108 where ID = 4;


update POOL set PRIORITY = 202 where ID = 1;

commit;

update POOL set PRIORITY = 1 where ID = 15;
update POOL set PRIORITY = 2 where ID = 100;
update POOL set PRIORITY = 3 where ID = 101;
update POOL set PRIORITY = 4 where ID = 5;
update POOL set PRIORITY = 5 where ID = 3;
update POOL set PRIORITY = 6 where ID = 7;
update POOL set PRIORITY = 7 where ID = 8;
update POOL set PRIORITY = 8 where ID = 4;

update POOL set PRIORITY = 1 where ID = 1;

commit;