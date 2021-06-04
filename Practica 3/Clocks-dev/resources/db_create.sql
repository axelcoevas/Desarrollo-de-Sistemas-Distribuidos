drop database if exists libreria;
create database libreria;
use libreria;

create table libro (
	ISBN bigint primary key,
    nombre nvarchar(1024) not null,
    autor nvarchar(1024) not null,
    editorial nvarchar(1024) not null,
    precio decimal(16, 2) not null,
    portada nvarchar(1024) not null
);

create table usuario (
	idUsuario int auto_increment primary key,
    ip nvarchar(39) not null
);

create table sesion(
	idSesion int auto_increment primary key,
    fechaInicio datetime not null,
    fechaFin datetime
);

create table pedido (
	idLibro bigint not null,
    idUsuario int not null,
    idSesion int not null,
    fecha datetime not null,
    primary key (idLibro, idUsuario, idSesion),
    foreign key (idLibro) references libro(ISBN) on delete cascade,
	foreign key (idUsuario) references usuario(idUsuario)on delete cascade,
	foreign key (idSesion) references sesion(idSesion)on delete cascade
);

insert into libro values (8437507219, "Psicológicamente hablando", "Adrián Triglia, Bertrand Regader", "Paidos", 345, "psicologicamente.jpg");
insert into libro values (6073193025, "Historia de dos Cuidades", "Charles Dickens", "Editorial Juventud", 432, "historia.jpg");
insert into libro values (6073139110, "Guerra y Paz", " León Tolstói", "Miel Libro", 253, "guerra.jpg");
insert into libro values (6070728793, "La insoportable levedad del ser", "Milan Kudera", "Maxi Tusquets", 512.3, "insoportable.jpg");

delimiter //
drop procedure if exists spNuevaSesion//
create procedure spNuevaSesion(in inicioDate datetime)
begin
	insert into sesion(fechaInicio) values (inicioDate);
    select MAX(idSesion) from sesion;
end;//

drop procedure if exists spNuevoLibroCliente//
create procedure spNuevoLibroCliente(in ipCliente nvarchar(39), in libroId bigint, in sesionId int, in fechaPedido datetime)
begin
	declare idPersona int;
    set idPersona = (select idUsuario from usuario where ip = ipCliente);
    if idPersona is null then
		insert into usuario(ip) values (ipCliente);
		set idPersona = (select max(idUsuario) from usuario);
	end if;
    insert into pedido values (libroId, idPersona, sesionId, fechaPedido);
    
    select sesionId;
end;//

delimiter ;