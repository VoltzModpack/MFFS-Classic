package universalelectricity.prefab.network;

import com.google.common.io.ByteArrayDataInput;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

public interface IPacketReceiver {

	void handlePacketData(INetworkManager var1, int var2, Packet250CustomPayload var3, EntityPlayer var4, ByteArrayDataInput var5);
}
