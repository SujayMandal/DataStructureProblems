create table SYND_REO_STIGMA(
                ID char(36) COLLATE utf8_bin NOT NULL,
                NAME_PRODUCT varchar(50) COLLATE utf8_bin NOT NULL, 
                DESCRIPTION varchar(200) COLLATE utf8_bin NOT NULL,
                CREATED_BY char(36) COLLATE utf8_bin NOT NULL,
                CREATED_ON bigint(20) NOT NULL,
                LAST_UPDATED_BY char(36) COLLATE utf8_bin DEFAULT NULL,
                LAST_UPDATED_ON bigint(20) DEFAULT NULL,
                PRIMARY KEY (`ID`));

create table SYND_REO_MSACODE(
                ID char(36) COLLATE utf8_bin NOT NULL,
                NAME_PRODUCT varchar(50) COLLATE utf8_bin NOT NULL, 
                DESCRIPTION varchar(200) COLLATE utf8_bin NOT NULL,
                CREATED_BY char(36) COLLATE utf8_bin NOT NULL,
                CREATED_ON bigint(20) NOT NULL,
                LAST_UPDATED_BY char(36) COLLATE utf8_bin DEFAULT NULL,
                LAST_UPDATED_ON bigint(20) DEFAULT NULL,
                PRIMARY KEY (`ID`));
				
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626600','ResoStigma','ResoStigma Container', '1', 'SYND_REO_STIGMA',1362114000000,1372651140000,'Admin', 1359694800000,'Admin', 1359694800000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626601','ResoStigma','ResoStigma Container', '2', 'SYND_REO_STIGMA',1372651200000,1380599940000,'Admin', 1371700800000,'Admin', 1371700800000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626602','ResoStigma','ResoStigma Container', '3', 'SYND_REO_STIGMA',1380600000000,1388552340000,'Admin', 1379217600000,'Admin', 1379217600000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626603','ResoStigma','ResoStigma Container', '4', 'SYND_REO_STIGMA',1388552400000,1393649940000,'Admin', 1386824400000,'Admin', 1386824400000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626604','ResoStigma','ResoStigma Container', '5', 'SYND_REO_STIGMA',1393650000000,1398916740000,'Admin', 1392008400000,'Admin', 1392008400000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626605','ResoStigma','ResoStigma Container', '6', 'SYND_REO_STIGMA',1398916800000,1412049600000,'Admin', 1396324800000,'Admin', 1396324800000,1);


insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626606','MsaCodes','MsaCodes Container', '1', 'SYND_REO_MSACODE',1362114000000,1372651140000,'Admin', 1359694800000,'Admin', 1359694800000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626607','MsaCodes','MsaCodes Container', '2', 'SYND_REO_MSACODE',1372651200000,1380599940000,'Admin', 1371700800000,'Admin', 1371700800000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626608','MsaCodes','MsaCodes Container', '3', 'SYND_REO_MSACODE',1380600000000,1388552340000,'Admin', 1379217600000,'Admin', 1379217600000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626609','MsaCodes','MsaCodes Container', '4', 'SYND_REO_MSACODE',1388552400000,1393649940000,'Admin', 1386824400000,'Admin', 1386824400000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626610','MsaCodes','MsaCodes Container', '5', 'SYND_REO_MSACODE',1393650000000,1398916740000,'Admin', 1392008400000,'Admin', 1392008400000,1);
insert into SYNDICATED_DATA values('bb888f56-7101-48eb-a12b-a4457e626611','MsaCodes','MsaCodes Container', '6', 'SYND_REO_MSACODE',1398916800000,1412049600000,'Admin', 1396324800000,'Admin', 1396324800000,1);

commit;




