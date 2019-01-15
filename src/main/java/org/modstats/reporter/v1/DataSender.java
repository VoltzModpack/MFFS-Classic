package org.modstats.reporter.v1;

import argo.jdom.JdomParser;
import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import argo.jdom.JsonStringNode;
import argo.saj.InvalidSyntaxException;
import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.versioning.ComparableVersion;
import net.minecraft.client.Minecraft;
import net.minecraft.crash.CallableMinecraftVersion;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import org.modstats.ModVersionData;
import org.modstats.ModsUpdateEvent;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

class DataSender extends Thread {

	private static final String urlAutoTemplate = "http://modstats.org/api/v1/report?mc=%s&user=%s&data=%s&sign=%s&beta=%b&strict=%b";
	private static final String urlManualTemplate = "http://modstats.org/api/v1/check?mc=%s&user=%s&data=%s&sign=%s&beta=%b&strict=%b";
	private final Reporter reporter;
	public final boolean manual;

	public DataSender(Reporter reporter, boolean manual) {
		this.reporter = reporter;
		this.manual = manual;
	}

	private String toHexString(byte[] bytes) {
		char[] hexArray = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		char[] hexChars = new char[bytes.length * 2];

		for (int j = 0; j < bytes.length; ++j) {
			int v = bytes[j] & 255;
			hexChars[j * 2] = hexArray[v / 16];
			hexChars[j * 2 + 1] = hexArray[v % 16];
		}

		return new String(hexChars);
	}

	private String getPlayerId() throws IOException {
		File statDir = new File(Minecraft.getMinecraftDir(), "stats");
		if (!statDir.exists()) {
			statDir.mkdirs();
		}

		String mac = "";

		try {
			InetAddress address = InetAddress.getLocalHost();
			NetworkInterface ni = NetworkInterface.getByInetAddress(address);
			byte[] macArray = ni.getHardwareAddress();
			if (macArray != null) {
				mac = this.toHexString(macArray);
			}
		} catch (Exception var6) {
		}

		File uidFile = new File(statDir, "player.uid");
		String uid;
		if (uidFile.exists() && uidFile.canRead() && uidFile.length() == (long) (32 + mac.length())) {
			uid = Files.toString(uidFile, Charsets.US_ASCII);
			String storedMac = uid.substring(32);
			if (storedMac.equalsIgnoreCase(mac)) {
				return uid.substring(0, 32);
			}
		}

		uidFile.createNewFile();
		if (uidFile.canWrite()) {
			uid = UUID.randomUUID().toString().replace("-", "");
			FileOutputStream output = new FileOutputStream(uidFile);
			output.write((uid + mac).getBytes());
			output.close();
			return uid;
		} else {
			return "";
		}
	}

	private String getSignature(String data) {
		return Hashing.md5().hashString(data).toString();
	}

	private String getData() {
		StringBuilder b = new StringBuilder();
		Iterator i$ = this.reporter.registeredMods.entrySet().iterator();

		while (i$.hasNext()) {
			Entry item = (Entry) i$.next();
			b.append((String) item.getKey()).append("+").append(((ModVersionData) item.getValue()).version).append("$");
		}

		return b.toString();
	}

	private boolean checkIsNewer(String current, String received) {
		return (new ComparableVersion(received)).compareTo(new ComparableVersion(current)) > 0;
	}

