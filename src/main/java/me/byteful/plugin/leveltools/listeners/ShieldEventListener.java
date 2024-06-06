package me.byteful.plugin.leveltools.listeners;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.byteful.plugin.leveltools.LevelToolsPlugin;
import me.byteful.plugin.leveltools.util.LevelToolsUtil;

public class ShieldEventListener extends XPListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityHitShield(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) {
            return;
        }

        Player defender = (Player) e.getEntity();

        if (!defender.hasPermission("leveltools.enabled")) {
            return;
        }

        final String ltype = LevelToolsPlugin.getInstance().getConfig().getString("entity_list_type", "blacklist");
        final Set<EntityType> entities = LevelToolsPlugin.getInstance().getConfig().getStringList("entity_list")
                .stream()
                .map(str -> {
                    try {
                        return EntityType.valueOf(str);
                    } catch (Exception ignored) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (ltype != null && ltype.equalsIgnoreCase("whitelist") && !entities.contains(e.getDamager().getType())) {
            return;
        }

        if (ltype != null && ltype.equalsIgnoreCase("blacklist") && entities.contains(e.getDamager().getType())) {
            return;
        }

        if (defender.isBlocking() && e.getFinalDamage() == 0) {
            ItemStack mainHand = LevelToolsUtil.getHand(defender);
            ItemStack offHand = LevelToolsUtil.getOffHand(defender);

            if (LevelToolsUtil.isShield(mainHand.getType())) {
                handle(LevelToolsUtil.createLevelToolsItem(mainHand), defender, LevelToolsUtil.getCombatModifier(e.getEntityType()));
            } else if (LevelToolsUtil.isShield(offHand.getType())) {
                handle(LevelToolsUtil.createLevelToolsItem(offHand), defender, LevelToolsUtil.getCombatModifier(e.getEntityType()));
            }
        }
    }
}