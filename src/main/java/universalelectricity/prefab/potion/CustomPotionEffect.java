package universalelectricity.prefab.potion;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class CustomPotionEffect extends PotionEffect {

	public CustomPotionEffect(int potionID, int duration, int amplifier) {
		super(potionID, duration, amplifier);
	}

	public CustomPotionEffect(Potion potion, int duration, int amplifier) {
		this(potion.getId(), duration, amplifier);
	}

	public CustomPotionEffect(int potionID, int duration, int amplifier, List curativeItems) {
		super(potionID, duration, amplifier);
		if (curativeItems == null) {
			this.setCurativeItems(new ArrayList());
		} else {
			this.setCurativeItems(curativeItems);
		}

	}
}
