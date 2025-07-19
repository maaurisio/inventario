package com.krakedev.inventarios.entidades;

import java.util.ArrayList;
import java.util.Date;

public class CabeceraPedido {
	private int codigo;
	private Proveedor proveedor;
	private Date fecha;
	private EstadoPedido estado;
	
	private ArrayList<DetallePedido> detalles;

	public CabeceraPedido() {
	}

	public CabeceraPedido(int codigo, Proveedor proveedor, Date fecha, EstadoPedido estado) {
		this.codigo = codigo;
		this.proveedor = proveedor;
		this.fecha = fecha;
		this.estado = estado;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public int getCodigo() {
		return codigo;
	}

	public void setCodigo(int codigo) {
		this.codigo = codigo;
	}

	public Proveedor getProveedor() {
		return proveedor;
	}

	public void setProveedor(Proveedor proveedor) {
		this.proveedor = proveedor;
	}

	public EstadoPedido getEstado() {
		return estado;
	}

	public void setEstado(EstadoPedido estado) {
		this.estado = estado;
	}
	

	public ArrayList<DetallePedido> getDetalles() {
		return detalles;
	}

	public void setDetalles(ArrayList<DetallePedido> detalles) {
		this.detalles = detalles;
	}

	@Override
	public String toString() {
		return "CabeceraPedido [codigo=" + codigo + ", proveedor=" + proveedor + ", estado=" + estado + "]";
	}

}
