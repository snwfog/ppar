alter table peers add date_of_birth date default '0000-00-00';
alter table peers add country varchar(256);
alter table peers add industry varchar(256);
alter table peers add experience int;