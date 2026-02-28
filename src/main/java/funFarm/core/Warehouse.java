package funFarm.core;

import funFarm.core.model.WarehouseInfo;

import java.util.List;

public interface Warehouse {
    int getPlantCount(String plant) throws WarehouseException;
    void updatePlantCount(String plant, int count) throws WarehouseException;

    List<WarehouseInfo> getInfo() throws WarehouseException;
}
