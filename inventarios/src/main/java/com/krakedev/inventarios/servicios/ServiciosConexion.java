package com.krakedev.inventarios.servicios;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Path("conexion")
public class ServiciosConexion {
	@GET
	@Path("verificar")
	@Produces(MediaType.TEXT_PLAIN)
	public Response verificarConexion() {
	    String url = "jdbc:postgresql://localhost:5432/inventario";
	    String user = "postgres";
	    String password = "maurisio";

	    try (Connection conn = DriverManager.getConnection(url, user, password)) {
	        if (conn != null && !conn.isClosed()) {
	            return Response.ok("✅ Conexión exitosa a la base de datos.").build();
	        } else {
	            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                    .entity("⚠️ No se pudo establecer la conexión.").build();
	        }
	    } catch (SQLException e) {
	        e.printStackTrace(); // Esto imprime el error real en la consola
	        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
	                .entity("❌ Error al conectar: " + e.getMessage()).build();
	    }
	}

}
