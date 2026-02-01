package io.github.afamiliarquiet.be_a_doll.diary;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.item.DollcraftItem;
import io.github.afamiliarquiet.be_a_doll.item.RibbonItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Rarity;

import java.util.Optional;
import java.util.function.Function;

public class BeACollector {
	public static final Item CARVING_KNIFE = registerItem("carving_knife", settings -> new DollcraftItem(settings, weapon(4, -2.4f), BeADoll.Variant.WOODEN), new Item.Settings()
		.maxDamage(310));
	public static final Item MODELING_TOOL = registerItem("modeling_tool", settings -> new DollcraftItem(settings, weapon(2, -1.3f), BeADoll.Variant.CLAY), new Item.Settings()
		.maxDamage(310));
	public static final Item SEWING_NEEDLE = registerItem("sewing_needle", settings -> new DollcraftItem(settings, weapon(3, -2f), BeADoll.Variant.CLOTH), new Item.Settings()
		.maxDamage(310));
	public static final Item FLUSH_CUTTER = registerItem("flush_cutter", settings -> new DollcraftItem(settings, weapon(2.5f, -1.6f), BeADoll.Variant.PLASTIC), new Item.Settings()
		.maxDamage(310));
	public static final Item WATCHMAKERS_SCREWDRIVER = registerItem("watchmakers_screwdriver", settings -> new DollcraftItem(settings, weapon(3.5f, -2.4f), BeADoll.Variant.CLOCKWORK), new Item.Settings()
		.maxDamage(310));

	public static final Item ESSENCE_FRAGMENT = registerItem("essence_fragment", Item::new, new Item.Settings()
		.maxCount(1)
		.rarity(Rarity.EPIC)
		.food(new FoodComponent.Builder()
			.hunger(0)
			.saturationModifier(0)
			.meat()
			.statusEffect(new StatusEffectInstance(BeAWitch.FRAGMENTED.value(), 7200, 5), 1.f)
			.statusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, 0), 1.f)
			.build()
		)
	);

	public static final Item DOLL_RIBBON = registerItem("ribbon", RibbonItem::new, new Item.Settings());

	public static Optional<BeADoll.Variant> getDollVariant(ItemStack stack) {
		if (stack.getItem() instanceof DollcraftItem dollCraft) {
			return Optional.of(dollCraft.getVariant());
		}

		return getDollVariantFromStackNbt(stack);
	}

	private static Optional<BeADoll.Variant> getDollVariantFromStackNbt(ItemStack stack) {
		NbtCompound nbt = stack.getNbt();
		if (nbt != null && nbt.contains("be_a_doll:variant")) {
			return BeADoll.Variant.CODEC.parse(NbtOps.INSTANCE, nbt.get("be_a_doll:variant"))
				.resultOrPartial(str -> {});
		}
		return Optional.empty();
	}

	public static void setDollVariant(ItemStack stack, BeADoll.Variant variant) {
		BeADoll.Variant.CODEC.encodeStart(NbtOps.INSTANCE, variant)
			.resultOrPartial(BeADoll::warn)
			.ifPresent(element -> stack.getOrCreateNbt().put("be_a_doll:variant", element));
	}

	public static void inquireAboutTheCollection() {
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(itemGroup -> {
			itemGroup.addAfter(Items.BRUSH, CARVING_KNIFE, MODELING_TOOL, SEWING_NEEDLE, FLUSH_CUTTER, WATCHMAKERS_SCREWDRIVER, DOLL_RIBBON);
		});
	}



	public static Item registerItem(RegistryKey<Item> key, Function<Item.Settings, Item> factory, Item.Settings settings) {
		Item item = factory.apply(settings/*.registryKey(key)*/);
		if (item instanceof BlockItem blockItem) {
			blockItem.appendBlocks(Item.BLOCK_ITEMS, item);
		}

		return Registry.register(Registries.ITEM, key, item);
	}

	public static Item registerItem(String id, Function<Item.Settings, Item> factory, Item.Settings settings) {
		return registerItem(key(id), factory, settings);
	}

	public static RegistryKey<Item> key(String thing) {
		return RegistryKey.of(RegistryKeys.ITEM, BeADoll.id(thing));
	}

	public static Multimap<EntityAttribute, EntityAttributeModifier> weapon(float attackDamage, float attackSpeed) {
		return ImmutableMultimap.<EntityAttribute, EntityAttributeModifier>builder()
			.put(
				EntityAttributes.GENERIC_ATTACK_DAMAGE,
				new EntityAttributeModifier(Item.ATTACK_DAMAGE_MODIFIER_ID, "doll.attack_damage", attackDamage, EntityAttributeModifier.Operation.ADDITION)
			)
			.put(
				EntityAttributes.GENERIC_ATTACK_SPEED,
				new EntityAttributeModifier(Item.ATTACK_SPEED_MODIFIER_ID, "doll.attack_speed", attackSpeed, EntityAttributeModifier.Operation.ADDITION)
			)
			.build();
	}
}
