create table avatars
(
	id int not null AUTO_INCREMENT,
	img_data BLOB,
	img_title varchar(256),
	peer_id int,
	PRIMARY KEY(id),
	FOREIGN KEY (peer_id) REFERENCES peers (id) ON DELETE set null
);