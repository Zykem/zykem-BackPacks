package gg.entry.backpacks;

import gg.entry.backpacks.handling.BackpackHandling;
import gg.entry.backpacks.handling.Config;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Backpacks extends JavaPlugin {

    private static Economy econ = null;
    private static Backpacks plugin;
    public Config config;

    public static Backpacks getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        this.saveDefaultConfig();
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BackpackHandling(), this);
        getCommand("buybackpack").setExecutor(new BackpackHandling());
        getCommand("backpack").setExecutor(new BackpackHandling());
        if (!setupEconomy() ) {
            Bukkit.getLogger().info("No VAULT Plugin found, bye!!!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupEconomy();

    }

    public Config getConfiguration() {
        return config;
    }
    public static String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        BackpackHandling.saveBackPacks();
        saveDefaultConfig();


    }
    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

}
