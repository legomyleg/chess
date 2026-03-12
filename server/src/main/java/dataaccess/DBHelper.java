package dataaccess;

import java.sql.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class DBHelper {
    static public int updateHelper(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

                setUpStatement(ps, params);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) return rs.getInt(1);

                return 0;

            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    static public String getStringHelper(String tableName, String columnLabel, String primaryKeyName, Object key) throws DataAccessException {
        var statement = "SELECT %s FROM %s WHERE %s=?".formatted(columnLabel, tableName, primaryKeyName);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                setUpStatement(ps, new Object[] {key});
                ResultSet rs = executeQuery(ps);

                return rs.getString(columnLabel);
            }
        }
        catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    static public Integer getIntHelper(String tableName, String columnLabel, String primaryKeyName, Object key) throws DataAccessException {
        var statement = "SELECT %s FROM %s WHERE %s=?".formatted(columnLabel, tableName, primaryKeyName);
        try (Connection conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                setUpStatement(ps, new Object[] {key});
                ResultSet rs = executeQuery(ps);

                return rs.getInt(columnLabel);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    static private void setUpStatement(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object param = params[i];
            if (param instanceof String p) ps.setString(i + 1, p);
            else if (param instanceof Integer p) ps.setInt(i + 1, p);
            else
                throw new RuntimeException(String.format("Object type %s not currently supported.", param.getClass().getName()));
        }
    }

    static private ResultSet executeQuery(PreparedStatement ps) throws SQLException {
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs;
        }
        throw new SQLException("Statement '%s' returned null.".formatted(ps.toString()));
    }
}
