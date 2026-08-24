package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import io.github.afamiliarquiet.be_a_doll.item.DollcraftItem;
import io.github.afamiliarquiet.be_a_doll.item.RibbonItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.PlaySoundConsumeEffect;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public class BeACollector {
	public static final DataComponentType<BeADoll.Variant> DOLL_VARIANT_COMPONENT = registerComponent(
		"doll_variant", builder -> builder.persistent(BeADoll.Variant.CODEC).networkSynchronized(BeADoll.Variant.PACKET_CODEC)
	);

	public static final Item CARVING_KNIFE = registerItem("carving_knife", DollcraftItem::new, new Item.Properties()
		.repairable(Items.IRON_INGOT).durability(310).attributes(weapon(4, -2.4f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.WOODEN));
	public static final Item MODELING_TOOL = registerItem("modeling_tool", DollcraftItem::new, new Item.Properties()
		.repairable(Items.IRON_INGOT).durability(310).attributes(weapon(2, -1.3f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLAY));
	public static final Item SEWING_NEEDLE = registerItem("sewing_needle", DollcraftItem::new, new Item.Properties()
		.repairable(Items.IRON_INGOT).durability(310).attributes(weapon(3, -2f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLOTH));
	public static final Item FLUSH_CUTTER = registerItem("flush_cutter", DollcraftItem::new, new Item.Properties()
		.repairable(Items.IRON_INGOT).durability(310).attributes(weapon(2.5f, -1.6f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.PLASTIC));
	public static final Item WATCHMAKERS_SCREWDRIVER = registerItem("watchmakers_screwdriver", DollcraftItem::new, new Item.Properties()
		.repairable(Items.IRON_INGOT).durability(310).attributes(weapon(3.5f, -2.4f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLOCKWORK));

	public static final Item ESSENCE_FRAGMENT = registerItem("essence_fragment", Item::new, new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.EPIC)
		.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
		.component(DOLL_VARIANT_COMPONENT, BeADoll.Variant.REPRESSED)
		.component(DataComponents.CONSUMABLE, new Consumable(
			3.1f, ItemUseAnimation.EAT, SoundEvents.GENERIC_EAT, true,
			List.of(
				new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(BeAWitch.FRAGMENTED, 7200, 5)),
				new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.INSTANT_DAMAGE, 1, 0)),
				new PlaySoundConsumeEffect(Holder.direct(BeABirdwatcher.ESSENCE_EAT_HEY_WAIT_WHAT_DO_YOU_MEAN_EATEN))
			)
		))
	);

	public static final Item DOLL_RIBBON = registerItem("ribbon", RibbonItem::new, new Item.Properties());



	public static void inquireAboutTheCollection() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(itemGroup -> {
			itemGroup.addAfter(Items.BRUSH, CARVING_KNIFE, MODELING_TOOL, SEWING_NEEDLE, FLUSH_CUTTER, WATCHMAKERS_SCREWDRIVER, DOLL_RIBBON);
		});
	}



	public static Item registerItem(ResourceKey<Item> key, Function<Item.Properties, Item> factory, Item.Properties settings) {
		Item item = factory.apply(settings.setId(key));
		if (item instanceof BlockItem blockItem) {
			blockItem.registerBlocks(Item.BY_BLOCK, item);
		}

		return Registry.register(BuiltInRegistries.ITEM, key, item);
	}

	public static Item registerItem(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
		return registerItem(key(id), factory, settings);
	}

	public static ResourceKey<Item> key(String thing) {
		return ResourceKey.create(Registries.ITEM, BeADoll.id(thing));
	}



	private static <T> DataComponentType<T> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, BeADoll.id(id), builderOperator.apply(DataComponentType.builder()).build());
	}



	public static ItemAttributeModifiers weapon(float attackDamage, float attackSpeed) {
		return ItemAttributeModifiers.builder()
			.add(
				Attributes.ATTACK_DAMAGE,
				new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, attackDamage, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
			)
			.add(
				Attributes.ATTACK_SPEED,
				new AttributeModifier(Item.BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE),
				EquipmentSlotGroup.MAINHAND
			)
			.build();
	}
}
