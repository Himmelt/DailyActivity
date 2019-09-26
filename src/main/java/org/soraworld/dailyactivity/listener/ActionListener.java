package org.soraworld.dailyactivity.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.soraworld.dailyactivity.manager.ActivityManager;
import org.soraworld.violet.data.DataAPI;
import org.soraworld.violet.inject.EventListener;
import org.soraworld.violet.inject.Inject;

import java.util.UUID;

/**
 * @author Himmelt
 */
@EventListener
public class ActionListener implements Listener {
    @Inject
    ActivityManager manager;

    private static final String KILL_AMOUNT_KEY = "dailyactivity.killamount";
    private static final String CHAT_AMOUNT_KEY = "dailyactivity.chatamount";

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damagee = event.getEntity();
        if (damager instanceof Player && damagee instanceof Creature) {
            Player player = (Player) damager;
            UUID uuid = player.getUniqueId();
            if (event.getFinalDamage() >= ((Creature) damagee).getHealth()) {
                int amount = DataAPI.getTempInt(uuid, KILL_AMOUNT_KEY, 0);
                amount++;
                if (amount >= manager.getKillMax()) {
                    if (manager.giveActivation(player, 1)) {
                        manager.sendKey(player, "killOneActivation");
                    }
                    amount = 0;
                }
                DataAPI.setTempInt(uuid, KILL_AMOUNT_KEY, amount);
            }
        }
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        int amount = DataAPI.getTempInt(uuid, CHAT_AMOUNT_KEY, 0);
        amount++;
        if (amount >= manager.getChatMax()) {
            if (manager.giveActivation(event.getPlayer(), 1)) {
                Bukkit.getScheduler().runTaskLater(manager.getPlugin(), () -> manager.sendKey(event.getPlayer(), "chatOneActivation"), 2);
            }
            amount = 0;
        }
        DataAPI.setTempInt(uuid, CHAT_AMOUNT_KEY, amount);
    }
}
