package mffs;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import mffs.gui.*;
import mffs.render.*;
import mffs.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import universalelectricity.core.vector.Vector3;

public class ClientProxy extends CommonProxy {

	public void preInit() {
		super.preInit();
		MinecraftForge.EVENT_BUS.register(SoundHandler.INSTANCE);
	}

	public void init() {
		super.init();
		RenderingRegistry.registerBlockHandler(new RenderBlockHandler());
		RenderingRegistry.registerBlockHandler(new RenderForceField());
		MinecraftForgeClient.registerItemRenderer(ModularForceFieldSystem.itemCardID.itemid, new RenderIDCard());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFortronCapacitor.class, new RenderFortronCapacitor());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCoercionDeriver.class, new RenderCoercionDeriver());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceManipulator.class, new RenderForceManipulator());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityForceFieldProjector.class, new RenderForceFieldProjector());
	}

	public World getClientWorld() {
		return FMLClientHandler.instance().getClient().theWorld;
	}

	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity = world.getTileEntity(x, y, z);
		if (tileEntity != null) {
			if (tileEntity.getClass() == TileEntityFortronCapacitor.class) {
				return new GuiFortronCapacitor(player, (TileEntityFortronCapacitor) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityForceFieldProjector.class) {
				return new GuiForceFieldProjector(player, (TileEntityForceFieldProjector) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityCoercionDeriver.class) {
				return new GuiCoercionDeriver(player, (TileEntityCoercionDeriver) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityBiometricIdentifier.class) {
				return new GuiBiometricIdentifier(player, (TileEntityBiometricIdentifier) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityInterdictionMatrix.class) {
				return new GuiInterdictionMatrix(player, (TileEntityInterdictionMatrix) tileEntity);
			}

			if (tileEntity.getClass() == TileEntityForceManipulator.class) {
				return new GuiForceManipulator(player, (TileEntityForceManipulator) tileEntity);
			}
		}

		return null;
	}

	public boolean isOp(String username) {
		return false;
	}

	public void renderBeam(World world, Vector3 position, Vector3 target, float red, float green, float blue, int age) {
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXBeam(world, position, target, red, green, blue, age));
	}

	public void renderHologram(World world, Vector3 position, float red, float green, float blue, int age, Vector3 targetPosition) {
		FMLClientHandler.instance().getClient().effectRenderer.addEffect((new FXHologram(world, position, red, green, blue, age)).setTarget(targetPosition));
	}

	public void renderHologramMoving(World world, Vector3 position, float red, float green, float blue, int age) {
		FMLClientHandler.instance().getClient().effectRenderer.addEffect(new FXHologramMoving(world, position, red, green, blue, age));
	}

}
