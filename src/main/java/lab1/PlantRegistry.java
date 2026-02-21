package lab1;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PlantRegistry {
    private final List<PlantInfo> registry = new ArrayList<>();

    public void register(String name, Function<PlantGrowState, Plant> constructor) {
        registry.add(new PlantInfo(name, constructor));
    }

    public Plant create(int index) {
        if (index < 0 || index >= registry.size()) {
            throw new FarmException("Plant with index " + index + " not found");
        }
        return registry.get(index).factory().apply(PlantGrowState.GROWING);
    }

    public Plant createWithState(int index, PlantGrowState state) {
        if (index < 0 || index >= registry.size()) {
            throw new FarmException("Plant with index " + index + " not found");
        }
        return registry.get(index).factory().apply(state);
    }

    public List<String> getNames() {
        return registry.stream().map(PlantInfo::name).toList();
    }
}
