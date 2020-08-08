package software.angus.sorcery;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.*;


public class Casting implements Listener {
    private static final HashSet<Spellbook> Spelllist = new HashSet<>();
    private static final String manaRedstone = ChatColor.DARK_RED + "Cost: ";
    private static final HashMap<Spellbook, Integer> SpellCost = new HashMap<>();
    private static final HashMap<Spellbook, String> SpellLore = new HashMap<>();
    static {
        Spelllist.addAll(Arrays.asList(Spellbook.FIREBALL,
                Spellbook.SNOWBALL, Spellbook.DRAGONBREATH,
                Spellbook.EGG, Spellbook.WITHER, Spellbook.LIGHTNING,
                Spellbook.FORTIFY, Spellbook.HEAL, Spellbook.IMPERFECTION,
                Spellbook.MEDITATE, Spellbook.EARTHENSHIELD, Spellbook.DETECT,
                Spellbook.METEOR));

        SpellCost.put(Spellbook.FIREBALL, 1);
        SpellLore.put(Spellbook.FIREBALL, "An ancient spell. Simple, yet practical");

        SpellCost.put(Spellbook.SNOWBALL, 0);
        SpellLore.put(Spellbook.SNOWBALL, "A spell devised by a apprentice sorcerer");

        SpellCost.put(Spellbook.DRAGONBREATH, 3);
        SpellLore.put(Spellbook.DRAGONBREATH, "Harnessing the power of Dragons is a dangerous practice");

        SpellCost.put(Spellbook.EGG, 0);
        SpellLore.put(Spellbook.EGG, "A spell devised by a apprentice sorcerer");

        SpellCost.put(Spellbook.WITHER, 1);
        SpellLore.put(Spellbook.WITHER, "Wield the power of a mythical creature");

        SpellCost.put(Spellbook.LIGHTNING, 1);
        SpellLore.put(Spellbook.LIGHTNING, "Wield a force of nature");

        SpellCost.put(Spellbook.FORTIFY, 1);
        SpellLore.put(Spellbook.FORTIFY, "The first step to Immortality");

        SpellCost.put(Spellbook.HEAL, 1);
        SpellLore.put(Spellbook.HEAL, "Mend the bones, heal the flesh");

        SpellCost.put(Spellbook.IMPERFECTION, 15);
        SpellLore.put(Spellbook.IMPERFECTION, "Gods envy you");

        SpellCost.put(Spellbook.MEDITATE, 0);
        SpellLore.put(Spellbook.MEDITATE, "Calm your mind and absorb the mana");

        SpellCost.put(Spellbook.EARTHENSHIELD, 1);
        SpellLore.put(Spellbook.EARTHENSHIELD, "The Earth bends to shield you");

        SpellCost.put(Spellbook.DETECT, 3);
        SpellLore.put(Spellbook.DETECT, "Feel the flow of mana that the living project");

        SpellCost.put(Spellbook.METEOR, 64);
        SpellLore.put(Spellbook.METEOR, "The consummate of destructive magic");
        
    }


    @EventHandler
    public void bookWrite(PlayerEditBookEvent e) {

        if (e.isSigning()) {
            BookMeta bookMeta = e.getNewBookMeta();
            Spellbook spell = Spellbook.valueOf(bookMeta.getTitle().toUpperCase());
            if (Spelllist.contains(spell)) {
                String newBookName = ChatColor.DARK_AQUA + bookMeta.getTitle().toUpperCase();
                bookMeta.setTitle(newBookName);
                bookMeta.setDisplayName(newBookName);
                List<String> lore = new ArrayList<>();
                lore.add(manaRedstone+ SpellCost.get(spell));
                lore.add(SpellLore.get(spell));
                bookMeta.setLore(lore);
                bookMeta.setAuthor("Sorcery");
                e.setNewBookMeta(bookMeta);
                e.getPlayer().sendMessage("Spell successfully created");
            }
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = e.getPlayer();
            ItemStack item = player.getInventory().getItemInMainHand();
            ItemStack item2 = player.getInventory().getItemInOffHand();
            if (item.getType() == Material.WRITTEN_BOOK) {
                Spellbook spell = Spellbook.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if (Spelllist.contains(spell)) {
                    e.setUseItemInHand(Event.Result.DENY);
                    Cast(player, item.getItemMeta(), e, spell);
                }
            }
            else if (item2.getType() == Material.WRITTEN_BOOK) {
                Spellbook spell = Spellbook.valueOf(ChatColor.stripColor(item2.getItemMeta().getDisplayName()));
                if (Spelllist.contains(spell)) {
                    e.setUseItemInHand(Event.Result.DENY);
                    Cast(player, item.getItemMeta(), e, spell);
                }
            }
        }
    }

