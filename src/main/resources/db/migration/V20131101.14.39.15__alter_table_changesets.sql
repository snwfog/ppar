alter table changesets add peer_id int;
alter table changesets add foreign key (peer_id) references peers (id) on delete cascade;