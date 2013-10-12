ALTER TABLE grand_childs ADD creation_date timestamp default '0000-00-00 00:00:00';
ALTER TABLE grand_childs ADD last_modified_date timestamp default now() on update now();

ALTER TABLE childs ADD creation_date timestamp default '0000-00-00 00:00:00';
ALTER TABLE childs ADD last_modified_date timestamp default now() on update now();

ALTER TABLE persons ADD creation_date timestamp default '0000-00-00 00:00:00';
ALTER TABLE persons ADD last_modified_date timestamp default now() on update now();