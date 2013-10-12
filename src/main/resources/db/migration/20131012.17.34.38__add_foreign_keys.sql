ALTER TABLE peers ADD FOREIGN KEY (rank_id) REFERENCES ranks (id);

ALTER TABLE documents ADD FOREIGN KEY (peer_id) REFERENCES peers (id);

ALTER TABLE snapshots ADD FOREIGN KEY (document_id) REFERENCES documents (id);

ALTER TABLE comments ADD FOREIGN KEY (document_id) REFERENCES documents (id);

ALTER TABLE comments ADD FOREIGN KEY (peer_id) REFERENCES peers (id);

ALTER TABLE likes ADD FOREIGN KEY (peer_id) REFERENCES peers (id);

ALTER TABLE likes ADD FOREIGN KEY (comment_id) REFERENCES comments (id);

ALTER TABLE changesets ADD FOREIGN KEY (document_id) REFERENCES documents (id);
