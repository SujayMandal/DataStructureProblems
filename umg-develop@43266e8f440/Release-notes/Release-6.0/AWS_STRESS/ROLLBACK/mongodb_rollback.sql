use ra_transaction_documents;
db.ocwen_documents.dropIndex( { transactionId: 1 } )
db.ocwen_documents.createIndex( { transactionId: 1 } );

db.equator_documents.dropIndex( { transactionId: 1 } )
db.equator_documents.createIndex( { transactionId: 1 } );