package mffs.tileentity;

import calclavia.lib.CalculationHelper;
import mffs.DelayedEvent;
import mffs.IDelayedEventHandler;
import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.ICache;
import mffs.api.IFieldInteraction;
import mffs.api.modules.IModule;
import mffs.api.modules.IProjectorMode;
import mffs.base.TileEntityModuleAcceptor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;
import universalelectricity.core.vector.Vector3;
import universalelectricity.core.vector.VectorHelper;

import java.util.*;

public abstract class TileEntityFieldInteraction extends TileEntityModuleAcceptor implements IFieldInteraction, IDelayedEventHandler {

	protected static final int MODULE_SLOT_ID = 2;
	protected boolean isCalculating = false;
	protected boolean isCalculated = false;
	protected final Set calculatedField = Collections.synchronizedSet(new HashSet());
	private final List delayedEvents = new ArrayList();
	private final List quedDelayedEvents = new ArrayList();

	public void updateEntity() {
		super.updateEntity();
		if (this.delayedEvents.size() > 0) {
			do {
				this.quedDelayedEvents.clear();
				Iterator it = this.delayedEvents.iterator();

				while (it.hasNext()) {
					DelayedEvent evt = (DelayedEvent) it.next();
					evt.update();
					if (evt.ticks <= 0) {
						it.remove();
					}
				}

				this.delayedEvents.addAll(this.quedDelayedEvents);
			} while (!this.quedDelayedEvents.isEmpty());
		}

	}

	protected void calculateForceField(ProjectorCalculationThread.IThreadCallBack callBack) {
		if (!super.worldObj.isRemote && !this.isCalculating && this.getMode() != null) {
			if (this.getModeStack().getItem() instanceof ICache) {
				((ICache) this.getModeStack().getItem()).clearCache();
			}

			this.calculatedField.clear();
			(new ProjectorCalculationThread(this, callBack)).start();
		}

	}

	protected void calculateForceField() {
		this.calculateForceField((ProjectorCalculationThread.IThreadCallBack) null);
	}

	public ItemStack getModeStack() {
		return this.getStackInSlot(2) != null && this.getStackInSlot(2).getItem() instanceof IProjectorMode ? this.getStackInSlot(2) : null;
	}

	public IProjectorMode getMode() {
		return this.getModeStack() != null ? (IProjectorMode) this.getModeStack().getItem() : null;
	}

	public int getSidedModuleCount(IModule module, ForgeDirection... direction) {
		int count = 0;
		if (direction != null && direction.length > 0) {
			ForgeDirection[] arr$ = direction;
			int len$ = direction.length;

			for (int i$ = 0; i$ < len$; ++i$) {
				ForgeDirection checkDir = arr$[i$];
				count += this.getModuleCount(module, this.getSlotsBasedOnDirection(checkDir));
			}
		} else {
			for (int i = 0; i < 6; ++i) {
				ForgeDirection checkDir = ForgeDirection.getOrientation(i);
				count += this.getModuleCount(module, this.getSlotsBasedOnDirection(checkDir));
			}
		}

		return count;
	}

	public int[] getModuleSlots() {
		return new int[]{15, 16, 17, 18, 19, 20};
	}

