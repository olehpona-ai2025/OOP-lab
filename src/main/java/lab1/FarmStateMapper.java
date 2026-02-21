package lab1;

import java.util.Objects;

public class FarmStateMapper {
    public static FarmState getFarmState(Farm farm) {
        return new FarmState(farm.getFarmAreaInfo());
    }

    public static void setFarmState(Farm farm, FarmState state, PlantRegistry registry) {
        for (FarmAreaInfo info: state.farmAreaStateList()) {
            farm.loadNewArea(info.area(), info.id());
            Plant plant = null;
            if (!Objects.equals(info.plantName(), "null")) {
                plant = registry.createWithState();
            }
        }
    }
}
