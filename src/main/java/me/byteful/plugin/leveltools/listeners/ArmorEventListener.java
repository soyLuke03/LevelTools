package me.byteful.plugin.leveltools.listeners;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import me.byteful.plugin.leveltools.LevelToolsPlugin;
import me.byteful.plugin.leveltools.util.LevelToolsUtil;

public class ArmorEventListener extends XPListener {
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityHitHelmet(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player) {

      Player defender = (Player) e.getEntity();

      if (defender == null || !defender.hasPermission("leveltools.enabled")) {
        return;
      }

      if (LevelToolsUtil.getHelmet(defender) != null) {
        final ItemStack helmet = LevelToolsUtil.getHelmet(defender);

        final String ltype = LevelToolsPlugin.getInstance().getConfig().getString("entity_list_type", "blacklist");
        final Set<EntityType> entities = LevelToolsPlugin.getInstance().getConfig().getStringList("entity_list")
            .stream()
            .map(
                str -> {
                  try {
                    return EntityType.valueOf(str);
                  } catch (Exception ignored) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (ltype != null
            && ltype.equalsIgnoreCase("whitelist")
            && !entities.contains(e.getDamager().getType())) {
          return;
        }

        if (ltype != null
            && ltype.equalsIgnoreCase("blacklist")
            && entities.contains(e.getDamager().getType())) {
          return;
        }

        if (LevelToolsUtil.getDamageFromValidCause(e.getCause())) {
          handle(
              LevelToolsUtil.createLevelToolsItem(helmet),
              defender,
              LevelToolsUtil.getCombatModifier(e.getDamager().getType()));
        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityHitChestplate(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player) {

      Player defender = (Player) e.getEntity();

      if (defender == null || !defender.hasPermission("leveltools.enabled")) {
        return;
      }

      if (LevelToolsUtil.getChestplate(defender) != null) {
        final ItemStack chestplate = LevelToolsUtil.getChestplate(defender);

        final String ltype = LevelToolsPlugin.getInstance().getConfig().getString("entity_list_type", "blacklist");
        final Set<EntityType> entities = LevelToolsPlugin.getInstance().getConfig().getStringList("entity_list")
            .stream()
            .map(
                str -> {
                  try {
                    return EntityType.valueOf(str);
                  } catch (Exception ignored) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (ltype != null
            && ltype.equalsIgnoreCase("whitelist")
            && !entities.contains(e.getDamager().getType())) {
          return;
        }

        if (ltype != null
            && ltype.equalsIgnoreCase("blacklist")
            && entities.contains(e.getDamager().getType())) {
          return;
        }

        if (LevelToolsUtil.getDamageFromValidCause(e.getCause())) {
          handle(
              LevelToolsUtil.createLevelToolsItem(chestplate),
              defender,
              LevelToolsUtil.getCombatModifier(e.getDamager().getType()));

        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityHitArmorLeggings(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player) {

      Player defender = (Player) e.getEntity();

      if (defender == null || !defender.hasPermission("leveltools.enabled")) {
        return;
      }

      if (LevelToolsUtil.getLeggings(defender) != null) {
        final ItemStack leggings = LevelToolsUtil.getLeggings(defender);

        final String ltype = LevelToolsPlugin.getInstance().getConfig().getString("entity_list_type", "blacklist");
        final Set<EntityType> entities = LevelToolsPlugin.getInstance().getConfig().getStringList("entity_list")
            .stream()
            .map(
                str -> {
                  try {
                    return EntityType.valueOf(str);
                  } catch (Exception ignored) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (ltype != null
            && ltype.equalsIgnoreCase("whitelist")
            && !entities.contains(e.getDamager().getType())) {
          return;
        }

        if (ltype != null
            && ltype.equalsIgnoreCase("blacklist")
            && entities.contains(e.getDamager().getType())) {
          return;
        }

        if (LevelToolsUtil.getDamageFromValidCause(e.getCause())) {
          handle(
              LevelToolsUtil.createLevelToolsItem(leggings),
              defender,
              LevelToolsUtil.getCombatModifier(e.getDamager().getType()));

        }
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onEntityHitBoots(EntityDamageByEntityEvent e) {
    if (e.getEntity() instanceof Player) {

      Player defender = (Player) e.getEntity();

      if (defender == null || !defender.hasPermission("leveltools.enabled")) {
        return;
      }

      if (LevelToolsUtil.getBoots(defender) != null) {
        final ItemStack boots = LevelToolsUtil.getBoots(defender);

        final String ltype = LevelToolsPlugin.getInstance().getConfig().getString("entity_list_type", "blacklist");
        final Set<EntityType> entities = LevelToolsPlugin.getInstance().getConfig().getStringList("entity_list")
            .stream()
            .map(
                str -> {
                  try {
                    return EntityType.valueOf(str);
                  } catch (Exception ignored) {
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (ltype != null
            && ltype.equalsIgnoreCase("whitelist")
            && !entities.contains(e.getDamager().getType())) {
          return;
        }

        if (ltype != null
            && ltype.equalsIgnoreCase("blacklist")
            && entities.contains(e.getDamager().getType())) {
          return;
        }

        if (LevelToolsUtil.getDamageFromValidCause(e.getCause())) {
          handle(
              LevelToolsUtil.createLevelToolsItem(boots),
              defender,
              LevelToolsUtil.getCombatModifier(e.getDamager().getType()));
        }
      }
    }
  }

}
