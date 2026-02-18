package lab1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class PlantRegistry {
    private List<PlantInfo> registry = new ArrayList<>();

    public void register(String name, Supplier<Plant> constructor) {
        registry.add(new PlantInfo(name, constructor));
    }

    public Plant create(int index) {
        if (index < 0 || index >= registry.size()) {
            return null;
        }
        return registry.get(index).factory().get();
    }

    public List<String> getNames() {
        return registry.stream().map(PlantInfo::name).toList();
    }
}
