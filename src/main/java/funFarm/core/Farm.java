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

    public String addNewArea(int area) {
        FarmArea newArea = new FarmArea(area);
        this.areas.put(newArea.id, newArea);
        return newArea.id;
    }

    public void loadNewArea(int area, String id) {
        FarmArea newArea = new FarmArea(id, area);
        this.areas.put(id, newArea);
    }

    public void removeArea(String id) {
        if (!areas.containsKey(id)) {
            throw new FarmException("Area with index " + id + " not found");
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
            return new PlantResult(false, "Area with index " + id + " not found", 0);
        }


        return area.plant(plant);
    }

    public HarvestResult harvestArea(String id) {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            return new HarvestResult(false,"Area with index " + id + " not found", 0, null);
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

    public FarmAreaInfo getFarmAreaInfo(String id) {
        if (this.areas.containsKey(id)) {
            return this.areas.get(id).getInfo();
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }
    }

    public List<FarmAreaInfo> getFarmAreaInfo() {
        List<FarmAreaInfo> infos = new ArrayList<>();

        areas.forEach((_, area) -> {
            infos.add(area.getInfo());
        });
        return infos;
    }

    public void customAction (AreaFunction function) {
        if (function == null) {
            throw new FarmException("Area function can't be null");
        }
        areas.forEach((_, area) -> function.inspect(area));
    }
}
