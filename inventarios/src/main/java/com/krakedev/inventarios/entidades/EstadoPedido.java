package com.krakedev.inventarios.entidades;

public class EstadoPedido {
	private int codigo;
	private String descripcion;

	public EstadoPedido() {
	}

	public EstadoPedido(int codigo, String descripcion) {
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	@Override
	public String toString() {
		return "EstadoPedido [codigo=" + codigo + ", descripcion=" + descripcion + "]";
	}

}
