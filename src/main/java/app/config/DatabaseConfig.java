package app.config;

public class DatabaseConfig {

    private final String url;
    private final String user;
    private final String password;
    private final String tableName;
    private final String primaryKeyColumn;

    public DatabaseConfig(String url, String user, String password,
                          String tableName, String primaryKeyColumn) {
        this.url = url;
        this.user = user;
        this.password = password;
        this.tableName = tableName;
        this.primaryKeyColumn = primaryKeyColumn;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKeyColumn() {
        return primaryKeyColumn;
    }
}

