CREATE TABLE person_tests
(
  id        INT NOT NULL AUTO_INCREMENT,
  lastName  VARCHAR(255),
  firstName VARCHAR(255),
  email     VARCHAR(255),
  creation_date timestamp default '0000-00-00 00:00:00',
  last_modified_date timestamp default now() on update now(),
  PRIMARY KEY (id)
);

CREATE TABLE child_tests
(
  id        INT NOT NULL,
  childName VARCHAR(255),
  creation_date timestamp default '0000-00-00 00:00:00',
  last_modified_date timestamp default now() on update now(),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES person_tests (id)

);

CREATE TABLE grand_child_tests
(
  id             INT NOT NULL,
  grandChildName VARCHAR(255),
  creation_date timestamp default '0000-00-00 00:00:00',
  last_modified_date timestamp default now() on update now(),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES child_tests (id)
);