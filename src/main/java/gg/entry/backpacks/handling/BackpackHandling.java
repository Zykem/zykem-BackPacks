package gg.entry.backpacks.handling;

import gg.entry.backpacks.Backpacks;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BackpackHandling implements Listener, CommandExecutor {

    public static HashMap<OfflinePlayer, Inventory> playerBackpack;

    public static HashMap<String, Integer> accessPlecak;
    private static File file;
    private static FileConfiguration config;


    public BackpackHandling() {

        accessPlecak = new HashMap<>();
        playerBackpack = new HashMap<>();

        file = new File(Backpacks.getPlugin().getDataFolder(), "player_backpacks.yml");
        config = YamlConfiguration.loadConfiguration(file);
    }

    public static void loadBackPacks() {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            if (config.contains(player.getName())) {
                String inventory = config.getString(player.getName());
                playerBackpack.put(player, inventoryFromBase64(inventory));
            }
        }
    }

    public static void saveBackPacks() {
        for (Map.Entry<OfflinePlayer, Inventory> entry : playerBackpack.entrySet()) {
            config.set(entry.getKey().getName(), inventoryToBase64(entry.getValue()));
        }

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String inventoryToBase64(Inventory inventory) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
            dataOutput.writeInt(inventory.getSize());
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static Inventory inventoryFromBase64(String base64) {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(base64));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.createInventory(null, dataInput.readInt());

            for (int i = 0; i < inventory.getSize(); i++) {
                inventory.setItem(i, (ItemStack) dataInput.readObject());
            }
            dataInput.close();
            return inventory;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Economy eco = Backpacks.getEconomy();
        Player p = (Player) sender;
        Config access = Backpacks.getPlugin().config;
        boolean hasacc = Backpacks.getPlugin().getConfig().getBoolean(p.getName() + ".access");
        p.sendMessage(String.valueOf(hasacc));


        if(sender instanceof Player) {



            if(command.getName().equalsIgnoreCase("backpack")) {
//accessPlecak.containsKey(p.getName())
                if(hasacc) {
                    if (playerBackpack.containsKey(p)) {

                        p.openInventory(playerBackpack.get(p));

                    } else {

                        Inventory inv = Bukkit.createInventory(null, 18);
                        p.openInventory(inv);
                        playerBackpack.put(p, inv);

                    }
                } else {

                    p.sendTitle(Backpacks.color(Backpacks.getPlugin().getConfig().getString("no_perms_title")), Backpacks.color(Backpacks.getPlugin().getConfig().getString("no_perms_subtitle")));
                    p.sendMessage(Backpacks.color(Backpacks.getPlugin().getConfig().getString("no_perms_chatmsg")));

                }

            }
            if(command.getName().equalsIgnoreCase("buybackpack")) {

                if(eco.getBalance(p.getName()) > 2500) {


                    Backpacks.getPlugin().getConfig().set(p.getName() + ".access", true);
                    Backpacks.getPlugin().saveConfig();

                    eco.withdrawPlayer(p, 2000);
                    p.sendMessage(Backpacks.color(Backpacks.getPlugin().getConfig().getString("bought_backpack")));
                    accessPlecak.put(p.getName(), 1);

                } else {

                    p.sendMessage(Backpacks.color(Backpacks.getPlugin().getConfig().getString("not_enough_money")));

                }

            }


        }

        return true;
    }
}