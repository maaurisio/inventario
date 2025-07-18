package com.krakedev.inventarios.bdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

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
	        ps = con.prepareStatement("select prov.identificador, prov.tipo_documento,td.descripcion, prov.nombre, prov.telefono, prov.correo, prov.direccion\r\n"
	        		+ "from proveedores prov, tipo_documentos td\r\n"
	        		+ "where prov.tipo_documento = td.codigo\r\n"
	        		+ "and upper(nombre) like ?");
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
	            if (rs != null) rs.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        try {
	            if (ps != null) ps.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        try {
	            if (con != null) con.close();
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
			PreparedStatement ps = con
					.prepareStatement("insert into proveedores (identificador, tipo_documento, nombre, telefono, correo, direccion)\r\n"
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

}
