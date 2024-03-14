package me.ketty64.extrabows;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class GrapplingBow implements Listener {
    private final JavaPlugin plugin;
    private final List<Player> grapplingPlayers;
    private double grapplingForce;


    public GrapplingBow(JavaPlugin plugin) {
        this.plugin = plugin;
        reloadConfigValues();
        this.grapplingPlayers = new ArrayList<>();
        this.grapplingForce = plugin.getConfig().getDouble("grappling-bow.force", 3.0);
        this.grapplingForce = this.grapplingForce;
    }

    public void reloadConfigValues() {
        grapplingForce = plugin.getConfig().getDouble("grappling-bow.force", 3.0);
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§9§lGrapplingBow");
        List<String> lore = new ArrayList<>();
        lore.add("This Bow is a Grappling Hook");
        meta.setLore(lore);
        meta.addEnchant(Enchantment.DURABILITY, 10, true);
        bow.setItemMeta(meta);
        return bow;
    }

    @EventHandler
    public void onShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        ItemStack bowItem = event.getBow();
        if (bowItem == null || !bowItem.hasItemMeta() || !bowItem.getItemMeta().hasDisplayName() || !bowItem.getItemMeta().getDisplayName().equals("§9§lGrapplingBow"))
            return;

        Arrow arrow = (Arrow) event.getProjectile();
        arrow.setShooter(player);
        arrow.setCritical(true); // Render the arrow as critical for visibility
        arrow.setPickupStatus(Arrow.PickupStatus.ALLOWED);
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getEntity();
            if (arrow.getShooter() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                if (shooter.getInventory().getItemInMainHand().hasItemMeta() && shooter.getInventory().getItemInMainHand().getItemMeta().hasDisplayName()
                        && shooter.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§9§lGrapplingBow")) {
                    Location hitLocation = arrow.getLocation();
                    pullPlayerToLocation(shooter, hitLocation);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (grapplingPlayers.contains(player)) {
            if (event.getTo().distanceSquared(event.getFrom()) < 0.01) {
                grapplingPlayers.remove(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (grapplingPlayers.contains(player) && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    private void pullPlayerToLocation(Player player, Location targetLocation) {
        grapplingPlayers.add(player);

        Location playerLocation = player.getLocation();
        Vector direction = targetLocation.toVector().subtract(playerLocation.toVector()).normalize();

        Vector velocity = direction.multiply(grapplingForce);
        velocity.setY(velocity.getY() * 0.5);
        player.setVelocity(velocity);

        player.setFallDistance(0);

        if (player.isOnGround()) {
            player.setAllowFlight(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.setAllowFlight(false);
                }
            }.runTaskLater(plugin, 5);
        }
    }
}
