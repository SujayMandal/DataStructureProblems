cd %~dp0
mysql -u root < main.sql
echo Options: Press Enter to exit.
set /p answer=Press Enter to exit: