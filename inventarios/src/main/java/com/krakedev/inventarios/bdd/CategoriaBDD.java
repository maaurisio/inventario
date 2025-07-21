package com.krakedev.inventarios.bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.krakedev.inventarios.entidades.Categoria;
import com.krakedev.inventarios.entidades.TipoDocumento;
import com.krakedev.inventarios.excepciones.KrakeDevException;
import com.krakedev.inventarios.utils.ConexionBDD;

public class CategoriaBDD {
	public void insertar(Categoria categoria) throws KrakeDevException {
		Connection con = null;
		try {
			con = ConexionBDD.obtenerConexion();
			PreparedStatement ps = con.prepareStatement(
				"INSERT INTO categorias (nombre, categoria_padre) VALUES (?, ?);"
			);

			ps.setString(1, categoria.getNombre());

			// Manejar null para categoría padre
			if (categoria.getCategoriaPadre() != null) {
				ps.setInt(2, categoria.getCategoriaPadre().getCodigo());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al insertar categoría. Detalle: " + e.getMessage());
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


	public void actualizar(Categoria categoria) throws KrakeDevException {
		Connection con = null;
		try {
			con = ConexionBDD.obtenerConexion();
			PreparedStatement ps = con.prepareStatement(
				"UPDATE categorias SET nombre = ?, categoria_padre = ? WHERE codigo_categoria = ?"
			);

			ps.setString(1, categoria.getNombre());

			if (categoria.getCategoriaPadre() != null) {
				ps.setInt(2, categoria.getCategoriaPadre().getCodigo());
			} else {
				ps.setNull(2, java.sql.Types.INTEGER);
			}

			ps.setInt(3, categoria.getCodigo());

			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al actualizar categoría. Detalle: " + e.getMessage());
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


	public ArrayList<Categoria> recuperarTodos() throws KrakeDevException {
		ArrayList<Categoria> categorias = new ArrayList<>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			con = ConexionBDD.obtenerConexion();
			ps = con.prepareStatement("SELECT * FROM categorias");
			rs = ps.executeQuery();

			while (rs.next()) {
				int codigo = rs.getInt("codigo_categoria");
				String nombre = rs.getString("nombre");

				Categoria categoriaPadre = null;
				int codigoPadre = rs.getInt("categoria_padre");

				// Si el campo en la BD es NULL, getInt devuelve 0, pero debemos verificar si era NULL realmente
				if (!rs.wasNull()) {
					// Buscar la categoría padre
					categoriaPadre = buscarCategoriaPorCodigo(codigoPadre, con);
				}

				Categoria cat = new Categoria(codigo, nombre, categoriaPadre);
				categorias.add(cat);
			}

		} catch (SQLException e) {
			e.printStackTrace();
			throw new KrakeDevException("Error al consultar categorías. Detalle: " + e.getMessage());
		}

		return categorias;
	}
	private Categoria buscarCategoriaPorCodigo(int codigo, Connection con) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		Categoria cat = null;

		ps = con.prepareStatement("SELECT * FROM categorias WHERE codigo_categoria = ?");
		ps.setInt(1, codigo);
		rs = ps.executeQuery();

		if (rs.next()) {
			int cod = rs.getInt("codigo_categoria");
			String nombre = rs.getString("nombre");

			// No se busca recursivamente al padre del padre (para evitar loops)
			cat = new Categoria(cod, nombre, null);
		}

		rs.close();
		ps.close();

		return cat;
	}

}
