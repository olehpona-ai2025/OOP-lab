package funFarm.service;

import funFarm.core.FarmException;
import funFarm.core.WarehouseException;
import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;

import java.util.List;

public interface FarmService {
    List<String> getPlants();
    void buyPlants(String plantName, int plantCount) throws WarehouseException, FarmException;

    void createFarmArea(int area);
    void removeFarmArea(String id) throws FarmException;
    void setFarmAreaName(String id, String name) throws FarmException;
    List<FarmAreaInfo> getFarmAreas();
    PlantResult plantFarmArea(String areaId, String plantName) throws WarehouseException, FarmException;
    HarvestResult harvestFarmArea(String farmArea)throws WarehouseException, FarmException;

    void growLoop();
    void turboGrow();

    List<FarmReport> getReport();

    void saveData();
}
