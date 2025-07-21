package com.krakedev.inventarios.bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.krakedev.inventarios.entidades.CabeceraPedido;
import com.krakedev.inventarios.entidades.DetallePedido;
import com.krakedev.inventarios.entidades.EstadoPedido;
import com.krakedev.inventarios.entidades.Producto;
import com.krakedev.inventarios.entidades.Proveedor;
import com.krakedev.inventarios.entidades.TipoDocumento;
import com.krakedev.inventarios.excepciones.KrakeDevException;
import com.krakedev.inventarios.utils.ConexionBDD;

public class ProveedoresBDD {
	public ArrayList<Proveedor> buscar(String subcadena) throws KrakeDevException {
		ArrayList<Proveedor> proveedores = new ArrayList<Proveedor>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Proveedor proveedor = null;
		try {
			con = ConexionBDD.obtenerConexion();
			ps = con.prepareStatement(
					"select prov.identificador, prov.tipo_documento,td.descripcion, prov.nombre, prov.telefono, prov.correo, prov.direccion\r\n"
							+ "from proveedores prov, tipo_documentos td\r\n"
							+ "where prov.tipo_documento = td.codigo\r\n" + "and upper(nombre) like ?");
			ps.setString(1, "%" + subcadena.toUpperCase() + "%");
			rs = ps.executeQuery();

			while (rs.next()) {
				int identificador = rs.getInt("identificador");
				String codigoTipoDocumento = rs.getString("tipo_documento");
				String descripcionTipoDocumento = rs.getString("descripcion");
				String nombre = rs.getString("nombre");
				String telefono = rs.getString("telefono");
				String correo = rs.getString("correo");
				String direccion = rs.getString("direccion");

				TipoDocumento td = new TipoDocumento(codigoTipoDocumento, descripcionTipoDocumento);

				proveedor = new Proveedor(identificador, td, nombre, telefono, correo, direccion);
				proveedores.add(proveedor);
			}

		} catch (KrakeDevException e) {
			e.printStackTrace();
			throw e;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al consultar. Detalle: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (ps != null)
					ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			try {
				if (con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return proveedores;
	}

	public void insertar(Proveedor proveedor) throws KrakeDevException {
		Connection con = null;

		try {
			con = ConexionBDD.obtenerConexion();
			PreparedStatement ps = con.prepareStatement(
					"insert into proveedores (identificador, tipo_documento, nombre, telefono, correo, direccion)\r\n"
							+ "values (?,?,?,?,?,?)");

			ps.setInt(1, proveedor.getIdentificador());
			ps.setString(2, proveedor.getTipoDocumento().getCodigo());
			ps.setString(3, proveedor.getNombre());
			ps.setString(4, proveedor.getTelefono());
			ps.setString(5, proveedor.getCorreo());
			ps.setString(6, proveedor.getDireccion());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al insertar proveedor. Detalle: " + e.getMessage());
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

	public boolean hayConexion() throws KrakeDevException {
		try (Connection con = ConexionBDD.obtenerConexion()) {
			return con != null && !con.isClosed();
		} catch (Exception e) {
			throw new KrakeDevException("No se pudo establecer conexión: " + e.getMessage());
		}
	}

	public ArrayList<Proveedor> buscarProveedor(String subcadena) throws KrakeDevException {
		ArrayList<Proveedor> proveedores = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = ConexionBDD.obtenerConexion();

			String sql = "SELECT prov.identificador, prov.nombre, prov.telefono, prov.correo, prov.direccion "
					+ "FROM proveedores prov WHERE CAST(prov.identificador AS TEXT) LIKE ?";

			ps = con.prepareStatement(sql);
			ps.setString(1, "%" + subcadena + "%"); // ← aquí se enlaza el parámetro correctamente
			rs = ps.executeQuery();

			while (rs.next()) {
				int identificador = rs.getInt("identificador");
				String nombre = rs.getString("nombre");
				String telefono = rs.getString("telefono");
				String correo = rs.getString("correo");
				String direccion = rs.getString("direccion");

				Proveedor proveedor = new Proveedor();
				proveedor.setIdentificador(identificador);
				proveedor.setNombre(nombre);
				proveedor.setTelefono(telefono);
				proveedor.setCorreo(correo);
				proveedor.setDireccion(direccion);

				proveedores.add(proveedor);
			}

		} catch (Exception e) {
			throw new KrakeDevException("Error al buscar proveedores: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}

		return proveedores;
	}

	public ArrayList<Proveedor> buscarDetallesProveedor(String subcadena) throws KrakeDevException {
		ArrayList<Proveedor> proveedores = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = ConexionBDD.obtenerConexion();

			String sql = "SELECT " + "    detPed.codigo_detalle_pedido AS codigo_detalle, "
					+ "    cabPed.numero AS numero_pedido, " + "    cabPed.proveedor AS identificador_proveedor, "
					+ "    cabPed.fecha AS fecha_pedido, " + "    cabPed.estado AS estado_pedido, "
					+ "    detPed.producto AS codigo_producto, " + "    pro.nombre AS nombre_producto, "
					+ "    detPed.cantidad_solicitada, " + "    detPed.cantidad_recibida, " + "    detPed.subtotal "
					+ "FROM detalle_pedido detPed "
					+ "JOIN cabecera_pedido cabPed ON detPed.cabecera_pedido = cabPed.numero "
					+ "JOIN productos pro ON pro.codigo_producto = detPed.producto "
					+ "WHERE CAST(cabPed.proveedor AS TEXT) LIKE ? "
					+ "ORDER BY cabPed.numero, detPed.codigo_detalle_pedido";

			ps = con.prepareStatement(sql);
			ps.setString(1, "%" + subcadena + "%");
			rs = ps.executeQuery();

			// Usamos un mapa para evitar proveedores duplicados
			Map<Integer, Proveedor> mapaProveedores = new HashMap<>();

			while (rs.next()) {
				int idProveedor = rs.getInt("identificador_proveedor");

				Proveedor proveedor = mapaProveedores.get(idProveedor);
				if (proveedor == null) {
					proveedor = new Proveedor();
					proveedor.setIdentificador(idProveedor);
					// Si quieres, aquí puedes setear más campos del proveedor si los tienes en la
					// consulta o si haces otra consulta para obtenerlos
					mapaProveedores.put(idProveedor, proveedor);
				}

				// Creamos objeto CabeceraPedido
				CabeceraPedido cabecera = new CabeceraPedido();
				cabecera.setCodigo(rs.getInt("numero_pedido"));
				cabecera.setProveedor(proveedor);
				cabecera.setFecha(rs.getDate("fecha_pedido"));
				String estadoStr = rs.getString("estado_pedido");
				cabecera.setEstado(new EstadoPedido(0, estadoStr)); // Ajusta según tu clase EstadoPedido

				// Creamos objeto Producto
				Producto producto = new Producto();
				producto.setCodigo(rs.getInt("codigo_producto"));
				producto.setNombre(rs.getString("nombre_producto"));

				// Creamos DetallePedido y asignamos
				DetallePedido detalle = new DetallePedido();
				detalle.setCodigo(rs.getInt("codigo_detalle"));
				detalle.setCabeceraPedido(cabecera);
				detalle.setProducto(producto);
				detalle.setCantidadSolicitada(rs.getInt("cantidad_solicitada"));
				detalle.setCantidadRecibida(rs.getInt("cantidad_recibida"));
				detalle.setSubtotal(rs.getBigDecimal("subtotal"));

				// Agregamos el detalle a la lista de detalles del proveedor
				proveedor.getDetalles().add(detalle);
			}

			// Convertimos el mapa a lista
			proveedores = new ArrayList<>(mapaProveedores.values());
			// 🔁 Evita referencias circulares para que el JSON no crezca innecesariamente
			for (Proveedor proveedor : proveedores) {
				for (DetallePedido detalle : proveedor.getDetalles()) {
					if (detalle.getCabeceraPedido() != null) {
						detalle.getCabeceraPedido().setProveedor(null);
					}
				}
			}

		} catch (Exception e) {
			throw new KrakeDevException("Error al buscar proveedores: " + e.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (Exception e) {
			}
			try {
				if (ps != null)
					ps.close();
			} catch (Exception e) {
			}
			try {
				if (con != null)
					con.close();
			} catch (Exception e) {
			}
		}

		return proveedores;
	}
}
