package me.byteful.plugin.leveltools.listeners;

import static redempt.redlib.misc.FormatUtils.formatMoney;

import com.cryptomorin.xseries.XSound;
import java.util.List;
import me.byteful.plugin.leveltools.LevelToolsPlugin;
import me.byteful.plugin.leveltools.api.event.LevelToolsLevelIncreaseEvent;
import me.byteful.plugin.leveltools.api.event.LevelToolsXPIncreaseEvent;
import me.byteful.plugin.leveltools.api.item.LevelToolsItem;
import me.byteful.plugin.leveltools.util.LevelToolsUtil;
import me.byteful.plugin.leveltools.util.Text;
import me.byteful.plugin.leveltools.util.XPBooster;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class XPListener implements Listener {

  protected void handle(LevelToolsItem tool, Player player, double modifier) {
    World world = player.getWorld();

    final List<String> disabledWorlds = LevelToolsPlugin.getInstance().getConfig().getStringList("disabled_worlds");
    if (disabledWorlds.contains(world.getName())) {
      return;
    }

    modifier = Math.max(
        0,
        XPBooster.apply(
            player, modifier)); // It's best if we just prevent negative XP addition :)
    double newXp = LevelToolsUtil.round(tool.getXp() + modifier, 1);

    final LevelToolsXPIncreaseEvent xpEvent = new LevelToolsXPIncreaseEvent(tool, player, newXp, newXp, false);
    Bukkit.getPluginManager().callEvent(xpEvent);

    if (xpEvent.isCancelled()) {
      return;
    }

    tool.setXp(xpEvent.getNewXp());

    if (LevelToolsPlugin.getInstance().getConfig().getBoolean("display.actionBar.enabled")) {
      final String text = Text.colorize(
          LevelToolsPlugin.getInstance()
              .getConfig()
              .getString("display.actionBar.text")
              .replace(
                  "{progress_bar}",
                  LevelToolsUtil.createDefaultProgressBar(tool.getXp(), tool.getMaxXp()))
              .replace("{xp}", String.valueOf(tool.getXp()))
              .replace("{max_xp}", String.valueOf(tool.getMaxXp()))
              .replace("{level}", String.valueOf(tool.getLevel()))
              .replace("{max_xp_formatted}", formatMoney(tool.getMaxXp()))
              .replace("{xp_formatted}", formatMoney(tool.getXp())));
      LevelToolsUtil.sendActionBar(player, text);
    }

    if (tool.getXp() >= tool.getMaxXp()) {
      int newLevel = tool.getLevel() + 1;

      if (newLevel >= LevelToolsPlugin.getInstance().getConfig().getInt("max_level")) {
        return;
      }

      final LevelToolsLevelIncreaseEvent levelEvent = new LevelToolsLevelIncreaseEvent(tool, player, newLevel, false);
      Bukkit.getPluginManager().callEvent(levelEvent);

      if (levelEvent.isCancelled()) {
        return;
      }

      tool.setXp(LevelToolsUtil.round(Math.abs(tool.getXp() - tool.getMaxXp()), 1));
      tool.setLevel(levelEvent.getNewLevel());

      final ConfigurationSection soundCs = LevelToolsPlugin.getInstance().getConfig()
          .getConfigurationSection("level_up_sound");

      final String sound;
      final XSound parsed;

      if (soundCs != null) {
        sound = soundCs.getString("sound", null);
        if (sound != null) {
          parsed = XSound.matchXSound(sound).orElse(null);

          if (parsed != null && parsed.isSupported()) {
            if (parsed.parseSound() != null) {
              player.playSound(
                  player.getLocation(),
                  parsed.parseSound(),
                  (float) soundCs.getDouble("pitch"),
                  (float) soundCs.getDouble("volume"));
            }
          }
        }
      }
    }

    // Checks if the tool is a tool or weapon and its slot and returns it in his
    // position
    if (LevelToolsUtil.isSupportedTool(tool.getItemStack().getType())) {
      if (LevelToolsUtil.getHand(player).getType().equals(Material.SHIELD)
          && tool.getItemStack().getType().equals(Material.SHIELD)) {
        LevelToolsUtil.setHand(player, tool.getItemStack());
      } else if (LevelToolsUtil.getOffHand(player).getType().equals(Material.SHIELD)
          && tool.getItemStack().getType().equals(Material.SHIELD)) {
        LevelToolsUtil.setOffHand(player, tool.getItemStack());
      } else {
        LevelToolsUtil.setHand(player, tool.getItemStack());
      }
    }

    // Checks if the tool is in some Armor slot and returns it in his position
    else if (LevelToolsUtil.isSupportedArmor(tool.getItemStack().getType())) {
      if (LevelToolsUtil.isHelmet(tool.getItemStack().getType())) {
        LevelToolsUtil.setHelmet(player, tool.getItemStack());
      } else if (LevelToolsUtil.isChestplate(tool.getItemStack().getType())) {
        LevelToolsUtil.setChestplate(player, tool.getItemStack());
      } else if (LevelToolsUtil.isLeggings(tool.getItemStack().getType())) {
        LevelToolsUtil.setLeggings(player, tool.getItemStack());
      } else if (LevelToolsUtil.isBoots(tool.getItemStack().getType())) {
        LevelToolsUtil.setBoots(player, tool.getItemStack());
      }
    }

    // Update LevelTools stuff before running rewards to prevent any weird errors.
    LevelToolsUtil.handleReward(tool, player);
  }
}
