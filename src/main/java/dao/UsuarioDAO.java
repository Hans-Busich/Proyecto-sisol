package dao;
import java.sql.*;
import model.Usuario; 

public class UsuarioDAO {
    public Usuario autenticar(String username, String password) throws SQLException {
        Connection conn = DBConnection.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement("SELECT idUsuario, nombreUsuario, contraseña, rol FROM Usuario WHERE nombreUsuario = ?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String pwd = rs.getString("contraseña");
            // En desarrollo comparamos en texto; en producción usa hash+salt
            if (pwd.equals(password)) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("idUsuario"));
                u.setNombreUsuario(rs.getString("nombreUsuario"));
                u.setRol(rs.getString("rol"));
                return u;
            }
        }
        return null;
    }
}
