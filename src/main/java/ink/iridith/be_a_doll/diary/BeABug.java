package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class BeABug {
	// hi bugs!! sorry this is a dolls mod nothing special for you here

	public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, BeADoll.MOD_ID);

	public static final DeferredHolder<ParticleType<?>, SimpleParticleType> FRAGMENTED = ohCoolBug("fragmented");

	public static void lookAtBug(IEventBus modBus) {
		PARTICLE_TYPES.register(modBus);
	}

	public static DeferredHolder<ParticleType<?>, SimpleParticleType> ohCoolBug(String bugThing) {
		return PARTICLE_TYPES.register(bugThing, () -> new SimpleParticleType(false));
	}
}
