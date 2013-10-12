ALTER TABLE persons DROP COLUMN lastname, DROP COLUMN firstname;

ALTER TABLE persons ADD last_name varchar(255) AFTER id;

ALTER TABLE persons ADD first_name varchar(255) AFTER last_name;