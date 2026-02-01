package io.github.afamiliarquiet.be_a_doll;

import io.github.afamiliarquiet.be_a_doll.diary.*;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.ValueLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.IntFunction;

public class BeADoll implements ModInitializer {
	public static final String MOD_ID = "be_a_doll";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		log(BeADollthing.syntheticKeysmashing("oh are we logging?! HELLO WORLD! I'M READY TO MAKE SOME MORE DOLLS!"));
		BeAMaid.bestowApron();
		BeACollector.inquireAboutTheCollection();
		BeAPenPal.fillPen();
		BeABirdwatcher.offerTea();
		BeACook.placeOrders();
		BeALibrarian.lookForABook();
		BeAResearcher.grantFunding();
		BeAWitch.putOnHat();
		BeABug.lookAtBug();
		BeACurator.payAVisit(); // this isn't necessary but it's cute
	}

	public static Identifier id(String thing) {
		return Identifier.of(MOD_ID, thing);
	}

	// probably a good habit to always log my id whenever i'm throwing things in the log, even if it's just a quick test
	public static void log(String message) {
		LOGGER.info("[Be a doll!] {}", message);
	}

	public static void warn(String message) {
		LOGGER.warn("[Would you be a doll?] {}", message);
	}

	// todo - turn this into interface/abstract w/ a registry
	public enum Variant implements StringIdentifiable {
		REPRESSED(0, "player",
			ItemTags.ANVIL, Items.ANVIL,
			SoundEvents.BLOCK_ANVIL_FALL,
			Identifier.of("missing", "texture"), Identifier.of("missing", "texture"), Identifier.of("missing", "texture")), // gonna look really silly in your throat.
		WOODEN(1, "wooden",
			BeAResearcher.WOODEN_DOLL_CARE_MATERIALS, Items.STICK,
			BeABirdwatcher.CARE_WOODEN,
			BeACurator.WOODEN_FOOD_EMPTY, BeACurator.WOODEN_FOOD_HALF, BeACurator.WOODEN_FOOD_FULL),
		CLAY(2, "clay",
			BeAResearcher.CLAY_DOLL_CARE_MATERIALS, Items.CLAY_BALL,
			BeABirdwatcher.CARE_CLAY,
			BeACurator.CLAY_FOOD_EMPTY, BeACurator.CLAY_FOOD_HALF, BeACurator.CLAY_FOOD_FULL),
		CLOTH(3, "cloth",
			BeAResearcher.CLOTH_DOLL_CARE_MATERIALS, Items.STRING,
			BeABirdwatcher.CARE_CLOTH,
			BeACurator.CLOTH_FOOD_EMPTY, BeACurator.CLOTH_FOOD_HALF, BeACurator.CLOTH_FOOD_FULL),
		PLASTIC(4, "plastic",
			BeAResearcher.PLASTIC_DOLL_CARE_MATERIALS, Items.DRIED_KELP,
			BeABirdwatcher.CARE_PLASTIC,
			BeACurator.PLASTIC_FOOD_EMPTY, BeACurator.PLASTIC_FOOD_HALF, BeACurator.PLASTIC_FOOD_FULL),
		CLOCKWORK(5, "clockwork",
			BeAResearcher.CLOCKWORK_DOLL_CARE_MATERIALS, Items.GOLD_NUGGET,
			BeABirdwatcher.CARE_CLOCKWORK,
			BeACurator.CLOCKWORK_FOOD_EMPTY, BeACurator.CLOCKWORK_FOOD_HALF, BeACurator.CLOCKWORK_FOOD_FULL);

		public static final BeADoll.Variant DEFAULT = WOODEN;
		public static final com.mojang.serialization.Codec<BeADoll.Variant> CODEC = StringIdentifiable.createCodec(BeADoll.Variant::values);
		private static final IntFunction<BeADoll.Variant> INDEX_MAPPER = ValueLists.createIdToValueFunction(
			BeADoll.Variant::getIndex, values(), ValueLists.OutOfBoundsHandling.ZERO
		);
		private final int index;
		private final String id;
		private final TagKey<Item> careMaterial;
		private final Item defaultCareMaterial;
		private final SoundEvent careSound;
		private final Identifier foodSpriteEmpty;
		private final Identifier foodSpriteHalf;
		private final Identifier foodSpritFull;

		Variant(final int index, final String id, TagKey<Item> careMaterial, Item defaultCareMaterial, SoundEvent careSound, Identifier foodSpriteEmpty, Identifier foodSpriteHalf, Identifier foodSpritFull) {
			this.index = index;
			this.id = id;
			this.careMaterial = careMaterial;
			this.defaultCareMaterial = defaultCareMaterial;
			this.careSound = careSound;
			this.foodSpriteEmpty = foodSpriteEmpty;
			this.foodSpriteHalf = foodSpriteHalf;
			this.foodSpritFull = foodSpritFull;
		}

		public boolean isDollish() {
			return this != REPRESSED;
		}

		public SoundEvent getCareSound() {
			return this.careSound;
		}

		@Override
		public String asString() {
			return this.id;
		}

		public int getIndex() {
			return this.index;
		}

		public static BeADoll.Variant byIndex(int index) {
			return (BeADoll.Variant)INDEX_MAPPER.apply(index);
		}

		public TagKey<Item> getCareMaterialTag() {
			return this.careMaterial;
		}

		public Item getDefaultCareMaterial() {
			return defaultCareMaterial;
		}

		public Identifier getFoodSpriteEmpty() {
			return foodSpriteEmpty;
		}

		public Identifier getFoodSpriteHalf() {
			return foodSpriteHalf;
		}

		public Identifier getFoodSpritFull() {
			return foodSpritFull;
		}
	}
}
