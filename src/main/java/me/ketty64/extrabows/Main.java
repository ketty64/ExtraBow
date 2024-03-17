package me.ketty64.extrabows;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class Main extends JavaPlugin implements CommandExecutor {

    private IceBow iceBow;
    private LightningBow lightningBow;
    private ExplosiveBow explosiveBow;
    private TeleportationBow teleportationBow;
    private GrapplingBow grapplingBow;
    private HealingBow healingBow;


    @Override
    public void onEnable() {
        getLogger().info("@ ExtraBows plugin enabled.");
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        saveConfig();


        float explosionRadius = (float) getConfig().getDouble("explosive-bow.explosion-radius", 4.0);
        int maxDistance = getConfig().getInt("explosive-bow.max-distance", 200);
        double grapplingForce = getConfig().getDouble("grappling-bow.force", 3.0);
        double teleportationRadius = getConfig().getDouble("teleportation-bow.teleportation-radius", 10.0);
        double healAmount = getConfig().getDouble("healing-bow.heal-amount", 10);


        iceBow = new IceBow(this);
        lightningBow = new LightningBow(this);
        explosiveBow = new ExplosiveBow(this);
        teleportationBow = new TeleportationBow(this);
        grapplingBow = new GrapplingBow(this);
        healingBow = new HealingBow(this);


        getServer().getPluginManager().registerEvents(iceBow, this);
        getServer().getPluginManager().registerEvents(lightningBow, this);
        getServer().getPluginManager().registerEvents(explosiveBow, this);
        getServer().getPluginManager().registerEvents(teleportationBow, this);
        getServer().getPluginManager().registerEvents(grapplingBow, this);
        getServer().getPluginManager().registerEvents(healingBow, this);


        this.getCommand("extrabows").setExecutor(this);
    }
    @Override
    public void onDisable(){
        getLogger().info("@ ExtraBows plugin disabled.");
        saveConfig();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;

        String commandName = cmd.getName().toLowerCase();
        if (commandName.equals("extrabows")) {
            if (args.length == 0) {
                player.sendMessage("§bRunning §9§lExtraBows 1.0 §bby §9§lketty64");
                return true;
            } else if (args.length == 1) {
                String commandArg = args[0].toLowerCase();
                if (commandArg.equals("icebow")) {
                    player.getInventory().addItem(iceBow.getBow());
                    player.sendMessage("§bYou have received an §3§lIceBow!");
                    return true;
                } else if (commandArg.equals("lightningbow")) {
                    player.getInventory().addItem(lightningBow.getBow());
                    player.sendMessage("§bYou have received a §e§lLightningBow!");
                    return true;
                } else if (commandArg.equals("explosivebow")) {
                    player.getInventory().addItem(explosiveBow.getBow());
                    player.sendMessage("§bYou have received a §c§lExplosivebow!");
                    return true;
                } else if (commandArg.equals("teleportationbow")) {
                    player.getInventory().addItem(teleportationBow.getBow());
                    player.sendMessage("§bYou have received a §5§lTeleportationBow!");
                    return true;
                } else if (commandArg.equals("grapplingbow")) {
                    player.getInventory().addItem(grapplingBow.getBow());
                    player.sendMessage("§bYou have received a §6§lGrapplingbow!");
                    return true;
                } else if (commandArg.equals("healingbow")) {
                    player.getInventory().addItem(healingBow.getBow());
                    player.sendMessage("§bYou have received a §a§lHealingbow!");
                    return true;
                } else if (commandArg.equals("reload")) {
                    if (player.hasPermission("extrabows.reload")) {
                        reloadConfig();
                        explosiveBow.reloadConfigValues();
                        grapplingBow.reloadConfigValues();
                        healingBow.reloadConfigValues();

                        player.sendMessage("§fExtraBows file config reloaded.");
                    } else {
                        player.sendMessage("§cYou don't have permission to use this command.");
                    }
                    return true;
                }
            }
            player.sendMessage("§3§lExtrabows: §cinvalid command!");
            return true;
        }
        return false;
    }






    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (cmd.getName().equalsIgnoreCase("extrabows") || cmd.getName().equalsIgnoreCase("eb")) {
            if (args.length == 1) {
                completions.add("icebow");
                completions.add("lightningbow");
                completions.add("explosivebow");
                completions.add("teleportationbow");
                completions.add("grapplingbow");
                completions.add("healingbow");
                completions.add("reload");


            }
        }
        return completions;
    }
}
