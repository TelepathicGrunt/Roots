package epicsquid.roots.init;

import javax.annotation.Nonnull;

import epicsquid.mysticallib.block.BlockBase;
import epicsquid.mysticallib.event.RegisterContentEvent;
import epicsquid.roots.Roots;
import epicsquid.roots.block.BlockBonfire;
import epicsquid.roots.block.BlockImbuer;
import epicsquid.roots.block.BlockMortar;
import epicsquid.roots.block.BlockStructureMarker;
import epicsquid.roots.tileentity.TileEntityBonfire;
import epicsquid.roots.tileentity.TileEntityImbuer;
import epicsquid.roots.tileentity.TileEntityMortar;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;

public class ModBlocks {

  // All block
  public static Block mortar, bonfire, runestone, chiseled_runestone, imbuer, structure_marker;

  /**
   * Register all block
   */
  public static void registerBlocks(@Nonnull RegisterContentEvent event) {
    event.addBlock(mortar = new BlockMortar(Material.ROCK, SoundType.STONE, 1.4f, "mortar", TileEntityMortar.class)).setCreativeTab(Roots.tab).setLightOpacity(0);
    event.addBlock(bonfire = new BlockBonfire(Material.WOOD, SoundType.WOOD, 1.4f, "bonfire", TileEntityBonfire.class)).setCreativeTab(Roots.tab).setLightOpacity(0);
    event.addBlock(imbuer = new BlockImbuer(Material.WOOD, SoundType.WOOD, 1.4f, "imbuer", TileEntityImbuer.class)).setCreativeTab(Roots.tab).setLightOpacity(0);
    event.addBlock(runestone = new BlockBase(Material.ROCK, SoundType.METAL, 1.4f, "runestone")).setCreativeTab(Roots.tab);
    event.addBlock(chiseled_runestone = new BlockBase(Material.ROCK, SoundType.METAL, 1.4f, "chiseled_runestone")).setCreativeTab(Roots.tab);
    event.addBlock(structure_marker = new BlockStructureMarker());
  }
}
