package mffs;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IGuiHandler;
import mffs.container.*;
import mffs.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

public class CommonProxy implements IGuiHandler {

	public void preInit() {
	}

	public void init() {
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null) {
			if (tileEntity.getClass() == TileEntityFortronCapacitor.class) {
				return new ContainerFortronCapacitor(player, (TileEntityFortronCapacitor) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityForceFieldProjector.class) {
				return new ContainerForceFieldProjector(player, (TileEntityForceFieldProjector) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityCoercionDeriver.class) {
				return new ContainerCoercionDeriver(player, (TileEntityCoercionDeriver) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityBiometricIdentifier.class) {
				return new ContainerBiometricIdentifier(player, (TileEntityBiometricIdentifier) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityInterdictionMatrix.class) {
				return new ContainerInterdictionMatrix(player, (TileEntityInterdictionMatrix) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityForceManipulator.class) {
				return new ContainerForceManipulator(player, (TileEntityForceManipulator) tileEntity);
			}
		}

		return null;
	}

	public World getClientWorld() {
		return null;
	}

	public boolean isOp(String username) {
		MinecraftServer theServer = FMLCommonHandler.instance().getMinecraftServerInstance();
		return theServer != null ? theServer.getConfigurationManager().getOps().contains(username.trim().toLowerCase()) : false;
	}

	public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age) {
	}

	public void renderHologram(World world, Vector3 position, float red, float green, float blue, int age, Vector3 targetPosition) {
	}

	public void renderHologramMoving(World world, Vector3 position, float red, float green, float blue, int age) {
	}
}
