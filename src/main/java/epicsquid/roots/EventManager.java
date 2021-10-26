package epicsquid.roots;

import epicsquid.mysticallib.network.PacketHandler;
import epicsquid.mysticallib.proxy.ClientProxy;
import epicsquid.roots.capability.life_essence.LifeEssenceCapabilityProvider;
import epicsquid.roots.capability.runic_shears.RunicShearsCapabilityProvider;
import epicsquid.roots.entity.spell.EntityBoost;
import epicsquid.roots.init.ModDamage;
import epicsquid.roots.init.ModPotions;
import epicsquid.roots.init.ModRecipes;
import epicsquid.roots.init.ModSounds;
import epicsquid.roots.modifiers.instance.staff.ModifierSnapshot;
import epicsquid.roots.modifiers.instance.staff.StaffModifierInstanceList;
import epicsquid.roots.network.MessageLightDrifterSync;
import epicsquid.roots.network.fx.MessageGeasFX;
import epicsquid.roots.network.fx.MessageGeasRingFX;
import epicsquid.roots.network.fx.MessagePetalShellBurstFX;
import epicsquid.roots.spell.*;
import epicsquid.roots.util.Constants;
import epicsquid.roots.util.SlaveUtil;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(modid = Roots.MODID)
public class EventManager {
  public static long ticks = 0;

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onTick(TickEvent.ClientTickEvent event) {
    if (event.side == Dist.CLIENT) {
      ClientProxy.particleRenderer.updateParticles();
      ticks++;
    }
  }

  @SubscribeEvent
  public static void addCapabilities(AttachCapabilitiesEvent<Entity> event) {
    if (ModRecipes.getRunicShearEntities().contains(event.getObject().getClass())) {
      event.addCapability(RunicShearsCapabilityProvider.IDENTIFIER, new RunicShearsCapabilityProvider());
    }
    if (event.getObject() instanceof LivingEntity) {
      if (ModRecipes.isLifeEssenceAllowed((LivingEntity) event.getObject())) {
        event.addCapability(LifeEssenceCapabilityProvider.IDENTIFIER, new LifeEssenceCapabilityProvider());
      }
    }
  }

