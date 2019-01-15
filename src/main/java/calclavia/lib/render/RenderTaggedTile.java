package calclavia.lib.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

@SideOnly(Side.CLIENT)
public abstract class RenderTaggedTile extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f) {
		if (t != null && t instanceof ITagRender && this.getPlayer().getDistance((double) t.xCoord, (double) t.yCoord, (double) t.zCoord) <= (double) RenderPlayer.NAME_TAG_RANGE) {
			HashMap tags = new HashMap();
			float height = ((ITagRender) t).addInformation(tags, this.getPlayer());
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			if (player.ridingEntity == null) {
				MovingObjectPosition objectPosition = player.rayTrace(8.0D, 1.0F);
				if (objectPosition != null) {
					boolean isLooking = false;

					for (int h = 0; (float) h < height; ++h) {
						if (objectPosition.blockX == t.xCoord && objectPosition.blockY == t.yCoord + h && objectPosition.blockZ == t.zCoord) {
							isLooking = true;
						}
					}

					if (isLooking) {
						Iterator it = tags.entrySet().iterator();

						for (int i = 0; it.hasNext(); ++i) {
							Entry entry = (Entry) it.next();
							if (entry.getKey() != null) {
								CalclaviaRenderHelper.renderFloatingText((String) entry.getKey(), (float) x + 0.5F, (float) y + (float) i * 0.25F - 2.0F + height, (float) z + 0.5F, (Integer) entry.getValue());
							}
						}
					}
				}
			}
		}

	}

	public EntityPlayer getPlayer() {
		EntityLiving entity = super.tileEntityRenderer.entityLivingPlayer;
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
	}
}
