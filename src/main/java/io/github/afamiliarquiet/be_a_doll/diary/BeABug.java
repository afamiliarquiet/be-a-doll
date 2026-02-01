package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class BeABug {
	// hi bugs!! sorry this is a dolls mod nothing special for you here

	public static final DefaultParticleType FRAGMENTED = ohCoolBug("fragmented");

	public static void lookAtBug() {

	}

	public static DefaultParticleType ohCoolBug(String bugThing) {
		return Registry.register(Registries.PARTICLE_TYPE, BeADoll.id(bugThing), FabricParticleTypes.simple());
	}
}
