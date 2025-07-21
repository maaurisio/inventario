package com.krakedev.inventarios.entidades;

import java.util.ArrayList;

public class Proveedor {
	private int identificador;
	private TipoDocumento tipoDocumento;
	private String nombre;
	private String telefono;
	private String correo;
	private String direccion;
	private ArrayList<DetallePedido> detalles = new ArrayList<>();

	public Proveedor() {
	}

	public Proveedor(int identificador, TipoDocumento tipoDocumento, String nombre, String telefono, String correo,
			String direccion, ArrayList<DetallePedido> detalles) {
		this.identificador = identificador;
		this.tipoDocumento = tipoDocumento;
		this.nombre = nombre;
		this.telefono = telefono;
		this.correo = correo;
		this.direccion = direccion;
		this.detalles = detalles;
	}
	public Proveedor(int identificador, TipoDocumento tipoDocumento, String nombre, String telefono, String correo,
			String direccion) {
		this.identificador = identificador;
		this.tipoDocumento = tipoDocumento;
		this.nombre = nombre;
		this.telefono = telefono;
		this.correo = correo;
		this.direccion = direccion;
	}

	public int getIdentificador() {
		return identificador;
	}

	public void setIdentificador(int identificador) {
		this.identificador = identificador;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getCorreo() {
		return correo;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public ArrayList<DetallePedido> getDetalles() {
		return detalles;
	}

	public void setDetalles(ArrayList<DetallePedido> detalles) {
		this.detalles = detalles;
	}

}
