package funFarm.core.model;

public record HarvestResult(boolean success, String msg, int harvested, String targetPlant) {}
