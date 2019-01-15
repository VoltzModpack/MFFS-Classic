package buildcraft.api.transport;

import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class PipeManager {

	public static List extractionHandlers = new ArrayList();

	public static void registerExtractionHandler(IExtractionHandler handler) {
		extractionHandlers.add(handler);
	}

	public static boolean canExtractItems(Object extractor, World world, int i, int j, int k) {
		Iterator i$ = extractionHandlers.iterator();

		IExtractionHandler handler;
		do {
			if (!i$.hasNext()) {
				return true;
			}

			handler = (IExtractionHandler) i$.next();
		} while (handler.canExtractItems(extractor, world, i, j, k));

		return false;
	}

	public static boolean canExtractLiquids(Object extractor, World world, int i, int j, int k) {
		Iterator i$ = extractionHandlers.iterator();

		IExtractionHandler handler;
		do {
			if (!i$.hasNext()) {
				return true;
			}

			handler = (IExtractionHandler) i$.next();
		} while (handler.canExtractLiquids(extractor, world, i, j, k));

		return false;
	}
}
