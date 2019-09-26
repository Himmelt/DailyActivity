package org.soraworld.dailyactivity;

import me.clip.placeholderapi.PlaceholderAPIPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.dailyactivity.expansion.ActivityExpansion;
import org.soraworld.dailyactivity.manager.ActivityManager;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.util.UUID;

/**
 * @author Himmelt
 */
public final class DailyActivity extends SpigotPlugin<ActivityManager> {

    private static final String ONLINE_AMOUNT_KEY = "dailyactivity.onlineamount";

    @Override
    public void afterEnable() {
        try {
            PlaceholderAPIPlugin placeholderPlugin = (PlaceholderAPIPlugin) Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
            if (placeholderPlugin != null && placeholderPlugin.getExpansionManager().registerExpansion(ActivityExpansion.class.getConstructor(ActivityManager.class).newInstance(manager))) {
                manager.consoleKey("placeholder.expansionSuccess");
            } else {
                manager.consoleKey("placeholder.expansionFailed");
            }
        } catch (Throwable ignored) {
            manager.console(ChatColor.RED + "NameExpansion Construct Instance failed !!!");
            manager.consoleKey("placeholder.notHook");
        }
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                UUID uuid = player.getUniqueId();
                int amount = DataAPI.getTempInt(uuid, ONLINE_AMOUNT_KEY, 0);
                amount++;
                if (amount >= manager.getOnlineMax()) {
                    if (manager.giveActivation(player, 1)) {
                        manager.sendKey(player, "onlineOneActivation");
                    }
                    amount = 0;
                }
                DataAPI.setTemp(uuid, ONLINE_AMOUNT_KEY, amount);
            }
        }, 12000, 12000);
    }
}
