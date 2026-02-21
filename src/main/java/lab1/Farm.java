package lab1;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Farm {
    private final Map<String, FarmArea> areas = new HashMap<>();
    private final List<EventListener> listeners = new ArrayList<>();
    private final Warehouse warehouse;

    Farm(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void registerListener(EventListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners(FarmEvent event) {
        for (var listener: listeners) {
            listener.pushEvent(event);
        }
    }

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

    public FarmOpResult plantArea(String id, Plant plant) {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }

        int neededForPlant = area.getNeededToPlant(plant);
        int available = warehouse.getPlantCount(plant.getPlantName());

        if (available < neededForPlant){
            return new FarmOpResult(false, "Not enough to plant, needed " + neededForPlant + " but have " + available, neededForPlant);
        }

        FarmOpResult planted = area.plant(plant);

        if (planted.success()) {
            warehouse.updatePlantCount(plant.getPlantName(), -neededForPlant);
            notifyListeners(FarmEvent.planted(plant.getPlantName(), planted.count()));
        }
        return planted;
    }

    public FarmOpResult harvestArea(String id) {
        FarmArea area;
        if (this.areas.containsKey(id)) {
            area = this.areas.get(id);
        } else {
            throw new FarmException("Area with index " + id + " not found");
        }
        Plant currentPlant = area.getCurrentPlant();

        if (currentPlant == null) return new FarmOpResult(false, "Nothing planted", 0);

        var harvested = area.harvest();
        if (harvested.success()) {
            warehouse.updatePlantCount(currentPlant.getPlantName(), harvested.count());
            notifyListeners(FarmEvent.harvested(currentPlant.getPlantName(), harvested.count()));
        }

        return harvested;
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

    public void buyPlant(Plant plant, int count) {
        warehouse.updatePlantCount(plant.getPlantName(), count);
    }
}
