package epicsquid.roots.entity;

import java.util.List;

import epicsquid.roots.ritual.RitualRegistry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityRitualWindwall extends EntityRitualBase {

  public EntityRitualWindwall(World worldIn) {
    super(worldIn);
    getDataManager().register(lifetime, RitualRegistry.ritual_windwall.duration + 20);
  }

  @Override
  public void onUpdate() {
    ticksExisted++;
    float alpha = (float) Math.min(40, (RitualRegistry.ritual_windwall.duration + 20) - getDataManager().get(lifetime)) / 40.0f;
    getDataManager().set(lifetime, getDataManager().get(lifetime) - 1);
    getDataManager().setDirty(lifetime);
    if (getDataManager().get(lifetime) < 0) {
      setDead();
    }
    if (world.isRemote && getDataManager().get(lifetime) > 0) {
      //todo: fix particle when available | ParticleUtil.spawnParticleStar(world, (float)posX, (float)posY, (float)posZ, 0, 0, 0, 70, 70, 70, 0.5f*alpha, 20.0f, 40);
      for (float i = 0; i < 360; i += 120) {
        float ang = (float) (ticksExisted % 360);
        float tx = (float) posX + 2.5f * (float) Math.sin(Math.toRadians(2.0f * (i + ang)));
        float ty = (float) posY + 0.5f * (float) Math.sin(Math.toRadians(4.0f * (i + ang)));
        float tz = (float) posZ + 2.5f * (float) Math.cos(Math.toRadians(2.0f * (i + ang)));
        //todo: fix particle when available | ParticleUtil.spawnParticleStar(world, tx, ty, tz, 0, 0, 0, 70, 70, 70, 0.5f*alpha, 10.0f, 40);
      }
      if (rand.nextInt(5) == 0) {
        //todo: fix particle when available | ParticleUtil.spawnParticleSpark(world, (float)posX, (float)posY, (float)posZ, 0.125f*(rand.nextFloat()-0.5f), 0.0625f*(rand.nextFloat()), 0.125f*(rand.nextFloat()-0.5f), 70, 70, 70, 1.0f*alpha, 1.0f+rand.nextFloat(), 160);
      }
    }
    if (this.ticksExisted % 5 == 0) {
      List<EntityLivingBase> entities = world
          .getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(posX - 31.5, posY - 31.5, posZ - 31.5, posX + 31.5, posY + 31.5, posZ + 31.5));
      for (EntityLivingBase e : entities) {
        if (e instanceof EntityMob && Math.pow((posX - e.posX), 2) + Math.pow((posY - e.posY), 2) + Math.pow((posZ - e.posZ), 2) < 1000) {
          e.knockBack(this, 1.0f, posX - e.posX, posZ - e.posZ);
          if (world.isRemote) {
            for (int i = 0; i < 10; i++) {
              //todo: fix particle when available | ParticleUtil.spawnParticleSmoke(world, (float)e.posX, (float)e.posY, (float)e.posZ, (float)e.motionX*rand.nextFloat()*0.5f, (float)e.motionY*rand.nextFloat()*0.5f, (float)e.motionZ*rand.nextFloat()*0.5f, 0.65f, 0.65f, 0.65f, 0.15f, 12.0f+24.0f*rand.nextFloat(), 80, false);
            }
          }
        }
      }
    }
  }

}