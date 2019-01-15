package basiccomponents.common.block;

import basiccomponents.common.BasicComponents;
import basiccomponents.common.tileentity.TileEntityCopperWire;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.prefab.block.BlockConductor;

public class BlockCopperWire extends BlockConductor {

	public BlockCopperWire(int id) {
		super(BasicComponents.CONFIGURATION.getItem("copperWire", id).getInt(id), Material.cloth);
		this.setUnlocalizedName("basiccomponents:copperWire");
		this.setStepSound(Block.soundClothFootstep);
		this.setResistance(0.2F);
		this.setHardness(0.1F);
		this.setBlockBounds(0.3F, 0.3F, 0.3F, 0.7F, 0.7F, 0.7F);
		this.setCreativeTab(CreativeTabs.tabRedstone);
		Block.setBurnProperties(super.blockID, 30, 60);
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return -1;
	}

	public TileEntity createNewTileEntity(World var1) {
		return new TileEntityCopperWire();
	}
}
