package adhdmc.nerffarms.util;

import adhdmc.nerffarms.NerfFarms;
import adhdmc.nerffarms.config.ConfigParser;

public class Util {
    /**
     * Used for the beginning of method calls
     * @param message Debug Message String
     */
    public static void debugLvl1(String message) {
        if (ConfigParser.debugLevel() == 1 || ConfigParser.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for return statements, and their explanations
     * @param message Debug Message String
     */
    public static void debugLvl2(String message){
        if (ConfigParser.debugLevel() == 2 || ConfigParser.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }

    /**
     * Used for methods that are called in assistance to other methods
     * @param message Debug Message String
     */
    public static void debugLvl3(String message){
        if (ConfigParser.debugLevel() == 3 || ConfigParser.debugLevel() == 4) {
            NerfFarms.getInstance().getLogger().info(message);
        }
    }
}
