package mffs.render;

import calclavia.lib.render.CalclaviaRenderHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mffs.ModularForceFieldSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import universalelectricity.core.vector.Vector3;

@SideOnly(Side.CLIENT)
public class FXHologramMoving extends EntityFX {

	public FXHologramMoving(World par1World, Vector3 position, float red, float green, float blue, int age) {
		super(par1World, position.x, position.y, position.z);
		this.setRBGColorF(red, green, blue);
		super.particleMaxAge = age;
		super.noClip = true;
	}

	public void onUpdate() {
		super.prevPosX = super.posX;
		super.prevPosY = super.posY;
		super.prevPosZ = super.posZ;
		if (super.particleAge++ >= super.particleMaxAge) {
			this.setDead();
		}

	}

	public void renderParticle(Tessellator tessellator, float f, float f1, float f2, float f3, float f4, float f5) {
		tessellator.draw();
		GL11.glPushMatrix();
		float xx = (float) (super.prevPosX + (super.posX - super.prevPosX) * (double) f - EntityFX.interpPosX);
		float yy = (float) (super.prevPosY + (super.posY - super.prevPosY) * (double) f - EntityFX.interpPosY);
		float zz = (float) (super.prevPosZ + (super.posZ - super.prevPosZ) * (double) f - EntityFX.interpPosZ);
		GL11.glTranslated((double) xx, (double) yy, (double) zz);
		GL11.glScalef(1.01F, 1.01F, 1.01F);
		double completion = (double) super.particleAge / (double) super.particleMaxAge;
		GL11.glTranslated(0.0D, (completion - 1.0D) / 2.0D, 0.0D);
		GL11.glScaled(1.0D, completion, 1.0D);
		float op = 0.5F;
		if (super.particleMaxAge - super.particleAge <= 4) {
			op = 0.5F - (float) (5 - (super.particleMaxAge - super.particleAge)) * 0.1F;
		}

		GL11.glColor4d((double) super.particleRed, (double) super.particleGreen, (double) super.particleBlue, (double) (op * 2.0F));
		CalclaviaRenderHelper.disableLighting();
		CalclaviaRenderHelper.enableBlending();
		Minecraft.getMinecraft().renderEngine.bindTexture("/terrain.png");
		CalclaviaRenderHelper.renderNormalBlockAsItem(ModularForceFieldSystem.blockForceField, 0, new RenderBlocks());
		CalclaviaRenderHelper.disableBlending();
		CalclaviaRenderHelper.enableLighting();
		GL11.glPopMatrix();
		tessellator.startDrawingQuads();
		Minecraft.getMinecraft().renderEngine.bindTexture("/particles.png");
	}
}
