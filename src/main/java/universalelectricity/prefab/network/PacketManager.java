package universalelectricity.prefab.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketManager implements IPacketHandler, IPacketReceiver {

	public static void writeNBTTagCompound(NBTTagCompound tag, DataOutputStream dataStream) throws IOException {
		if (tag == null) {
			dataStream.writeShort(-1);
		} else {
			byte[] var2 = CompressedStreamTools.compress(tag);
			dataStream.writeShort((short) var2.length);
			dataStream.write(var2);
		}

	}

	public static NBTTagCompound readNBTTagCompound(ByteArrayDataInput dataStream) throws IOException {
		short var1 = dataStream.readShort();
		if (var1 < 0) {
			return null;
		} else {
			byte[] var2 = new byte[var1];
			dataStream.readFully(var2);
			return CompressedStreamTools.decompress(var2);
		}
	}

	public static Packet getPacketWithID(String channelName, int id, Object... sendData) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.writeInt(id);
			encodeDataStream(data, sendData);
			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = channelName;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			return packet;
		} catch (IOException var6) {
			System.out.println("Failed to create packet.");
			var6.printStackTrace();
			return null;
		}
	}

	public static Packet getPacket(String channelName, Object... sendData) {
		return getPacketWithID(channelName, PacketManager.PacketType.UNSPECIFIED.ordinal(), sendData);
	}

	public static Packet getPacket(String channelName, TileEntity sender, Object... sendData) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		DataOutputStream data = new DataOutputStream(bytes);

		try {
			data.writeInt(PacketManager.PacketType.TILEENTITY.ordinal());
			data.writeInt(sender.xCoord);
			data.writeInt(sender.yCoord);
			data.writeInt(sender.zCoord);
			encodeDataStream(data, sendData);
			Packet250CustomPayload packet = new Packet250CustomPayload();
			packet.channel = channelName;
			packet.data = bytes.toByteArray();
			packet.length = packet.data.length;
			return packet;
		} catch (IOException var6) {
			System.out.println("Failed to create packet.");
			var6.printStackTrace();
			return null;
		}
	}

	public static void sendPacketToClients(Packet packet, World worldObj, Vector3 position, double range) {
		try {
			PacketDispatcher.sendPacketToAllAround(position.x, position.y, position.z, range, worldObj.provider.dimensionId, packet);
		} catch (Exception var6) {
			System.out.println("Sending packet to client failed.");
			var6.printStackTrace();
		}

	}

	public static void sendPacketToClients(Packet packet, World worldObj) {
		try {
			PacketDispatcher.sendPacketToAllInDimension(packet, worldObj.provider.dimensionId);
		} catch (Exception var3) {
			System.out.println("Sending packet to client failed.");
			var3.printStackTrace();
		}

	}

	public static void sendPacketToClients(Packet packet) {
		try {
			PacketDispatcher.sendPacketToAllPlayers(packet);
		} catch (Exception var2) {
			System.out.println("Sending packet to client failed.");
			var2.printStackTrace();
		}

	}

	public static DataOutputStream encodeDataStream(DataOutputStream data, Object... sendData) {
		try {
			Object[] arr$ = sendData;
			int len$ = sendData.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				Object dataValue = arr$[i$];
				if (dataValue instanceof Integer) {
					data.writeInt((Integer) dataValue);
				} else if (dataValue instanceof Float) {
					data.writeFloat((Float) dataValue);
				} else if (dataValue instanceof Double) {
					data.writeDouble((Double) dataValue);
				} else if (dataValue instanceof Byte) {
					data.writeByte((Byte) dataValue);
				} else if (dataValue instanceof Boolean) {
					data.writeBoolean((Boolean) dataValue);
				} else if (dataValue instanceof String) {
					data.writeUTF((String) dataValue);
				} else if (dataValue instanceof Short) {
					data.writeShort((Short) dataValue);
				} else if (dataValue instanceof Long) {
					data.writeLong((Long) dataValue);
				} else if (dataValue instanceof NBTTagCompound) {
					writeNBTTagCompound((NBTTagCompound) dataValue, data);
				}
			}

			return data;
		} catch (IOException var6) {
			System.out.println("Packet data encoding failed.");
			var6.printStackTrace();
			return data;
		}
	}

	public void onPacketData(INetworkManager network, Packet250CustomPayload packet, Player player) {
		try {
			ByteArrayDataInput data = ByteStreams.newDataInput(packet.data);
			int packetTypeID = data.readInt();
			PacketManager.PacketType packetType = PacketManager.PacketType.get(packetTypeID);
			if (packetType == PacketManager.PacketType.TILEENTITY) {
				int x = data.readInt();
				int y = data.readInt();
				int z = data.readInt();
				World world = ((EntityPlayer) player).worldObj;
				if (world != null) {
					TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
					if (tileEntity != null && tileEntity instanceof IPacketReceiver) {
						((IPacketReceiver) tileEntity).handlePacketData(network, packetTypeID, packet, (EntityPlayer) player, data);
					}
				}
			} else {
				this.handlePacketData(network, packetTypeID, packet, (EntityPlayer) player, data);
			}
		} catch (Exception var12) {
			var12.printStackTrace();
		}

	}

	public void handlePacketData(INetworkManager network, int packetType, Packet250CustomPayload packet, EntityPlayer player, ByteArrayDataInput dataStream) {
	}

	public static enum PacketType {
		UNSPECIFIED,
		TILEENTITY;

		public static PacketManager.PacketType get(int id) {
			return id >= 0 && id < values().length ? values()[id] : UNSPECIFIED;
		}
	}
}
