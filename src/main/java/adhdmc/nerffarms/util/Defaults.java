package adhdmc.nerffarms.util;

import adhdmc.nerffarms.NerfFarms;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Arrays;
import java.util.List;

public class Defaults {
    public static void configDefaults() {
        FileConfiguration config = NerfFarms.getInstance().getConfig();
        config.addDefault("debug", 0);
        config.addDefault("only-nerf-hostiles", true);
        config.addDefault("whitelisted-mobs", List.of(""));
        config.addDefault("blacklisted-mobs", List.of(""));
        config.addDefault("modification-type", "BOTH");
        config.addDefault("whitelisted-spawn-reasons", List.of("CUSTOM"));
        config.addDefault("blacklisted-spawn-reasons", List.of(""));
        config.addDefault("blacklisted-below", Arrays.asList("MAGMA_BLOCK", "HONEY_BLOCK", "LAVA"));
        config.addDefault("blacklisted-in", Arrays.asList("HONEY_BLOCK", "LAVA", "BUBBLE_COLUMN"));
        config.addDefault("allow-projectile-damage", true);
        config.addDefault("require-path", true);
        config.addDefault("require-open-surroundings", true);
        config.addDefault("require-line-of-sight", true);
        config.addDefault("skeletons-can-damage-creepers", true);
        config.addDefault("withers-can-damage-entities", true);
        config.addDefault("frogs-can-eat-slimes", true);
        config.addDefault("frogs-can-eat-magma-cubes", true);
        config.addDefault("iron-golems-can-damage-entities", false);
        config.addDefault("max-distance", 15);
        config.addDefault("max-height-difference", 10);
        config.addDefault("blacklisted-damage-types", Arrays.asList("BLOCK_EXPLOSION", "CONTACT", "CRAMMING",
                "DRAGON_BREATH", "DROWNING", "DRYOUT", "FALL", "FALLING_BLOCK", "FIRE", "FIRE_TICK", "FREEZE", "HOT_FLOOR",
                "LAVA", "LIGHTNING", "SUFFOCATION", "SUICIDE"));
        config.addDefault("max-blacklisted-damage-percent", 75);
        config.addDefault("whitelist-mobs-with-weakness", true);
        config.addDefault("whitelist-mobs-in-vehicles", false);
        config.addDefault("whitelist-named-mobs", false);
        config.addDefault("whitelist-leashed-mobs", true);
        config.addDefault("blacklisted-pickups-mob", List.of(""));
    }
}
