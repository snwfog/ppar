alter table peers add rank_id int;
ALTER TABLE peers ADD FOREIGN KEY (rank_id) REFERENCES ranks (id) on delete cascade;

alter table documents drop column class;
alter table documents add doc_type ENUM('resume','cover_letter');
