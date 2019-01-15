package buildcraft.api.gates;

import buildcraft.api.transport.IPipe;
import net.minecraft.block.Block;
import net.minecraft.tileentity.TileEntity;

import java.util.Iterator;
import java.util.LinkedList;

public class ActionManager {

	public static ITrigger[] triggers = new ITrigger[1024];
	public static IAction[] actions = new IAction[1024];
	private static LinkedList triggerProviders = new LinkedList();
	private static LinkedList actionProviders = new LinkedList();

	public static void registerTriggerProvider(ITriggerProvider provider) {
		if (provider != null && !triggerProviders.contains(provider)) {
			triggerProviders.add(provider);
		}

	}

	public static LinkedList getNeighborTriggers(Block block, TileEntity entity) {
		LinkedList triggers = new LinkedList();
		Iterator i$ = triggerProviders.iterator();

		while (true) {
			LinkedList toAdd;
			do {
				if (!i$.hasNext()) {
					return triggers;
				}

				ITriggerProvider provider = (ITriggerProvider) i$.next();
				toAdd = provider.getNeighborTriggers(block, entity);
			} while (toAdd == null);

			Iterator i$ = toAdd.iterator();

			while (i$.hasNext()) {
				ITrigger t = (ITrigger) i$.next();
				if (!triggers.contains(t)) {
					triggers.add(t);
				}
			}
		}
	}

	public static void registerActionProvider(IActionProvider provider) {
		if (provider != null && !actionProviders.contains(provider)) {
			actionProviders.add(provider);
		}

	}

	public static LinkedList getNeighborActions(Block block, TileEntity entity) {
		LinkedList actions = new LinkedList();
		Iterator i$ = actionProviders.iterator();

		while (true) {
			LinkedList toAdd;
			do {
				if (!i$.hasNext()) {
					return actions;
				}

				IActionProvider provider = (IActionProvider) i$.next();
				toAdd = provider.getNeighborActions(block, entity);
			} while (toAdd == null);

			Iterator i$ = toAdd.iterator();

			while (i$.hasNext()) {
				IAction t = (IAction) i$.next();
				if (!actions.contains(t)) {
					actions.add(t);
				}
			}
		}
	}

	public static LinkedList getPipeTriggers(IPipe pipe) {
		LinkedList triggers = new LinkedList();
		Iterator i$ = triggerProviders.iterator();

		while (true) {
			LinkedList toAdd;
			do {
				if (!i$.hasNext()) {
					return triggers;
				}

				ITriggerProvider provider = (ITriggerProvider) i$.next();
				toAdd = provider.getPipeTriggers(pipe);
			} while (toAdd == null);

			Iterator i$ = toAdd.iterator();

			while (i$.hasNext()) {
				ITrigger t = (ITrigger) i$.next();
				if (!triggers.contains(t)) {
					triggers.add(t);
				}
			}
		}
	}
}
