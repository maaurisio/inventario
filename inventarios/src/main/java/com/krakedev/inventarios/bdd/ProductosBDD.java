package com.krakedev.inventarios.bdd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.krakedev.inventarios.entidades.Categoria;
import com.krakedev.inventarios.entidades.Producto;
import com.krakedev.inventarios.entidades.UnidadDeMedida;
import com.krakedev.inventarios.excepciones.KrakeDevException;
import com.krakedev.inventarios.utils.ConexionBDD;

public class ProductosBDD {
	public ArrayList<Producto> buscar(String subcadena) throws KrakeDevException {
		ArrayList<Producto> productos = new ArrayList<Producto>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		Producto producto = null;
		try {
			con = ConexionBDD.obtenerConexion();
			ps = con.prepareStatement(
					"select prod.codigo_producto, prod.nombre AS nombre_producto, udm.nombre AS numbre_udm, udm.descripcion AS descripcion_udm,\r\n"
							+ "prod.precio_venta, prod.tiene_iva, prod.coste, prod.categoria, cat.nombre AS nombre_categoria, stock\r\n"
							+ "from productos prod, unidades_de_medida udm, categorias cat\r\n"
							+ "where prod.unidad_de_medida = udm.nombre\r\n"
							+ "and prod.categoria = cat.codigo_categoria\r\n" + "and upper (prod.nombre) like ?");
			ps.setString(1, "%" + subcadena.toUpperCase() + "%");
			rs = ps.executeQuery();

			while (rs.next()) {
				int codigoProducto = rs.getInt("codigo_producto");
				String nombreProducto = rs.getString("nombre_producto");
				String nombreUnidadMedida = rs.getString("numbre_udm");
				String descripcionUnidaMedida = rs.getString("descripcion_udm");
				String descripcion = rs.getString("descripcion_udm");
				BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
				boolean tieneIva = rs.getBoolean("tiene_iva");
				BigDecimal coste = rs.getBigDecimal("coste");
				int codigoCategoria = rs.getInt("categoria");
				String nombreCategoria = rs.getString("nombre_categoria");
				int stock = rs.getInt("stock");

				UnidadDeMedida udm = new UnidadDeMedida();
				udm.setNombre(nombreUnidadMedida);
				udm.setDescripcion(descripcionUnidaMedida);

				Categoria cat = new Categoria();
				cat.setCodigo(codigoCategoria);
				cat.setNombre(nombreCategoria);

				producto = new Producto();
				producto.setCodigo(codigoProducto);
				producto.setNombre(nombreProducto);
				producto.setUnidadMedida(udm);
				producto.setPrecioVenta(precioVenta);
				producto.setTieneIva(tieneIva);
				producto.setCoste(coste);
				producto.setCategoria(cat);
				producto.setStock(stock);
				productos.add(producto);
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
		return productos;
	}

	public void insertar(Producto producto) throws KrakeDevException {
		Connection con = null;

		try {
			con = ConexionBDD.obtenerConexion();
			PreparedStatement ps = con.prepareStatement("INSERT INTO productos (\r\n"
					+ "    nombre, unidad_de_medida, precio_venta, tiene_iva, coste, categoria, stock\r\n"
					+ ") VALUES (?,?,?,?,?,?,?)");

			ps.setString(1, producto.getNombre());
			ps.setString(2, producto.getUnidadMedida().getNombre());
			ps.setBigDecimal(3, producto.getPrecioVenta());
			ps.setBoolean(4, producto.isTieneIva());
			ps.setBigDecimal(5, producto.getCoste());
			ps.setInt(6, producto.getCategoria().getCodigo());
			ps.setInt(7, producto.getStock());

			ps.executeUpdate();
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

	public void actualizar(Producto producto) throws KrakeDevException {
		Connection con = null;
		con = ConexionBDD.obtenerConexion();
		try {
			PreparedStatement ps = con
					.prepareStatement("update productos set nombre = ?, unidad_de_medida= ?, precio_venta= ?,\r\n"
							+ "tiene_iva= ?, coste= ?, categoria= ?, stock= ?\r\n" + "where codigo_producto = ?");
			ps.setString(1, producto.getNombre());
			ps.setString(2, producto.getUnidadMedida().getNombre());
			ps.setBigDecimal(3, producto.getPrecioVenta());
			ps.setBoolean(4, producto.isTieneIva());
			ps.setBigDecimal(5, producto.getCoste());
			ps.setInt(6, producto.getCategoria().getCodigo());
			ps.setInt(7, producto.getStock());
			ps.setInt(8, producto.getCodigo());
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al actualizar producto. Detalle: " + e.getMessage());
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

	public ArrayList<Producto> buscarProductoPorCodigo(String identificadorExacto) throws KrakeDevException {
		ArrayList<Producto> productos = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = ConexionBDD.obtenerConexion();

			String sql = "select pro.codigo_producto, pro.nombre, pro.unidad_de_medida, pro.precio_venta,\r\n"
					+ "pro.tiene_iva, pro.coste, pro.categoria, pro.stock\r\n" + "from productos pro\r\n"
					+ "where codigo_producto = ?";
			ps = con.prepareStatement(sql);
			int idInt = Integer.parseInt(identificadorExacto); // convertimos String a int
			ps.setInt(1, idInt);
			rs = ps.executeQuery();

			while (rs.next()) {
				int codigoProducto = rs.getInt("codigo_producto");
				String nombre = rs.getString("nombre");
				String nombreUnidadMedida = rs.getString("unidad_de_medida");
				BigDecimal precioVenta = rs.getBigDecimal("precio_venta");
				Boolean tieneIva = rs.getBoolean("tiene_iva");
				BigDecimal coste = rs.getBigDecimal("coste");
				String nombreCategoria = rs.getString("categoria");
				int stock = rs.getInt("stock");

				Categoria cat = new Categoria();
				cat.setNombre(nombreCategoria);

				UnidadDeMedida udm = new UnidadDeMedida();
				udm.setNombre(nombreUnidadMedida);

				Producto producto = new Producto();
				producto.setCodigo(codigoProducto);
				producto.setNombre(nombre);
				producto.setUnidadMedida(udm);
				producto.setPrecioVenta(precioVenta);
				producto.setTieneIva(tieneIva);
				producto.setCoste(coste);
				producto.setCategoria(cat);
				producto.setStock(stock);

				productos.add(producto);
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

		return productos;
	}
}
