package me.byteful.plugin.leveltools.util;

import static me.byteful.plugin.leveltools.util.Text.colorize;
import static me.byteful.plugin.leveltools.util.Text.decolorize;
import static redempt.redlib.misc.FormatUtils.formatMoney;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.messages.ActionBar;
import com.google.common.base.Strings;
import de.tr7zw.changeme.nbtapi.NBTItem;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import me.byteful.plugin.leveltools.LevelToolsPlugin;
import me.byteful.plugin.leveltools.api.RewardType;
import me.byteful.plugin.leveltools.api.item.LevelToolsItem;
import me.byteful.plugin.leveltools.api.item.impl.NBTLevelToolsItem;
import me.byteful.plugin.leveltools.api.item.impl.PDCLevelToolsItem;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.math.NumberUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import redempt.redlib.RedLib;
import xyz.mackan.ItemNames.ItemNames;

public final class LevelToolsUtil {
  private static final String LORE_PREFIX = "&f&l&o&n&m&k";

  public static String getProgressBar(
      double percent,
      int totalBars,
      String prefixSymbol,
      String suffixSymbol,
      String barSymbol,
      ChatColor prefixColor,
      ChatColor suffixColor,
      ChatColor completedColor,
      ChatColor placeholderColor) {
    int progressBars = roundDown(totalBars * percent);

    return colorize(
        ""
            + prefixColor
            + prefixSymbol
            + Strings.repeat("" + completedColor + barSymbol, progressBars)
            + Strings.repeat("" + placeholderColor + barSymbol, Math.abs(totalBars - progressBars))
            + suffixColor
            + suffixSymbol);
  }

  public static double getCombatModifier(EntityType entityType) {
    final ConfigurationSection combat_xp_modifiers = LevelToolsPlugin.getInstance().getConfig()
        .getConfigurationSection("combat_xp_modifiers");

    final Double custom = getCustomModifier(combat_xp_modifiers, entityType.name());
    if (custom != null)
      return custom;

    final ConfigurationSection default_combat_xp_modifier = LevelToolsPlugin.getInstance()
        .getConfig()
        .getConfigurationSection("default_combat_xp_modifier");

    return calculateFromRange(default_combat_xp_modifier);
  }

  public static double getBlockModifier(Material material) {
    final ConfigurationSection block_xp_modifiers = LevelToolsPlugin.getInstance().getConfig()
        .getConfigurationSection("block_xp_modifiers");

    final Double custom = getCustomModifier(block_xp_modifiers, material.name());
    if (custom != null)
      return custom;

    final ConfigurationSection default_block_xp_modifier = LevelToolsPlugin.getInstance()
        .getConfig()
        .getConfigurationSection("default_block_xp_modifier");

    return calculateFromRange(default_block_xp_modifier);
  }

  private static Double getCustomModifier(ConfigurationSection config, String type) {
    for (String modifier : config.getKeys(false)) {
      if (modifier.equalsIgnoreCase(type)) {
        final ConfigurationSection modifierCs = config.getConfigurationSection(modifier);

        return calculateFromRange(modifierCs);
      }
    }
    return null;
  }

  @NotNull
  private static Double calculateFromRange(ConfigurationSection modifierCs) {
    double min = modifierCs.getDouble("min");
    double max = modifierCs.getDouble("max");
    if (Double.compare(min, max) == 0) {
      return min;
    }
    if (min > max) {
      double hold = min;
      min = max;
      max = hold;
    }
    return round(ThreadLocalRandom.current().nextDouble(min, max), 1);
  }

  public static boolean isPickaxe(Material material) {
    return material.name().endsWith("_PICKAXE");
  }

  public static boolean isAxe(Material material) {
    return material.name().endsWith("_AXE");
  }

  public static boolean isShovel(Material material) {
    return material.name().endsWith("_SHOVEL");
  }

  public static boolean isSword(Material material) {
    return material.name().endsWith("_SWORD");
  }

  public static boolean isTrident(Material material) {
    return material.name().endsWith("TRIDENT");
  }

  public static boolean isHoe(Material material) {
    return material.name().endsWith("_HOE");
  }

  public static boolean isFishingRod(Material material) {
    return material.name().endsWith("FISHING_ROD");
  }

