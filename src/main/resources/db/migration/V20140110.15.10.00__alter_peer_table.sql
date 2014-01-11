ALTER TABLE peers ADD avatar_id int;
ALTER TABLE peers ADD FOREIGN KEY (avatar_id) REFERENCES avatars (id) on delete set null;