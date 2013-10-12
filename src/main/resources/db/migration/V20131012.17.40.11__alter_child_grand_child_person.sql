ALTER TABLE persons DROP COLUMN last_modified_date;

ALTER TABLE persons ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE childs DROP COLUMN last_modified_date;

ALTER TABLE childs ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE grand_childs DROP COLUMN last_modified_date;

ALTER TABLE grand_childs ADD last_modified_date timestamp default '0000-00-00 00:00:00';