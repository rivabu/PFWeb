rem mvn clean install -Pcargo 
cd E:\trading\workspace\PFWebGit\PFWeb
rmdir cargo /r
call mvn clean install -DskipTests -Pcargo
call mvn cargo:run -Pcargo
