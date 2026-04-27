package funFarm.core;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class PlantRegistry {
    private final Map<String, Plant> registry = new HashMap<>();

    public void register(Plant plant) {
        registry.put(plant.getPlantName(), plant);
    }

    public Plant create(String name) {
        if (!registry.containsKey(name)) {
            return null;
        }
        return new Plant(registry.get(name), PlantGrowState.GROWING);
    }

    public Plant createWithState(String name, PlantGrowState state) {
        if (state == null) {
            throw new FarmException("State should not be null");
        }
        if (!registry.containsKey(name)) {
            return null;
        }
        return new Plant(registry.get(name), state);
    }

    public List<Plant> getPlants() {
        return registry.values().stream().toList();
    }
}
