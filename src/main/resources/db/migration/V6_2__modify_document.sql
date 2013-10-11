ALTER TABLE documents DROP COLUMN last_modified_date;

ALTER TABLE documents ADD last_modified_date timestamp default now() on update now();

ALTER TABLE documents DROP COLUMN creation_date;

ALTER TABLE documents ADD creation_date timestamp default now();