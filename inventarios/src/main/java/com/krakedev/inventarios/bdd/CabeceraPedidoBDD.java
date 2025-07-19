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
			ps.setString(3, "R");
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
}
