create database airline_booking_system;

create table user (
  id int(11) auto_increment not null primary key,
  username varchar(255) not null,
  password varchar(255) not null,
  role enum('ADMIN', 'USER') not null default 'USER');