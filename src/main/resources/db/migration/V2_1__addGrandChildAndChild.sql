CREATE TABLE childs
(
	id int not null,
	childName varchar(255),
	primary key (id),
	FOREIGN KEY (id) REFERENCES persons(id)
	
);

CREATE TABLE grand_childs
(
	id int not null,
	grandChildName varchar(255),
	primary key (id),
	FOREIGN KEY (id) REFERENCES childs(id)
);