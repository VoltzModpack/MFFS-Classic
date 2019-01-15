package mffs.block;

import mffs.base.BlockMachine;
import mffs.base.TileEntityBase;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.IBlockAccess;

public class BlockMachineBlock extends BlockMachine {

	protected Icon blockIconTop;
	protected Icon blockIconOn;
	protected Icon blockIconTopOn;

	public BlockMachineBlock(int id, String name) {
		super(id, name);
	}

	public Icon getBlockTexture(IBlockAccess par1IBlockAccess, int x, int y, int z, int side) {
		TileEntity tileEntity = par1IBlockAccess.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof TileEntityBase && ((TileEntityBase) tileEntity).isActive()) {
			return side != 0 && side != 1 ? this.blockIconOn : this.blockIconTopOn;
		} else {
			return side != 0 && side != 1 ? super.blockIcon : this.blockIconTop;
		}
	}

	public void registerIcons(IconRegister par1IconRegister) {
		super.blockIcon = par1IconRegister.registerIcon(this.getUnlocalizedName2());
		this.blockIconTop = par1IconRegister.registerIcon(this.getUnlocalizedName2() + "_top");
		this.blockIconOn = par1IconRegister.registerIcon(this.getUnlocalizedName2() + "_on");
		this.blockIconTopOn = par1IconRegister.registerIcon(this.getUnlocalizedName2() + "_top_on");
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean renderAsNormalBlock() {
		return true;
	}

	public int getRenderType() {
		return 0;
	}
}
