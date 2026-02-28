package funFarm.core;

import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.HarvestResult;
import funFarm.core.model.PlantResult;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

@Component
public class Farm {
    private final Map<String, FarmArea> areas = new HashMap<>();

    public void addNewArea(int area) {
        FarmArea newArea = new FarmArea(area);
        this.areas.put(newArea.id, newArea);
    }

    public void loadNewArea(int area, String id) {
        FarmArea newArea = new FarmArea(id, area);
        this.areas.put(id, newArea);
    }

    public void removeArea(String id) throws FarmException {
        if (!areas.containsKey(id)) {
            throw new FarmException("Area with id " + id + " not found");
        }
        areas.remove(id);
    }

    public int getNeededToPlant(String id, Plant plant) throws FarmException {
        if (this.areas.containsKey(id)) {
            return this.areas.get(id).getNeededToPlant(plant);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }
    }

    public PlantResult plantArea(String id, Plant plant) throws FarmException {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }


        return area.plant(plant);
    }

    public HarvestResult harvestArea(String id) throws FarmException {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }

        return area.harvest();
    }

    public void areaLoop() {
        areas.forEach((_, area) -> {
            if (area.getCurrentPlant() != null ) {
                area.plantGrowLoop();
            }
        });
    }

    public void setFarmAreaName(String id, String name) throws FarmException {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }
        area.setName(name);
    }

    public List<FarmAreaInfo> getFarmAreaInfo() {
        List<FarmAreaInfo> infos = new ArrayList<>();

        areas.forEach((key, area) -> {
            Plant plant = area.getCurrentPlant();
            infos.add(new FarmAreaInfo(key, area.getName(), area.area, plant != null? plant.getPlantName(): "null", plant!= null? plant.getState().name(): "null"));
        });
        return infos;
    }

    public void customAction (AreaFunction function) throws FarmException {
        if (function == null) {
            throw new FarmException("Area function can't be null");
        }
        areas.forEach((_, area) -> {
            function.inspect(area);
        });
    }
}
