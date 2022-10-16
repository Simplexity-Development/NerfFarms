package adhdmc.nerffarms.config;

import adhdmc.nerffarms.NerfFarms;

public enum ModType {
    EXP, DROPS, BOTH, NEITHER;
    private static ModType modType;

    public static void setModType(){
        String modificationTypeString = NerfFarms.plugin.getConfig().getString("modification-type");
        try {
            modType = ModType.valueOf(modificationTypeString);
        } catch (IllegalArgumentException exception) {
            NerfFarms.plugin.getLogger().severe(modificationTypeString + " is not a valid modification type. Plugin will not function properly until this is fixed.");
            modType = ModType.NEITHER;
        }
    }
    public static ModType getModType() {
        return modType;
    }

}
