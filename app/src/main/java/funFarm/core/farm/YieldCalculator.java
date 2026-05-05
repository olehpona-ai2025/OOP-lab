package funFarm.core.farm;

public class YieldCalculator {
    private YieldCalculator(){};

    public static int calculatePercentage(int planted, int harvested) {
        if (planted == 0) {
            return harvested > 0 ? 100 : 0;
        }
        double ratio = ((double) harvested / planted) * 100;
        return (int) Math.round(ratio);
    }
}
