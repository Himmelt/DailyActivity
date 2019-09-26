package org.soraworld.dailyactivity.core;

import org.soraworld.hocon.node.Setting;

import java.io.Serializable;

/**
 * @author Himmelt
 */
public class ActivationKit implements Serializable {
    @Setting
    private final int price = 0;
    @Setting
    private final String permission = "";

    public int getPrice() {
        return price;
    }

    public String getPermission() {
        return permission;
    }
}
