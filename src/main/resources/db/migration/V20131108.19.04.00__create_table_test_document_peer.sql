CREATE TABLE peer_tests
(
	id int not null AUTO_INCREMENT,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255),
    user_name varchar(255),
    password varchar(255),
    point int,
    personal_website varchar(255),
    rank_id int,
    description varchar(256),
    semaphore int(1) default 0 not null,
    etag varchar(256),
    creation_date TIMESTAMP DEFAULT '0000-00-00 00:00:00',
	last_modified_date TIMESTAMP DEFAULT now() ON UPDATE now(),
    PRIMARY KEY(id)
);

CREATE TABLE document_tests
(
	id int not null AUTO_INCREMENT,
	doc_name varchar(255),
	class ENUM('resume','cover_letter'),
	creation_date TIMESTAMP DEFAULT '0000-00-00 00:00:00',
	last_modified_date TIMESTAMP DEFAULT now() ON UPDATE now(),
	thumbnail_path varchar(255),
	peer_test_id int,
	PRIMARY KEY(id),
	FOREIGN KEY (peer_test_id) REFERENCES peer_tests (id) ON DELETE CASCADE
);

 



