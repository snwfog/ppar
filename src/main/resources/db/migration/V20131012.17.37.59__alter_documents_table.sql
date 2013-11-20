ALTER TABLE documents DROP FOREIGN KEY documents_ibfk_1;

ALTER TABLE documents ADD FOREIGN KEY(peer_id) REFERENCES peers(id) on delete set null;