package buildcraft.api.transport;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Method;

public class FacadeManager {

	private static Method addFacade;

	public static void addFacade(ItemStack is) {
		try {
			if (addFacade == null) {
				Class facade = Class.forName("buildcraft.transport.ItemFacade");
				addFacade = facade.getMethod("addFacade", ItemStack.class);
			}

			addFacade.invoke((Object) null, is);
		} catch (Exception var2) {
		}

	}
}
