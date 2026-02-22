package lab1.core.model;

import lab1.core.plants.Plant;

import java.util.function.Function;

public record PlantInfo(String name, Function<PlantGrowState, Plant> factory) {
}
