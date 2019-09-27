--liquibase formatted sql
--changeset diaitskov:r1.0.001

create table festival(
    fid int(11) not null auto_increment primary key,
    state varchar(10) not null, -- pending, open, closed
    opens_at timestamp(3) not null,
    name varchar(90) not null unique,
    menu text,
    created timestamp(3) default current_timestamp(3)
);

create table users(
    uid int(11) not null auto_increment primary key,
    festival_id int(11) not null,
    session_key varchar(40) not null,
    name varchar(40) not null,  -- Magda
    type varchar(10) not null,  -- kelner, admin, kasier, cook, customer
    state varchar(10) not null, -- pending, approved, banned
    created timestamp(3) default current_timestamp(3),
    foreign key (festival_id) references festival(fid)
);

create table token(
    tid int(11) not null primary key,
    festival_id int(11) not null,
    amount int(11) not null,
    customer_id int(11) not null,
    kasier_id int(11) not null,
    created timestamp(3) default current_timestamp(3),
    foreign key (festival_id) references festival(fid),
    foreign key (customer_id) references users(uid),
    foreign key (kasier_id) references users(uid)
);

create table orders(
    oid int(11) not null auto_increment primary key,
    label int(11) not null,
    festival_id int(11) not null,
    kelner_id int(11) null,
    kasier_id int(11) null,
    token_id int(11) null,
    spent_tokens int(11) not null,
    customer_id int(11) not null,
    requirements text not null, -- json
    created timestamp(3) default current_timestamp(3),
    state varchar(10) not null, -- sent, paid, ready, handed, cancelled, returned
    foreign key (customer_id) references users(uid),
    foreign key (token_id) references token(tid),
    foreign key (festival_id) references festival(fid),
    foreign key (kasier_id) references users(uid),
    foreign key (kelner_id) references users(uid)
);

create table labels (
    festival_id int(11) not null,
    label int(11) not null,
    oid int(11) null,
    created timestamp(3) default current_timestamp(3),
    foreign key (oid) references orders(oid),
    foreign key (festival_id) references festival(fid),
    primary key (festival_id, label)
);

--rollback
