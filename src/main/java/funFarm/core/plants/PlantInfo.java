package funFarm.core.plants;

import funFarm.core.model.PlantGrowState;

import java.util.function.Function;

public record PlantInfo(String name, Function<PlantGrowState, Plant> factory) {
}