    private void Cast(Player player, ItemMeta itemMeta, PlayerInteractEvent e, Spellbook spell) {
        int cost = SpellCost.get(spell);
        if (player.getInventory().containsAtLeast(new ItemStack(Material.REDSTONE), cost)) {
            player.sendMessage("You've cast " + player.getInventory().getItemInMainHand().getItemMeta().getDisplayName());
            payCost(player, spell);
            switch (spell) {
                case FIREBALL:
                    player.launchProjectile(Fireball.class);
                    break;
                case SNOWBALL:
                    player.launchProjectile(Snowball.class);
                    break;
                case DRAGONBREATH:
                    player.launchProjectile(DragonFireball.class);
                    break;
                case WITHER:
                    player.launchProjectile(WitherSkull.class);
                    break;
                case FORTIFY:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 1200, 0));
                    break;
                case HEAL:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 1200, 0));
                    break;
                case EGG:
                    player.launchProjectile(Egg.class);
                    break;
                case LIGHTNING:
                    Location target = eyeDestination(player, 25);
                    player.getWorld().strikeLightning(target);
                    break;
                case IMPERFECTION:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 60, 10));
                    break;
                case MEDITATE:
                    ItemStack itemStack = new ItemStack(Material.REDSTONE);
                    player.getInventory().addItem(itemStack);
                    break;
                case EARTHENSHIELD:
                    createShield(player);
                    break;
                case DETECT:
                    Location location = player.getLocation();
                    Collection<LivingEntity> entityCollection = location.getNearbyLivingEntities(50.0, 50.0, 50.0);
                    for (LivingEntity entity: entityCollection) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 300, 1));
                    }
                    break;
                case METEOR:
                    Location eyeLocation = eyeDestination(player, 100);
                    double BlockY = eyeLocation.getBlockY() + 80; double BlockX = eyeLocation.getBlockX(); double BlockZ = eyeLocation.getBlockZ();
                    Location Explosion = new Location(player.getWorld(), BlockX, BlockY, BlockZ);
                    TNTPrimed tntPrimed = (TNTPrimed) player.getWorld().spawnEntity(Explosion, EntityType.PRIMED_TNT);
                    tntPrimed.setFuseTicks(100); tntPrimed.setGlowing(true); tntPrimed.setYield(80); tntPrimed.setIsIncendiary(true);
                    tntPrimed.setCustomName(ChatColor.RED + "METEOR"); tntPrimed.setCustomNameVisible(true);
                case RESTORE:
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20, 1));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 1));

            }
        }
    }
    private void payCost(Player player, Spellbook spell) {
        int cost = SpellCost.get(spell);
        PlayerInventory playerInventory = player.getInventory();
        for (ItemStack itemStack : playerInventory) {
            if (itemStack == null) {
                continue;
            }
             if (itemStack.getType() == Material.REDSTONE) {
                itemStack.setAmount(itemStack.getAmount() - cost);
                break;
            }
        }
    }
    private Location eyeDestination(Player player, int distance) {
        World world = player.getWorld();
        Location location = player.getEyeLocation();
        Vector vector = location.getDirection();
        RayTraceResult rayTraceResult = world.rayTraceBlocks(location, vector, distance);
        Vector destinationPos;
        if (rayTraceResult == null) {
            destinationPos = location.toVector().add(vector.multiply(distance));
        } else {
            rayTraceResult.getHitPosition();
            destinationPos = rayTraceResult.getHitPosition();
        }
        return new Location(world, destinationPos.getX(), destinationPos.getY(), destinationPos.getZ());
    }

    private void createShield(Player player) {
        Location playerLocation = player.getLocation();
        double x = playerLocation.getBlockX();
        double y = playerLocation.getBlockY();
        double z = playerLocation.getBlockZ();
        String cardinalDirection = getCardinalDirection(player);
        assert cardinalDirection != null;
        switch (cardinalDirection) {
            case "W":
                for (double i = -1; i < 2; i++) {
                    Location blockPlace = new Location(player.getWorld(), x - 2, y, z + i);
                    Location blockPlace2 = new Location(player.getWorld(), x - 2, y + 1, z + i);
                    Location blockPlace3 = new Location(player.getWorld(), x - 2, y + 2, z + i);
                    blockPlace.getBlock().setType(Material.STONE);
                    blockPlace2.getBlock().setType(Material.STONE);
                    blockPlace3.getBlock().setType(Material.STONE);
                }
                break;
            case "N":
                for (double i = -1; i < 2; i++) {
                    Location blockPlace = new Location(player.getWorld(), x + i, y, z - 2);
                    Location blockPlace2 = new Location(player.getWorld(), x + i, y + 1, z - 2);
                    Location blockPlace3 = new Location(player.getWorld(), x + i, y + 2, z - 2);
                    blockPlace.getBlock().setType(Material.STONE);
                    blockPlace2.getBlock().setType(Material.STONE);
                    blockPlace3.getBlock().setType(Material.STONE);
                }
                break;
            case "S":
                for (double i = -1; i < 2; i++) {
                    Location blockPlace = new Location(player.getWorld(), x + i, y, z + 2);
                    Location blockPlace2 = new Location(player.getWorld(), x + i, y + 1, z + 2);
                    Location blockPlace3 = new Location(player.getWorld(), x + i, y + 2, z + 2);
                    blockPlace.getBlock().setType(Material.STONE);
                    blockPlace2.getBlock().setType(Material.STONE);
                    blockPlace3.getBlock().setType(Material.STONE);
                }
                break;
            case "E":
                for (double i = -1; i < 2; i++) {
                    Location blockPlace = new Location(player.getWorld(), x + 2, y, z + i);
                    Location blockPlace2 = new Location(player.getWorld(), x + 2, y + 1, z + i);
                    Location blockPlace3 = new Location(player.getWorld(), x + 2, y + 2, z + i);
                    blockPlace.getBlock().setType(Material.STONE);
                    blockPlace2.getBlock().setType(Material.STONE);
                    blockPlace3.getBlock().setType(Material.STONE);
                }
                break;
        }
    }
    public static String getCardinalDirection(Player player) {
        double rotation = (player.getLocation().getYaw()) % 360;
        if (rotation < 0) {
            rotation += 360.0;
        }
        if (45 <= rotation && rotation < 135) {
            return "W";
        } else if (135 <= rotation && rotation < 225) {
            return "N";
        } else if (225 <= rotation && rotation < 315) {
            return "E";
        } else if (315 <= rotation && rotation < 360) {
            return "S";
        } else if ( 0 <= rotation && rotation < 45) {
            return "S";
        } else {
            return null;
        }
    }
}
