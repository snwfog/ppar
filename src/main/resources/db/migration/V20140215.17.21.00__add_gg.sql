create table g_c(
	c_id int not null REFERENCES categories(id),
	g_id int not null REFERENCES groups(id),
	PRIMARY KEY(c_id,g_id)
);