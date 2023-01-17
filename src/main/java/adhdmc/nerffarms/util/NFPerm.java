package adhdmc.nerffarms.util;

import org.bukkit.permissions.Permission;

public enum NFPerm {
    NF_RELOAD(new Permission("nerffarms.reload")),
    NF_BYPASS(new Permission("nerffarms.bypass")),
    NF_COMMANDS(new Permission("nerffarms.commands"));
    final Permission perm;

    NFPerm(org.bukkit.permissions.Permission perm) {
        this.perm = perm;
    }
    public Permission getPerm(){
        return perm;
    }
}
