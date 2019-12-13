-- in mongo prompt execute the below statements replace the ${tenantCode} with tenant name. For example : ocwen 

use ra_transaction_documents;
db.${tenantCode}_documents.createIndex( { "runAsOfDate": -1, "versionName": 1 },{ background: true } );
db.${tenantCode}_documents.createIndex( { "runAsOfDate": -1, "status": 1 },{ background: true } );
db.${tenantCode}_documents.dropIndex( { "runAsOfDate": 0},{ background: true } );
db.${tenantCode}_documents.createIndex( { "runAsOfDate": -1},{ background: true } );
db.${tenantCode}_documents.dropIndex( { "createdDate": 0},{ background: true } );
db.${tenantCode}_documents.createIndex( { "createdDate": -1},{ background: true } );
