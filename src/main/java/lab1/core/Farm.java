package lab1.core;

import lab1.core.model.AreaFunction;
import lab1.core.model.FarmAreaInfo;
import lab1.core.model.HarvestResult;
import lab1.core.model.PlantResult;
import lab1.core.plants.Plant;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

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

    public void removeArea(String id) {
        if (!areas.containsKey(id)) {
            throw new FarmException("Area with id " + id + " not found");
        }
        areas.remove(id);
    }

    public int getNeededToPlant(String id, Plant plant) {
        if (this.areas.containsKey(id)) {
            return this.areas.get(id).getNeededToPlant(plant);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }
    }

    public PlantResult plantArea(String id, Plant plant) {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }


        return area.plant(plant);
    }

    public HarvestResult harvestArea(String id) {
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

    public void setFarmAreaName(String id, String name) {
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

    public void customAction (AreaFunction function) {
        areas.forEach((_, area) -> {
            function.inspect(area);
        });
    }
}
