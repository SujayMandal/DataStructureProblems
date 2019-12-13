# following script is used for generating data dictionary for tables and view from a schema(s)


# Create a temporary table to store comments for the common columns used across tables
create temporary table if not exists temp_column_comments (
	column_name varchar(64) not null,
	column_comment varchar(254) not null);

truncate temp_column_comments;

# Insert comments for common columns
insert into temp_column_comments values ('CREATED_BY', 'User created the record');
insert into temp_column_comments values ('CREATED_ON', 'Record created time');
insert into temp_column_comments values ('LAST_UPDATED_BY', 'User last updated the record');
insert into temp_column_comments values ('LAST_UPDATED_ON', 'Record last updated time');
insert into temp_column_comments values ('DATA_TYPE', 'Syndicate Data Query Type');
insert into temp_column_comments values ('DEACTIVATED_BY', 'User deactivated the record');
insert into temp_column_comments values ('DEACTIVATED_ON', 'Record last deactivated time');
insert into temp_column_comments values ('EVENT_ID', 'Logging Event ID');
insert into temp_column_comments values ('PUBLISHED_BY', 'User published the record');
insert into temp_column_comments values ('PUBLISHED_ON', 'Record last publishedmapping_audit time');
insert into temp_column_comments values ('REV', 'Revision Information ID');
insert into temp_column_comments values ('REVTYPE', 'Revision Type');
insert into temp_column_comments values ('TENANT_ID', 'Tenant ID for the record');


# create a temporary table to store table constraints and populate for all columns in a schema
create temporary table if not exists temp_table_constraints
select 
	kcu.TABLE_SCHEMA as 'TABLE_SCHEMA',
	kcu.TABLE_NAME as 'TABLE_NAME', 
	kcu.COLUMN_NAME as 'COLUMN_NAME', 
	group_concat(kcu.CONSTRAINT_NAME) as 'CONSTRAINT_NAME', 
	group_concat(tc.CONSTRAINT_TYPE) as 'CONSTRAINT_TYPE'
from information_schema.key_column_usage as kcu
	inner join information_schema.table_constraints as tc
		on kcu.CONSTRAINT_NAME = tc.CONSTRAINT_NAME
		and kcu.TABLE_NAME = tc.TABLE_NAME
		and kcu.TABLE_SCHEMA = tc.TABLE_SCHEMA
#		and kcu.TABLE_NAME = 'dx_document'
where kcu.TABLE_SCHEMA = 'davxotdrnq02'			# Schema Name
GROUP BY kcu.COLUMN_NAME;


# Final result set (Data Dictionary for a schema)
select 
	t.TABLE_SCHEMA as 'Database Name',
	t.TABLE_NAME as 'Table Name',
	
	(case 
		when t.TABLE_TYPE = 'BASE TABLE' 
			then 'Table'
		when t.TABLE_TYPE = 'VIEW' then 'View'
			else t.TABLE_TYPE
	end) as 'Table Type',
	
	c.COLUMN_NAME as 'Column Name',
	
	# below case logic to get only column type and skips column length.
	# for example, return just varchar from varchar(45) column type and skips (45) 
	(case 
		when LOCATE('(', c.COLUMN_TYPE) > 0 
			then SUBSTRING(c.COLUMN_TYPE, 1, (LOCATE('(', c.COLUMN_TYPE) - 1))
			else c.COLUMN_TYPE
	end) as 'Column Type',
	
	# below case logic to get only column length from column type - just reverse to above logic
	# for example, return just 45 from varchar(45) column type and skips varchar
	(case 
		when LOCATE('(', c.COLUMN_TYPE) > 0 and LOCATE(')', c.COLUMN_TYPE) > 0   
			then SUBSTRING(c.COLUMN_TYPE, (LOCATE('(', c.COLUMN_TYPE) - LOCATE(')', c.COLUMN_TYPE)), (LOCATE(')', c.COLUMN_TYPE) - LOCATE('(', c.COLUMN_TYPE) - 1))
			else ""
	end) as 'Column Length',

	# logic to get Constraint Type for example PRIMARY KEY, FOREIGN KEY
	(case 
		when ttc.CONSTRAINT_TYPE is not null
			then ttc.CONSTRAINT_TYPE
			else ""
	end) as 'Constraint Type',
	
	# logic to get user defined name of contraint
	(case 
		when ttc.CONSTRAINT_NAME is not null
			then ttc.CONSTRAINT_NAME
			else ""
	end) as 'Constraint Name',

	c.IS_NULLABLE as 'Nullable',
	
	# logic to get comment for all columns which don't have comment in table defination 
	(case 
		when c.COLUMN_COMMENT is null or c.COLUMN_COMMENT = ''
			then 
				(case
					when tcc.column_comment is not null
						then tcc.column_comment
						else ''
				end)
			else c.COLUMN_COMMENT
	end) as 'Comment',
	
	c.COLUMN_DEFAULT as 'Default Value',
	c.ORDINAL_POSITION as 'ORDINAL_POSITION'
from information_schema.TABLES as t
	inner join information_schema.COLUMNS as c
		on t.TABLE_NAME = c.TABLE_NAME
		and t.TABLE_SCHEMA = c.TABLE_SCHEMA
	left outer join temp_table_constraints ttc
		on t.table_name = ttc.TABLE_NAME
		and t.TABLE_SCHEMA = ttc.TABLE_SCHEMA
		and c.COLUMN_NAME = ttc.COLUMN_NAME
	left outer join temp_column_comments tcc
		on c.COLUMN_NAME = tcc.column_name
where t.TABLE_TYPE in('base table', 'view')			# only consider table and view
	and t.TABLE_SCHEMA = 'davxotdrnq02'				# schename name
	and t.TABLE_NAME not like 'synd\\_%'			# skip some tables
#	and t.TABLE_NAME = 'dx_document'
order by t.TABLE_SCHEMA, t.TABLE_NAME, c.ORDINAL_POSITION;


# Drop temporary tables
drop temporary table temp_column_comments;
drop temporary table temp_table_constraints;