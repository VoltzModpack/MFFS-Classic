package universalelectricity.prefab.block;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.block.IConductor;

public abstract class BlockConductor extends BlockContainer {

	public BlockConductor(int id, Material material) {
		super(id, material);
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof IConductor) {
			((IConductor) tileEntity).updateAdjacentConnections();
		}

	}

	public void onNeighborBlockChange(World world, int x, int y, int z, int blockID) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity instanceof IConductor) {
			((IConductor) tileEntity).updateAdjacentConnections();
		}

	}
}
