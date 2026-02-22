package lab1.infrastructure.storage;

import lab1.core.model.FarmAreaInfo;
import lab1.core.state.FarmState;
import lab1.service.FarmStore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteStateStore implements FarmStore {
    Connection conn;

    public SqliteStateStore(String dbName) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + dbName);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("create table if not exists farm_areas (id string, name string, area int, plant_name string, plant_state string)");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveFarmState(FarmState state) {
        try {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement("insert into farm_areas (id, name, area, plant_name, plant_state) values (?,?,?,?,?)");) {
                stmt.execute("delete from farm_areas");
                for (FarmAreaInfo info : state.farmAreaStateList()) {
                    pstmt.setString(1, info.id());
                    pstmt.setString(2, info.name());
                    pstmt.setInt(3, info.area());
                    pstmt.setString(4, info.plantName());
                    pstmt.setString(5, info.plantState());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
                pstmt.clearBatch();
            }
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException inner) {
                inner.printStackTrace();
            }
            throw new RuntimeException(e);
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException inner) {
                inner.printStackTrace();
            }
        }
    }

    @Override
    public FarmState loadFarmState() {
        List<FarmAreaInfo> infos = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            if (stmt.execute("select * from farm_areas")) {
                ResultSet result = stmt.getResultSet();
                while (result.next()) {
                    infos.add(new FarmAreaInfo(result.getString("id"), result.getString("name"), result.getInt("area"), result.getString("plant_name"), result.getString("plant_state")));
                }
                result.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new FarmState(infos.stream().toList());
    }

    public void close() throws SQLException {
        conn.close();
    }
}
