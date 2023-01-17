package adhdmc.nerffarms.util;

import adhdmc.nerffarms.NerfFarms;

public class Util {
    /**
     * Used for the beginning of method calls
     * @param message Debug Message String
     */
    public static void debugLvl1(String message) {
        if (NFConfig.debugLevel() == 1 || NFConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for return statements, and their explanations
     * @param message Debug Message String
     */
    public static void debugLvl2(String message){
        if (NFConfig.debugLevel() == 2 || NFConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for methods that are called in assistance to other methods
     * @param message Debug Message String
     */
    public static void debugLvl3(String message){
        if (NFConfig.debugLevel() == 3 || NFConfig.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }
}
