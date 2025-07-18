package com.krakedev.inventarios.entidades;

public class UnidadDeMedida {
	private String nombre;
	private String descripcion;
	private UnidadDeMedida categoriaUnidadMedida;

	public UnidadDeMedida() {
	}

	public UnidadDeMedida(String nombre, String descripcion, UnidadDeMedida categoriaUnidadMedida) {
		this.nombre = nombre;
		this.descripcion = descripcion;
		this.categoriaUnidadMedida = categoriaUnidadMedida;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public UnidadDeMedida getCategoriaUnidadMedida() {
		return categoriaUnidadMedida;
	}

	public void setCategoriaUnidadMedida(UnidadDeMedida categoriaUnidadMedida) {
		this.categoriaUnidadMedida = categoriaUnidadMedida;
	}

	@Override
	public String toString() {
		return "UnidadDeMedida [nombre=" + nombre + ", descripcion=" + descripcion + ", categoriaUnidadMedida="
				+ categoriaUnidadMedida + "]";
	}

}
