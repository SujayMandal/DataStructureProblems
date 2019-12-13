cd %~dp0
mysql -u root < update_tenant_schema.sql
echo Options: Press Enter to exit.
set /p answer=Press Enter to exit: