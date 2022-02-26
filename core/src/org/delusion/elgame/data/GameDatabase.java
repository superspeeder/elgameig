package org.delusion.elgame.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class GameDatabase {
    private Connection conn;

    public GameDatabase() {
        String path = "jdbc:sqlite:" + DataManager.ROOT_STORAGE_PATH.resolve("saveData.db").toString();

        try {
            conn = DriverManager.getConnection(path);

            setupDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private void setupDatabase() {
        String sql = """
                BEGIN TRANSACTION;
                CREATE TABLE IF NOT EXISTS chunks (x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    PRIMARY KEY(x, y));
                CREATE TABLE IF NOT EXISTS tile_data(x INTEGER NOT NULL,
                    y INTEGER NOT NULL,
                    foreground_tile_id INTEGER,
                    background_tile_id INTEGER,
                    chunkid INTEGER NOT NULL,
                    FOREIGN KEY(chunkid) REFERENCES chunks(rowid),
                    PRIMARY KEY(x, y));
                CREATE INDEX tile_data_by_chunk ON tile_data (chunkid);
                COMMIT;""";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
