package basiccomponents.common.item;

import basiccomponents.common.BasicComponents;
import net.minecraft.item.Item;
import net.minecraft.util.Icon;

public class ItemBase extends Item {

	protected final Icon[] icons = new Icon[256];

	public ItemBase(String name, int id) {
		super(BasicComponents.CONFIGURATION.getItem(name, id).getInt(id));
		this.setUnlocalizedName("basiccomponents:" + name);
		this.setNoRepair();
	}
}
