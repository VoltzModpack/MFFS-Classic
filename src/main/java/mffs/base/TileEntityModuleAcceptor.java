package mffs.base;

import mffs.ModularForceFieldSystem;
import mffs.Settings;
import mffs.api.ICache;
import mffs.api.modules.IModule;
import mffs.api.modules.IModuleAcceptor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.*;

public abstract class TileEntityModuleAcceptor extends TileEntityFortron implements IModuleAcceptor, ICache {

	public final HashMap cache = new HashMap();
	public int startModuleIndex = 0;
	public int endModuleIndex = this.getSizeInventory() - 1;
	protected int capacityBase = 500;
	protected int capacityBoost = 5;

	public void initiate() {
		super.initiate();
		super.fortronTank.setCapacity((this.getModuleCount(ModularForceFieldSystem.itemModuleCapacity) * this.capacityBoost + this.capacityBase) * 1000);
	}

	public void consumeCost() {
		if (this.getFortronCost() > 0) {
			this.requestFortron(this.getFortronCost(), true);
		}

	}

	public ItemStack getModule(IModule module) {
		String cacheID = "getModule_" + module.hashCode();
		if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof ItemStack) {
			return (ItemStack) this.cache.get(cacheID);
		} else {
			ItemStack returnStack = new ItemStack((Item) module, 0);
			Iterator i$ = this.getModuleStacks().iterator();

			while (i$.hasNext()) {
				ItemStack comparedModule = (ItemStack) i$.next();
				if (comparedModule.getItem() == module) {
					returnStack.stackSize += comparedModule.stackSize;
				}
			}

			if (Settings.USE_CACHE) {
				this.cache.put(cacheID, returnStack.copy());
			}

			return returnStack;
		}
	}

	public int getModuleCount(IModule module, int... slots) {
		int count = 0;
		if (module != null) {
			String cacheID = "getModuleCount_" + module.hashCode();
			if (slots != null) {
				cacheID = cacheID + "_" + Arrays.hashCode(slots);
			}

			if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof Integer) {
				return (Integer) this.cache.get(cacheID);
			}

			if (slots != null && slots.length > 0) {
				int[] arr$ = slots;
				int len$ = slots.length;

				for (int i$ = 0; i$ < len$; ++i$) {
					int slotID = arr$[i$];
					if (this.getStackInSlot(slotID) != null && this.getStackInSlot(slotID).getItem() == module) {
						count += this.getStackInSlot(slotID).stackSize;
					}
				}
			} else {
				Iterator i$ = this.getModuleStacks().iterator();

				while (i$.hasNext()) {
					ItemStack itemStack = (ItemStack) i$.next();
					if (itemStack.getItem() == module) {
						count += itemStack.stackSize;
					}
				}
			}

			if (Settings.USE_CACHE) {
				this.cache.put(cacheID, count);
			}
		}

		return count;
	}

	public Set getModuleStacks(int... slots) {
		String cacheID = "getModuleStacks_";
		if (slots != null) {
			cacheID = cacheID + Arrays.hashCode(slots);
		}

		if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof Set) {
			return (Set) this.cache.get(cacheID);
		} else {
			Set modules = new HashSet();
			if (slots != null && slots.length > 0) {
				int[] arr$ = slots;
				int len$ = slots.length;

				for (int i$ = 0; i$ < len$; ++i$) {
					int slotID = arr$[i$];
					ItemStack itemStack = this.getStackInSlot(slotID);
					if (itemStack != null && itemStack.getItem() instanceof IModule) {
						modules.add(itemStack);
					}
				}
			} else {
				for (int slotID = this.startModuleIndex; slotID <= this.endModuleIndex; ++slotID) {
					ItemStack itemStack = this.getStackInSlot(slotID);
					if (itemStack != null && itemStack.getItem() instanceof IModule) {
						modules.add(itemStack);
					}
				}
			}

			if (Settings.USE_CACHE) {
				this.cache.put(cacheID, modules);
			}

			return modules;
		}
	}

	public Set getModules(int... slots) {
		String cacheID = "getModules_";
		if (slots != null) {
			cacheID = cacheID + Arrays.hashCode(slots);
		}

		if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof Set) {
			return (Set) this.cache.get(cacheID);
		} else {
			Set modules = new HashSet();
			if (slots != null && slots.length > 0) {
				int[] arr$ = slots;
				int len$ = slots.length;

				for (int i$ = 0; i$ < len$; ++i$) {
					int slotID = arr$[i$];
					ItemStack itemStack = this.getStackInSlot(slotID);
					if (itemStack != null && itemStack.getItem() instanceof IModule) {
						modules.add((IModule) itemStack.getItem());
					}
				}
			} else {
				for (int slotID = this.startModuleIndex; slotID <= this.endModuleIndex; ++slotID) {
					ItemStack itemStack = this.getStackInSlot(slotID);
					if (itemStack != null && itemStack.getItem() instanceof IModule) {
						modules.add((IModule) itemStack.getItem());
					}
				}
			}

			if (Settings.USE_CACHE) {
				this.cache.put(cacheID, modules);
			}

			return modules;
		}
	}

	public int getFortronCost() {
		String cacheID = "getFortronCost";
		if (Settings.USE_CACHE && this.cache.containsKey(cacheID) && this.cache.get(cacheID) instanceof Integer) {
			return (Integer) this.cache.get(cacheID);
		} else {
			float cost = 0.0F;
			Iterator i$ = this.getModuleStacks().iterator();

			while (i$.hasNext()) {
				ItemStack itemStack = (ItemStack) i$.next();
				if (itemStack != null) {
					cost += (float) itemStack.stackSize * ((IModule) itemStack.getItem()).getFortronCost(this.getAmplifier());
				}
			}

			int result = Math.round(cost);
			if (Settings.USE_CACHE) {
				this.cache.put(cacheID, result);
			}

			return result;
		}
	}

	protected float getAmplifier() {
		return 1.0F;
	}

	public void onInventoryChanged() {
		super.onInventoryChanged();
		super.fortronTank.setCapacity((this.getModuleCount(ModularForceFieldSystem.itemModuleCapacity) * this.capacityBoost + this.capacityBase) * 1000);
		this.clearCache();
	}

	public Object getCache(String cacheID) {
		return this.cache.get(cacheID);
	}

	public void clearCache(String cacheID) {
		this.cache.remove(cacheID);
	}

	public void clearCache() {
		this.cache.clear();
	}
}