  public static boolean isShield(Material material) {
    return material.name().endsWith("SHIELD");
  }

  public static boolean isShears(Material material) {
    return material.name().endsWith("SHEARS");
  }

  public static boolean isProjectileShooter(Material material) {
    return material == XMaterial.BOW.parseMaterial()
        || (RedLib.MID_VERSION >= 14 && material == XMaterial.CROSSBOW.parseMaterial());
  }

  public static boolean isSupportedTool(Material material) {
    return isPickaxe(material)
        || isAxe(material)
        || isShovel(material)
        || isSword(material)
        || isProjectileShooter(material)
        || isShield(material)
        || isFishingRod(material)
        || isHoe(material)
        || isShears(material)
        || isTrident(material);
  }

  public static boolean isHelmet(Material material) {
    return material.name().endsWith("_HELMET");
  }

  public static boolean isChestplate(Material material) {
    return material.name().endsWith("_CHESTPLATE");
  }

  public static boolean isLeggings(Material material) {
    return material.name().endsWith("_LEGGINGS");
  }

  public static boolean isBoots(Material material) {
    return material.name().endsWith("_BOOTS");
  }

  public static boolean isSupportedArmor(Material material) {
    return isHelmet(material)
        || isChestplate(material)
        || isLeggings(material)
        || isBoots(material);
  }

  public static ItemStack getHand(Player player) {
    return RedLib.MID_VERSION >= 9
        ? player.getInventory().getItemInMainHand().clone()
        : player.getItemInHand().clone();
  }

  public static ItemStack getOffHand(Player player) {
    ItemStack offHand = player.getInventory().getItemInOffHand();
    return offHand != null ? offHand.clone() : null;
}

public static ItemStack getHelmet(Player player) {
    ItemStack helmet = player.getInventory().getHelmet();
    return helmet != null ? helmet.clone() : null;
}

  public static ItemStack getChestplate(Player player) {
    ItemStack chestplate = player.getInventory().getChestplate();
    return chestplate != null ? chestplate.clone() : null;
}

  public static ItemStack getLeggings(Player player) {
    ItemStack leggings = player.getInventory().getLeggings();
    return leggings!= null? leggings.clone() : null;
}

  public static ItemStack getBoots(Player player) {
    ItemStack boots = player.getInventory().getBoots();
    return boots != null ? boots.clone() : null;
}

  public static void setHand(Player player, ItemStack stack) {
    if (RedLib.MID_VERSION >= 9) {
      player.getInventory().setItemInMainHand(stack);
    } else {
      player.setItemInHand(stack);
    }
  }

  public static void setOffHand(Player player, ItemStack stack) {
    if (RedLib.MID_VERSION >= 9) {
      player.getInventory().setItemInOffHand(stack);
    }
  }

  public static void setHelmet(Player player, ItemStack stack) {
    player.getInventory().setHelmet(stack);
  }

  public static void setChestplate(Player player, ItemStack stack) {
    player.getInventory().setChestplate(stack);
  }

  public static void setLeggings(Player player, ItemStack stack) {
    player.getInventory().setLeggings(stack);
  }

  public static void setBoots(Player player, ItemStack stack) {
    player.getInventory().setBoots(stack);
  }

  public static boolean getDamageFromValidCause(DamageCause damageCause) {
    if (damageCause.equals(DamageCause.BLOCK_EXPLOSION)
        || damageCause.equals(DamageCause.ENTITY_ATTACK)
        || damageCause.equals(DamageCause.ENTITY_EXPLOSION)
        || damageCause.equals(DamageCause.ENTITY_SWEEP_ATTACK)
        || damageCause.equals(DamageCause.FALL)
        || damageCause.equals(DamageCause.FALLING_BLOCK)
        || damageCause.equals(DamageCause.LIGHTNING)
        || damageCause.equals(DamageCause.PROJECTILE)
        || damageCause.equals(DamageCause.THORNS)) {
      return true;
    }

    return false;
  }

