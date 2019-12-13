source create_tenant_schema_cloud.sql;

source tables/REVINFO.sql;
source tables/SYSTEM_KEY/SYSTEM_KEY.sql;
source tables/TENANT/TENANT.sql
source tables/TENANT_CONFIG/TENANT_CONFIG.sql;
source tables/ADDRESS/ADDRESS.sql;
source tables/MODEL_IMPLEMENTATION_TYPE/MODEL_IMPLEMENTATION_TYPE.sql;
source tables/SYNDICATED_DATA/SYNDICATED_DATA.sql;

use umg_admin;
source data/all_delete.sql;
source data/system_data_cloud_prod.sql;