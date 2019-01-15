package mffs.item.module.projector;

import mffs.api.IProjector;
import mffs.item.module.ItemModule;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.Set;

public class ItemModuleSponge extends ItemModule {

	public ItemModuleSponge(int i) {
		super(i, "moduleSponge");
		this.setMaxStackSize(1);
	}

	public boolean onProject(IProjector projector, Set fields) {
		if (projector.getTicks() % 60L == 0L) {
			World world = ((TileEntity) projector).worldObj;
			Iterator i$ = projector.getInteriorPoints().iterator();

			while (i$.hasNext()) {
				Vector3 point = (Vector3) i$.next();
				if (Block.blocksList[point.getBlockID(world)] instanceof BlockFluid) {
					point.setBlock(world, 0);
				}
			}
		}

		return super.onProject(projector, fields);
	}
}
