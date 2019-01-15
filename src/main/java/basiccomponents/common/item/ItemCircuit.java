package basiccomponents.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

import java.util.List;

public class ItemCircuit extends ItemBase {

	public static final String[] TYPES = new String[]{"circuitBasic", "circuitAdvanced", "circuitElite"};

	public ItemCircuit(int id, int texture) {
		super("circuit", id);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int damage) {
		return damage;
	}

	public String getUnlocalizedName(ItemStack itemStack) {
		return "item.basiccomponents:" + TYPES[itemStack.getItemDamage()];
	}

	public void getSubItems(int par1, CreativeTabs par2CreativeTabs, List list) {
		for (int i = 0; i < TYPES.length; ++i) {
			list.add(new ItemStack(this, 1, i));
		}

	}
}
