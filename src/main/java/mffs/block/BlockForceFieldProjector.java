package mffs.block;

import mffs.base.BlockMachine;
import mffs.tileentity.TileEntityForceFieldProjector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockForceFieldProjector extends BlockMachine {

	public BlockForceFieldProjector(int id) {
		super(id, "projector");
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8F, 1.0F);
	}

	public TileEntity createNewTileEntity(World world) {
		return new TileEntityForceFieldProjector();
	}

	public boolean onMachineActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9) {
		TileEntityForceFieldProjector tileentity = (TileEntityForceFieldProjector) world.getBlockTileEntity(i, j, k);
		return tileentity.isDisabled() ? false : super.onMachineActivated(world, i, j, k, entityplayer, par6, par7, par8, par9);
	}

	public int getLightValue(IBlockAccess iBlockAccess, int x, int y, int z) {
		TileEntity tileEntity = iBlockAccess.getBlockTileEntity(x, y, z);
		return tileEntity instanceof TileEntityForceFieldProjector && ((TileEntityForceFieldProjector) tileEntity).getMode() != null ? 10 : super.getLightValue(iBlockAccess, x, y, z);
	}
}
