	USE umg_admin;
	
	ALTER TABLE ADDRESS MODIFY ADDRESS_1 VARCHAR(45) NOT NULL ; 
	ALTER TABLE ADDRESS MODIFY ADDRESS_2 VARCHAR(45) ;
	ALTER TABLE ADDRESS MODIFY CITY VARCHAR(45) NOT NULL;
	ALTER TABLE ADDRESS MODIFY STATE VARCHAR(45) NOT NULL;
	ALTER TABLE ADDRESS MODIFY ZIP VARCHAR(45) NOT NULL;
	ALTER TABLE ADDRESS MODIFY COUNTRY VARCHAR(45) NOT NULL;
	
	commit;