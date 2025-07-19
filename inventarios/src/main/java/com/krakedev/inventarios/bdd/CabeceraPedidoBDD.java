package com.krakedev.inventarios.bdd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.krakedev.inventarios.entidades.CabeceraPedido;
import com.krakedev.inventarios.entidades.DetallePedido;
import com.krakedev.inventarios.excepciones.KrakeDevException;
import com.krakedev.inventarios.utils.ConexionBDD;

public class CabeceraPedidoBDD {
	public void insertar(CabeceraPedido cabPedido) throws KrakeDevException {
		Connection con = null;
		PreparedStatement ps = null;
		PreparedStatement psDet = null;
		ResultSet rsClave = null;
		int codigoCabecera = 0;

		Date fechaActual = new Date();
		long tiempoSinMilis = (fechaActual.getTime() / 1000) * 1000; // Elimina los milisegundos
		java.sql.Timestamp timestampSinMilis = new java.sql.Timestamp(tiempoSinMilis);

		try {
			con = ConexionBDD.obtenerConexion();
			ps = con.prepareStatement("INSERT INTO cabecera_pedido (proveedor, fecha, estado)\r\n" + "VALUES (?,?,?);",
					Statement.RETURN_GENERATED_KEYS);
			ps.setInt(1, cabPedido.getProveedor().getIdentificador());
			// Usar timestampSinMilis en lugar de fechaSQL
			ps.setTimestamp(2, timestampSinMilis); // ✅ ahora guarda fecha y hora
			ps.setString(3, "S");// Solicitado
			ps.executeUpdate();

			rsClave = ps.getGeneratedKeys();
			if (rsClave.next()) {
				codigoCabecera = rsClave.getInt(1);
			}

			// obtener la lista
			ArrayList<DetallePedido> detallesPedido = cabPedido.getDetalles();
			DetallePedido det;
			for (int i = 0; i < detallesPedido.size(); i++) {
				det = detallesPedido.get(i);
				psDet = con.prepareStatement(
						"INSERT INTO detalle_pedido (cabecera_pedido, producto, cantidad_solicitada, subtotal, cantidad_recibida)\r\n"
								+ "VALUES (?,?,?,?,?)");
				psDet.setInt(1, codigoCabecera);
				psDet.setInt(2, det.getProducto().getCodigo());
				psDet.setInt(3, det.getCantidadSolicitada());

				BigDecimal pv = det.getProducto().getPrecioVenta();
				BigDecimal cantidad = new BigDecimal(det.getCantidadSolicitada());
				BigDecimal subtotal = pv.multiply(cantidad);
				psDet.setBigDecimal(4, subtotal);
				psDet.setInt(5, 0);
				psDet.executeUpdate();
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al insertar producto. Detalle: " + e.getMessage());
		} catch (KrakeDevException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void actualizarYrecibirPedido(CabeceraPedido cabPedido) throws KrakeDevException {
	    Connection con = null;
	    PreparedStatement psCabecera = null;
	    PreparedStatement psDetalle = null;

	    try {
	        con = ConexionBDD.obtenerConexion();

	        // 1) Actualizar estado de la cabecera_pedido a 'R' (Recibido)
	        String sqlActualizarCabecera = "UPDATE cabecera_pedido SET estado = 'R' WHERE numero = ?";
	        psCabecera = con.prepareStatement(sqlActualizarCabecera);
	        psCabecera.setInt(1, cabPedido.getCodigo());
	        int filasActualizadas = psCabecera.executeUpdate();

	        if (filasActualizadas == 0) {
	            throw new KrakeDevException("No se encontró la cabecera con código: " + cabPedido.getCodigo());
	        }

	        // 2) Actualizar cada detalle relacionado con esta cabecera, producto y detalle específico
	        String sqlActualizarDetalle = ("UPDATE detalle_pedido\r\n"
	        		+ "            SET cantidad_recibida = ?, subtotal = ?\r\n"
	        		+ "            WHERE codigo_detalle_pedido = ? AND cabecera_pedido = ? AND producto = ?");

	        psDetalle = con.prepareStatement(sqlActualizarDetalle);

	        for (DetallePedido det : cabPedido.getDetalles()) {
	            int cantidadRecibida = det.getCantidadRecibida();
	            BigDecimal precioVenta = det.getProducto().getPrecioVenta();
	            BigDecimal subtotal = precioVenta.multiply(BigDecimal.valueOf(cantidadRecibida));

	            psDetalle.setInt(1, cantidadRecibida);
	            psDetalle.setBigDecimal(2, subtotal);
	            psDetalle.setInt(3, det.getCodigo());
	            psDetalle.setInt(4, cabPedido.getCodigo());
	            psDetalle.setInt(5, det.getProducto().getCodigo());

	            int filasDetalle = psDetalle.executeUpdate();

	            if (filasDetalle == 0) {
	                throw new KrakeDevException("No se encontró detalle con código: " + det.getCodigo()
	                    + ", cabecera: " + cabPedido.getCodigo()
	                    + " y producto: " + det.getProducto().getCodigo());
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        throw new KrakeDevException("Error al actualizar y recibir pedido. Detalle: " + e.getMessage());
	    } finally {
	        try {
	            if (psDetalle != null) psDetalle.close();
	            if (psCabecera != null) psCabecera.close();
	            if (con != null) con.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

}
