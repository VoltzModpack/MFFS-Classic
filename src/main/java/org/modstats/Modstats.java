package org.modstats;

import cpw.mods.fml.common.FMLLog;

public class Modstats {

	private static final Modstats INSTANCE = new Modstats();
	private static final String CLASS_TEMPLATE = "org.modstats.reporter.v%d.Reporter";
	private IModstatsReporter reporter = this.locateReporter();

	private Modstats() {
	}

	public IModstatsReporter getReporter() {
		return this.reporter;
	}

	private IModstatsReporter locateReporter() {
		int i = 1;

		Class latest;
		for (latest = null; i < 100; ++i) {
			try {
				Class candidate = Class.forName(String.format("org.modstats.reporter.v%d.Reporter", i));
				if (IModstatsReporter.class.isAssignableFrom(candidate)) {
					latest = candidate;
				}
			} catch (Exception var5) {
				break;
			}
		}

		if (latest == null) {
			FMLLog.warning("Modstats reporter class not found.", new Object[0]);
		} else {
			try {
				return (IModstatsReporter) latest.newInstance();
			} catch (Exception var4) {
				FMLLog.warning("Modstats reporter class can't be instantiated.", new Object[0]);
			}
		}

		return null;
	}

	public static Modstats instance() {
		return INSTANCE;
	}
}
