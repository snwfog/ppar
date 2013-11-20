Concordia University Capstone 2013
===

Active Record Library

## Description
PPAR (PeerPen Active Record) is an active record library inspired by
Rails' active record pattern built from scratch in order to accomodate
the evaluation criteria of the Capstone project 2013. PPAR does not
depend on database, but on annotation that are placed inside of the
model. PPAR satisfies *most* of the commonly used interface of the
active record pattern. Furthermore, PPAR is lazy loaded, and uses
optimistic offline locking. Therefore, it imposes a few default fields
that must be present in the database, and these are `id`, `created_at`, `updated_at`, `etag`, and `semaphore`.

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
