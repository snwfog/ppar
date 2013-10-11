ALTER TABLE childs DROP COLUMN childName;

ALTER TABLE grand_childs DROP COLUMN grandChildName;

ALTER TABLE childs ADD child_name varchar(255) AFTER id;

ALTER TABLE grand_childs ADD grand_child_name varchar(255) AFTER id;