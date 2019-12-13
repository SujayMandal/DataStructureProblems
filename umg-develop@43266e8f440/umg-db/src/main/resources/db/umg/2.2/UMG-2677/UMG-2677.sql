create index batch_txn_runtime_txn_mapping_index_2 USING BTREE ON batch_txn_runtime_txn_mapping (TRANSACTION_ID);

create index umg_version_index_1 USING BTREE ON umg_version (MODEL_LIBRARY_ID);