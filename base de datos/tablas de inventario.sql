-- DROP en orden inverso a la dependencia
DROP TABLE IF EXISTS detalle_ventas;
DROP TABLE IF EXISTS historial_stock;
DROP TABLE IF EXISTS cabecera_ventas;
DROP TABLE IF EXISTS detalle_pedido;
DROP TABLE IF EXISTS cabecera_pedido;
DROP TABLE IF EXISTS estado_pedido;
DROP TABLE IF EXISTS productos;
DROP TABLE IF EXISTS unidades_de_medida;
DROP TABLE IF EXISTS categorias_unidad_medida;
DROP TABLE IF EXISTS categorias;
DROP TABLE IF EXISTS proveedores;
DROP TABLE IF EXISTS tipo_documentos;

-- 1. Crear tabla categorias
CREATE TABLE categorias (
    codigo_categoria SERIAL NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    categoria_padre INT,
    CONSTRAINT categorias_pk PRIMARY KEY (codigo_categoria),
    CONSTRAINT categorias_fk FOREIGN KEY (categoria_padre)
        REFERENCES categorias (codigo_categoria)
);

-- Insertar datos en categorias
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Materia Prima', null);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Proteina', 1);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Salsas', 1);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Punto de Venta', null);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Bebidas', 4);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Con alcohol', 5);
INSERT INTO categorias (nombre, categoria_padre) VALUES ('Sin alcohol', 5);

-- 2. Crear tabla categorias_unidad_medida
CREATE TABLE categorias_unidad_medida (
    codigo_categoria_udm CHAR(1) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    CONSTRAINT codigo_categoria_um_pk PRIMARY KEY (codigo_categoria_udm)
);

-- Insertar datos en categorias_unidad_medida
INSERT INTO categorias_unidad_medida (codigo_categoria_udm, nombre) VALUES ('U', 'Unidades');
INSERT INTO categorias_unidad_medida (codigo_categoria_udm, nombre) VALUES ('V', 'Volumen');
INSERT INTO categorias_unidad_medida (codigo_categoria_udm, nombre) VALUES ('P', 'Peso');

-- 3. Crear tabla unidades_de_medida
CREATE TABLE unidades_de_medida (
    nombre VARCHAR(50) PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL,
    categoria_udm CHAR(1) NOT NULL,
    CONSTRAINT categoria_udm_fk FOREIGN KEY (categoria_udm)
        REFERENCES categorias_unidad_medida (codigo_categoria_udm)
);

-- Insertar datos en unidades_de_medida
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('ml', 'mililitros', 'V');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('l', 'litros', 'V');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('u', 'unidad', 'U');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('d', 'docena', 'U');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('g', 'gramos', 'P');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('kg', 'kilogramos', 'P');
INSERT INTO unidades_de_medida (nombre, descripcion, categoria_udm) VALUES ('lb', 'libras', 'P');

select * from unidades_de_medida;

-- 4. Crear tabla productos
CREATE TABLE productos (
    codigo_producto SERIAL NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    unidad_de_medida VARCHAR(50) NOT NULL,
    precio_venta DECIMAL(10, 2) NOT NULL,
    tiene_iva BOOLEAN NOT NULL,
    coste DECIMAL(10, 2) NOT NULL,
    categoria INT NOT NULL,
	stock int not null,
    CONSTRAINT codigo_producto_pk PRIMARY KEY (codigo_producto),
    CONSTRAINT unidad_de_medida_fk FOREIGN KEY (unidad_de_medida)
        REFERENCES unidades_de_medida(nombre),
    CONSTRAINT categoria_fk FOREIGN KEY (categoria)
        REFERENCES categorias(codigo_categoria)
);

-- Insertar datos en productos
INSERT INTO productos (
    nombre, unidad_de_medida, precio_venta, tiene_iva, coste, categoria, stock
) VALUES (
    'Coca Cola', 'u', 12.50, TRUE, 9.30, 1, 25
);
--5.
create table tipo_documentos (
codigo char(1) not null,
descripcion varchar(20),
constraint codigo_pk primary key (codigo)
);
--insertar de tipo_documentos
insert into tipo_documentos (codigo, descripcion)
values ('C', 'CEDULA');
insert into tipo_documentos (codigo, descripcion)
values ('R', 'RUC');
--6.
create table proveedores(
identificador int not null,
tipo_documento char (1) not null,
nombre varchar(50) not null,
telefono int not null,
correo varchar (50) not null,
direccion varchar (50) not null,
constraint identificador_pk primary key (identificador),
constraint tipo_documento_fk foreign key (tipo_documento)
references tipo_documentos (codigo)
);

--insertar de proveedores
insert into proveedores (identificador, tipo_documento, nombre, telefono, correo, direccion)
values (1728632256, 'C', 'Mauricio', 0981318987, 'mau@gmail.com', 'condado');

select * from proveedores;
------------------------------------------------
---7.
create table estado_pedido(
codigo_estado_pedido char (1) not null,
descripcion varchar(20),
constraint codigo_estado_p_pk primary key (codigo_estado_pedido)
);
--insertar de estado_pedido
insert into estado_pedido (codigo_estado_pedido, descripcion)
values ('S', 'Solicitado');
insert into estado_pedido (codigo_estado_pedido, descripcion)
values ('R', 'Recibido');
---8.
create table cabecera_pedido(
numero serial not null,
proveedor int not null,
fecha timestamp not null, 
estado char (1) not null,
constraint numero_pk primary key (numero),
constraint proveedor_fk foreign key (proveedor)
references proveedores (identificador),
constraint estado_fk foreign key (estado)
references estado_pedido (codigo_estado_pedido)
);
-- CABECERAS DE PEDIDO
INSERT INTO cabecera_pedido (proveedor, fecha, estado)
VALUES 
(1728632256, '2025-07-15 10:30:00', 'S'),
(1728632256, '2025-07-16 14:15:00', 'R'),
(1728632256, '2025-07-17 09:00:00', 'S');
SELECT * FROM cabecera_pedido;

