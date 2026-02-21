package lab1;

import java.util.function.Function;

public record PlantInfo(String name, Function<PlantGrowState,Plant> factory) {
}