  @SubscribeEvent(priority = EventPriority.HIGHEST)
  public static void onDamage(LivingHurtEvent event) {
    LivingEntity entity = event.getEntityLiving();
    Entity trueSource = event.getSource().getTrueSource();
    DamageSource damage = event.getSource();

    if (entity.getActivePotionEffect(ModPotions.aqua_bubble) != null) {
      ModifierSnapshot mods = StaffModifierInstanceList.fromSnapshot(entity.getEntityData(), SpellAquaBubble.instance);

      float totalFire = damage.isFireDamage() ? event.getAmount() : 0;

      if (trueSource != null && totalFire > 0 && mods.has(SpellAquaBubble.REFLECT_FIRE) && entity instanceof PlayerEntity) {
        trueSource.attackEntityFrom(ModDamage.physicalDamageFrom(entity), totalFire);
      }

      if (trueSource instanceof LivingEntity && event.getAmount() > 0) {
        LivingEntity attacker = (LivingEntity) trueSource;
        if (mods.has(SpellAquaBubble.KNOCKBACK)) {
          attacker.knockBack(entity, SpellAquaBubble.instance.knockback, entity.posX - attacker.posX, entity.posZ - attacker.posZ);
        }
        if (mods.has(SpellAquaBubble.SLOW)) {
          attacker.addPotionEffect(new EffectInstance(Effects.SLOWNESS, SpellAquaBubble.instance.slow_duration, SpellAquaBubble.instance.slow_amplifier));
        }
        if (mods.has(SpellAquaBubble.UNDEAD) && attacker.isEntityUndead()) {
          float amount = event.getAmount() * SpellAquaBubble.instance.undead_reduction;
          event.setAmount(amount < 1 ? 0 : amount);
        }
      }

      if (damage.isMagicDamage() && mods.has(SpellAquaBubble.MAGIC_RESIST)) {
        float amount = event.getAmount() * SpellAquaBubble.instance.magic_reduction;
        event.setAmount(amount < 1 ? 0 : amount);
      }

      if (damage.isFireDamage()) {
        float amount = event.getAmount() * SpellAquaBubble.instance.fire_reduction;
        event.setAmount(amount < 1 ? 0 : amount);
      }
      if (damage == DamageSource.LAVA) {
        float amount = event.getAmount() * SpellAquaBubble.instance.lava_reduction;
        event.setAmount(amount < 1 ? 0 : amount);
      }
    }

    if (entity.getActivePotionEffect(ModPotions.storm_cloud) != null) {
      ModifierSnapshot mods = StaffModifierInstanceList.fromSnapshot(entity.getEntityData(), SpellStormCloud.instance);
      if (trueSource != null) {
        if (mods.has(SpellStormCloud.JOLT)) {
          trueSource.attackEntityFrom(DamageSource.causeMobDamage(entity), SpellStormCloud.instance.lightning_damage);
        }
        if (mods.has(SpellStormCloud.MAGNETISM)) {
          trueSource.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
        }
      }
    }

    if (entity.getActivePotionEffect(ModPotions.time_stop) != null) {
      event.setAmount(event.getAmount() * 0.1f);
    }
    // TODO: MAYBE NOT
    EffectInstance invuln = entity.getActivePotionEffect(Effects.RESISTANCE);
    if (invuln != null && invuln.getAmplifier() == 10) {
      event.setCanceled(true);
    }

    World world = entity.getEntityWorld();

    EffectInstance effect = entity.getActivePotionEffect(ModPotions.petal_shell);
    if (!world.isRemote) {
      if (effect != null) {
        int newCount = effect.getAmplifier() - 1;
        entity.removePotionEffect(ModPotions.petal_shell);
        ModifierSnapshot mods = StaffModifierInstanceList.fromSnapshot(entity.getEntityData(), SpellPetalShell.instance);
        if (newCount > 0) {
          entity.addPotionEffect(new EffectInstance(ModPotions.petal_shell, SpellPetalShell.instance.duration, newCount, false, false));
          if (SpellPetalShell.instance.shouldPlaySound()) {
            entity.playSound(ModSounds.Spells.PETAL_SHELL_EFFECT_BREAK, SpellPetalShell.instance.getSoundVolume(), 1);
          }
        } else {
          if (SpellPetalShell.instance.shouldPlaySound()) {
            entity.playSound(ModSounds.Spells.PETAL_SHELL_EFFECT_END, SpellPetalShell.instance.getSoundVolume(), 1);
          }
        }
        if (trueSource != null) {
          if (mods.has(SpellPetalShell.RADIANT)) {
            trueSource.attackEntityFrom(ModDamage.radiantDamageFrom(entity), SpellPetalShell.instance.radiant_damage);
            // TODO: Particle?
          }
          if (mods.has(SpellPetalShell.SLASHING)) {
            trueSource.attackEntityFrom(ModDamage.physicalDamageFrom(entity), SpellPetalShell.instance.dagger_damage);
            if (trueSource instanceof LivingEntity) {
              ((LivingEntity) trueSource).addPotionEffect(new EffectInstance(ModPotions.bleeding, SpellPetalShell.instance.bleed_duration, SpellPetalShell.instance.bleed_amplifier));
            }
          }
          if (mods.has(SpellPetalShell.POISON)) {
            if (trueSource instanceof LivingEntity) {
              ((LivingEntity) trueSource).addPotionEffect(new EffectInstance(Effects.POISON, SpellPetalShell.instance.poison_duration, SpellPetalShell.instance.poison_amplifier));
            }
          }
          if (mods.has(SpellPetalShell.LEVITATE)) {
            if (trueSource instanceof LivingEntity) {
              ((LivingEntity) trueSource).addPotionEffect(new EffectInstance(Effects.LEVITATION, SpellPetalShell.instance.levitate_duration, 0));
            }
          }
          if (mods.has(SpellPetalShell.FIRE)) {
            trueSource.setFire(SpellPetalShell.instance.fire_duration);
            trueSource.attackEntityFrom(ModDamage.fireDamageFrom(entity), SpellPetalShell.instance.fire_damage);
          }
          if (mods.has(SpellPetalShell.WEAKNESS)) {
            if (trueSource instanceof LivingEntity) {
              ((LivingEntity) trueSource).addPotionEffect(new EffectInstance(Effects.WEAKNESS, SpellPetalShell.instance.weakness_duration, SpellPetalShell.instance.weakness_amplifier));
            }
          }
          if (mods.has(SpellPetalShell.SLOW)) {
            if (trueSource instanceof LivingEntity) {
              ((LivingEntity) trueSource).addPotionEffect(new EffectInstance(Effects.SLOWNESS, SpellPetalShell.instance.slow_duration, SpellPetalShell.instance.slow_amplifier));
            }
          }
        }
        event.setAmount(0);
        event.setCanceled(true);

        PacketHandler.sendToAllTracking(new MessagePetalShellBurstFX(entity.posX, entity.posY + 1.0f, entity.posZ, mods), entity);
      }
    }
    if (trueSource instanceof LivingEntity) {
      LivingEntity trueLiving = (LivingEntity) trueSource;
      if (trueLiving.getActivePotionEffect(ModPotions.geas) != null && !SlaveUtil.isSlave(trueLiving)) {
        trueLiving.attackEntityFrom(ModDamage.PSYCHIC_DAMAGE, 3);
        event.setAmount(0);
        PacketHandler.sendToAllTracking(new MessageGeasRingFX(trueLiving.posX, trueLiving.posY + 1.0, trueLiving.posZ), trueLiving);
      }
    }
  }

