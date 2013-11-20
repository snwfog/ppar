ALTER TABLE documents DROP FOREIGN KEY documents_ibfk_1;

ALTER TABLE documents ADD FOREIGN KEY(peer_id) REFERENCES peers(id) ON DELETE CASCADE;

ALTER TABLE snapshots DROP FOREIGN KEY snapshots_ibfk_1;

ALTER TABLE snapshots ADD FOREIGN KEY(document_id) REFERENCES documents(id) ON DELETE CASCADE;

ALTER TABLE changesets DROP FOREIGN KEY changesets_ibfk_1;

ALTER TABLE changesets ADD FOREIGN KEY(document_id) REFERENCES documents(id) ON DELETE set null;

ALTER TABLE resumes ADD FOREIGN KEY(id) REFERENCES documents(id) ON DELETE CASCADE;

ALTER TABLE cover_letters ADD FOREIGN KEY(id) REFERENCES documents(id) ON DELETE CASCADE;

