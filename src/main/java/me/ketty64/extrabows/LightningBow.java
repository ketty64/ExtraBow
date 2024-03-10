package me.ketty64.extrabows;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class LightningBow implements Listener {
    private final JavaPlugin plugin;

    public LightningBow(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public ItemStack getBow() {
        ItemStack bow = new ItemStack(Material.BOW);
        ItemMeta meta = bow.getItemMeta();
        meta.setDisplayName("§6§lLightningBow");
        List<String> lore = new ArrayList<>();
        lore.add("This Bow creates lightning strikes when arrow hits a block or entity");
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
        if (bowItem == null || !bowItem.hasItemMeta() || !bowItem.getItemMeta().hasDisplayName() || !bowItem.getItemMeta().getDisplayName().equals("§6§lLightningBow"))
            return;

        Arrow arrow = (Arrow) event.getProjectile();
        arrow.setShooter(player);
        arrow.setCritical(true); // Render the arrow as critical for visibility
        arrow.setPickupStatus(AbstractArrow.PickupStatus.ALLOWED);
        new LightningBowStrike(arrow).runTaskTimer(plugin, 1, 1); // Start task immediately and run it every tick
    }

    private static class LightningBowStrike extends BukkitRunnable {
        private final Arrow arrow;
        private static final int MAX_DISTANCE = 200; // Aumentiamo la distanza massima controllata
        private int ticksLived = 0;

        public LightningBowStrike(Arrow arrow) {
            this.arrow = arrow;
        }

        @Override
        public void run() {
            if (arrow.isDead() || ticksLived++ >= MAX_DISTANCE) {
                this.cancel();
                return;
            }

            if (arrow.isOnGround()) {
                arrow.getWorld().strikeLightning(arrow.getLocation());
                arrow.remove();
                cancel();
                return;
            }

            for (Entity entity : arrow.getNearbyEntities(2, 2, 2)) { // Incrementiamo la dimensione della ricerca dell'entità
                if (entity instanceof LivingEntity && entity != arrow.getShooter()) {
                    arrow.getWorld().strikeLightning(entity.getLocation());
                    arrow.remove();
                    cancel();
                    return;
                }
            }

            // Se la freccia ha viaggiato una distanza eccessiva senza collisioni, generiamo comunque un fulmine
            if (ticksLived >= MAX_DISTANCE) {
                arrow.getWorld().strikeLightning(arrow.getLocation());
                arrow.remove();
                cancel();
                return;
            }
        }
    }
    }
