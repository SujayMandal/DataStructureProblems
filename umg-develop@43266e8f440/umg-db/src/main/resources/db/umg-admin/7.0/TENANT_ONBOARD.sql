	USE umg_admin;
	
	ALTER TABLE ADDRESS MODIFY ADDRESS_1 VARCHAR(200) NOT NULL ; 
	ALTER TABLE ADDRESS MODIFY ADDRESS_2 VARCHAR(200) ;
	ALTER TABLE ADDRESS MODIFY CITY VARCHAR(200) NOT NULL;
	ALTER TABLE ADDRESS MODIFY STATE VARCHAR(200) NOT NULL;
	ALTER TABLE ADDRESS MODIFY ZIP VARCHAR(200) NOT NULL;
	ALTER TABLE ADDRESS MODIFY COUNTRY VARCHAR(200) NOT NULL;
	
	commit;