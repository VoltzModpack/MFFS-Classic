package universalelectricity.prefab.flag;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import universalelectricity.core.vector.Vector3;
import universalelectricity.prefab.vector.Region3;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FlagRegion extends FlagBase {

	public FlagWorld flagWorld;
	public String name;
	public Region3 region;
	private final List flags = new ArrayList();

	public FlagRegion(FlagWorld worldFlagData) {
		this.flagWorld = worldFlagData;
	}

	public FlagRegion(FlagWorld flagWorld, String name, Region3 region) {
		this.flagWorld = flagWorld;
		this.name = name;
		this.region = region;
	}

	public void readFromNBT(NBTTagCompound nbt) {
		this.name = nbt.getName();
		Vector3 startVector = Vector3.readFromNBT(nbt.getCompoundTag("min"));
		Vector3 endVector = Vector3.readFromNBT(nbt.getCompoundTag("max"));
		this.region = new Region3(startVector, endVector);
		NBTTagList flagList = nbt.getTagList("flags");

		for (int i = 0; i < flagList.tagCount(); ++i) {
			NBTTagCompound childNode = (NBTTagCompound) flagList.tagAt(i);

			try {
				Flag flag = new Flag(this);
				flag.readFromNBT(childNode);
				this.flags.add(flag);
			} catch (Exception var8) {
				System.out.println("Mod Flag: Failed to read flag data: " + childNode.getName());
				var8.printStackTrace();
			}
		}

	}

	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setName(this.name);
		nbt.setTag("min", this.region.min.writeToNBT(new NBTTagCompound()));
		nbt.setTag("max", this.region.max.writeToNBT(new NBTTagCompound()));
		NBTTagList flagList = new NBTTagList();
		Iterator i$ = this.getFlags().iterator();

		while (i$.hasNext()) {
			Flag flag = (Flag) i$.next();

			try {
				flagList.appendTag(flag.getNBT());
			} catch (Exception var6) {
				System.out.println("Failed to save world flag data: " + flag.name);
				var6.printStackTrace();
			}
		}

		nbt.setTag("flags", flagList);
	}

	public boolean containsValue(String flagName, String checkValue, Vector3 position) {
		Iterator i$ = this.flags.iterator();

		Flag flag;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			flag = (Flag) i$.next();
		} while (!flag.name.equalsIgnoreCase(flagName) || !flag.value.equalsIgnoreCase(checkValue));

		return true;
	}

	public boolean setFlag(String flagName, String value) {
		this.removeFlag(flagName);
		return value != null && value != "" && !this.containsFlag(flagName) ? this.flags.add(new Flag(this, flagName, value)) : false;
	}

	public boolean containsFlag(String flagName) {
		Iterator i$ = this.flags.iterator();

		Flag region;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			region = (Flag) i$.next();
		} while (!region.name.equalsIgnoreCase(flagName));

		return true;
	}

	public boolean removeFlag(String flagName) {
		Iterator i$ = this.flags.iterator();

		Flag region;
		do {
			if (!i$.hasNext()) {
				return false;
			}

			region = (Flag) i$.next();
		} while (!region.name.equalsIgnoreCase(flagName));

		this.flags.remove(region);
		return true;
	}

	public List getFlags() {
		Iterator it = this.flags.iterator();

		while (true) {
			while (it.hasNext()) {
				Flag flag = (Flag) it.next();
				if (flag == null) {
					it.remove();
				} else if (flag.name == null || flag.name == "") {
					it.remove();
				}
			}

			return this.flags;
		}
	}

	public void edit(Vector3 position, int radius) {
		Vector3 minVec = new Vector3((double) (position.intX() - radius), 0.0D, (double) (position.intZ() - radius));
		Vector3 maxVec = new Vector3((double) (position.intX() + radius), (double) this.flagWorld.world.getHeight(), (double) (position.intZ() + radius));
		this.region = new Region3(minVec, maxVec);
	}
}
