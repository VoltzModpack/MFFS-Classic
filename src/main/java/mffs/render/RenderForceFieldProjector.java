package mffs.render;

import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.Settings;
import mffs.render.model.ModelForceFieldProjector;
import mffs.tileentity.TileEntityForceFieldProjector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderForceFieldProjector extends TileEntitySpecialRenderer {

	public static final String TEXTURE_ON = "projector_on.png";
	public static final String TEXTURE_OFF = "projector_off.png";
	public static final ModelForceFieldProjector MODEL = new ModelForceFieldProjector();

	public void renderTileEntityAt(TileEntity t, double x, double y, double z, float f) {
		if (t instanceof TileEntityForceFieldProjector) {
			TileEntityForceFieldProjector tileEntity = (TileEntityForceFieldProjector) t;
			GL11.glPushMatrix();
			GL11.glTranslated(x + 0.5D, y + 1.5D, z + 0.5D);
			if (tileEntity.isActive()) {
				this.bindTextureByName("/mods/mffs/textures/models/projector_on.png");
			} else {
				this.bindTextureByName("/mods/mffs/textures/models/projector_off.png");
			}

			GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
			MODEL.render(tileEntity.animation, 0.0625F);
			GL11.glPopMatrix();
			if (tileEntity.getMode() != null) {
				Tessellator tessellator = Tessellator.instance;
				RenderHelper.disableStandardItemLighting();
				GL11.glPushMatrix();
				GL11.glTranslated(x + 0.5D, y + 0.5D, z + 0.5D);
				double var10001 = (double) tileEntity.xCoord;
				double xDifference = Minecraft.getMinecraft().thePlayer.posX - (var10001 + 0.5D);
				var10001 = (double) tileEntity.zCoord;
				double zDifference = Minecraft.getMinecraft().thePlayer.posZ - (var10001 + 0.5D);
				float rotatation = (float) Math.toDegrees(Math.atan2(zDifference, xDifference));
				GL11.glRotatef(-rotatation + 27.0F, 0.0F, 1.0F, 0.0F);
				GL11.glDisable(3553);
				GL11.glShadeModel(7425);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 1);
				GL11.glDisable(3008);
				GL11.glEnable(2884);
				GL11.glDepthMask(false);
				GL11.glPushMatrix();
				tessellator.startDrawing(6);
				float height = 2.0F;
				float width = 2.0F;
				tessellator.setColorRGBA(72, 198, 255, 255);
				tessellator.addVertex(0.0D, 0.0D, 0.0D);
				tessellator.setColorRGBA_I(0, 0);
				tessellator.addVertex(-0.866D * (double) width, (double) height, (double) (-0.5F * width));
				tessellator.addVertex(0.866D * (double) width, (double) height, (double) (-0.5F * width));
				tessellator.addVertex(0.0D, (double) height, (double) (1.0F * width));
				tessellator.addVertex(-0.866D * (double) width, (double) height, (double) (-0.5F * width));
				tessellator.draw();
				GL11.glPopMatrix();
				GL11.glDepthMask(true);
				GL11.glDisable(2884);
				GL11.glDisable(3042);
				GL11.glShadeModel(7424);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glEnable(3553);
				GL11.glEnable(3008);
				RenderHelper.enableStandardItemLighting();
				GL11.glPopMatrix();
				if (Settings.HIGH_GRAPHICS) {
					GL11.glPushMatrix();
					GL11.glTranslated(x + 0.5D, y + 1.35D, z + 0.5D);
					this.bindTextureByName("/mods/mffs/textures/models/force_cube.png");
					CalclaviaRenderHelper.enableBlending();
					CalclaviaRenderHelper.disableLighting();
					GL11.glPushMatrix();
					GL11.glColor4f(1.0F, 1.0F, 1.0F, (float) Math.sin((double) tileEntity.getTicks() / 10.0D) / 2.0F + 1.0F);
					GL11.glTranslatef(0.0F, (float) Math.sin(Math.toRadians((double) (tileEntity.getTicks() * 3L))) / 7.0F, 0.0F);
					GL11.glRotatef((float) (tileEntity.getTicks() * 4L), 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(36.0F + (float) (tileEntity.getTicks() * 4L), 0.0F, 1.0F, 1.0F);
					tileEntity.getMode().render(tileEntity, x, y, z, f, tileEntity.getTicks());
					GL11.glPopMatrix();
					CalclaviaRenderHelper.enableLighting();
					CalclaviaRenderHelper.disableBlending();
					GL11.glPopMatrix();
				}
			}
		}

	}
}