--9.
create table detalle_pedido(
codigo_detalle_pedido serial not null,
cabecera_pedido int not null,
producto int not null,
cantidad_solicitada int not null,
subtotal DECIMAL(10, 2) NOT NULL,
cantidad_recibida int not null,
constraint codigo_detalle_pedido_pk primary key (codigo_detalle_pedido),
constraint cabecera_pedido_fk foreign key (cabecera_pedido)
references cabecera_pedido (numero),
constraint producto_fk foreign key (producto)
references productos (codigo_producto)
);
-- Pedido 1
INSERT INTO detalle_pedido (cabecera_pedido, producto, cantidad_solicitada, subtotal, cantidad_recibida)
VALUES 
(1, 1, 10, 125.00, 0);
-- Pedido 2
INSERT INTO detalle_pedido (cabecera_pedido, producto, cantidad_solicitada, subtotal, cantidad_recibida)
VALUES 
(2, 1, 5, 62.50, 5);

-- Pedido 3
INSERT INTO detalle_pedido (cabecera_pedido, producto, cantidad_solicitada, subtotal, cantidad_recibida)
VALUES 
(3, 1, 20, 250.00, 10);

SELECT * FROM detalle_pedido;

--10.
create table cabecera_ventas(
codigo_cabecera_ventas serial not null,
fecha timestamp not null, 
total_sin_iva DECIMAL(10, 2) NOT NULL,
iva DECIMAL(5,2) not null,
total int not null,
constraint codigo_cabecera_ventas_pk primary key (codigo_cabecera_ventas)
);
-- Insert 1
--NOW() ES PARA CAPTURAR LA FECHA Y HORA ACTUAL
INSERT INTO cabecera_ventas (fecha, total_sin_iva, iva, total)
VALUES (NOW(), 25.00, 3.00, 28);

-- Insert 2
INSERT INTO cabecera_ventas (fecha, total_sin_iva, iva, total)
VALUES (NOW(), 50.00, 6.00, 56);

-- Insert 3
INSERT INTO cabecera_ventas (fecha, total_sin_iva, iva, total)
VALUES (NOW(), 100.00, 12.00, 112);

--11.
create table detalle_ventas(
codigo_detalle_ventas serial not null,
cabecera_ventas int not null,
productos int not null,
cantidad int not null,
precio_venta DECIMAL(5,2) not null,
subtotal DECIMAL(5,2) not null,
subtotal_con_iva DECIMAL(5,2) not null,
constraint codigo_detalle_ventas_pk primary key (codigo_detalle_ventas),
constraint cabecera_ventas_fk foreign key (cabecera_ventas)
references cabecera_ventas (codigo_cabecera_ventas),
constraint productos_detalle_ventas_fk foreign key (productos)
references productos (codigo_producto)
);
-- Insert 1 (detalle de la primera venta)
INSERT INTO detalle_ventas (cabecera_ventas, productos, cantidad, precio_venta, subtotal, subtotal_con_iva)
VALUES (1, 1, 2, 12.50, 25.00, 28.00);

-- Insert 2
INSERT INTO detalle_ventas (cabecera_ventas, productos, cantidad, precio_venta, subtotal, subtotal_con_iva)
VALUES (2, 1, 4, 12.50, 50.00, 56.00);

-- Insert 3
INSERT INTO detalle_ventas (cabecera_ventas, productos, cantidad, precio_venta, subtotal, subtotal_con_iva)
VALUES (3, 1, 8, 12.50, 100.00, 112.00);

--12.
create table historial_stock(
codigo_historial_stock serial not null,
fecha timestamp not null, 
referencia varchar(50) not null,
productos int not null,
cantidad int not null,
constraint codigo_historial_stock_pk primary key (codigo_historial_stock),
constraint productos_historial_stock_fk foreign key (productos)
references productos (codigo_producto)
);

-- Insert 1
INSERT INTO historial_stock (fecha, referencia, productos, cantidad)
VALUES (NOW(), 'Venta 1', 1, -2);

-- Insert 2
INSERT INTO historial_stock (fecha, referencia, productos, cantidad)
VALUES (NOW(), 'Venta 2', 1, -4);

-- Insert 3
INSERT INTO historial_stock (fecha, referencia, productos, cantidad)
VALUES (NOW(), 'Venta 3', 1, -8);

/*
select * from historial_stock;

select prov.identificador, prov.tipo_documento,td.descripcion, prov.nombre, prov.telefono, prov.correo, prov.direccion
from proveedores prov, tipo_documentos td
where prov.tipo_documento = td.codigo
and upper(nombre) like '%A%'

ALTER TABLE proveedores 
ALTER COLUMN telefono TYPE VARCHAR(15);
*/

select prod.codigo_producto, prod.nombre AS nombre_producto, udm.nombre AS numbre_udm, udm.descripcion AS descripcion_udm,
prod.precio_venta, prod.tiene_iva, prod.coste, prod.categoria, cat.nombre AS nombre_categoria, stock
from productos prod, unidades_de_medida udm, categorias cat
where prod.unidad_de_medida = udm.nombre
and prod.categoria = cat.codigo_categoria
and upper (prod.nombre) like '%C%';