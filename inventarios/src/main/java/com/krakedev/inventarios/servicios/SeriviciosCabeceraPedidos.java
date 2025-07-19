package com.krakedev.inventarios.servicios;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.krakedev.inventarios.bdd.CabeceraPedidoBDD;
import com.krakedev.inventarios.entidades.CabeceraPedido;
import com.krakedev.inventarios.excepciones.KrakeDevException;

@Path("cabPedidos")
public class SeriviciosCabeceraPedidos {
	@Path("crear")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crear(CabeceraPedido cabPedido) {
		CabeceraPedidoBDD cabPedBdd = new CabeceraPedidoBDD();
		try {
			cabPedBdd.insertar(cabPedido);
			return Response.ok().build();
		} catch (KrakeDevException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}

	}
	@Path("recibir")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	public Response actualizarYrecibir(CabeceraPedido cabPedido) {
		CabeceraPedidoBDD cabPedBdd = new CabeceraPedidoBDD();
		try {
			cabPedBdd.actualizarYrecibirPedido(cabPedido);
			return Response.ok().build();
		} catch (KrakeDevException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
}
