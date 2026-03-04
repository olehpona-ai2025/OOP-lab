package funFarm.core.model;

public enum PlantGrowState {
    GROWING,
    GREW,
    OVERGREW;

    public float getModifier() {
        return switch (this) {
            case GREW -> 1;
            case GROWING -> 0;
            case OVERGREW -> 0.5F;
        };
    }
}
