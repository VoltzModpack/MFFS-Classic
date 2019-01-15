package mffs.item.module.projector;

import mffs.api.IFieldInteraction;
import mffs.item.module.ItemModule;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.Set;

public class ItemModuleManipulator extends ItemModule {

	public ItemModuleManipulator(int i) {
		super(i, "moduleManipulator");
	}

	public void onCalculate(IFieldInteraction projector, Set fieldBlocks) {
		Iterator it = fieldBlocks.iterator();

		while (it.hasNext()) {
			Vector3 position = (Vector3) it.next();
			if (position.y < (double) ((TileEntity) projector).yCoord) {
				it.remove();
			}
		}

	}
}
