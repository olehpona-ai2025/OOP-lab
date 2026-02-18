package lab1;

import java.util.List;
import java.util.ArrayList;

public class Farm {
    private final List<FarmArea> areas = new ArrayList<>();
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
        this.areas.add(new FarmArea(area, this.warehouse));
    }

    public FarmOpResult plantArea(int index, Plant plant) {
        FarmOpResult planted;
        try {
            planted = this.areas.get(index).plant(plant);
        } catch (IndexOutOfBoundsException e) {
            throw new FarmException("Area with index " + index + " not found");
        }

        if (planted.success()) {
            notifyListeners(FarmEvent.planted(plant.getPlantName(), planted.count()));
        }
        return planted;
    }

    public FarmOpResult harvestArea(int index) {
        FarmArea area;
        try {
            area = this.areas.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new FarmException("Area with index " + index + " not found");
        }
        Plant currentPlant = area.getCurrentPlant();

        if (currentPlant == null) return new FarmOpResult(false, "Nothing planted", 0);

        var harvested = area.harvest();
        if (harvested.success()) {
            notifyListeners(FarmEvent.harvested(currentPlant.getPlantName(), harvested.count()));
        }

        return harvested;
    }

    public void areaLoop() {
        for (int idx = 0; idx < areas.size(); idx ++) {
            FarmArea area = this.areas.get(idx);
            if (area.getCurrentPlant() != null ) {
                if (area.getCurrentPlant().getState() == PlantGrowState.GREW) {
                    harvestArea(idx);
                } else {
                    area.plantGrowLoop();
                }
            }
        }
    }

    public List<FarmAreaInfo> getFarmAreaInfo() {
        List<FarmAreaInfo> infos = new ArrayList<>();

        areas.forEach((v) -> {
            infos.add(new FarmAreaInfo(v.id, v.area));
        });
        return infos;
    }

    public void customAction (AreaFunction function) {
        for (FarmArea area: areas) {
            function.inspect(area);
        }
    }

    public void buyPlant(Plant plant, int count) {
        warehouse.updatePlantCount(plant.getPlantName(), count);
    }
}
