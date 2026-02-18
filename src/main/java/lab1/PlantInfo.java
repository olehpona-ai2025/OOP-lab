package lab1;

import java.util.function.Supplier;

public record PlantInfo(String name, Supplier<Plant> factory) {
}
