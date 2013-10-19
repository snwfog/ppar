ALTER TABLE person_tests DROP COLUMN lastName;
ALTER TABLE person_tests DROP COLUMN firstName;
ALTER TABLE person_tests ADD last_name VARCHAR(255);
ALTER TABLE person_tests ADD first_name VARCHAR(255);

ALTER TABLE child_tests DROP COLUMN childName;
ALTER TABLE child_tests ADD child_name VARCHAR(255);

ALTER TABLE grand_child_tests DROP COLUMN grandChildName;
ALTER TABLE grand_child_tests ADD grand_child_name VARCHAR(255);
