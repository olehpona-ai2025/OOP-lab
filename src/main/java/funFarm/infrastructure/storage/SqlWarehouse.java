package funFarm.infrastructure.storage;

import funFarm.core.Warehouse;
import funFarm.core.WarehouseException;
import funFarm.core.model.WarehouseInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//@Component("SqlWarehouse")
public class SqlWarehouse implements Warehouse {
    private final Connection conn;

    public SqlWarehouse(Connection conn) {
        this.conn = conn;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("create table if not exists farm_warehouse (id text primary key, count int)");
        } catch (SQLException e) {
            throw new WarehouseException("Failed creating table");
        }
    }

    @Override
    public int getPlantCount(String plant) {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        try (PreparedStatement pstmt = conn.prepareStatement("select count from farm_warehouse where id = ?")) {
            pstmt.setString(1, plant);
            pstmt.execute();
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                } else {
                    return 0;
                }
            }
        } catch (SQLException e) {
            throw new WarehouseException("Failed fetching plant count");
        }
    }

    @Override
    public void updatePlantCount(String plant, int count) {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        try (PreparedStatement pstmt = conn.prepareStatement("insert into farm_warehouse (id, count) values (?,?) ON CONFLICT(id) DO UPDATE SET count = farm_warehouse.count + EXCLUDED.count")){
            pstmt.setString(1,plant);
            pstmt.setInt(2,count);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            throw new WarehouseException("Failed updating plant count");
        }
    }

    @Override
    public List<WarehouseInfo> getInfo() {
        try (Statement stmt = conn.createStatement()) {
            List<WarehouseInfo> data = new ArrayList<>();
            try (ResultSet set =  stmt.executeQuery("select * from farm_warehouse")){
                while (set.next()) {
                    data.add(new WarehouseInfo(set.getString("id"), set.getInt("count")));
                }
            }
            return data.stream().toList();
        } catch (SQLException e) {
            throw new WarehouseException("Failed getting warehouse info");
        }
    }
}
