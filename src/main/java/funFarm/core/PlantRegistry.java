package funFarm.core;

import funFarm.core.model.PlantGrowState;
import funFarm.core.plants.PlantInfo;
import funFarm.core.plants.Plant;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class PlantRegistry {
    private final Map<String, PlantInfo> registry = new HashMap<>();

    public void register(Function<PlantGrowState, Plant> constructor) {
        String name = constructor.apply(PlantGrowState.GROWING).getPlantName();
        registry.put(name, new PlantInfo(name, constructor));
    }

    public Plant create(String name) {
        if (!registry.containsKey(name)) {
            throw new FarmException("Plant with index " + name + " not found");
        }
        return registry.get(name).factory().apply(PlantGrowState.GROWING);
    }

    public Plant createWithState(String name, PlantGrowState state) {
        if (state == null) {
            throw new FarmException("State should not be null");
        }
        if (!registry.containsKey(name)) {
            throw new FarmException("Plant with index " + name + " not found");
        }
        return registry.get(name).factory().apply(state);
    }

    public List<String> getNames() {
        return registry.keySet().stream().toList();
    }
}
