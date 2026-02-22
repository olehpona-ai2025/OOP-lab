package lab1.core.state;

import lab1.core.Farm;
import lab1.core.plants.Plant;
import lab1.core.PlantRegistry;
import lab1.core.model.FarmAreaInfo;
import lab1.core.model.PlantGrowState;

import java.util.Objects;

public class FarmStateMapper {
    public static FarmState getFarmState(Farm farm) {
        return new FarmState(farm.getFarmAreaInfo());
    }

    public static void setFarmState(Farm farm, FarmState state, PlantRegistry registry) {
        for (FarmAreaInfo info: state.farmAreaStateList()) {
            farm.loadNewArea(info.area(), info.id());
            farm.setFarmAreaName(info.id(), info.name());
            Plant plant;
            if (!Objects.equals(info.plantName(), "null")) {
                plant = registry.createWithState(info.plantName(), PlantGrowState.valueOf(info.plantState()));
                farm.plantArea(info.id(), plant);
            }
        }
    }
}