  public static String createDefaultProgressBar(double xp, double maxXp) {
    ConfigurationSection cs = LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("progress_bar");

    return LevelToolsUtil.getProgressBar(
        (xp / maxXp),
        cs.getInt("total_bars"),
        cs.getString("prefix_symbol"),
        cs.getString("suffix_symbol"),
        cs.getString("bar_symbol"),
        ChatColor.getByChar(cs.getString("prefix_color")),
        ChatColor.getByChar(cs.getString("suffix_color")),
        ChatColor.getByChar(cs.getString("completed_color")),
        ChatColor.getByChar(cs.getString("placeholder_color")));
  }

  public static double round(double value, int places) {
    if (places < 0)
      throw new IllegalArgumentException();

    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);

    return bd.doubleValue();
  }

  public static int roundDown(double value) {
    BigDecimal bd = BigDecimal.valueOf(value);
    bd = bd.setScale(1, RoundingMode.DOWN);

    return bd.intValue();
  }

  public static LevelToolsItem createLevelToolsItem(ItemStack stack) {
    if (RedLib.MID_VERSION >= 14) {
      if (RedLib.MID_VERSION < 18) {
        final NBTItem nbt = new NBTItem(stack);
        if (nbt.getKeys().stream().anyMatch(s -> s.startsWith("levelTools"))) {
          return new NBTLevelToolsItem(
              stack); // Support tools created with "old" NBT system for 1.14+.
        }
      }

      return new PDCLevelToolsItem(stack);
    } else {
      return new NBTLevelToolsItem(stack);
    }
  }

  public static String getLocalizedName(ItemStack item) {
    return RedLib.MID_VERSION > 16
        ? Objects.requireNonNull(item.getItemMeta()).getLocalizedName()
        : ItemNames.getItemName(item);
  }

  public static ItemStack buildItemStack(
      ItemStack stack, Map<Enchantment, Integer> enchantments, int level, double xp, double maxXp) {
    final ConfigurationSection cs = LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("display");

    final ItemMeta meta = stack.getItemMeta();
    assert meta != null : "ItemMeta is null! Should not happen.";
    final String progressBar = LevelToolsUtil.createDefaultProgressBar(xp, maxXp);
    if (cs.getBoolean("name.enabled")) {
      meta.setDisplayName(
          Text.colorize(
              cs.getString("name.text")
                  .replace("{item}", getLocalizedName(stack))
                  .replace("{level}", String.valueOf(level))
                  .replace("{xp}", String.valueOf(xp))
                  .replace("{max_xp}", String.valueOf(maxXp))
                  .replace("{max_xp_formatted}", formatMoney(maxXp))
                  .replace("{xp_formatted}", formatMoney(xp))
                  .replace("{progress_bar}", progressBar)));
    }
    if (cs.getBoolean("lore.enabled")) {
      List<String> lines = cs.getStringList("lore.lines").stream()
          .map(str -> LORE_PREFIX + str)
          .map(
              str -> colorize(
                  str.replace("{level}", String.valueOf(level))
                      .replace("{xp}", String.valueOf(xp))
                      .replace("{max_xp}", String.valueOf(maxXp))
                      .replace("{progress_bar}", progressBar))
                  .replace("{max_xp_formatted}", formatMoney(maxXp))
                  .replace("{xp_formatted}", formatMoney(xp)))
          .collect(Collectors.toList());
      smartSetLore(meta, lines);
    }
    for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
      meta.addEnchant(entry.getKey(), entry.getValue(), true);
    }
    if (LevelToolsPlugin.getInstance().getConfig().getBoolean("hide_attributes", true)) {
      meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    }
    stack.setItemMeta(meta);

    return stack;
  }

  private static void smartSetLore(@NotNull ItemMeta meta, @NotNull List<String> toAdd) {
    final List<String> lore = meta.getLore();
    if (!meta.hasLore() || lore == null) {
      meta.setLore(toAdd);

      return;
    }

    final int start = findPrefixStart(lore);
    if (start == -1) {
      lore.addAll(toAdd);
      meta.setLore(lore);

      return;
    }
    final int end = start + toAdd.size();
    if (end > lore.size()) {
      meta.setLore(toAdd);

      return;
    }
    final List<String> sub = lore.subList(start, end);
    sub.clear();
    sub.addAll(toAdd);
    meta.setLore(lore);
  }

  private static int findPrefixStart(@NotNull List<String> lore) {
    for (int i = 0; i < lore.size(); i++) {
      if (decolorize(lore.get(i)).startsWith(LORE_PREFIX)) {
        return i;
      }
    }

    return -1;
  }

  public static void handleReward(LevelToolsItem tool, Player player) {
    final ConfigurationSection rewardCs = getCsFromType(tool.getItemStack().getType());

    for (String key : rewardCs.getKeys(false)) {
      if (!NumberUtils.isNumber(key)) {
        continue;
      }

      final int keyNum = Integer.parseInt(key);
      if (keyNum < 0)
        continue;
      if (keyNum != tool.getLevel())
        continue;
      if (tool.getLastHandledReward() == keyNum)
        return;

      tool.setLastHandledReward(keyNum);
      setItemInHisMainSlot(tool, player);
      for (String rewardStr : rewardCs.getStringList(key)) {
        final String[] split = rewardStr.split(" ");

        if (split.length < 2) {
          continue;
        }

        RewardType.fromConfigKey(
            split[0].toLowerCase(Locale.ROOT).trim().replace(" ", "-").replace("_", "-"))
            .ifPresent(
                type -> {
                  type.apply(tool, split, player);
                  if (type.isShouldUpdate()) {

                    setItemInHisMainSlot(tool, player);
                  }
                });
      }

      return;
    }
  }

  private static void setItemInHisMainSlot(LevelToolsItem tool, Player player) {
    // Checks if the tool is a tool or weapon and its slot and returns it in his
    // position
    if (LevelToolsUtil.isSupportedTool(tool.getItemStack().getType())) {
      if (LevelToolsUtil.getHand(player).getType().equals(Material.SHIELD)
          && tool.getItemStack().getType().equals(Material.SHIELD)) {
        setHand(player, tool.getItemStack());
      } else if (LevelToolsUtil.getOffHand(player).getType().equals(Material.SHIELD)
          && tool.getItemStack().getType().equals(Material.SHIELD)) {
        setOffHand(player, tool.getItemStack());
      } else {
        setHand(player, tool.getItemStack());
      }
    }
    // Checks if the tool is in some Armor slot and returns it in his position
    else if (LevelToolsUtil.isSupportedArmor(tool.getItemStack().getType())) {
      if (LevelToolsUtil.isHelmet(tool.getItemStack().getType())) {
        setHelmet(player, tool.getItemStack());
      } else if (LevelToolsUtil.isChestplate(tool.getItemStack().getType())) {
        setChestplate(player, tool.getItemStack());
      } else if (LevelToolsUtil.isLeggings(tool.getItemStack().getType())) {
        setLeggings(player, tool.getItemStack());
      } else if (LevelToolsUtil.isBoots(tool.getItemStack().getType())) {
        setBoots(player, tool.getItemStack());

      }
    }
  }

  private static ConfigurationSection getCsFromType(Material material) {
    if (LevelToolsUtil.isSword(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("sword_rewards");
    } else if (LevelToolsUtil.isProjectileShooter(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("bow_rewards");
    } else if (LevelToolsUtil.isAxe(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("axe_rewards");
    } else if (LevelToolsUtil.isHoe(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("hoe_rewards");
    } else if (LevelToolsUtil.isPickaxe(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("pickaxe_rewards");
    } else if (LevelToolsUtil.isShield(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("shield_rewards");
    } else if (LevelToolsUtil.isShears(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("shears_rewards");
    } else if (LevelToolsUtil.isShovel(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("shovel_rewards");
    } else if (LevelToolsUtil.isTrident(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("trident_rewards");
    } else if (LevelToolsUtil.isFishingRod(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("fishing_rod_rewards");
    } else if (LevelToolsUtil.isHelmet(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("helmet_rewards");
    } else if (LevelToolsUtil.isChestplate(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("chestplate_rewards");
    } else if (LevelToolsUtil.isLeggings(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("leggings_rewards");
    } else if (LevelToolsUtil.isBoots(material)) {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("boots_rewards");
    } else {
      return LevelToolsPlugin.getInstance().getConfig().getConfigurationSection("tool_rewards");
    }
  }

  public static void sendActionBar(Player player, String msg) {
    if (RedLib.MID_VERSION > 12) {
      player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(msg));
    } else {
      ActionBar.sendActionBar(player, msg);
    }
  }
}
