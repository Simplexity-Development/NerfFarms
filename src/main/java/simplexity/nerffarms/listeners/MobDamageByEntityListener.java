package simplexity.nerffarms.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftMob;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class MobDamageByEntityListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onMobDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof CraftMob mob)) return;
        ServerPlayer nmsPlayer = ((CraftPlayer) player).getHandle();
        Mob nmsMob = mob.getHandle();
        // Basic Checks: Gamemode, Targetability, Invulnerability, Line of Sight
        if (nmsMob.canAttack(nmsPlayer, TargetingConditions.forCombat())) {
            player.sendMessage("[canAttack()] Enemy can attack player.");
        }
        else { player.sendMessage("[canAttack()] Enemy cannot attack player."); }
        // Melee Damage Distance
        if (canMelee(nmsMob, nmsPlayer)) {
            player.sendMessage("[canMelee()] Enemy can attack player.");
        }
        else { player.sendMessage("[canMelee()] Enemy cannot attack player."); }
    }

    // Referenced from net.minecraft.world.entity.ai.goal.MeleeAttackGoal
    protected boolean canMelee(LivingEntity nmsMob, LivingEntity nmsPlayer) {
        return getAttackReachSqr(nmsMob, nmsPlayer) >= nmsMob.distanceToSqr(nmsPlayer.getX(), nmsPlayer.getY(), nmsPlayer.getZ());
    }

    // Referenced from net.minecraft.world.entity.ai.goal.MeleeAttackGoal
    protected double getAttackReachSqr(LivingEntity a, LivingEntity b) {
        return a.getBbWidth() * 2.0F * a.getBbWidth() * 2.0F + b.getBbWidth();
    }

}
