package funFarm.infrastructure.storage;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.state.FarmAreaState;
import funFarm.core.state.FarmState;
import funFarm.core.state.FarmStateMapper;
import funFarm.service.FarmStore;
import funFarm.service.FarmStoreException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Component("SqlStateStore")
//@ConditionalOnProperty(name = "stateStore", value = "Sql")
public class SqlStateStore implements FarmStore {
    private final Connection conn;

    public SqlStateStore(Connection conn) {
        this.conn = conn;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("create table if not exists farm_areas (id text primary key, name text, area int, plant_name text, plant_state text)");
        } catch (SQLException e) {
            throw new FarmStoreException("Failed creating table");
        }
    }

    @Override
    public void saveFarmState(FarmState state) {
        if (state == null) {
            throw new FarmStoreException("State should not be null");
        }
        try {
            conn.setAutoCommit(false);
            try (Statement stmt = conn.createStatement();
                 PreparedStatement pstmt = conn.prepareStatement("insert into farm_areas (id, name, area, plant_name, plant_state) values (?,?,?,?,?)");) {
                stmt.execute("delete from farm_areas");
                for (FarmAreaState info : state.farmAreaStateList()) {
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
                throw new FarmStoreException("State writing failed, rollback failed");
            }
            throw new FarmStoreException("State writing failed, rolled back");
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
            throw new FarmStoreException("State loading failed");
        }
        return new FarmState(infos.stream().map(FarmStateMapper::getFarmAreaState).toList());
    }

    @Override
    public void updateFarmAreaState(FarmAreaState state) {
        if (state == null) {
            throw new FarmStoreException("State should not be null");
        }
        try (PreparedStatement pstmt = conn.prepareStatement("update farm_areas set name = ?, area = ?, plant_name = ?, plant_state = ? where id = ?")) {
            pstmt.setString(1, state.name());
            pstmt.setInt(2, state.area());
            pstmt.setString(3, state.plantName());
            pstmt.setString(4, state.plantState());
            pstmt.setString(5, state.id());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new FarmStoreException("State updating failed");
        }
    }

    @Override
    public void removeFarmArea(String id) {
        if (id == null) {
            throw new FarmStoreException("ID should not be null");
        }
        try (PreparedStatement pstmt = conn.prepareStatement("delete from farm_areas where id = ?")) {
            pstmt.setString(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new FarmStoreException("Area removal failed");
        }
    }
}