  @SubscribeEvent
  public static void onEntityTarget(LivingSetAttackTargetEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity.getActivePotionEffect(ModPotions.geas) != null) {
      if (SlaveUtil.isSlave(entity)) {
        return;
      }
      if (entity instanceof MobEntity && !SlaveUtil.isSlave(entity)) {
        MobEntity living = (MobEntity) entity;
        if (living.getAttackTarget() != null) {
          living.attackTarget = null;
        }
      }
    }
  }

  @SubscribeEvent
  public static void onEntityTick(LivingUpdateEvent event) {
    LivingEntity entity = event.getEntityLiving();
    if (entity.getActivePotionEffect(ModPotions.time_stop) != null) {
      entity.removePotionEffect(ModPotions.time_stop);
      event.setCanceled(true);
    }
    if (entity instanceof PlayerEntity && event.getEntity().getEntityData().contains(Constants.LIGHT_DRIFTER_TAG) && !event.getEntity().getEntityWorld().isRemote) {
      event.getEntity().getEntityData().putInt(Constants.LIGHT_DRIFTER_TAG, event.getEntity().getEntityData().getInt(Constants.LIGHT_DRIFTER_TAG) - 1);
      if (event.getEntity().getEntityData().getInt(Constants.LIGHT_DRIFTER_TAG) <= 0) {
        PlayerEntity player = ((PlayerEntity) event.getEntity());
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, 40, 10, false, false));
        player.posX = event.getEntity().getEntityData().getDouble(Constants.LIGHT_DRIFTER_X);
        player.posY = event.getEntity().getEntityData().getDouble(Constants.LIGHT_DRIFTER_Y);
        player.posZ = event.getEntity().getEntityData().getDouble(Constants.LIGHT_DRIFTER_Z);
        PacketHandler.sendToAllTracking(new MessageLightDrifterSync(event.getEntity().getUniqueID(), player.posX, player.posY, player.posZ, false, event.getEntity().getEntityData().getInt(Constants.LIGHT_DRIFTER_MODE)), player);
        player.capabilities.allowFlying = false;
        player.capabilities.disableDamage = false;
        player.noClip = false;
        player.capabilities.isFlying = false;
        player.setPositionAndUpdate(player.posX, player.posY, player.posZ);
        player.setGameType(GameType.getByID(event.getEntity().getEntityData().getInt(Constants.LIGHT_DRIFTER_MODE)));
        player.extinguish();
        //PacketHandler.sendToAllTracking(new MessageLightDrifterFX(event.getEntity().posX, event.getEntity().posY + 1.0f, event.getEntity().posZ), event.getEntity());
        event.getEntity().getEntityData().removeTag(Constants.LIGHT_DRIFTER_TAG);
        event.getEntity().getEntityData().removeTag(Constants.LIGHT_DRIFTER_X);
        event.getEntity().getEntityData().removeTag(Constants.LIGHT_DRIFTER_Y);
        event.getEntity().getEntityData().removeTag(Constants.LIGHT_DRIFTER_Z);
        event.getEntity().getEntityData().removeTag(Constants.LIGHT_DRIFTER_MODE);
        if (SpellAugment.instance.shouldPlaySound()) {
          player.world.playSound(null, player.getPosition(), ModSounds.Spells.LIGHT_DRIFTER_EFFECT_END, SoundCategory.PLAYERS, SpellAugment.instance.getSoundVolume(), 1);
        }
      }
    }
    if (entity.getActivePotionEffect(ModPotions.geas) != null) {
      PacketHandler.sendToAllTracking(new MessageGeasFX(entity.posX, entity.posY + entity.getEyeHeight() + 0.75f, entity.posZ), entity);
    }
  }

  @SubscribeEvent
  public static void onLooting(LootingLevelEvent event) {
    if (event.getDamageSource().damageType.equals(ModDamage.FEY_FIRE)) {
      event.setLootingLevel(event.getLootingLevel() + 2);
    }
  }

  @SubscribeEvent
  public static void onLeafEvent(GetCollisionBoxesEvent event) {
    if (!(event.getEntity() instanceof PlayerEntity)) {
      return;
    }

    if (EntityBoost.beingBoosted(event.getEntity())) {
      List<AxisAlignedBB> collisions = event.getCollisionBoxesList();
      for (int i = collisions.size() - 1; i >= 0; i--) {
        AxisAlignedBB aabb = collisions.get(i);
        BlockPos pos = new BlockPos(aabb.minX + (aabb.maxX - aabb.minX) * 0.5f, aabb.minY + (aabb.maxY - aabb.minY) * 0.5f, aabb.minZ + (aabb.maxZ - aabb.minZ) * 0.5f);
        BlockState state = event.getWorld().getBlockState(pos);
        if (state.getBlock().isLeaves(state, event.getWorld(), pos) && event.getEntity().posY < aabb.maxY) {
          event.getCollisionBoxesList().remove(i);
        }
      }
    }
  }

  @SubscribeEvent
  public static void onFall(LivingFallEvent event) {
    if (event.getEntityLiving() instanceof PlayerEntity && !event.getEntityLiving().world.isRemote) {
      if (EntityBoost.safe((PlayerEntity) event.getEntityLiving())) {
        event.setDamageMultiplier(0);
      }
    }
  }
}
