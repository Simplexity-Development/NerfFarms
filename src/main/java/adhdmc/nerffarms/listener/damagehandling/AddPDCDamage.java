package adhdmc.nerffarms.listener.damagehandling;

import adhdmc.nerffarms.util.Util;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class AddPDCDamage {
    /**
     * Adds nerfed damage to the mob's Persistent data container.
     * @param mobPDC Mob's Persistent Data Container
     * @param damage double Total Damage Dealt
     */
    public static void addPDCDamage(EntityDamageEvent event, PersistentDataContainer mobPDC, double damage) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;
        double totalHealth = Objects.requireNonNull(livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getValue();
        double damageTotal = mobPDC.getOrDefault(DamageListener.blacklistedDamage, PersistentDataType.DOUBLE, 0.0);
        if (damageTotal + damage > totalHealth) {
            double currentHealth = ((LivingEntity) event.getEntity()).getHealth();
            damageTotal += currentHealth;
            Util.debugLvl3("damageTotal + damage was greater than total health, adding current health to PDC, which is: " + currentHealth
            + ". Total damage is now " + damageTotal);
        } else {
            damageTotal += damage;
            Util.debugLvl3("added " + damage + " to mob's PDC. Total damage is now " + damageTotal);
        }
        mobPDC.set(DamageListener.blacklistedDamage, PersistentDataType.DOUBLE, damageTotal);
    }
}
