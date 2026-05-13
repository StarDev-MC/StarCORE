package net.starcore.starecon.database;

/**
 * Database configuration settings.
 */
public class DatabaseConfig {
    private String databaseType = "sqlite"; // sqlite, mysql, mariadb
    private String filePath = "plugins/StarEcon/data.db"; // For SQLite
    private String host = "localhost";
    private int port = 3306;
    private String database = "starcore";
    private String username = "";
    private String password = "";

    public String getDatabaseType() { return databaseType; }
    public String getFilePath() { return filePath; }
    public String getHost() { return host; }
    public int getPort() { return port; }
    public String getDatabase() { return database; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public DatabaseConfig withType(String type) { this.databaseType = type; return this; }
    public DatabaseConfig withFilePath(String path) { this.filePath = path; return this; }
    public DatabaseConfig withHost(String host) { this.host = host; return this; }
    public DatabaseConfig withPort(int port) { this.port = port; return this; }
    public DatabaseConfig withDatabase(String db) { this.database = db; return this; }
    public DatabaseConfig withUsername(String user) { this.username = user; return this; }
    public DatabaseConfig withPassword(String pass) { this.password = pass; return this; }
}
