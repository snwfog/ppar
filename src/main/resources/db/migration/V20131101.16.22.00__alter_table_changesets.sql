alter table changesets add hunk_id int;
alter table changesets add foreign key (hunk_id) references hunks (id) on delete cascade;