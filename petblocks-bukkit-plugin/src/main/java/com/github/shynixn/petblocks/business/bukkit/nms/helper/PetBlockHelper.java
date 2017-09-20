package com.github.shynixn.petblocks.business.bukkit.nms.helper;

import com.github.shynixn.petblocks.api.PetBlocksApi;
import com.github.shynixn.petblocks.api.bukkit.event.PetBlockCannonEvent;
import com.github.shynixn.petblocks.api.bukkit.event.PetBlockMoveEvent;
import com.github.shynixn.petblocks.api.bukkit.event.PetBlockRideEvent;
import com.github.shynixn.petblocks.api.bukkit.event.PetBlockWearEvent;
import com.github.shynixn.petblocks.api.business.entity.PetBlock;
import com.github.shynixn.petblocks.api.persistence.entity.ParticleEffectMeta;
import com.github.shynixn.petblocks.api.persistence.entity.PetMeta;
import com.github.shynixn.petblocks.api.persistence.entity.SoundMeta;
import com.github.shynixn.petblocks.business.bukkit.nms.NMSRegistry;
import com.github.shynixn.petblocks.business.logic.business.configuration.ConfigPet;
import com.github.shynixn.petblocks.business.logic.persistence.entity.ParticleEffectData;
import com.github.shynixn.petblocks.business.logic.persistence.entity.PetData;
import com.github.shynixn.petblocks.business.logic.persistence.entity.SoundBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.logging.Level;

/**
 * Created by Shynixn
 */
