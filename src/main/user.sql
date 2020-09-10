create database airline_booking_system;

-- Table: User
create table user
(
	id int auto_increment,
	username char(255) not null,
	password char(255) not null,
	role enum("ADMIN", "USER") default 'USER' not null,
	constraint user_pk
		primary key (id)
);

create unique index user_username_uindex
	on user (username);

-- Table Customer Info
create table customer_info
(
	id int not null,
	name char(255) not null,
	birth_date char(11) not null,
	gender enum("male", "female") not null,
	constraint customer_info_pk
		primary key (id),
	constraint customer_info_user_id_fk
		foreign key (id) references user (id)
			on delete cascade
);

