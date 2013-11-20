create table hunks
(
  id INT NOT NULL AUTO_INCREMENT,
  id_view varchar(256),
  document_id int,
  content text,
  creation_date timestamp default '0000-00-00 00:00:00',
  last_modified_date timestamp default now() on update now(),
  PRIMARY KEY (id),
  FOREIGN KEY(document_id) REFERENCES documents(id) ON DELETE set null
);
