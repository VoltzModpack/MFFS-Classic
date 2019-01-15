package universalelectricity.prefab.flag;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlagWorld extends FlagBase {

	public static final String GLOBAL_REGION = "dimension";
	public World world;
	private final List regions = new ArrayList();

	public FlagWorld(World world) {
		this.world = world;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		Iterator childCompounds = nbt.getTags().iterator();

		while (childCompounds.hasNext()) {
			NBTTagCompound childCompound = (NBTTagCompound) childCompounds.next();

			try {
				FlagRegion flagRegion = new FlagRegion(this);
				flagRegion.readFromNBT(childCompound);
				this.regions.add(flagRegion);
			} catch (Exception var5) {
				System.out.println("Mod Flag: Failed to read flag data: " + childCompound.getName());
				var5.printStackTrace();
			}
		}

	}

	public void writeToNBT(NBTTagCompound nbt) {
		Iterator i$ = this.regions.iterator();

		while (i$.hasNext()) {
			FlagRegion region = (FlagRegion) i$.next();

			try {
				NBTTagCompound flagCompound = new NBTTagCompound();
				region.writeToNBT(flagCompound);
				nbt.setTag(region.name, flagCompound);
			} catch (Exception var5) {
				System.out.println("Failed to save world flag data: " + region.name);
				var5.printStackTrace();
			}
		}

	}

	public List getFlagsInPosition(Vector3 position) {
		List returnFlags = new ArrayList();
		Iterator i$ = this.regions.iterator();

		while (true) {
			FlagRegion flagRegion;
			do {
				if (!i$.hasNext()) {
					return returnFlags;
				}

				flagRegion = (FlagRegion) i$.next();
			} while (!flagRegion.region.isIn(position) && !flagRegion.name.equalsIgnoreCase("dimension"));

			Iterator i$ = flagRegion.getFlags().iterator();

			while (i$.hasNext()) {
				Flag flag = (Flag) i$.next();
				returnFlags.add(flag);
			}
		}
	}

	public List getValues(String flagName, Vector3 position) {
		List values = new ArrayList();
		Iterator i$ = this.getFlagsInPosition(position).iterator();

		while (i$.hasNext()) {
			Flag flag = (Flag) i$.next();
			values.add(flag.value);
		}

		return values;
	}

	public boolean containsValue(String flagName, String checkValue, Vector3 position) {
		Iterator i$ = this.getFlagsInPosition(position).iterator();

		Flag flag;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			flag = (Flag) i$.next();
		} while (!flag.name.equalsIgnoreCase(flagName) || !flag.value.equalsIgnoreCase(checkValue));

		return true;
	}

	public boolean addRegion(String name, Vector3 position, int radius) {
		Vector3 minVec = new Vector3((double) (position.intX() - radius), 0.0D, (double) (position.intZ() - radius));
		Vector3 maxVec = new Vector3((double) (position.intX() + radius), (double) this.world.getHeight(), (double) (position.intZ() + radius));
		return this.regions.add(new FlagRegion(this, name, new Region3(minVec, maxVec)));
	}

	public FlagRegion getRegion(String name) {
		Iterator i$ = this.regions.iterator();

		FlagRegion region;
		do {
			if (!i$.hasNext()) {
				return null;
			}

			region = (FlagRegion) i$.next();
		} while (!region.name.equals(name));

		return region;
	}

	public List getRegions(Vector3 position) {
		List returnRegions = new ArrayList();
		Iterator i$ = this.regions.iterator();

		while (i$.hasNext()) {
			FlagRegion region = (FlagRegion) i$.next();
			if (region.region.isIn(position)) {
				returnRegions.add(region);
			}
		}

		return returnRegions;
	}

	public boolean removeRegion(String name) {
		Iterator i$ = this.regions.iterator();

		FlagRegion region;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			region = (FlagRegion) i$.next();
		} while (!region.name.equals(name));

		this.regions.remove(region);
		return true;
	}

	public List getRegions() {
		Iterator it = this.regions.iterator();

		while (true) {
			while (it.hasNext()) {
				FlagRegion region = (FlagRegion) it.next();
				if (region == null) {
					it.remove();
				} else if (region.name == null || region.name == "") {
					it.remove();
				}
			}

			return this.regions;
		}
	}
}
