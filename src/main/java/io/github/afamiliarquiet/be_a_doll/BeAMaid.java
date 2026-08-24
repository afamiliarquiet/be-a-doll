package io.github.afamiliarquiet.be_a_doll;

import com.google.common.collect.HashMultimap;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class BeAMaid {
	// to be completely honest, most of this is probably better suited to BeADoll in name
	// but the problem is that i like my main initializer classes to be clean and tidy. so, maid here to help!

	// id used for checking on things, map used for removing
	public static final ResourceLocation DOLLIFIED_MODIFIER_ID = BeADoll.id("dollified");
	public static final HashMultimap<Holder<Attribute>, AttributeModifier> DOLL_MODIFICATIONS = HashMultimap.create();
	static {
		DOLL_MODIFICATIONS.put(Attributes.SCALE, new AttributeModifier(DOLLIFIED_MODIFIER_ID, -0.7, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		DOLL_MODIFICATIONS.put(Attributes.MAX_HEALTH, new AttributeModifier(DOLLIFIED_MODIFIER_ID, -0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
		DOLL_MODIFICATIONS.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(DOLLIFIED_MODIFIER_ID, -0.8, AttributeModifier.Operation.ADD_VALUE));
	}

	public static void bestowApron() {
		ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) -> {
			// should i care about alive? in theory maybe but it doesn't really matter
			BeAMaid.setDoll(newPlayer, BeALibrarian.inspectSupposedPlayer(oldPlayer));
			BeALibrarian.relabelDoll(newPlayer, BeALibrarian.inspectDollLabel(oldPlayer));
			BeALibrarian.filePasswordManager(newPlayer, BeALibrarian.checkFilesForPasswordManager(oldPlayer));
		}));
	}

	public static boolean isDoll(@Nullable Player player) {
		if (player == null) {
			return false;
		}

		// if there's any signs of being a doll.... yep, that's a doll
//		int dollPoints = 0;
		for (Holder<Attribute> attribute : DOLL_MODIFICATIONS.keySet()) {
			AttributeInstance instance = player.getAttributes().getInstance(attribute);
			if (instance != null && instance.hasModifier(DOLLIFIED_MODIFIER_ID)) {
				return true;
//				dollPoints++;
			}
		}
		return false;
//		return dollPoints * 2 >= BeADoll.DOLL_MODIFICATIONS.size(); // at least half doll? yeah that's doll enough
		// practically speaking dollPoints shouldn't really be relevant 'cause you should always have 100% doll or 0%
		// but in spirit that's what i want to do
	}

	public static void setDoll(@Nullable Player player, BeADoll.Variant variant) {
		if (player == null || player.level().isClientSide()) {
			return;
		}
		if (variant == BeALibrarian.inspectSupposedPlayer(player)) {
			// nothing to do boss, that doll is doll! or that.. not doll is not doll, i guess.
			return;
		}

		if (variant.isDollish()) {
			// add persistent instead of add temporary. because doll is a persistent fact of life
			DOLL_MODIFICATIONS.forEach((attribute, modifier) -> {
				AttributeInstance instance = player.getAttributes().getInstance(attribute);
				if (instance != null && modifier != null && !instance.hasModifier(modifier.id())) {
					instance.addPermanentModifier(modifier);
				}
			});
			BeALibrarian.reshapeDoll(player, variant);
		} else {
			BeALibrarian.repress(player);
			player.removeEffect(BeAWitch.CARED_FOR);
			player.getAttributes().removeAttributeModifiers(DOLL_MODIFICATIONS);
		}
	}
}
