create table if not exists students
(
    id         bigint       not null auto_increment,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    email      varchar(255) not null,
    primary key (id)
);