	private void parseResponse(String response) {
		try {
			JsonRootNode json = (new JdomParser()).parse(response);
			if (!json.isNode(new Object[]{"mods"})) {
				FMLLog.info("[Modstats] Empty result", new Object[0]);
				return;
			}

			List modList = json.getArrayNode(new Object[]{"mods"});
			ModsUpdateEvent event = new ModsUpdateEvent();
			Iterator i$ = modList.iterator();

			while (true) {
				while (i$.hasNext()) {
					JsonNode modObject = (JsonNode) i$.next();
					String prefix = modObject.getStringValue(new Object[]{"code"});
					if (!this.reporter.registeredMods.containsKey(prefix)) {
						FMLLog.warning("[Modstats] Extra mod '%s' in service response", new Object[]{prefix});
					} else {
						String version = modObject.getStringValue(new Object[]{"ver"});
						if (version != null && !version.equals(((ModVersionData) this.reporter.registeredMods.get(prefix)).version) && this.checkIsNewer(((ModVersionData) this.reporter.registeredMods.get(prefix)).version, version)) {
							ModVersionData data = new ModVersionData(prefix, ((ModVersionData) this.reporter.registeredMods.get(prefix)).name, version);
							Map fields = modObject.getFields();
							Iterator i$ = fields.entrySet().iterator();

							while (i$.hasNext()) {
								Entry entry = (Entry) i$.next();
								String fieldName = ((JsonStringNode) entry.getKey()).getText();
								if (!fieldName.equals("code") && !fieldName.equals("ver")) {
									if (!(entry.getValue() instanceof JsonStringNode)) {
										FMLLog.warning(String.format("[Modstats] Too complex data in response for field '%s'.", fieldName), new Object[0]);
									} else {
										String value = ((JsonStringNode) entry.getValue()).getText();
										if (fieldName.equals("chlog")) {
											data.changeLogUrl = value;
										} else if (fieldName.equals("link")) {
											data.downloadUrl = value;
										} else {
											data.extraFields.put(fieldName, value);
										}
									}
								}
							}

							event.add(data);
						}
					}
				}

				if (event.getUpdatedMods().size() > 0) {
					MinecraftForge.EVENT_BUS.post(event);
				}

				if (!event.isCanceled() && event.getUpdatedMods().size() > 0) {
					List updatedModsToOutput = event.getUpdatedMods();
					StringBuilder builder = new StringBuilder("Updates found: ");
					Iterator iterator = updatedModsToOutput.iterator();

					while (iterator.hasNext()) {
						ModVersionData modVersionData = (ModVersionData) iterator.next();
						builder.append(modVersionData.name).append(" (").append(modVersionData.version).append(")").append(iterator.hasNext() ? "," : ".");
					}

					FMLLog.info("[Modstats] %s", new Object[]{builder.toString()});
					if (!this.reporter.config.logOnly && FMLCommonHandler.instance().getSide().isClient()) {
						Minecraft mc = FMLClientHandler.instance().getClient();

						for (int maxTries = 30; mc.thePlayer == null && maxTries > 0; --maxTries) {
							try {
								sleep(1000L);
							} catch (InterruptedException var15) {
							}
						}

						if (mc.thePlayer != null) {
							mc.thePlayer.addChatMessage(builder.toString());
						}
					}
				}
				break;
			}
		} catch (InvalidSyntaxException var16) {
			FMLLog.warning("[Modstats] Can't parse response: '%s'.", new Object[]{var16.getMessage()});
		}

	}

	public void run() {
		try {
			String data = this.getData();
			String playerId = this.getPlayerId();
			String hash = this.getSignature(playerId + "!" + data);
			String template = this.manual ? "http://modstats.org/api/v1/check?mc=%s&user=%s&data=%s&sign=%s&beta=%b&strict=%b" : "http://modstats.org/api/v1/report?mc=%s&user=%s&data=%s&sign=%s&beta=%b&strict=%b";
			String mcVersion = (new CallableMinecraftVersion((CrashReport) null)).minecraftVersion();
			URL url = new URL(String.format(template, mcVersion, playerId, data, hash, this.reporter.config.betaNotifications, this.reporter.config.forCurrentMinecraftVersion));
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

			String line;
			String out;
			for (out = ""; (line = reader.readLine()) != null; out = out + line) {
			}

			reader.close();
			this.parseResponse(out);
		} catch (MalformedURLException var11) {
			FMLLog.warning("[Modstats] Invalid stat report url", new Object[0]);
		} catch (IOException var12) {
			FMLLog.info("[Modstats] Stat wasn't reported '" + var12.getMessage() + "'", new Object[0]);
		} catch (Exception var13) {
			FMLLog.warning("[Modstats] Something wrong: " + var13.toString(), new Object[0]);
		}

	}
}
