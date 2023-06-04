package simplexity.nerffarms.config;

import simplexity.nerffarms.NerfFarms;

public enum ConfigToggle {
    //Bypass toggles
    ONLY_NERF_HOSTILES(true),
    IGNORE_MOBS_ATTACKING_MOBS(true),
    ALLOW_SKELETON_CREEPER_DAMAGE(true),
    ALLOW_WITHER_DAMAGE(true),
    ALLOW_FROG_SLIME_DAMAGE(true),
    ALLOW_FROG_MAGMA_CUBE_DAMAGE(true),
    ALLOW_IRON_GOLEM_DAMAGE(false),
    //Nerfing checks
    REQUIRE_PATH(true),
    REQUIRE_LINE_OF_SIGHT(true),
    ALLOW_PROJECTILE_DAMAGE(true),
    WHITELIST_WEAKNESS(true),
    WHITELIST_VEHICLES(false),
    WHITELIST_NAMED(false),
    WHITELIST_LEASHED(true);

    boolean toggle;
    ConfigToggle(boolean toggle){
        this.toggle = toggle;
    }

    public boolean isEnabled() {
        return toggle;
    }

    private void setEnabled(boolean toggle){
        this.toggle = toggle;
    }

    public static void reloadToggles(){
    }
}
