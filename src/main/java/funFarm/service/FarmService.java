package funFarm.service;

import funFarm.core.model.*;
import funFarm.core.plants.Plant;
import funFarm.core.workers.profiles.WorkerProfileType;

import java.util.List;

public interface FarmService {
    void createFarmArea(int area);
    void removeFarmArea(String id);
    void setFarmAreaName(String id, String name);
    List<FarmAreaInfo> getFarmAreas();
    PlantResult plantFarmArea(String areaId, String plantName);
    HarvestResult harvestFarmArea(String farmArea);
}
