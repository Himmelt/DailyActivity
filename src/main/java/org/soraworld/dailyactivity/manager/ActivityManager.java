package org.soraworld.dailyactivity.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.dailyactivity.core.ActivationKit;
import org.soraworld.hocon.node.Setting;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.MainManager;
import org.soraworld.violet.manager.VManager;
import org.soraworld.violet.plugin.SpigotPlugin;
import org.soraworld.violet.util.ChatColor;

import java.nio.file.Path;
import java.util.Calendar;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author Himmelt
 */
@MainManager
public class ActivityManager extends VManager {

    @Setting
    private int killMax;
    @Setting
    private int chatMax;
    @Setting
    private int onlineMax;
    @Setting(comment = "comment.maxActivation")
    private long maxActivation = 100;
    @Setting
    private String progressSymbol = "█";
    @Setting
    private int progressLength = 10;
    @Setting(comment = "comment.giveKitCommand")
    private String giveKitCommand = "give ${player} ${name} 1";
    @Setting(comment = "comment.kitPrices")
    private final HashMap<String, ActivationKit> activationKits = new HashMap<>();

    public static final String LAST_REDEEM_KEY = "dailyactivity.lastredeem";
    public static final String KIT_ACTIVATION_KEY = "dailyactivity.activation";

    public ActivityManager(SpigotPlugin plugin, Path path) {
        super(plugin, path);
    }

    @Override
    public ChatColor defChatColor() {
        return ChatColor.YELLOW;
    }

    public void tryRedeemKit(Player player, String name) {
        ActivationKit kit = activationKits.get(name);
        if (kit != null) {
            UUID uuid = player.getUniqueId();
            int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
            int lastDay = DataAPI.getStoreInt(uuid, LAST_REDEEM_KEY);
            if (today != lastDay) {
                String command = giveKitCommand.replaceAll("\\$\\{player}", player.getName()).replaceAll("\\$\\{name}", name);
                long activation = DataAPI.getStoreLong(uuid, KIT_ACTIVATION_KEY);
                if (hasPermission(player, kit.getPermission())) {
                    if (activation >= kit.getPrice()) {
                        giveActivation(player, -kit.getPrice());
                        DataAPI.setStoreInt(uuid, LAST_REDEEM_KEY, today);
                        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
                    } else {
                        sendKey(player, "notEnoughActivation");
                    }
                } else {
                    sendKey(player, "hasNoPerm", kit.getPermission());
                }
            } else {
                sendKey(player, "alreadyRedeemToday");
            }
        }
    }

    public void showInfo(Player player) {
        sendKey(player, getActivation(player));
        sendKey(player, "infoActivation", DataAPI.getStoreLong(player.getUniqueId(), KIT_ACTIVATION_KEY));
    }

    public String getActivation(Player player) {
        long activation = DataAPI.getStoreLong(player.getUniqueId(), KIT_ACTIVATION_KEY);
        long size = Math.round(1.0D * activation / maxActivation * progressLength);
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i <= progressLength; i++) {
            if (i <= size) {
                builder.append(ChatColor.GREEN).append(progressSymbol);
            } else {
                builder.append(ChatColor.GRAY).append(progressSymbol);
            }
        }
        return builder.toString();
    }

    public boolean giveActivation(Player player, int amount) {
        long activation = DataAPI.getStoreLong(player.getUniqueId(), KIT_ACTIVATION_KEY);
        if (activation + amount <= maxActivation) {
            DataAPI.setStoreLong(player.getUniqueId(), KIT_ACTIVATION_KEY, activation + amount);
            return true;
        } else {
            DataAPI.setStoreLong(player.getUniqueId(), KIT_ACTIVATION_KEY, maxActivation);
            return false;
        }
    }

    public int getKillMax() {
        return killMax;
    }

    public int getChatMax() {
        return chatMax;
    }

    public int getOnlineMax() {
        return onlineMax;
    }
}
