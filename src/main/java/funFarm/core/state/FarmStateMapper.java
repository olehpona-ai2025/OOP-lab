package funFarm.core.state;

import funFarm.core.Farm;
import funFarm.core.plants.Plant;
import funFarm.core.PlantRegistry;
import funFarm.core.model.FarmAreaInfo;
import funFarm.core.model.PlantGrowState;

import java.util.Objects;

public class FarmStateMapper {
    private FarmStateMapper(){};

    public static FarmState getFarmState(Farm farm)
    {
        Objects.requireNonNull(farm, "Farm should not be null");
        return new FarmState(farm.getFarmAreaInfo()
                .stream()
                .map(FarmStateMapper::getFarmAreaState).toList());
    }

    public static FarmAreaState getFarmAreaState(FarmAreaInfo areaInfo) {
        return new FarmAreaState(areaInfo.id(), areaInfo.name(), areaInfo.area(), areaInfo.plantName(), areaInfo.plantState());
    }

    private static FarmAreaInfo getFarmAreaInfo(FarmAreaState areaState) {
        return new FarmAreaInfo(areaState.id(), areaState.name(), areaState.area(), areaState.plantName(), areaState.plantState());
    }

    public static void setFarmState(Farm farm, FarmState state, PlantRegistry registry) {
        Objects.requireNonNull(farm, "Farm should not be null");
        Objects.requireNonNull(state, "Farm state should not be null");
        Objects.requireNonNull(registry, "Plant registry should not be null");

        for (FarmAreaInfo info: state.farmAreaStateList().stream().map(FarmStateMapper::getFarmAreaInfo).toList()) {
            farm.loadNewArea(info.area(), info.id());
            farm.setFarmAreaName(info.id(), info.name());
            Plant plant;
            if (info.plantName() != null && info.plantState() != null) {
                plant = registry.createWithState(info.plantName(), PlantGrowState.valueOf(info.plantState()));
                farm.plantArea(info.id(), plant);
            }
        }
    }
}
