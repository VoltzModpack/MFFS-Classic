package mffs.tileentity;

import calclavia.lib.CalculationHelper;
import mffs.ModularForceFieldSystem;
import mffs.api.IFieldInteraction;
import mffs.api.modules.IModule;
import net.minecraft.tileentity.TileEntity;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.Set;

public class ProjectorCalculationThread extends Thread {

	private IFieldInteraction projector;
	private ProjectorCalculationThread.IThreadCallBack callBack;

	public ProjectorCalculationThread(IFieldInteraction projector) {
		this.projector = projector;
	}

	public ProjectorCalculationThread(IFieldInteraction projector, ProjectorCalculationThread.IThreadCallBack callBack) {
		this(projector);
		this.callBack = callBack;
	}

	public void run() {
		this.projector.setCalculating(true);

		try {
			if (this.projector.getMode() != null) {
				Set newField;
				if (this.projector.getModuleCount(ModularForceFieldSystem.itemModuleInvert, new int[0]) > 0) {
					newField = this.projector.getMode().getInteriorPoints(this.projector);
				} else {
					newField = this.projector.getMode().getExteriorPoints(this.projector);
				}

				Vector3 translation = this.projector.getTranslation();
				int rotationYaw = this.projector.getRotationYaw();
				int rotationPitch = this.projector.getRotationPitch();
				Iterator i$ = newField.iterator();

				while (i$.hasNext()) {
					Vector3 position = (Vector3) i$.next();
					if (rotationYaw != 0 || rotationPitch != 0) {
						CalculationHelper.rotateByAngle(position, (double) rotationYaw, (double) rotationPitch);
					}

					position.add(new Vector3((TileEntity) this.projector));
					position.add(translation);
					if (position.intY() <= ((TileEntity) this.projector).worldObj.getHeight()) {
						this.projector.getCalculatedField().add(position.round());
					}
				}

				i$ = this.projector.getModules(this.projector.getModuleSlots()).iterator();

				while (i$.hasNext()) {
					IModule module = (IModule) i$.next();
					module.onCalculate(this.projector, this.projector.getCalculatedField());
				}
			}
		} catch (Exception var7) {
			var7.printStackTrace();
		}

		this.projector.setCalculating(false);
		this.projector.setCalculated(true);
		if (this.callBack != null) {
			this.callBack.onThreadComplete();
		}

	}

	public interface IThreadCallBack {

		void onThreadComplete();
	}
}
