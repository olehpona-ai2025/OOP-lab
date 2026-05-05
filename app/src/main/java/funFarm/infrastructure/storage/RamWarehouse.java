package funFarm.infrastructure.storage;

import funFarm.core.warehouse.Warehouse;
import funFarm.core.warehouse.WarehouseException;
import funFarm.core.model.WarehouseInfo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("RamWarehouse")
@ConditionalOnProperty(name = "storage.warehouse", havingValue = "ram")
public class RamWarehouse implements Warehouse {
    private final Map<String, Integer> content = new HashMap<>();

    @Override
    public int getPlantCount(String plant)
    {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        return this.content.getOrDefault(plant, 0);
    }

    @Override
    public void updatePlantCount(String plant, int count) {
        if (plant == null || plant.isEmpty()) {
            throw new WarehouseException("Incorrect key");
        }
        this.content.merge(plant, count, Integer::sum);
    }

    @Override
    public List<WarehouseInfo> getInfo() {
        return content.keySet().stream().map((String value) -> new WarehouseInfo(value, content.get(value))).toList();
    }
}
