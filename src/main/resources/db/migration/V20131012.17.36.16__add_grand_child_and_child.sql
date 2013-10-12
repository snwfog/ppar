CREATE TABLE childs
(
  id        INT NOT NULL,
  childName VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES persons (id)

);

CREATE TABLE grand_childs
(
  id             INT NOT NULL,
  grandChildName VARCHAR(255),
  PRIMARY KEY (id),
  FOREIGN KEY (id) REFERENCES childs (id)
);