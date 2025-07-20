package com.krakedev.inventarios.servicios;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.krakedev.inventarios.bdd.CabeceraVentasBDD;
import com.krakedev.inventarios.entidades.CabeceraVenta;
import com.krakedev.inventarios.excepciones.KrakeDevException;

@Path("ventas")

public class ServicioCabeceraVenta {
	@Path("guardar")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response crear(CabeceraVenta cabVenta) {
		CabeceraVentasBDD cabVenBdd = new CabeceraVentasBDD();
		try {
			cabVenBdd.insertar(cabVenta);
			return Response.ok().build();
		} catch (KrakeDevException e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}
}
