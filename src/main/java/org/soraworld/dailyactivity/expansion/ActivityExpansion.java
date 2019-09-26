package org.soraworld.dailyactivity.expansion;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.soraworld.dailyactivity.manager.ActivityManager;

/**
 * @author Himmelt
 */
public class ActivityExpansion extends PlaceholderExpansion {

    private final ActivityManager manager;

    public ActivityExpansion(ActivityManager manager) {
        this.manager = manager;
    }

    @Override
    public String getIdentifier() {
        return manager.getPlugin().getId();
    }

    @Override
    public String getAuthor() {
        return "Himmelt";
    }

    @Override
    public String getVersion() {
        return manager.getPlugin().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if ("activation".equals(params)) {
            return manager.getActivation(player);
        }
        return "";
    }
}
