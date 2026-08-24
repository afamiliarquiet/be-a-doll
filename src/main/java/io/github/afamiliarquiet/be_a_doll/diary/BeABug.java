package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;

public class BeABug {
	// hi bugs!! sorry this is a dolls mod nothing special for you here

	public static final SimpleParticleType FRAGMENTED = ohCoolBug("fragmented");

	public static void lookAtBug() {

	}

	public static SimpleParticleType ohCoolBug(String bugThing) {
		return Registry.register(BuiltInRegistries.PARTICLE_TYPE, BeADoll.id(bugThing), FabricParticleTypes.simple());
	}
}
