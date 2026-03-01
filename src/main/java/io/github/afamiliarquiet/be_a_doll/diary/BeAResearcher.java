package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class BeAResearcher {
	public static final TagKey<DamageType> DOLL_IMMUNE = damage("doll_immune");
	public static final TagKey<DamageType> DOLL_MODIFIES_MESSAGE = damage("doll_modifies_message");

	public static final TagKey<Item> WOODEN_DOLL_CARE_MATERIALS = item("wooden_doll_care_materials");
	public static final TagKey<Item> CLAY_DOLL_CARE_MATERIALS = item("clay_doll_care_materials");
	public static final TagKey<Item> CLOTH_DOLL_CARE_MATERIALS = item("cloth_doll_care_materials");
	public static final TagKey<Item> PLASTIC_DOLL_CARE_MATERIALS = item("plastic_doll_care_materials");
	public static final TagKey<Item> CLOCKWORK_DOLL_CARE_MATERIALS = item("clockwork_doll_care_materials");

	public static final TagKey<Item> DOLLCRAFT_ITEMS = item("dollcraft_items");
	public static final TagKey<Item> UNDOLLING_ITEMS = item("undolling_items");

	public static void grantFunding() {

	}

	public static TagKey<DamageType> damage(String thing) {
		return TagKey.of(RegistryKeys.DAMAGE_TYPE, BeADoll.id(thing));
	}

	public static TagKey<Item> item(String thing) {
		return TagKey.of(RegistryKeys.ITEM, BeADoll.id(thing));
	}
}
