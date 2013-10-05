alter table peers ADD FOREIGN KEY(rank_id) REFERENCES ranks(id); 

alter table documents ADD FOREIGN KEY(peer_id) REFERENCES peers(id);

alter table snapshots ADD FOREIGN KEY(document_id) REFERENCES documents(id);

alter table comments ADD FOREIGN KEY(document_id) REFERENCES documents(id);

alter table comments ADD FOREIGN KEY(peer_id) REFERENCES peers(id);

alter table likes ADD FOREIGN KEY(peer_id) REFERENCES peers(id);

alter table likes ADD FOREIGN KEY(comment_id) REFERENCES comments(id);

alter table changesets ADD FOREIGN KEY(document_id) REFERENCES documents(id);
