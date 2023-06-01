package simplexity.nerffarms.util;

import simplexity.nerffarms.NerfFarms;

public class Debug {
    /**
     * Used for the beginning of method calls
     * @param message Debug Message String
     */
    public static void debugLvl1(String message) {
        if (NerfFarmsConfig.debugLevel() == 1 || NerfFarmsConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for return statements, and their explanations
     * @param message Debug Message String
     */
    public static void debugLvl2(String message){
        if (NerfFarmsConfig.debugLevel() == 2 || NerfFarmsConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for methods that are called in assistance to other methods
     * @param message Debug Message String
     */
    public static void debugLvl3(String message){
        if (NerfFarmsConfig.debugLevel() == 3 || NerfFarmsConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }
}
