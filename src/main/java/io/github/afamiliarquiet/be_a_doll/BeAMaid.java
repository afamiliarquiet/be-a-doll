package io.github.afamiliarquiet.be_a_doll;

import com.google.common.collect.HashMultimap;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class BeAMaid {
	// to be completely honest, most of this is probably better suited to BeADoll in name
	// but the problem is that i like my main initializer classes to be clean and tidy. so, maid here to help!

	// id used for checking on things, map used for removing
	public static final UUID DOLLIFIED_MODIFIER_ID = UUID.fromString("35114e62-2561-4e9a-9184-f1ddc0b9a3e0");
	public static final HashMultimap<EntityAttribute, EntityAttributeModifier> DOLL_MODIFICATIONS = HashMultimap.create();
	static {
		DOLL_MODIFICATIONS.put(EntityAttributes.GENERIC_MAX_HEALTH, new EntityAttributeModifier(DOLLIFIED_MODIFIER_ID, "dollified", -0.6, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
		DOLL_MODIFICATIONS.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(DOLLIFIED_MODIFIER_ID, "dollified",-0.8, EntityAttributeModifier.Operation.ADDITION));
	}

	public static void bestowApron() {
		ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
			// should i care about alive? in theory maybe but it doesn't really matter
			BeAMaid.setDoll(newPlayer, BeALibrarian.inspectSupposedPlayer(oldPlayer));
			BeALibrarian.relabelDoll(newPlayer, BeALibrarian.inspectDollLabel(oldPlayer));
			BeALibrarian.filePasswordManager(newPlayer, BeALibrarian.checkFilesForPasswordManager(oldPlayer));
		}));
	}

	public static boolean isDoll(@Nullable PlayerEntity player) {
		if (player == null) {
			return false;
		}

		// if there's any signs of being a doll.... yep, that's a doll
//		int dollPoints = 0;
		for (EntityAttribute attribute : DOLL_MODIFICATIONS.keySet()) {
			EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
			if (instance != null && instance.getModifier(DOLLIFIED_MODIFIER_ID) != null) {
				return true;
//				dollPoints++;
			}
		}
		return false;
//		return dollPoints * 2 >= BeADoll.DOLL_MODIFICATIONS.size(); // at least half doll? yeah that's doll enough
		// practically speaking dollPoints shouldn't really be relevant 'cause you should always have 100% doll or 0%
		// but in spirit that's what i want to do
	}

	public static void setDoll(@Nullable PlayerEntity player, BeADoll.Variant variant) {
		if (player == null || player.getWorld().isClient()) {
			return;
		}
		if (variant == BeALibrarian.inspectSupposedPlayer(player)) {
			// nothing to do boss, that doll is doll! or that.. not doll is not doll, i guess.
			return;
		}

		if (variant.isDollish()) {
			// add persistent instead of add temporary. because doll is a persistent fact of life
			DOLL_MODIFICATIONS.forEach((attribute, modifier) -> {
				EntityAttributeInstance instance = player.getAttributes().getCustomInstance(attribute);
				if (instance != null && modifier != null && !instance.hasModifier(modifier)) {
					instance.addPersistentModifier(modifier);
				}
			});
			BeALibrarian.DOLL_SCALE_TYPE.getScaleData(player)
					.setScale(0.3f);
			BeALibrarian.reshapeDoll(player, variant);
		} else {
			BeALibrarian.repress(player);
			BeALibrarian.DOLL_SCALE_TYPE.getScaleData(player)
				.setScale(1.0f);
			player.removeStatusEffect(BeAWitch.CARED_FOR.value());
			player.getAttributes().removeModifiers(DOLL_MODIFICATIONS);
		}
	}
}
