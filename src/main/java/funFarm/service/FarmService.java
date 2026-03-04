package funFarm.service;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.FarmReport;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;

import java.util.List;

public interface FarmService {
    List<String> getPlants();
    boolean buyPlants(String plantName, int plantCount);

    void createFarmArea(int area);
    void removeFarmArea(String id);
    void setFarmAreaName(String id, String name);
    List<FarmAreaInfo> getFarmAreas();
    PlantResult plantFarmArea(String areaId, String plantName);
    HarvestResult harvestFarmArea(String farmArea);

    void growLoop();
    void turboGrow();

    List<FarmReport> getReport();

    void saveData();
}
