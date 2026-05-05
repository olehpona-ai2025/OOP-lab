package funFarm.core.farm;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;

import java.util.List;

public class Farm {
    private FarmStore store;

    public Farm(FarmStore store) {
        this.store = store;
    }

    public String addNewArea(int area) {
        FarmArea newArea = new FarmArea(area);
        store.saveFarmArea(newArea);
        return newArea.id;
    }

    public void removeArea(String id) {
        store.removeFarmArea(id);
    }

    public int getNeededToPlant(String id, Plant plant) {
        return store.getFarmArea(id).getNeededToPlant(plant);
    }

    public PlantResult plantArea(String id, Plant plant) {
        FarmArea area;
        try {
            area = store.getFarmArea(id);
        } catch (FarmStoreException exception) {
            return new PlantResult(false, exception.getMessage(), 0);
        }
        var res = area.plant(plant);
        store.saveFarmArea(area);
        return res;
    }

    public HarvestResult harvestArea(String id) {
        FarmArea area;
        try {
            area = store.getFarmArea(id);
        } catch (FarmStoreException exception) {
            return new HarvestResult(false, exception.getMessage(), 0, null);
        }
        var res = area.harvest();
        store.saveFarmArea(area);
        return res;
    }

    public void areaLoop(String id) {
        FarmArea area = store.getFarmArea(id);
        if (area.getCurrentPlant() != null ) {
            area.plantGrowLoop();
            store.saveFarmArea(area);
        }
    }

    public void setFarmAreaName(String id, String name) {
        FarmArea area = store.getFarmArea(id);
        area.setName(name);
        store.saveFarmArea(area);
    }

    public FarmAreaInfo getFarmAreaInfo(String id) {
            return store.getFarmArea(id).getInfo();
    }

    public List<FarmAreaInfo> getFarmAreaInfo() {
        return store.getFarmAreaInfo();
    }
}
