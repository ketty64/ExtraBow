package me.ketty64.extrabows;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TeleportationBow implements Listener {
    private final JavaPlugin plugin;

    public TeleportationBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§5§lTeleportationBow");
        List<String> lore = new ArrayList<>();
        lore.add("This Bow teleports entities to a random location on hit");
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
        if (bowItem == null || !bowItem.hasItemMeta() || !bowItem.getItemMeta().hasDisplayName() || !bowItem.getItemMeta().getDisplayName().equals("§5§lTeleportationBow"))
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
                        && shooter.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§5§lTeleportationBow")) {
                    World world = arrow.getWorld();
                    Location hitLocation = arrow.getLocation();
                    for (Entity entity : world.getNearbyEntities(hitLocation, 2, 2, 2)) {
                        if (entity instanceof LivingEntity && entity != shooter) {
                            ((LivingEntity) entity).damage(4.0); // Inflict damage to the entity
                            teleportEntityRandomly((LivingEntity) entity);
                        }
                    }
                }
            }
        }
    }

    private void teleportEntityRandomly(LivingEntity entity) {
        Location currentLocation = entity.getLocation();
        Location teleportLocation = getRandomLocation(currentLocation, 10);
        if (teleportLocation != null) {
            entity.teleport(teleportLocation);
        }
    }

    private Location getRandomLocation(Location center, double radius) {
        Random rand = new Random();
        double randomX = center.getX() + (rand.nextDouble() * 2 - 1) * radius;
        double randomZ = center.getZ() + (rand.nextDouble() * 2 - 1) * radius;
        double y = center.getWorld().getHighestBlockYAt((int) randomX, (int) randomZ) + 1;
        return new Location(center.getWorld(), randomX, y, randomZ);
    }
}
