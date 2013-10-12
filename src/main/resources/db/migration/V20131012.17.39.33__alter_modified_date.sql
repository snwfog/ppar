ALTER TABLE categories DROP COLUMN last_modified_date;

ALTER TABLE categories ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE changesets DROP COLUMN last_modified_date;

ALTER TABLE changesets ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE comments DROP COLUMN last_modified_date;

ALTER TABLE comments ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE cover_letters DROP COLUMN last_modified_date;

ALTER TABLE cover_letters ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE feedables DROP COLUMN last_modified_date;

ALTER TABLE feedables ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE groups DROP COLUMN last_modified_date;

ALTER TABLE groups ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE likes DROP COLUMN last_modified_date;

ALTER TABLE likes ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE peers DROP COLUMN last_modified_date;

ALTER TABLE peers ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE ranks DROP COLUMN last_modified_date;

ALTER TABLE ranks ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE resumes DROP COLUMN last_modified_date;

ALTER TABLE resumes ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE snapshots DROP COLUMN last_modified_date;

ALTER TABLE snapshots ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE taggables DROP COLUMN last_modified_date;

ALTER TABLE taggables ADD last_modified_date timestamp default '0000-00-00 00:00:00';

ALTER TABLE tag_descriptors DROP COLUMN last_modified_date;

ALTER TABLE tag_descriptors ADD last_modified_date timestamp default '0000-00-00 00:00:00';