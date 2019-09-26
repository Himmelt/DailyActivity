package org.soraworld.dailyactivity.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.soraworld.dailyactivity.manager.ActivityManager;
import org.soraworld.violet.command.Sub;
import org.soraworld.violet.command.SubExecutor;
import org.soraworld.violet.inject.Command;
import org.soraworld.violet.inject.Inject;

/**
 * @author Himmelt
 */
@Command(name = "dailyactivity", usage = "/dailyactivity ", aliases = {"dac"})
public final class CommandActivity {

    @Inject
    private ActivityManager manager;

    @Sub(perm = "admin", usage = "/dac give <player> <activation>")
    public final SubExecutor give = (cmd, sender, args) -> {
        if (args.size() == 2) {
            Player player = Bukkit.getPlayer(args.first());
            if (player != null) {
                try {
                    manager.giveActivation(player, Integer.parseInt(args.get(1)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            cmd.sendUsage(sender);
        }
    };

    @Sub(perm = "admin", usage = "/dac take <player> <activation>")
    public final SubExecutor take = (cmd, sender, args) -> {
        if (args.size() == 2) {
            Player player = Bukkit.getPlayer(args.first());
            if (player != null) {
                try {
                    manager.giveActivation(player, -Integer.parseInt(args.get(1)));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        } else {
            cmd.sendUsage(sender);
        }
    };

    @Sub(usage = "/dac redeem <kit_name>")
    public final SubExecutor<Player> redeem = (cmd, player, args) -> {
        if (args.notEmpty()) {
            manager.tryRedeemKit(player, args.first());
        } else {
            cmd.sendUsage(player);
        }
    };

    @Sub(usage = "/dailykit info")
    public final SubExecutor<Player> info = (cmd, player, args) -> {
        manager.showInfo(player);
    };
}
