package mffs;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.api.security.IInterdictionMatrix;
import mffs.api.security.Permission;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.TextureStitchEvent.Post;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.liquids.LiquidDictionary;
import universalelectricity.core.vector.Vector3;

public class SubscribeEventHandler {

	@ForgeSubscribe
	@SideOnly(Side.CLIENT)
	public void textureHook(Post event) {
		if (event.map == Minecraft.getMinecraft().renderEngine.textureMapItems) {
			LiquidDictionary.getCanonicalLiquid("Fortron").setRenderingIcon(ModularForceFieldSystem.itemFortron.getIconFromDamage(0)).setTextureSheet("/gui/items.png");
		}

	}

	@ForgeSubscribe
	public void playerInteractEvent(PlayerInteractEvent evt) {
		if (evt.action == Action.RIGHT_CLICK_BLOCK || evt.action == Action.LEFT_CLICK_BLOCK) {
			if (evt.action == Action.LEFT_CLICK_BLOCK && evt.entityPlayer.worldObj.getBlockId(evt.x, evt.y, evt.z) == ModularForceFieldSystem.blockForceField.blockID) {
				evt.setCanceled(true);
				return;
			}

			if (evt.entityPlayer.capabilities.isCreativeMode) {
				return;
			}

			Vector3 position = new Vector3((double) evt.x, (double) evt.y, (double) evt.z);
			IInterdictionMatrix interdictionMatrix = MFFSHelper.getNearestInterdictionMatrix(evt.entityPlayer.worldObj, position);
			if (interdictionMatrix != null) {
				int blockID = position.getBlockID(evt.entityPlayer.worldObj);
				if (ModularForceFieldSystem.blockBiometricIdentifier.blockID == blockID && MFFSHelper.isPermittedByInterdictionMatrix(interdictionMatrix, evt.entityPlayer.username, Permission.SECURITY_CENTER_CONFIGURE)) {
					return;
				}

				boolean hasPermission = MFFSHelper.hasPermission(evt.entityPlayer.worldObj, new Vector3((double) evt.x, (double) evt.y, (double) evt.z), interdictionMatrix, evt.action, evt.entityPlayer);
				if (!hasPermission) {
					evt.entityPlayer.sendChatToPlayer("[" + ModularForceFieldSystem.blockInterdictionMatrix.getLocalizedName() + "] You have no permission to do that!");
					evt.setCanceled(true);
				}
			}
		}

	}

	@ForgeSubscribe
	public void livingSpawnEvent(LivingSpawnEvent evt) {
		IInterdictionMatrix interdictionMatrix = MFFSHelper.getNearestInterdictionMatrix(evt.world, new Vector3(evt.entityLiving));
		if (interdictionMatrix != null && interdictionMatrix.getModuleCount(ModularForceFieldSystem.itemModuleAntiSpawn, new int[0]) > 0) {
			evt.setResult(Result.DENY);
		}

	}
}
