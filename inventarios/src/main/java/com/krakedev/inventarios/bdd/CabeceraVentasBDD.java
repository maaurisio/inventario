package com.krakedev.inventarios.bdd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import com.krakedev.inventarios.entidades.CabeceraVenta;
import com.krakedev.inventarios.entidades.DetalleVentas;
import com.krakedev.inventarios.excepciones.KrakeDevException;
import com.krakedev.inventarios.utils.ConexionBDD;

public class CabeceraVentasBDD {
	public void insertar(CabeceraVenta cabVenta) throws KrakeDevException {
	    Connection con = null;
	    PreparedStatement ps = null;
	    ResultSet rsClave = null;
	    int codigoCabeceraVenta = 0;
	    PreparedStatement psDetVen = null;
	    PreparedStatement psIva = null;
	    ResultSet rsIva = null;

	    int totalSinIva = 0;
	    int iva = 0;
	    int total = 0;

	    Date fechaActual = new Date();
	    long tiempoSinMilis = (fechaActual.getTime() / 1000) * 1000;
	    java.sql.Timestamp timestampSinMilis = new java.sql.Timestamp(tiempoSinMilis);

	    try {
	        con = ConexionBDD.obtenerConexion();

	        // Insertar cabecera_ventas
	        ps = con.prepareStatement(
	                "INSERT INTO cabecera_ventas (fecha, total_sin_iva, iva, total) VALUES (?,?,?,?)",
	                Statement.RETURN_GENERATED_KEYS);

	        ps.setTimestamp(1, timestampSinMilis);
	        ps.setInt(2, totalSinIva);
	        ps.setInt(3, iva);
	        ps.setInt(4, total);
	        ps.executeUpdate();

	        rsClave = ps.getGeneratedKeys();
	        if (rsClave.next()) {
	            codigoCabeceraVenta = rsClave.getInt(1);
	        }

	        // Recorrer detalles
	        ArrayList<DetalleVentas> detallesVenta = cabVenta.getDetallesVenta();
	        for (DetalleVentas det : detallesVenta) {
	            int codigoProducto = det.getProducto().getCodigo();

	            // Consultar si el producto tiene IVA
	            psIva = con.prepareStatement("SELECT tiene_iva FROM productos WHERE codigo_producto = ?");
	            psIva.setInt(1, codigoProducto);
	            rsIva = psIva.executeQuery();

	            boolean tieneIva = false;
	            if (rsIva.next()) {
	                tieneIva = rsIva.getBoolean("tiene_iva");
	            }

	            int cantidad = det.getCantidad();
	            BigDecimal precioVenta = det.getPrecioVenta();
	            BigDecimal subtotal = precioVenta.multiply(new BigDecimal(cantidad));
	            BigDecimal subtotalConIva;

	            if (tieneIva) {
	                subtotalConIva = subtotal.multiply(new BigDecimal("1.12"));
	                iva += subtotalConIva.subtract(subtotal).intValue(); // IVA acumulado
	            } else {
	                subtotalConIva = subtotal;
	            }

	            totalSinIva += subtotal.intValue();
	            total += subtotalConIva.intValue();

	            // Insertar en detalle_ventas
	            psDetVen = con.prepareStatement(
	                "INSERT INTO detalle_ventas (cabecera_ventas, productos, cantidad, precio_venta, subtotal, subtotal_con_iva) VALUES (?,?,?,?,?,?)");
	            psDetVen.setInt(1, codigoCabeceraVenta);
	            psDetVen.setInt(2, codigoProducto);
	            psDetVen.setInt(3, cantidad);
	            psDetVen.setBigDecimal(4, precioVenta);
	            psDetVen.setBigDecimal(5, subtotal);
	            psDetVen.setBigDecimal(6, subtotalConIva);
	            psDetVen.executeUpdate();
	        }

	        // Actualizar totales en la cabecera
	        PreparedStatement psUpdate = con.prepareStatement(
	                "UPDATE cabecera_ventas SET total_sin_iva = ?, iva = ?, total = ? WHERE codigo_cabecera_ventas = ?");
	        psUpdate.setInt(1, totalSinIva);
	        psUpdate.setInt(2, iva);
	        psUpdate.setInt(3, total);
	        psUpdate.setInt(4, codigoCabeceraVenta);
	        psUpdate.executeUpdate();

	    } catch (SQLException e) {
	    	throw new KrakeDevException("Error al insertar la cabecera: " + e.getMessage());
	    } finally {
	        try {
	            if (rsClave != null) rsClave.close();
	            if (rsIva != null) rsIva.close();
	            if (ps != null) ps.close();
	            if (psDetVen != null) psDetVen.close();
	            if (psIva != null) psIva.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	
}
