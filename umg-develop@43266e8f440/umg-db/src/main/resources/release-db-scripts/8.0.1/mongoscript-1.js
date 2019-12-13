var stateCollections = db.getCollectionNames();
for(i = 0; i < stateCollections.length; i++) {
if(!(stateCollections[i]=='system.users'|| stateCollections[i]=='system.indexes' || stateCollections[i]=='notification_documents' || stateCollections[i].indexOf('MI') != -1 || stateCollections[i].indexOf('TI') != -1 || stateCollections[i].indexOf('MO') != -1 || stateCollections[i].indexOf('TO') != -1)){
print("creating unique index for Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).createIndex( { transactionId: 1 }, { unique: true } );
print("created unique index for Collection is ===="+stateCollections[i])
print("dropping index {clientTransactionId:1,runAsOfDate:-1} for  Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).dropIndex({clientTransactionId:1,runAsOfDate:-1});
print("dropped index {clientTransactionId:1,runAsOfDate:-1} for  Collection is ===="+stateCollections[i])
print("dropping index {transactionID:1,runAsOfDate:-1} for  Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).dropIndex({transactionID:1,runAsOfDate:-1});
print("dropped index {transactionID:1,runAsOfDate:-1} for  Collection is ===="+stateCollections[i])
print("creating index {Clnttranid-RunDate} for  Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).createIndex({clientTransactionID:1,runAsOfDate:-1},{name:"Clnttranid-RunDate"});
print("created index {Clnttranid-RunDate} for  Collection is ===="+stateCollections[i])
print("creating index {Tranid-RunDate} for  Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).createIndex({transactionId:1,runAsOfDate:-1},{name:"Tranid-RunDate"});
print("craeted index {Tranid-RunDate} for  Collection is ===="+stateCollections[i])
}
}