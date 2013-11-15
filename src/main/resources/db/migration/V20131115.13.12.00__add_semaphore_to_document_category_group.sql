alter table documents add semaphore int(1) default 0 not null;
alter table documents add etag varchar(256);

alter table categories add semaphore int(1) default 0 not null;
alter table categories add etag varchar(256);

alter table groups add semaphore int(1) default 0 not null;
alter table groups add etag varchar(256);