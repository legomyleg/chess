package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DBHelper {
    public int updateHelper(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {

                setUpStatement(ps, params);
                ps.executeUpdate();



            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    private void setUpStatement(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof String p) ps.setString(i + 1, p);
            else if (param instanceof Integer p) ps.setInt(i + 1, p);
            else
                throw new RuntimeException(String.format("Object type %s not currently supported.", param.getClass().getName()));
        }
    }
}
