Concordia University Capstone 2013
===

Active Record Library





### DATABASE CONNECTION (intellij maven project)

This is the new way to connect under maven project. It doesnt need xml files

1. make sure your local mysql service is started and you have the following:
database: harrydb
user:harry
pass:harry
table: testdata
col: id (int)
col: foo (varchar)
col: bar (int)

(or change above in DBConnection settings to your own db info)

2. add mysql-connector-java-5.1.26 to project as external library

3. run the dbconnectiontest, should output "foo bar"