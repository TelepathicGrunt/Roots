package epicsquid.roots.network;

import epicsquid.roots.GuiHandler;
import epicsquid.roots.Roots;
import epicsquid.roots.util.ServerHerbUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MessageServerOpenLibrary implements IMessage {
  public MessageServerOpenLibrary() {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  public static class MessageHolder implements IMessageHandler<MessageServerOpenLibrary, IMessage> {

    @Override
    public IMessage onMessage(MessageServerOpenLibrary message, MessageContext ctx) {
      FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() -> handleMessage(message, ctx));

      return null;
    }

    private void handleMessage(MessageServerOpenLibrary message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      player.openGui(Roots.getInstance(), GuiHandler.LIBRARY_ID, player.world, 0, 0, 0);
    }
  }
}
