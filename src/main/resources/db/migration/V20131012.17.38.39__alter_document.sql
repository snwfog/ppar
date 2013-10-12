ALTER TABLE documents DROP COLUMN last_modified_date;

ALTER TABLE documents ADD last_modified_date TIMESTAMP DEFAULT now() ON UPDATE now();

ALTER TABLE documents DROP COLUMN creation_date;

ALTER TABLE documents ADD creation_date TIMESTAMP DEFAULT '0000-00-00 00:00:00';