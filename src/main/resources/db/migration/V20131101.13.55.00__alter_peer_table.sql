alter table peers drop column id_view;
alter table peers drop column semaphore;
alter table peers add semaphore int(1) default 0 not null;