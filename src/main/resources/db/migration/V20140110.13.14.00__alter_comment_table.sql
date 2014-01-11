ALTER TABLE comments ADD changeset_id int;
ALTER TABLE comments ADD FOREIGN KEY (changeset_id) REFERENCES changesets (id) on delete set null;