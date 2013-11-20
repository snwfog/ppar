alter table childs add semaphore int(1) default 0 not null;
alter table childs add etag varchar(256);

alter table child_tests add semaphore int(1) default 0 not null;
alter table child_tests add etag varchar(256);

alter table persons add semaphore int(1) default 0 not null;
alter table persons add etag varchar(256);

alter table person_tests add semaphore int(1) default 0 not null;
alter table person_tests add etag varchar(256);

alter table grand_childs add semaphore int(1) default 0 not null;
alter table grand_childs add etag varchar(256);

alter table grand_child_tests add semaphore int(1) default 0 not null;
alter table grand_child_tests add etag varchar(256);
