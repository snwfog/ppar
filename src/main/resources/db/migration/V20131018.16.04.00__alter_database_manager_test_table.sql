ALTER TABLE database_manager_test ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE database_manager_test ADD last_modified_date timestamp default now() on update now();
