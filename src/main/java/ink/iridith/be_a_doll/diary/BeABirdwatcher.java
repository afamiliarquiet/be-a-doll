package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeABirdwatcher {
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, BeADoll.MOD_ID);
	
	public static final DeferredHolder<SoundEvent, SoundEvent> RAVEN_CHIRP = registerSound("item.be_a_doll.ribbon.tied");
	public static final DeferredHolder<SoundEvent, SoundEvent> RAVEN_CRY = registerSound("item.be_a_doll.ribbon.untied");

	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_WOODEN = registerSound("entity.be_a_doll.doll.care.wooden");
	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_CLAY = registerSound("entity.be_a_doll.doll.care.clay");
	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_CLOTH = registerSound("entity.be_a_doll.doll.care.cloth");
	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_PLASTIC = registerSound("entity.be_a_doll.doll.care.plastic");
	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_CLOCKWORK = registerSound("entity.be_a_doll.doll.care.clockwork");
	public static final DeferredHolder<SoundEvent, SoundEvent> CARE_COMPLETE = registerSound("entity.be_a_doll.doll.care.complete");

	public static final DeferredHolder<SoundEvent, SoundEvent> ESSENCE_TAKE = registerSound("entity.be_a_doll.doll.essence.take");
	public static final DeferredHolder<SoundEvent, SoundEvent> ESSENCE_PLACE = registerSound("entity.be_a_doll.doll.essence.place");
	public static final DeferredHolder<SoundEvent, SoundEvent> ESSENCE_EAT_HEY_WAIT_WHAT_DO_YOU_MEAN_EATEN = registerSound("entity.be_a_doll.essence.stop_it_dont_do_that");

	public static void offerTea(IEventBus modBus) {
		// have you heard their songs? listen a little, i've got a small catalogue
		SOUND_EVENTS.register(modBus);
	}

	private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String thing) {
		ResourceLocation id = BeADoll.id(thing);
		return SOUND_EVENTS.register(thing, () -> SoundEvent.createVariableRangeEvent(id));
	}
}
