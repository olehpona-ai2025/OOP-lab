package lab1;

public class Plants {
    public static class Potato extends Plant{
        Potato(PlantGrowState state) {
            super("Potato", state);
        }

        @Override
        public int getBaseYield() {
            return 10;
        }

        @Override
        public int getPlantingCost() {
            return 2;
        }
    }

    public static class Tomato extends Plant{
        Tomato(PlantGrowState state) {
            super("Tomato", state);
        }

        @Override
        public int getBaseYield() {
            return 5;
        }

        @Override
        public int getPlantingCost() {
            return 1;
        }
    }

}
