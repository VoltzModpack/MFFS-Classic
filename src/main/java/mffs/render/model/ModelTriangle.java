package mffs.render.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelTriangle extends ModelBase {

	public static final ModelTriangle INSTNACE = new ModelTriangle();
	private ModelRenderer cube = new ModelRenderer(this, 0, 0);

	public ModelTriangle() {
		int size = 16;
		this.cube.addBox((float) (-size / 8), (float) (-size / 2), (float) (-size / 2), size / 6, size, size);
		this.cube.setTextureSize(112, 70);
		this.cube.mirror = true;
	}

	public void render() {
		float f = 0.0625F;
		this.cube.render(f);
	}
}
