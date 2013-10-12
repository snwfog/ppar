create table peers (
    id int not null AUTO_INCREMENT,
    first_name varchar(255),
    last_name varchar(255),
    email varchar(255),
    user_name varchar(255),
    password varchar(255),
    point int,
    personal_website varchar(255),
    rank_id int,
    PRIMARY KEY(id)
);

create table ranks(
	id int not null AUTO_INCREMENT,
	rank_name varchar(255),
	min_point int,
	PRIMARY KEY(id)
);

create table documents(
	id int not null AUTO_INCREMENT,
	doc_name varchar(255),
	polymorphic_id int not null,
	class ENUM('resume','cover_letter'),
	creation_date DATETIME default null,
	last_modified_date timestamp on update current_timestamp default current_timestamp,
	thumbnail_path varchar(255),
	peer_id int,
	PRIMARY KEY(id)
);

create table resumes(
	id int not null AUTO_INCREMENT,
	PRIMARY KEY(id)
);

create table cover_letters(
	id int not null AUTO_INCREMENT,
	PRIMARY KEY(id)
);

create table snapshots(
	id int not null AUTO_INCREMENT,
	document_id int,
	PRIMARY KEY(id)
);

create table groups(
	id int not null AUTO_INCREMENT,
	group_name varchar(255),
	description varchar(255),
	PRIMARY KEY(id)
);

create table peers_groups(
	peer_id int not null REFERENCES peers(id),
	group_id int not null REFERENCES groups(id),
	PRIMARY KEY(peer_id,group_id)
);

create table categories(
	id int not null AUTO_INCREMENT,
	category_name varchar(255),
	description varchar(255),
	PRIMARY KEY(id)
);

create table groups_categories(
	category_id int not null REFERENCES categories(id),
	group_id int not null REFERENCES groups(id),
	PRIMARY KEY(group_id,category_id)
);

create table tag_descriptors(
	id int not null AUTO_INCREMENT,
	tag_name varchar(255),
	PRIMARY KEY(id)
);

create table taggables(
	id int not null AUTO_INCREMENT,
	polymorphic_id int not null,
	class ENUM('group','document'),
	PRIMARY KEY(id)
);

create table tags(
	tag_descriptor_id int not null REFERENCES tag_descriptors(id),
	taggable_id int not null REFERENCES taggables(id),
	PRIMARY KEY(tag_descriptor_id,taggable_id)
);

create table comments(
	id int not null AUTO_INCREMENT,
	message varchar(255),
	creation_date DATETIME default null,
	document_id int,
	peer_id int,
	PRIMARY KEY(id) 
);

create table likes(
 	id int not null AUTO_INCREMENT,
 	peer_id int,
 	comment_id int,
 	PRIMARY KEY(id)
);

create table changesets(
	id int not null AUTO_INCREMENT,
	document_id int,
	PRIMARY KEY(id)
);

create table feedables(
	id int not null AUTO_INCREMENT,
	polymorphic_id int not null,
	class ENUM('changeset','like','comment'),
	PRIMARY KEY(id)
);




















