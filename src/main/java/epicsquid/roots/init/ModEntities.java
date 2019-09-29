package epicsquid.roots.init;

import epicsquid.mysticallib.LibRegistry;
import epicsquid.mysticallib.entity.RenderNull;
import epicsquid.roots.Roots;
import epicsquid.roots.entity.EntityFairy;
import epicsquid.roots.entity.projectile.EntityFlare;
import epicsquid.roots.entity.render.RenderFairy;
import epicsquid.roots.entity.ritual.*;
import epicsquid.roots.entity.spell.EntityBoost;
import epicsquid.roots.entity.spell.EntityFireJet;
import epicsquid.roots.entity.spell.EntityThornTrap;
import epicsquid.roots.entity.spell.EntityTimeStop;
import epicsquid.roots.proxy.ClientProxy;
import net.minecraft.entity.Entity;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ModEntities {

  /**
   * Registers mobs in the game
   * <p>
   * Egg colours are defined as Background colour then Foreground (spots) colour
   * <p>
   * <p>
   * Format for registering a mob:
   * <p>
   * LibRegistry.registerEntity(Entity.class, BackgroundColour, ForegroundColour);
   * if (Mod.proxy instanceof ClientProxy)
   * LibRegistry.registerEntityRenderer(Entity.class, new RenderEntity.Factory());
   */
  public static void registerMobs() {
    // Helper entities
    LibRegistry.registerEntity(EntityFireJet.class);
    LibRegistry.registerEntity(EntityThornTrap.class);
    LibRegistry.registerEntity(EntityTimeStop.class);
    LibRegistry.registerEntity(EntityBoost.class);
    LibRegistry.registerEntity(EntityFlare.class);

    // Actual entities
    LibRegistry.registerEntity(EntityFairy.class, 0xf542e3, 0xdb7fa1);

    // Ritual entities
    List<Class<? extends Entity>> ritualClasses = Arrays.asList(
        EntityRitualAnimalHarvest.class,
        EntityRitualDivineProtection.class,
        EntityRitualFireStorm.class,
        EntityRitualFlowerGrowth.class,
        EntityRitualFrostLands.class,
        EntityRitualGathering.class,
        EntityRitualGermination.class,
        EntityRitualHealingAura.class,
        EntityRitualHeavyStorms.class,
        EntityRitualOvergrowth.class,
        EntityRitualPurity.class,
        EntityRitualSpreadingForest.class,
        EntityRitualTransmutation.class,
        EntityRitualWardingProtection.class,
        EntityRitualWildGrowth.class,
        EntityRitualWindwall.class
    );

    ritualClasses.forEach(LibRegistry::registerEntity);

    if (Roots.proxy instanceof ClientProxy) {
      LibRegistry.registerEntityRenderer(EntityFireJet.class, new RenderNull.Factory());
      LibRegistry.registerEntityRenderer(EntityThornTrap.class, new RenderNull.Factory());
      LibRegistry.registerEntityRenderer(EntityTimeStop.class, new RenderNull.Factory());
      LibRegistry.registerEntityRenderer(EntityBoost.class, new RenderNull.Factory());
      LibRegistry.registerEntityRenderer(EntityFlare.class, new RenderNull.Factory());
      LibRegistry.registerEntityRenderer(EntityFairy.class, new RenderFairy.Factory());

      ritualClasses.forEach(c -> LibRegistry.registerEntityRenderer(c, new RenderNull.Factory()));
    }
  }

  public static void registerLootTables() {
    Stream.of(EntityFairy.LOOT_TABLE).forEach(LootTableList::register);
  }
}
