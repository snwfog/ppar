ALTER TABLE categories ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE categories ADD last_modified_date timestamp default now() on update now();

ALTER TABLE changesets ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE changesets ADD last_modified_date timestamp default now() on update now();

ALTER TABLE comments DROP COLUMN creation_date;

ALTER TABLE comments ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE comments ADD last_modified_date timestamp default now() on update now();

ALTER TABLE cover_letters ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE cover_letters ADD last_modified_date timestamp default now() on update now();

ALTER TABLE feedables ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE feedables ADD last_modified_date timestamp default now() on update now();

ALTER TABLE groups ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE groups ADD last_modified_date timestamp default now() on update now();

ALTER TABLE likes ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE likes ADD last_modified_date timestamp default now() on update now();

ALTER TABLE peers ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE peers ADD last_modified_date timestamp default now() on update now();

ALTER TABLE ranks ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE ranks ADD last_modified_date timestamp default now() on update now();

ALTER TABLE resumes ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE resumes ADD last_modified_date timestamp default now() on update now();

ALTER TABLE snapshots ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE snapshots ADD last_modified_date timestamp default now() on update now();

ALTER TABLE taggables ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE taggables ADD last_modified_date timestamp default now() on update now();

ALTER TABLE tag_descriptors ADD creation_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE tag_descriptors ADD last_modified_date timestamp default now() on update now();