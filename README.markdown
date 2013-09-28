Concordia University Capstone 2013
===

Active Record Library





DATABASE CONNECTION (from scratch)
-------------------
1. Download mysql and start mysql service locally.
2. Create a user, a test db, test table and test data as follow:
use app or using terminal: mysql

GRANT ALL PRIVILEGES ON  harry@localhost IDENTIFIED BY 'somepass' WITH GRANT OPTION;
create database harrydb;
use harrydb;
create table testdata(
id int not null auto_increment primary key,
foo varchar(25),
bar int);

3. Download the following: (or you can copy from web-inf/lib folder)
mysql-connector-java-5.1.26
jakarta-taglibs-standard-1.1.1 (standard.jar & jstl.jar)

4. Eclipse > new dynamic web project
add tomcat server to project
add mysql-connector.jar, standard.jar and jstl.jar as external library
and add them in web-inf/lib folder

5. In webcontent/web-inf folder create web.xml:
<web-app xmlns="http://java.sun.com/xml/ns/j2ee"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://java.sun.com/xml/ns/j2ee
http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd"
    version="2.4">
  <resource-ref>
      <res-ref-name>jdbc/harrydb</res-ref-name>
      <res-type>javax.sql.DataSource</res-type>
      <res-auth>Container</res-auth>
  </resource-ref>
</web-app>

In webcontent/meta-inf folder, create context.xml:
<Context>
	<Resource name="jdbc/harrydb" auth="Container"
		type="javax.sql.DataSource" maxActive="20" maxIdle="30"
		maxWait="10000" username="harry" password="harry"
		driverClassName="com.mysql.jdbc.Driver"
		url="jdbc:mysql://localhost:3306/harrydb" />
</Context>

run the jsp, should work