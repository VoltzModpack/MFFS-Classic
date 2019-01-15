package mffs.render;

import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.block.BlockCoercionDeriver;
import mffs.block.BlockForceFieldProjector;
import mffs.block.BlockForceManipulator;
import mffs.block.BlockFortronCapacitor;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderBlockHandler implements ISimpleBlockRenderingHandler {

	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		if (modelID == ID) {
			GL11.glPushMatrix();
			if (block instanceof BlockFortronCapacitor) {
				GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mffs/textures/models/fortronCapacitor_on.png"));
				GL11.glTranslated(0.5D, 1.9D, 0.5D);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(1.3F, 1.3F, 1.3F);
				RenderFortronCapacitor.MODEL.render(0.0625F);
			} else if (block instanceof BlockForceFieldProjector) {
				GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mffs/textures/models/projector_on.png"));
				GL11.glTranslated(0.5D, 1.5D, 0.5D);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				RenderForceFieldProjector.MODEL.render(0.0F, 0.0625F);
			} else if (block instanceof BlockCoercionDeriver) {
				GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mffs/textures/models/coercionDeriver_on.png"));
				GL11.glTranslated(0.5D, 1.9D, 0.5D);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glScalef(1.3F, 1.3F, 1.3F);
				RenderCoercionDeriver.MODEL.render(0.0F, 0.0625F);
			} else if (block instanceof BlockForceManipulator) {
				GL11.glBindTexture(3553, FMLClientHandler.instance().getClient().renderEngine.getTexture("/mods/mffs/textures/models/forceManipulator_on.png"));
				GL11.glTranslated(0.5D, 1.4D, 0.5D);
				GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
				RenderForceManipulator.MODEL.render(0.0625F);
			}

			GL11.glPopMatrix();
		} else {
			CalclaviaRenderHelper.renderNormalBlockAsItem(block, metadata, renderer);
		}

	}

	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelID, RenderBlocks renderer) {
		return false;
	}

	public boolean shouldRender3DInInventory() {
		return true;
	}

	public int getRenderId() {
		return ID;
	}
}
