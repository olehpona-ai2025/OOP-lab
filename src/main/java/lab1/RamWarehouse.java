package lab1;

import java.util.HashMap;

public class RamWarehouse implements Warehouse {
    private final HashMap<String, Integer> content = new HashMap<>();

    @Override
    public int getPlantCount(String plant) {
        return this.content.getOrDefault(plant, 0);
    }

    @Override
    public void updatePlantCount(String plant, int count) {
        this.content.merge(plant, count, Integer::sum);
    }
}