public final class PetBlockHelper {
    private static final Random random = new Random();
    private static final SoundMeta explosionSound = new SoundBuilder("EXPLODE", 1.0F, 2.0F);
    private static final ParticleEffectMeta angryParticle = new ParticleEffectData()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.VILLAGER_ANGRY)
            .setOffset(2, 2, 2)
            .setSpeed(0.1)
            .setAmount(2);
    private static final ParticleEffectMeta cloud = new ParticleEffectData()
            .setEffectType(ParticleEffectMeta.ParticleEffectType.CLOUD)
            .setOffset(1, 1, 1)
            .setSpeed(0.1)
            .setAmount(100);

    private PetBlockHelper() {
        super();
    }

    public static int afraidWaterEffect(Entity entity, int counter) {
        if (ConfigPet.getInstance().isAfraidOfwater()) {
            if (entity.getLocation().getBlock().isLiquid() && counter <= 0) {
                final Vector vec = new Vector(random.nextInt(3) * isNegative(random), random.nextInt(3) * isNegative(random), random.nextInt(3) * isNegative(random));
                entity.setVelocity(vec);
                if (ConfigPet.getInstance().isAfraidwaterParticles()) {
                    angryParticle.apply(entity.getLocation());
                }
                counter = 20;
            }
            counter--;
        }
        return counter;
    }

    public static int isNegative(Random rand) {
        if (rand.nextInt(2) == 0)
            return -1;
        return 1;
    }

    public static void setSkin(PetBlock petBlock, String skin) {
        final ItemStack itemStack;
        if (skin.contains("textures.minecraft")) {
            if (!skin.startsWith("http://"))
                skin = "http://" + skin;
            itemStack = NMSRegistry.changeSkullSkin(new ItemStack(org.bukkit.Material.SKULL_ITEM, 1, (byte) 3), skin);
        } else {
            itemStack = activateHead(skin, new ItemStack(org.bukkit.Material.SKULL_ITEM, 1, (byte) 3));
        }
        refreshHeadItemMeta(petBlock, itemStack);
    }

    private static ItemStack setWithUnbreakable(PetMeta petMeta, ItemStack itemStack) {
        final Map<String, Object> data = new HashMap<>();
        data.put("Unbreakable", petMeta.isItemStackUnbreakable());
        itemStack = NMSRegistry.setItemStackTag(itemStack, data);
        return itemStack;
    }

    public static long executeMovingSound(Entity entity, Player owner, PetMeta petMeta, long previous) {
        final PetData petData = (PetData) petMeta;
        if (petMeta == null)
            return previous;
        final long milli = System.currentTimeMillis();
        if (milli - previous > 500) {
            if (petMeta.isSoundEnabled()) {
                try {
                    if (ConfigPet.getInstance().isDesign_allowOtherHearSound() && !petData.isHidden()) {

                        petData.getEngine().getWalkingSound().applyToLocation(entity.getLocation());

                    } else {
                        petData.getEngine().getWalkingSound().applyToPlayers(owner);
                    }
                } catch (final Exception e) {
                    Bukkit.getLogger()
                            .log(Level.WARNING, "Cannot play walking sound "
                                    + petData.getEngine().getWalkingSound().getName()
                                    + " of " + petData.getEngine().getEntityType() + '.');
                }
            }
            return milli;
        }
        return previous;
    }

    public static void setSkin(PetBlock petBlock, org.bukkit.Material material, byte data) {
        final ItemStack itemStack = new ItemStack(material, 1, data);
        refreshHeadItemMeta(petBlock, itemStack);
    }

    public static int doTick(int counter, PetBlock petBlock, TickCallBack callBack) {
        final PetData petData = (PetData) petBlock.getMeta();
        if (!getArmorstand(petBlock).isDead() && getArmorstand(petBlock).getPassenger() == null && getEngineEntity(petBlock) != null && getArmorstand(petBlock).getVehicle() == null) {
            Location location = null;
            if (petData.getAge() >= ConfigPet.getInstance().getAge_largeticks())
                location = new Location(getEngineEntity(petBlock).getLocation().getWorld(), getEngineEntity(petBlock).getLocation().getX(), getEngineEntity(petBlock).getLocation().getY() - 1.2, getEngineEntity(petBlock).getLocation().getZ(), getEngineEntity(petBlock).getLocation().getYaw(), getEngineEntity(petBlock).getLocation().getPitch());
            else if (petData.getAge() <= ConfigPet.getInstance().getAge_smallticks())
                location = new Location(getEngineEntity(petBlock).getLocation().getWorld(), getEngineEntity(petBlock).getLocation().getX(), getEngineEntity(petBlock).getLocation().getY() - 0.7, getEngineEntity(petBlock).getLocation().getZ(), getEngineEntity(petBlock).getLocation().getYaw(), getEngineEntity(petBlock).getLocation().getPitch());
            if (location != null)
                callBack.run(location);
            counter = doTickSounds(counter, petBlock);
        } else if (getEngineEntity(petBlock) != null) {
            getEngineEntity(petBlock).teleport(getArmorstand(petBlock).getLocation());
        }
        try {
            if (petData.getAge() >= ConfigPet.getInstance().getAge_maxticks()) {
                if (ConfigPet.getInstance().isAge_deathOnMaxTicks() && !petBlock.isDieing()) {
                    petBlock.setDieing();
                }
            } else {
                boolean respawn = false;
                if (petData.getAge() < ConfigPet.getInstance().getAge_largeticks()) {
                    respawn = true;
                }
                petData.setAge(petData.getAge() + 1);
                if (petData.getAge() >= ConfigPet.getInstance().getAge_largeticks() && respawn) {
                    petBlock.respawn();
                }
            }
        } catch (final Exception ex) {
            Bukkit.getLogger().log(Level.WARNING, "Catcher prevented server crash, please report the following error to author Shynixn!", ex);
        }
        getArmorstand(petBlock).setFireTicks(0);
        if (getEngineEntity(petBlock) != null)
            getEngineEntity(petBlock).setFireTicks(0);
        Bukkit.getPluginManager().callEvent(new PetBlockMoveEvent(petBlock));
        return counter;
    }

    private static int doTickSounds(int counter, PetBlock petBlock) {
        final PetData petData = (PetData) petBlock.getMeta();
        if (counter <= 0) {
            final Random random = new Random();
            if (!getEngineEntity(petBlock).isOnGround() || petData.getEngine().getEntityType().equalsIgnoreCase("ZOMBIE")) {
                if (petBlock.getMeta().isSoundEnabled()) {
                    try
                    {
                        if (ConfigPet.getInstance().isDesign_allowOtherHearSound()) {
                            petData.getEngine().getAmbientSound().applyToLocation(getEngineEntity(petBlock).getLocation());
                        } else {
                            petData.getEngine().getAmbientSound().applyToLocation(petBlock.getPlayer());
                        }
                    }
                    catch (final Exception e) {
                        Bukkit.getLogger()
                                .log(Level.WARNING, "Cannot play ambient sound "
                                        + petData.getEngine().getWalkingSound().getName()
                                        + " of " + petData.getEngine().getEntityType() + '.');
                    }
                }
            }
            counter = 20 * random.nextInt(20) + 1;
        }
        if (getEngineEntity(petBlock).isDead()) {
            PetBlocksApi.getDefaultPetBlockController().remove(petBlock);
        }
        if (petData.getParticleEffectMeta() != null) {
            if (!petBlock.getMeta().isVisible()) {
                petData.getParticleEffectMeta().apply(getArmorstand(petBlock).getLocation().add(0, 1, 0), Arrays.asList(petBlock.getPlayer()));
            } else {
                petData.getParticleEffectMeta().apply(getArmorstand(petBlock).getLocation().add(0, 1, 0));
            }
        }
        counter--;
        return counter;
    }

    public static void refreshHeadItemMeta(PetBlock petBlock, ItemStack itemStack) {
        final PetData petData = (PetData) petBlock.getMeta();
        final String name;
        if (petData.getHeadDisplayName() == null)
            name = petBlock.getDisplayName();
        else
            name = petData.getHeadDisplayName();
        if (petData.getHeadLore() != null)
            itemStack = nameItem(itemStack, name, petData.getHeadLore());
        else
            itemStack = nameItem(itemStack, name, null);
        itemStack = setWithUnbreakable(petBlock.getMeta(), itemStack);
        getArmorstand(petBlock).setHelmet(itemStack);
    }

    private static ItemStack nameItem(ItemStack item, String name, String[] lore) {
        if (item.getType() != Material.AIR) {
            final ItemMeta im = item.getItemMeta();
            if (name != null) {
                im.setDisplayName(name);
            }
            if (lore != null) {
                im.setLore(Arrays.asList(lore));
            } else {
                im.setLore(new ArrayList<>());
            }
            item.setItemMeta(im);
            return item;
        }
        return item;
    }

    public static void setItemConsideringAge(PetBlock petBlock) {
        final PetData petData = (PetData) petBlock.getMeta();
        final ItemStack itemStack;
        if (petData.getSkin() != null) {
            if (petData.getSkin().contains("http")) {
                itemStack = NMSRegistry.changeSkullSkin(new ItemStack(Material.getMaterial(petData.getItemId()), 1, (short) petData.getItemDamage()), petData.getSkin());
            } else {
                itemStack = activateHead(petData.getSkin(), new ItemStack(Material.getMaterial(petData.getItemId()), 1, (short) petData.getItemDamage()));
            }
        } else {
            itemStack = new ItemStack(petData.getItemId(), 1,(short) petData.getItemDamage());
        }
        if (petData.getAge() >= ConfigPet.getInstance().getAge_largeticks()) {
            refreshHeadItemMeta(petBlock, itemStack);
            getArmorstand(petBlock).setSmall(false);

        } else {
            refreshHeadItemMeta(petBlock, itemStack);
            getArmorstand(petBlock).setSmall(true);
        }
    }

    public static void setRiding(PetBlock petBlock, Player player) {
        if (getArmorstand(petBlock).getPassenger() == null && player.getPassenger() == null) {
            final PetBlockRideEvent event = new PetBlockRideEvent(petBlock, true);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCanceled()) {
                getArmorstand(petBlock).setVelocity(new Vector(0, 1, 0));
                getArmorstand(petBlock).setPassenger(player);
                player.closeInventory();
            }
        }
    }

    public static boolean setDieing(final PetBlock petBlock) {
        if (!petBlock.isDieing()) {
            petBlock.jump();
            if (petBlock.getArmorStand() != null && !getArmorstand(petBlock).isDead())
                getArmorstand(petBlock).setHeadPose(new EulerAngle(0, 1, 0));
            Bukkit.getPluginManager().getPlugin("PetBlocks").getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("PetBlocks"), () -> {
                cloud.apply(petBlock.getLocation());
                petBlock.remove();
            }, 20 * 2);
            return true;
        }
        return petBlock.isDieing();
    }

    private static ItemStack activateHead(String name, ItemStack itemStack) {
        try {
            if (itemStack.getItemMeta() instanceof SkullMeta) {
                final SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                meta.setOwner(name);
                itemStack.setItemMeta(meta);
            }
        } catch (final Exception e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
        }
        return itemStack;
    }

    public static double setDamage(PetBlock petBlock, double health, double damage, TickCallBack callBack) {
        if (ConfigPet.getInstance().isDesign_showDamageAnimation()) {
            callBack.run(null);
        }
        if (!ConfigPet.getInstance().isCombat_invincible()) {
            health -= damage;
            if (health <= 0) {
                petBlock.setDieing();
            }
        }
        return health;
    }

    public static void launch(PetBlock petBlock, Vector vector) {
        final PetBlockCannonEvent event = new PetBlockCannonEvent(petBlock);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCanceled()) {
            getEngineEntity(petBlock).setVelocity(vector);
            if (petBlock.getMeta().isSoundEnabled()) {
                try {
                    ((SoundBuilder) explosionSound).apply(((Player) petBlock.getPlayer()).getLocation());
                } catch (final Exception e) {
                    Bukkit.getLogger().log(Level.WARNING, "Cannot play sound.", e);
                }
            }
        }
    }

    public static void wear(PetBlock petBlock, Player player, TickCallBack callBack) {
        if (getArmorstand(petBlock).getPassenger() == null && player.getPassenger() == null) {
            final PetBlockWearEvent event = new PetBlockWearEvent(petBlock, true);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCanceled()) {
                player.setPassenger(getArmorstand(petBlock));
                player.closeInventory();
                if (callBack != null)
                    callBack.run(null);
            }
        }
    }

    public static void jump(PetBlock petBlock) {
        getEngineEntity(petBlock).setVelocity(new Vector(0, 0.5, 0));
    }

    public static void teleport(PetBlock petBlock, Location location) {
        getEngineEntity(petBlock).teleport(location);
        getArmorstand(petBlock).teleport(location);
    }

    public static boolean isDead(PetBlock petBlock) {
        return (getEngineEntity(petBlock).isDead()
                || getArmorstand(petBlock).isDead())
                || (getEngineEntity(petBlock).getWorld().getName().equals(getArmorstand(petBlock).getWorld().getName())
                && getEngineEntity(petBlock).getLocation().distance(getArmorstand(petBlock).getLocation()) > 10);
    }

    public static void setDisplayName(PetBlock petBlock, String name) {
        getArmorstand(petBlock).setCustomName(name);
        getArmorstand(petBlock).setCustomNameVisible(true);
        final ItemStack itemStack = getArmorstand(petBlock).getHelmet();
        refreshHeadItemMeta(petBlock, itemStack);
    }

    public static void eject(PetBlock petBlock, Player player, TickCallBack callBack) {
        final PetBlockWearEvent event = new PetBlockWearEvent(petBlock, false);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCanceled()) {
            player.eject();
            if (callBack != null)
                callBack.run(null);
        }
    }

    public static void respawn(PetBlock petBlock, TickCallBack callBack) {
        final Location location = ((Location) petBlock.getLocation()).clone();
        petBlock.remove();
        callBack.run(location);
    }

    public static void remove(PetBlock petBlock) {
        if (petBlock.getEngineEntity() != null && !((LivingEntity) petBlock.getEngineEntity()).isDead()) {
            ((LivingEntity) petBlock.getEngineEntity()).remove();
        }
        if (!((LivingEntity) petBlock.getArmorStand()).isDead()) {
            ((LivingEntity) petBlock.getArmorStand()).remove();
        }
    }

    private static ArmorStand getArmorstand(PetBlock petBlock) {
        return (ArmorStand) petBlock.getArmorStand();
    }

    private static LivingEntity getEngineEntity(PetBlock petBlock) {
        return (LivingEntity) petBlock.getEngineEntity();
    }

    @FunctionalInterface
    public interface TickCallBack {
        void run(Location location);
    }
}