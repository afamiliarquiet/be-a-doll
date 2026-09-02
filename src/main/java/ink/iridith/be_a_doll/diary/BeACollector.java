package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.item.DollcraftItem;
import ink.iridith.be_a_doll.item.RibbonItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@EventBusSubscriber(modid = BeADoll.MOD_ID)
public class BeACollector {
	private static final DeferredRegister<Item> ITEMS = DeferredRegister.createItems(BeADoll.MOD_ID);
	private static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, BeADoll.MOD_ID);

//	public static final Supplier<DataComponentType<BeADoll.Variant>> DOLL_VARIANT_COMPONENT = registerComponent(
//		"doll_variant", builder -> builder.persistent(BeADoll.Variant.CODEC).networkSynchronized(BeADoll.Variant.PACKET_CODEC)
//	);
	public static final Supplier<DataComponentType<BeADoll.Variant>> DOLL_VARIANT_COMPONENT = COMPONENTS.register(
		"doll_variant", () -> DataComponentType.<BeADoll.Variant>builder().persistent(BeADoll.Variant.CODEC).networkSynchronized(BeADoll.Variant.PACKET_CODEC).build()
	);

	public static final Supplier<Item> CARVING_KNIFE = ITEMS.register("carving_knife", () -> new DollcraftItem(new Item.Properties()
		.durability(310).attributes(weapon(4, -2.4f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.WOODEN)));
	public static final Supplier<Item> MODELING_TOOL = ITEMS.register("modeling_tool", () -> new DollcraftItem(new Item.Properties()
		.durability(310).attributes(weapon(2, -1.3f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLAY)));
	public static final Supplier<Item> SEWING_NEEDLE = ITEMS.register("sewing_needle", () -> new DollcraftItem(new Item.Properties()
		.durability(310).attributes(weapon(3, -2f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLOTH)));
	public static final Supplier<Item> FLUSH_CUTTER = ITEMS.register("flush_cutter", () -> new DollcraftItem(new Item.Properties()
		.durability(310).attributes(weapon(2.5f, -1.6f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.PLASTIC)));
	public static final Supplier<Item> WATCHMAKERS_SCREWDRIVER = ITEMS.register("watchmakers_screwdriver", () -> new DollcraftItem(new Item.Properties()
		.durability(310).attributes(weapon(3.5f, -2.4f))
		.component(BeACollector.DOLL_VARIANT_COMPONENT, BeADoll.Variant.CLOCKWORK)));

	public static final Supplier<Item> ESSENCE_FRAGMENT = ITEMS.register("essence_fragment", () -> new Item(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.EPIC)
		.component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
		.component(DOLL_VARIANT_COMPONENT, BeADoll.Variant.REPRESSED)
		.component(DataComponents.FOOD, new FoodProperties(
			0, 0, true, 3.1f, Optional.empty(),
			List.of(
				new FoodProperties.PossibleEffect(() -> new MobEffectInstance(BeAWitch.FRAGMENTED, 7200, 5), 1),
				new FoodProperties.PossibleEffect(() -> new MobEffectInstance(MobEffects.HARM, 1, 0), 1)//,
//				new PlaySoundConsumeEffect(RegistryEntry.of(BeABirdwatcher.ESSENCE_EAT_HEY_WAIT_WHAT_DO_YOU_MEAN_EATEN))
			)
		))
	));

	public static final Supplier<Item> DOLL_RIBBON = ITEMS.register("ribbon", () -> new RibbonItem(new Item.Properties()));



	public static void inquireAboutTheCollection(IEventBus modBus) {
//		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(itemGroup -> {
//			itemGroup.addAfter(Items.BRUSH, CARVING_KNIFE, MODELING_TOOL, SEWING_NEEDLE, FLUSH_CUTTER, WATCHMAKERS_SCREWDRIVER, DOLL_RIBBON);
//		});

		ITEMS.register(modBus);
		COMPONENTS.register(modBus);
	}

	@SubscribeEvent
	public static void beCarefulToSealAllPersonalBagsToEnsureDollsDoNotHopIn(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			neoforgeThereAreTimesWhenILoveYou(event, Items.BRUSH, List.of(CARVING_KNIFE, MODELING_TOOL, SEWING_NEEDLE, FLUSH_CUTTER, WATCHMAKERS_SCREWDRIVER, DOLL_RIBBON));
		}
	}

	@SuppressWarnings("SameParameterValue")
	private static void neoforgeThereAreTimesWhenILoveYou(BuildCreativeModeTabContentsEvent event, Item addAfter, List<Supplier<Item>> items) {
		ItemStack prevItem = addAfter.getDefaultInstance();
		for (Supplier<Item> item : items) {
			ItemStack currentItem = item.get().getDefaultInstance();
			event.insertAfter(prevItem, currentItem, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
			prevItem = currentItem;
		}
	}



//	public static Supplier<Item> registerItem(String id, Function<Item.Properties, Item> factory, Item.Properties settings) {
//		Item item = factory.apply(settings/*.registryKey(key)*/);
//		if (item instanceof BlockItem blockItem) {
//			blockItem.registerBlocks(Item.BY_BLOCK, item);
//		}
//
//		return ITEMS.register(id, () -> item);
//	}
//
//	public static ResourceKey<Item> key(String thing) {
//		return ResourceKey.create(Registries.ITEM, BeADoll.id(thing));
//	}
//
//
//
//	@SuppressWarnings("SameParameterValue")
//	private static <T> Supplier<DataComponentType<T>> registerComponent(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
//		return COMPONENTS.register(id, () -> builderOperator.apply(DataComponentType.builder()).build());
//	}



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
