package net.neednot.farmnerf;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class Utils {
    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

}
