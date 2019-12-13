cd %~dp0
mysql -u root < create_tenant_schema.sql
mysql -u root < main.sql
echo Options: Press Enter to exit.
set /p answer=Press Enter to exit: