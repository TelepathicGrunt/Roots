package mysticmods.roots.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import mysticmods.roots.api.RootsAPI;
import mysticmods.roots.api.property.Property;
import mysticmods.roots.init.ModRegistries;
import mysticmods.roots.network.ClientBoundRitualPropertyPacket;
import mysticmods.roots.network.Networking;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;


public class RitualPropertyReloadListener extends SimpleJsonResourceReloadListener {
  private static final Gson GSON = (new GsonBuilder()).create();
  private static final RitualPropertyReloadListener INSTANCE = new RitualPropertyReloadListener();

  public RitualPropertyReloadListener() {
    super(GSON, "properties/ritual");
  }

  @Override
  protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
    pObject.forEach((id, element) -> {
      Property.RitualProperty<?> prop = ModRegistries.RITUAL_PROPERTY_REGISTRY.get().getValue(id);
      if (prop == null) {
        // do something
      } else if (element.isJsonObject()) {
        prop.updateFromJson(element.getAsJsonObject());
      }
    });
  }

  public static RitualPropertyReloadListener getInstance() {
    return INSTANCE;
  }
}