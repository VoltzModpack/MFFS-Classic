package mffs.item.module.projector;

import icbm.api.IBlockFrequency;
import mffs.api.IProjector;
import mffs.api.fortron.IFortronFrequency;
import mffs.base.TileEntityBase;
import mffs.fortron.FrequencyGrid;
import mffs.item.module.ItemModule;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.Set;

public class ItemModuleFusion extends ItemModule {

	public ItemModuleFusion(int i) {
		super(i, "moduleFusion");
		this.setMaxStackSize(1);
		this.setCost(1.0F);
	}

	public boolean onProject(IProjector projector, Set fieldBlocks) {
		Set machines = FrequencyGrid.instance().get(((IFortronFrequency) projector).getFrequency());
		Iterator i$ = machines.iterator();

		while (true) {
			IBlockFrequency compareProjector;
			do {
				do {
					do {
						do {
							do {
								if (!i$.hasNext()) {
									return false;
								}

								compareProjector = (IBlockFrequency) i$.next();
							} while (!(compareProjector instanceof IProjector));
						} while (compareProjector == projector);
					} while (((TileEntity) compareProjector).worldObj != ((TileEntity) projector).worldObj);
				} while (!((TileEntityBase) compareProjector).isActive());
			} while (((IProjector) compareProjector).getMode() == null);

			Iterator it = fieldBlocks.iterator();

			while (it.hasNext()) {
				Vector3 position = (Vector3) it.next();
				if (((IProjector) compareProjector).getMode().isInField((IProjector) compareProjector, position.clone())) {
					it.remove();
				}
			}
		}
	}
}
