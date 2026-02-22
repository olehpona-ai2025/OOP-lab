package lab1.service;

import lab1.core.Farm;
import lab1.core.plants.Plant;
import lab1.core.PlantRegistry;
import lab1.core.model.FarmAreaInfo;
import lab1.core.model.FarmEvent;
import lab1.core.model.FarmReport;
import lab1.core.model.PlantGrowState;
import lab1.core.state.FarmState;
import lab1.core.state.FarmStateMapper;
import lab1.infrastructure.ui.DisplayableResult;

import java.util.List;

public class BaseService implements FarmService {
    private final PlantRegistry registry;
    private final Warehouse warehouse;
    private final EventNotifier notifier;
    private final Farm farm;
    private final FarmStore farmStore;
    private final Reporter reporter;

    public BaseService(PlantRegistry registry, Warehouse warehouse, EventNotifier notifier, Farm farm, FarmStore farmStore, Reporter reporter) {
        this.registry = registry;
        this.warehouse = warehouse;
        this.notifier = notifier;
        this.farm = farm;
        this.farmStore = farmStore;
        this.reporter = reporter;
    }
    @Override
    public List<String> getPlants() {
        return registry.getNames();
    }

    @Override
    public void buyPlants(String plantName, int plantCount) {
        Plant toBuy = registry.create(plantName);
        warehouse.updatePlantCount(toBuy.getPlantName(), plantCount);
    }

    @Override
    public void createFarmArea(int area) {
        farm.addNewArea(area);
    }

    @Override
    public void removeFarmArea(String area) {
        farm.removeArea(area);
    }

    @Override
    public void setFarmAreaName(String id, String name) {
        farm.setFarmAreaName(id, name);
    }

    @Override
    public List<FarmAreaInfo> getFarmAreas() {
        return farm.getFarmAreaInfo();
    }

    @Override
    public DisplayableResult plantFarmArea(String areaId, String plantName) {
        Plant toPlant = registry.create(plantName);
        int neededForPlant = farm.getNeededToPlant(areaId, toPlant);
        if (warehouse.getPlantCount(plantName) < neededForPlant) {
            return new DisplayableResult() {
                @Override
                public boolean isSuccess() {
                    return false;
                }

                @Override
                public String getMsg() {
                    return "Not enough to plant needed " + neededForPlant + " but has " + warehouse.getPlantCount(plantName);
                }
            };
        }
        var result = farm.plantArea(areaId, toPlant);
        if (result.success()) {
            warehouse.updatePlantCount(toPlant.getPlantName(), -result.planted());
            notifier.notifyListeners(FarmEvent.planted(plantName, result.planted()));
        }
        return result;
    }

    @Override
    public DisplayableResult harvestFarmArea(String farmArea) {
        var result = farm.harvestArea(farmArea);
        if (result.success()) {
            warehouse.updatePlantCount(result.targetPlant(), result.harvested());
            notifier.notifyListeners(FarmEvent.harvested(result.targetPlant(), result.harvested()));
        }
        return result;
    }

    @Override
    public void growLoop() {
        farm.areaLoop();
    }

    @Override
    public void turboGrow() {
        farm.customAction(area -> {
            Plant plant = area.getCurrentPlant();
            if (plant == null) return;

            while (plant.getState() != PlantGrowState.GREW) {
                plant.grow();
            }
        });
    }

    @Override
    public List<FarmReport> getReport() {
        return reporter.getReport();
    }

    @Override
    public void saveData() {
        FarmState state = FarmStateMapper.getFarmState(farm);
        farmStore.saveFarmState(state);
    }
}
