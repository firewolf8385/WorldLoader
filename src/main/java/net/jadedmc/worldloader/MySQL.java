package net.jadedmc.worldloader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Manages the connection process to MySQL.
 */
public class MySQL {
    private Connection connection;

    /**
     * Loads the MySQL database connection info.
     * @param plugin Instance of the plugin.
     */
    public MySQL(WorldLoaderPlugin plugin) {
        String host = plugin.settingsManager().getConfig().getString("MySQL.host");
        String database = plugin.settingsManager().getConfig().getString("MySQL.database");
        String username = plugin.settingsManager().getConfig().getString("MySQL.username");
        String password = plugin.settingsManager().getConfig().getString("MySQL.password");
        int port = plugin.settingsManager().getConfig().getInt("MySQL.port");

        // Runs connection tasks async.
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, ()-> {
            // Attempts to connect to the MySQL database.
            try {
                synchronized(WorldLoaderPlugin.class) {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false&characterEncoding=utf8", username, password);
                }
            }
            catch(SQLException | ClassNotFoundException exception) {
                // If the connection fails, logs the error.
                exception.printStackTrace();
                return;
            }

            // Prevents losing connection to MySQL.
            plugin.getServer().getScheduler().scheduleAsyncRepeatingTask(plugin, ()-> {
                try {
                    connection.isValid(0);
                }
                catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }, 504000, 504000);

            // Create tables if they do not exist.
            try {
                PreparedStatement maps = connection.prepareStatement("CREATE TABLE IF NOT EXISTS maps (" +
                        "mapName VARCHAR(36) NOT NULL, " +
                        "mapFile MEDIUMBLOB NOT NULL, " +
                        "PRIMARY KEY (mapName));");
                maps.execute();
            }
            catch (SQLException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Close a connection.
     */
    public void closeConnection() {
        if(isConnected()) {
            try {
                connection.close();
            }
            catch(SQLException exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Get the connection.
     * @return Connection
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Get if plugin is connected to the database.
     * @return Connected
     */
    private boolean isConnected() {
        return (connection != null);
    }
}