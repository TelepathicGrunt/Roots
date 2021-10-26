package epicsquid.roots.potion;

import epicsquid.roots.Roots;
import epicsquid.roots.init.ModSounds;
import epicsquid.roots.spell.SpellAugment;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.OnlyIn;

public class PotionReach extends Effect {
  private ResourceLocation texture = new ResourceLocation(Roots.MODID, "textures/gui/potions.png");

  public PotionReach() {
    super(false, 0xbe6fbe);
    setPotionName("Reach");
    setBeneficial();
    setIconIndex(7, 0);
  }

  public void loadComplete(double amount) {
    registerPotionAttributeModifier(PlayerEntity.REACH_DISTANCE, "c7e53f18-fd9a-427f-afca-36ee974a7adf", amount, 0);
    // Hopefully that's additive to the base
  }

  @Override
  @OnlyIn(Dist.CLIENT)
  public int getStatusIconIndex() {
    Minecraft.getMinecraft().renderEngine.bindTexture(texture);
    return super.getStatusIconIndex();
  }

  @Override
  public boolean shouldRenderHUD(EffectInstance effect) {
    return false;
  }

  @Override
  public boolean shouldRenderInvText(EffectInstance effect) {
    return true;
  }

  @Override
  public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
    super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
    if (SpellAugment.instance.shouldPlaySound()) {
      entityLivingBaseIn.playSound(ModSounds.Spells.REACH_EFFECT_END, SpellAugment.instance.getSoundVolume(), 1);
    }
  }
}
