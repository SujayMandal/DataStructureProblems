var stateCollections = db.getCollectionNames();
for(i = 0; i < stateCollections.length; i++) {
if(!(stateCollections[i]=='system.users'|| stateCollections[i]=='system.indexes' || stateCollections[i]=='notification_documents' || stateCollections[i].indexOf('MI') != -1 || stateCollections[i].indexOf('TI') != -1 || stateCollections[i].indexOf('MO') != -1 || stateCollections[i].indexOf('TO') != -1)){
print("deleting queued records for Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).remove({"status" : "Queued"});
print("deleted queued records for Collection is ===="+stateCollections[i])
print("deleting In Execution records for Collection is ===="+stateCollections[i])
db.getCollection(stateCollections[i]).remove({"status" : "In Execution"});
print("deleted In Execution records for Collection is ===="+stateCollections[i])
}
}