package universalelectricity.prefab.flag;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import universalelectricity.core.vector.Vector3;

import java.util.Iterator;
import java.util.List;

public class CommandFlag extends CommandBase {

	public static final String[] COMMANDS = new String[]{"list", "setregion", "removeregion", "set"};
	public String commandName;
	public ModFlag modFlagData;

	public CommandFlag(ModFlag modFlag) {
		this.commandName = "modflag";
		this.modFlagData = modFlag;
	}

	public CommandFlag(ModFlag modFlag, String commandName) {
		this(modFlag);
		this.commandName = commandName;
	}

	public String getCommandName() {
		return this.commandName;
	}

	public String getCommandUsage(ICommandSender par1ICommandSender) {
		String returnString = "";
		String[] arr$ = COMMANDS;
		int len$ = arr$.length;

		for (int i$ = 0; i$ < len$; ++i$) {
			String command = arr$[i$];
			returnString = returnString + "\n/" + this.getCommandName() + " " + command;
		}

		return returnString;
	}

	public void processCommand(ICommandSender sender, String[] args) {
		if (args.length > 0) {
			EntityPlayer entityPlayer = (EntityPlayer) sender;
			FlagWorld flagWorld = this.modFlagData.getFlagWorld(entityPlayer.worldObj);
			String commandName = args[0].toLowerCase();
			String regionName;
			String flagName;
			FlagRegion region;
			if (commandName.equalsIgnoreCase("list")) {
				if (args.length > 1) {
					regionName = args[1];
					Iterator i;
					if (regionName.equalsIgnoreCase("all")) {
						flagName = "";
						i = this.modFlagData.getFlagWorlds().iterator();

						FlagRegion flagRegion;
						while (i.hasNext()) {
							for (Iterator itRegion = ((FlagWorld) i.next()).getRegions().iterator(); itRegion.hasNext(); flagName = flagName + " " + flagRegion.name + " (" + flagRegion.region.min.x + "," + flagRegion.region.min.z + ")" + ",") {
								flagRegion = (FlagRegion) itRegion.next();
							}
						}

						if (flagName != "") {
							flagName = "List of regions in world:\n" + flagName;
						} else {
							flagName = "No regions in this world.";
						}

						sender.sendChatToPlayer(flagName);
					} else {
						Flag flag;
						if (flagWorld.getRegion(regionName) != null) {
							flagName = "";

							for (i = flagWorld.getRegion(regionName).getFlags().iterator(); i.hasNext(); flagName = flagName + " " + flag.name + " => " + flag.value + ",") {
								flag = (Flag) i.next();
							}

							if (flagName != "") {
								flagName = "List of flags in region " + regionName + ":\n" + flagName;
							} else {
								flagName = "No flags in this region.";
							}

							sender.sendChatToPlayer(flagName);
						} else {
							flagName = "Region does not exist, but here are existing flags in the position you are standing on:\n";

							for (i = flagWorld.getFlagsInPosition(new Vector3(entityPlayer)).iterator(); i.hasNext(); flagName = flagName + " " + flag.name + "=>" + flag.value + ",") {
								flag = (Flag) i.next();
							}

							sender.sendChatToPlayer(flagName);
						}
					}
				} else {
					regionName = "";

					for (Iterator i = flagWorld.getRegions().iterator(); i.hasNext(); regionName = regionName + " " + region.name + " (" + region.region.min.x + "," + region.region.min.z + ")" + ",") {
						region = (FlagRegion) i.next();
					}

					if (regionName != "") {
						regionName = "List of regions in this dimension:\n" + regionName;
					} else {
						regionName = "No regions in this dimension.";
					}

					sender.sendChatToPlayer(regionName);
				}

				return;
			}

			if (commandName.equalsIgnoreCase("setregion")) {
				if (args.length > 1) {
					regionName = args[1];
					if (regionName.equalsIgnoreCase("dimension")) {
						if (flagWorld.addRegion(regionName, new Vector3(entityPlayer), 1)) {
							sender.sendChatToPlayer("Created global dimension region setting.");
							return;
						}
					} else {
						if (args.length <= 2) {
							throw new WrongUsageException("/" + this.getCommandName() + " addregion <name> <radius>", new Object[0]);
						}

						boolean var13 = false;

						int radius;
						try {
							radius = Integer.parseInt(args[2]);
						} catch (Exception var12) {
							throw new WrongUsageException("Radius not a number!", new Object[0]);
						}

						if (radius <= 0) {
							throw new WrongUsageException("Radius has to be greater than zero!", new Object[0]);
						}

						region = flagWorld.getRegion(regionName);
						if (region == null) {
							if (flagWorld.addRegion(regionName, new Vector3(entityPlayer), radius)) {
								sender.sendChatToPlayer("Region " + regionName + " added.");
							}
						} else {
							region.edit(new Vector3(entityPlayer), radius);
							sender.sendChatToPlayer("Region " + regionName + " already exists. Modified region to have a radius of: " + radius);
						}
					}

					return;
				}

				throw new WrongUsageException("Please specify the region name.", new Object[0]);
			}

			if (commandName.equalsIgnoreCase("removeregion")) {
				if (args.length > 1) {
					regionName = args[1];
					if (flagWorld.removeRegion(regionName)) {
						sender.sendChatToPlayer("Region with name " + regionName + " is removed.");
						return;
					}

					throw new WrongUsageException("The specified region does not exist in this world.", new Object[0]);
				}

				throw new WrongUsageException("Please specify the region name.", new Object[0]);
			}

			if (commandName.equalsIgnoreCase("set")) {
				if (args.length <= 2) {
					throw new WrongUsageException("/" + this.getCommandName() + " set <regionName> <flagName> <value>", new Object[0]);
				}

				regionName = args[1];
				flagName = args[2];
				region = flagWorld.getRegion(regionName);
				if (region == null) {
					throw new WrongUsageException("The specified region '" + regionName + "' does not exist.", new Object[0]);
				}

				String flags;
				if (FlagRegistry.flags.contains(flagName)) {
					if (args.length > 3) {
						flags = args[3];
						region.setFlag(flagName, flags);
						sender.sendChatToPlayer("Flag '" + flagName + "' has been set to '" + flags + "' in " + regionName + ".");
					} else {
						region.removeFlag(flagName);
						sender.sendChatToPlayer("Removed flag '" + flagName + "'.");
					}

					return;
				}

				flags = "Flag does not exist. Existing flags:\n";

				String registeredFlag;
				for (Iterator i$ = FlagRegistry.flags.iterator(); i$.hasNext(); flags = flags + registeredFlag + ", ") {
					registeredFlag = (String) i$.next();
				}

				throw new WrongUsageException(flags, new Object[0]);
			}
		}

		throw new WrongUsageException(this.getCommandUsage(sender), new Object[0]);
	}

	public int getRequiredPermissionLevel() {
		return 2;
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args) {
		return args.length == 1 ? getListOfStringsMatchingLastWord(args, COMMANDS) : null;
	}
}
