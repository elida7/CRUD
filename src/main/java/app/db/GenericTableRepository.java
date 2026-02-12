package app.db;

import app.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class GenericTableRepository {

    private final DatabaseConnectionFactory connectionFactory;
    private final DatabaseConfig config;

    public GenericTableRepository(DatabaseConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        this.config = connectionFactory.getConfig();
    }

    public List<String> getColumnNames() throws SQLException {
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt =
                     conn.prepareStatement("SELECT * FROM " + config.getTableName() + " WHERE 1=0");
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            List<String> columns = new ArrayList<>();
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                columns.add(meta.getColumnName(i));
            }
            return columns;
        }
    }

    public List<GenericRow> findAll() throws SQLException {
        String sql = "SELECT * FROM " + config.getTableName();
        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            List<GenericRow> result = new ArrayList<>();

            while (rs.next()) {
                GenericRow row = new GenericRow();
                for (int i = 1; i <= columnCount; i++) {
                    String colName = meta.getColumnName(i);
                    Object value = rs.getObject(i);
                    row.put(colName, value);
                }
                result.add(row);
            }
            return result;
        }
    }

    public void insert(GenericRow row) throws SQLException {
        List<String> columns = new ArrayList<>(row.getValues().keySet());
        String colList = String.join(", ", columns);
        String placeholders = String.join(", ", columns.stream().map(c -> "?").toList());

        String sql = "INSERT INTO " + config.getTableName() +
                " (" + colList + ") VALUES (" + placeholders + ")";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            for (String col : columns) {
                stmt.setObject(index++, row.get(col));
            }
            stmt.executeUpdate();
        }
    }

    public void update(GenericRow row) throws SQLException {
        String pk = config.getPrimaryKeyColumn();

        List<String> columns = new ArrayList<>(row.getValues().keySet());
        columns.remove(pk);

        String setClause = String.join(", ", columns.stream().map(c -> c + " = ?").toList());

        String sql = "UPDATE " + config.getTableName() +
                " SET " + setClause +
                " WHERE " + pk + " = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int index = 1;
            for (String col : columns) {
                stmt.setObject(index++, row.get(col));
            }
            stmt.setObject(index, row.get(pk));
            stmt.executeUpdate();
        }
    }

    public void delete(Object pkValue) throws SQLException {
        String sql = "DELETE FROM " + config.getTableName() +
                " WHERE " + config.getPrimaryKeyColumn() + " = ?";

        try (Connection conn = connectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, pkValue);
            stmt.executeUpdate();
        }
    }
}

