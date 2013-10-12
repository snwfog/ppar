CREATE TABLE documents_taggables(
	document_id int not null REFERENCES documents(id),
	taggable_id int not null REFERENCES taggables(id),
	PRIMARY KEY(taggable_id,document_id)
);