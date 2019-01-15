package mffs.tileentity;

import universalelectricity.core.vector.Vector3;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ManipulatorCalculationThread extends Thread {

	private TileEntityForceManipulator manipulator;
	private ManipulatorCalculationThread.IThreadCallBack callBack;

	public ManipulatorCalculationThread(TileEntityForceManipulator projector) {
		this.manipulator = projector;
	}

	public ManipulatorCalculationThread(TileEntityForceManipulator projector, ManipulatorCalculationThread.IThreadCallBack callBack) {
		this(projector);
		this.callBack = callBack;
	}

	public void run() {
		this.manipulator.isCalculatingManipulation = true;

		try {
			Set mobilizationPoints = this.manipulator.getInteriorPoints();
			if (this.manipulator.canMove()) {
				this.manipulator.manipulationVectors = new HashSet();
				Iterator i$ = mobilizationPoints.iterator();

				while (i$.hasNext()) {
					Vector3 position = (Vector3) i$.next();
					this.manipulator.manipulationVectors.add(position.clone());
				}
			}
		} catch (Exception var4) {
			var4.printStackTrace();
		}

		this.manipulator.isCalculatingManipulation = false;
		if (this.callBack != null) {
			this.callBack.onThreadComplete();
		}

	}

	public interface IThreadCallBack {

		void onThreadComplete();
	}
}
