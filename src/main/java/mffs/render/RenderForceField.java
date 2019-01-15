package mffs.render;

import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.tileentity.TileEntityForceField;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderForceField implements ISimpleBlockRenderingHandler {

	public static final int ID = RenderingRegistry.getNextAvailableRenderId();

	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		CalclaviaRenderHelper.renderNormalBlockAsItem(block, metadata, renderer);
	}

	public boolean renderWorldBlock(IBlockAccess iBlockAccess, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		int renderType = 0;
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityForceField) {
			ItemStack checkStack = ((TileEntityForceField) tileEntity).camoStack;
			if (checkStack != null) {
				Block checkBlock = Block.blocksList[((ItemBlock) checkStack.getItem()).getBlockID()];
				if (checkBlock != null) {
					renderType = checkBlock.getRenderType();
				}
			}
		}

		if (renderType >= 0) {
			switch (renderType) {
				case 1:
					renderer.renderCrossedSquares(block, x, y, z);
					break;
				case 2:
				case 3:
				case 9:
				case 10:
				case 11:
				case 15:
				case 18:
				case 19:
				case 21:
				case 22:
				case 24:
				case 25:
				case 26:
				case 27:
				case 28:
				case 32:
				case 33:
				case 34:
				case 35:
				case 36:
				case 37:
				case 38:
				default:
					renderer.renderStandardBlock(block, x, y, z);
					break;
				case 4:
					renderer.renderBlockFluids(block, x, y, z);
					break;
				case 5:
					renderer.renderBlockRedstoneWire(block, x, y, z);
					break;
				case 6:
					renderer.renderBlockCrops(block, x, y, z);
					break;
				case 7:
					renderer.renderBlockDoor(block, x, y, z);
					break;
				case 8:
					renderer.renderBlockLadder(block, x, y, z);
					break;
				case 12:
					renderer.renderBlockLever(block, x, y, z);
					break;
				case 13:
					renderer.renderBlockCactus(block, x, y, z);
					break;
				case 14:
					renderer.renderBlockBed(block, x, y, z);
					break;
				case 16:
					renderer.renderPistonBase(block, x, y, z, false);
					break;
				case 17:
					renderer.renderPistonExtension(block, x, y, z, true);
					break;
				case 20:
					renderer.renderBlockVine(block, x, y, z);
					break;
				case 23:
					renderer.renderBlockLilyPad(block, x, y, z);
					break;
				case 29:
					renderer.renderBlockTripWireSource(block, x, y, z);
					break;
				case 30:
					renderer.renderBlockTripWire(block, x, y, z);
					break;
				case 31:
					renderer.renderBlockLog(block, x, y, z);
					break;
				case 39:
					renderer.renderBlockQuartz(block, x, y, z);
			}

			return true;
		} else {
			return false;
		}
	}

	public boolean shouldRender3DInInventory() {
		return true;
	}

	public int getRenderId() {
		return ID;
	}
}
