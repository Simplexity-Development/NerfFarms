package adhdmc.nerffarms.util;

import adhdmc.nerffarms.NerfFarms;
import org.bukkit.NamespacedKey;

public enum NFKey {
    BYPASS_MOB(new NamespacedKey(NerfFarms.getInstance(), "bypass-mob")),
    NERF_MOB(new NamespacedKey(NerfFarms.getInstance(), "nerf-mob")),
    BLACKLISTED_DAMAGE(new NamespacedKey(NerfFarms.getInstance(), "blacklisted-damage")),
    PICKED_UP_ITEM(new NamespacedKey(NerfFarms.getInstance(), "picked-up-item"));
    final NamespacedKey key;

    NFKey(org.bukkit.NamespacedKey key) {
        this.key = key;
    }

    public NamespacedKey getKey() {
        return key;
    }
}
