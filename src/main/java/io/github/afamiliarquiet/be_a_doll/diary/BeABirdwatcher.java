package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class BeABirdwatcher {
	public static final SoundEvent RAVEN_CHIRP = registerSound("item.be_a_doll.ribbon.tied");
	public static final SoundEvent RAVEN_CRY = registerSound("item.be_a_doll.ribbon.untied");

	public static final SoundEvent CARE_WOODEN = registerSound("entity.be_a_doll.doll.care.wooden");
	public static final SoundEvent CARE_CLAY = registerSound("entity.be_a_doll.doll.care.clay");
	public static final SoundEvent CARE_CLOTH = registerSound("entity.be_a_doll.doll.care.cloth");
	public static final SoundEvent CARE_PLASTIC = registerSound("entity.be_a_doll.doll.care.plastic");
	public static final SoundEvent CARE_CLOCKWORK = registerSound("entity.be_a_doll.doll.care.clockwork");
	public static final SoundEvent CARE_COMPLETE = registerSound("entity.be_a_doll.doll.care.complete");

	public static final SoundEvent ESSENCE_TAKE = registerSound("entity.be_a_doll.doll.essence.take");
	public static final SoundEvent ESSENCE_PLACE = registerSound("entity.be_a_doll.doll.essence.place");
	public static final SoundEvent ESSENCE_EAT_HEY_WAIT_WHAT_DO_YOU_MEAN_EATEN = registerSound("entity.be_a_doll.essence.stop_it_dont_do_that");

	public static void offerTea() {
		// have you heard their songs? listen a little, i've got a small catalogue
	}

	private static SoundEvent registerSound(String thing) {
		Identifier id = BeADoll.id(thing);
		return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
	}
}
