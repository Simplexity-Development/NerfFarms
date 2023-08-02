# Implementation Notes

> **Note**
> 
> For the most part, NMS and related libraries and source code will be referenced using their full path.
> 
> Our code will be referenced without a path.

## Checks and Values

### Targetable

A target is considered targetable if the enemy can lock on them and attempt attacks on the player.

`net.minecraft.world.entity.LivingEntity::canAttack(LivingEntity, TargetingConditions)`

- Using `TargetingConditions.forCombat()`, the checks are...
  - Will not attack itself.
  - Target is not in spectator and is alive.
  - Target is not the player in Peaceful mode.
  - Target is not invulnerable.
  - Attacker can attack the Target's Entity Type.
  - Target is not the Attacker's Ally.
  - Attacker has some positive range and passes calculation regarding invisibility detection.
    - Some mobs will ignore invisibility like the Evoker.
  - If line of sight needs to be checked and the Attacker is a Mob, check for line of sight to the Target.
    - Ignoring line of sight occurs with mobs like the Vex and Evoker.
  - The targeting conditions can be modified with the additional methods provided by the class.

### Attackable

A target is considered attackable if it is targetable and has the potential to be hit.

- Attackers without any ranged attacks must resort to melee attacks.
- Attackers with ranged attacks should consider a target attackable if they can fire towards the target successfully.
  - This does not necessarily mean the target can be hit, just that the projectile is fired towards the target.
    - May pose an issue since Skeletons and Pillagers both shoot even if the arrow cannot possibly hit the target.

`canMeleeAttack()` incorporates parts of the MeleeAttackGoal.

- `net.minecraft.world.entity.ai.goal.MeleeAttackGoal::getAttackReachSqr(LivingEntity)`
  - This function is modified for our use.
- `net.minecraft.world.entity.ai.goal.MeleeAttackGoal::canUse()`
  - This function contains a check for the attackable region around the Attacker.

### Reachable

A target is considered reachable if the attacker can pathfind close enough to attack.

- Attackers without any ranged attacks must be able to close the distance to the Target.
- This does not really concern ranged attackers as much, but since ranged attacks have a maximum distance, may be worth looking to if it poses as an issue.

### Environmental Damage

Damage is considered environmental if it is one of the configured blacklisted damage reasons. This should prevent bringing a mob's health down to a level where they can be killed with very little effort
