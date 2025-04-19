package dev.lurien.bot.configuration;

import org.bukkit.plugin.Plugin;

public class LinkedAccountsConfig extends LurienConfiguration{
    public LinkedAccountsConfig(Plugin plugin) {
        super("cuentas-vinculadas", plugin.getDataFolder(), plugin);
    }
}