	public Vector3 getTranslation() {
		String cacheID = "getTranslation";
		if (Settings.USE_CACHE && super.cache.containsKey(cacheID) && super.cache.get(cacheID) instanceof Vector3) {
			return (Vector3) super.cache.get(cacheID);
		} else {
			ForgeDirection direction = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN) {
				direction = ForgeDirection.NORTH;
			}

			int zTranslationNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.NORTH)));
			int zTranslationPos = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.SOUTH)));
			int xTranslationNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.WEST)));
			int xTranslationPos = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.EAST)));
			int yTranslationPos = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(ForgeDirection.UP));
			int yTranslationNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleTranslate, this.getSlotsBasedOnDirection(ForgeDirection.DOWN));
			Vector3 translation = new Vector3((double) (xTranslationPos - xTranslationNeg), (double) (yTranslationPos - yTranslationNeg), (double) (zTranslationPos - zTranslationNeg));
			if (Settings.USE_CACHE) {
				super.cache.put(cacheID, translation);
			}

			return translation;
		}
	}

	public Vector3 getPositiveScale() {
		String cacheID = "getPositiveScale";
		if (Settings.USE_CACHE && super.cache.containsKey(cacheID) && super.cache.get(cacheID) instanceof Vector3) {
			return (Vector3) super.cache.get(cacheID);
		} else {
			ForgeDirection direction = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN) {
				direction = ForgeDirection.NORTH;
			}

			int zScalePos = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.SOUTH)));
			int xScalePos = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.EAST)));
			int yScalePos = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(ForgeDirection.UP));
			int omnidirectionalScale = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getModuleSlots());
			zScalePos += omnidirectionalScale;
			xScalePos += omnidirectionalScale;
			yScalePos += omnidirectionalScale;
			Vector3 positiveScale = new Vector3((double) xScalePos, (double) yScalePos, (double) zScalePos);
			if (Settings.USE_CACHE) {
				super.cache.put(cacheID, positiveScale);
			}

			return positiveScale;
		}
	}

	public Vector3 getNegativeScale() {
		String cacheID = "getNegativeScale";
		if (Settings.USE_CACHE && super.cache.containsKey(cacheID) && super.cache.get(cacheID) instanceof Vector3) {
			return (Vector3) super.cache.get(cacheID);
		} else {
			ForgeDirection direction = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			if (direction == ForgeDirection.UP || direction == ForgeDirection.DOWN) {
				direction = ForgeDirection.NORTH;
			}

			int zScaleNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.NORTH)));
			int xScaleNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.WEST)));
			int yScaleNeg = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getSlotsBasedOnDirection(ForgeDirection.DOWN));
			int omnidirectionalScale = this.getModuleCount(ModularForceFieldSystem.itemModuleScale, this.getModuleSlots());
			zScaleNeg += omnidirectionalScale;
			xScaleNeg += omnidirectionalScale;
			yScaleNeg += omnidirectionalScale;
			Vector3 negativeScale = new Vector3((double) xScaleNeg, (double) yScaleNeg, (double) zScaleNeg);
			if (Settings.USE_CACHE) {
				super.cache.put(cacheID, negativeScale);
			}

			return negativeScale;
		}
	}

	public int getRotationYaw() {
		String cacheID = "getRotationYaw";
		if (Settings.USE_CACHE && super.cache.containsKey(cacheID) && super.cache.get(cacheID) instanceof Integer) {
			return (Integer) super.cache.get(cacheID);
		} else {
			ForgeDirection direction = this.getDirection(super.worldObj, super.xCoord, super.yCoord, super.zCoord);
			int horizontalRotation = this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.EAST))) - this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.WEST))) + this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.SOUTH))) - this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(VectorHelper.getOrientationFromSide(direction, ForgeDirection.NORTH)));
			horizontalRotation *= 2;
			if (Settings.USE_CACHE) {
				super.cache.put(cacheID, horizontalRotation);
			}

			return horizontalRotation;
		}
	}

	public int getRotationPitch() {
		String cacheID = "getRotationPitch";
		if (Settings.USE_CACHE && super.cache.containsKey(cacheID) && super.cache.get(cacheID) instanceof Integer) {
			return (Integer) super.cache.get(cacheID);
		} else {
			int verticleRotation = this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(ForgeDirection.UP)) - this.getModuleCount(ModularForceFieldSystem.itemModuleRotate, this.getSlotsBasedOnDirection(ForgeDirection.DOWN));
			verticleRotation *= 2;
			if (Settings.USE_CACHE) {
				super.cache.put(cacheID, verticleRotation);
			}

			return verticleRotation;
		}
	}

	public Set getInteriorPoints() {
		String cacheID = "getInteriorPoints";
		if (Settings.USE_CACHE && super.cache.containsKey("getInteriorPoints") && super.cache.get("getInteriorPoints") instanceof Set) {
			return (Set) super.cache.get("getInteriorPoints");
		} else {
			if (this.getModeStack().getItem() instanceof ICache) {
				((ICache) this.getModeStack().getItem()).clearCache();
			}

			Set newField = this.getMode().getInteriorPoints(this);
			Set returnField = new HashSet();
			Vector3 translation = this.getTranslation();
			int rotationYaw = this.getRotationYaw();
			int rotationPitch = this.getRotationPitch();
			Iterator i$ = newField.iterator();

			while (i$.hasNext()) {
				Vector3 position = (Vector3) i$.next();
				Vector3 newPosition = position.clone();
				if (rotationYaw != 0 || rotationPitch != 0) {
					CalculationHelper.rotateByAngle(newPosition, (double) rotationYaw, (double) rotationPitch);
				}

				newPosition.add(new Vector3(this));
				newPosition.add(translation);
				returnField.add(newPosition);
			}

			if (Settings.USE_CACHE) {
				super.cache.put("getInteriorPoints", returnField);
			}

			return returnField;
		}
	}

	public int[] getSlotsBasedOnDirection(ForgeDirection direction) {
		switch (direction) {
			case UP:
				return new int[]{3, 11};
			case DOWN:
				return new int[]{6, 14};
			case NORTH:
				return new int[]{8, 10};
			case SOUTH:
				return new int[]{7, 9};
			case WEST:
				return new int[]{4, 5};
			case EAST:
				return new int[]{12, 13};
			default:
				return new int[0];
		}
	}

	public void setCalculating(boolean bool) {
		this.isCalculating = bool;
	}

	public void setCalculated(boolean bool) {
		this.isCalculated = bool;
	}

	public Set getCalculatedField() {
		return this.calculatedField;
	}

	public List getDelayedEvents() {
		return this.delayedEvents;
	}

	public List getQuedDelayedEvents() {
		return this.quedDelayedEvents;
	}
}
