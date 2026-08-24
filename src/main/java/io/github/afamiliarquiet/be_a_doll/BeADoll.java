package io.github.afamiliarquiet.be_a_doll;

import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.diary.BeABug;
import io.github.afamiliarquiet.be_a_doll.diary.BeACollector;
import io.github.afamiliarquiet.be_a_doll.diary.BeACook;
import io.github.afamiliarquiet.be_a_doll.diary.BeACurator;
import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import io.github.afamiliarquiet.be_a_doll.diary.BeAPenPal;
import io.github.afamiliarquiet.be_a_doll.diary.BeAResearcher;
import io.github.afamiliarquiet.be_a_doll.diary.BeAWitch;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.ModInitializer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.ByIdMap;
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

	public static ResourceLocation id(String thing) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, thing);
	}

	// probably a good habit to always log my id whenever i'm throwing things in the log, even if it's just a quick test
	public static void log(String message) {
		LOGGER.info("[Be a doll!] {}", message);
	}

	public static void warn(String message) {
		LOGGER.warn("[Would you be a doll?] {}", message);
	}

	// todo - turn this into interface/abstract w/ a registry
	public enum Variant implements StringRepresentable {
		REPRESSED(0, "player",
			ItemTags.ANVIL, Items.ANVIL,
			SoundEvents.ANVIL_FALL,
			ResourceLocation.fromNamespaceAndPath("missing", "texture"), ResourceLocation.fromNamespaceAndPath("missing", "texture"), ResourceLocation.fromNamespaceAndPath("missing", "texture")), // gonna look really silly in your throat.
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
			BeAResearcher.PLASTIC_DOLL_CARE_MATERIALS, Items.RESIN_BRICK,
			BeABirdwatcher.CARE_PLASTIC,
			BeACurator.PLASTIC_FOOD_EMPTY, BeACurator.PLASTIC_FOOD_HALF, BeACurator.PLASTIC_FOOD_FULL),
		CLOCKWORK(5, "clockwork",
			BeAResearcher.CLOCKWORK_DOLL_CARE_MATERIALS, Items.GOLD_NUGGET,
			BeABirdwatcher.CARE_CLOCKWORK,
			BeACurator.CLOCKWORK_FOOD_EMPTY, BeACurator.CLOCKWORK_FOOD_HALF, BeACurator.CLOCKWORK_FOOD_FULL);

		public static final BeADoll.Variant DEFAULT = WOODEN;
		public static final StringRepresentable.EnumCodec<BeADoll.Variant> CODEC = StringRepresentable.fromEnum(BeADoll.Variant::values);
		private static final IntFunction<BeADoll.Variant> INDEX_MAPPER = ByIdMap.continuous(
			BeADoll.Variant::getIndex, values(), ByIdMap.OutOfBoundsStrategy.ZERO
		);
		public static final StreamCodec<ByteBuf, BeADoll.Variant> PACKET_CODEC = ByteBufCodecs.idMapper(INDEX_MAPPER, BeADoll.Variant::getIndex);
		private final int index;
		private final String id;
		private final TagKey<Item> careMaterial;
		private final Item defaultCareMaterial;
		private final SoundEvent careSound;
		private final ResourceLocation foodSpriteEmpty;
		private final ResourceLocation foodSpriteHalf;
		private final ResourceLocation foodSpritFull;

		Variant(final int index, final String id, TagKey<Item> careMaterial, Item defaultCareMaterial, SoundEvent careSound, ResourceLocation foodSpriteEmpty, ResourceLocation foodSpriteHalf, ResourceLocation foodSpritFull) {
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
		public String getSerializedName() {
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

		public ResourceLocation getFoodSpriteEmpty() {
			return foodSpriteEmpty;
		}

		public ResourceLocation getFoodSpriteHalf() {
			return foodSpriteHalf;
		}

		public ResourceLocation getFoodSpritFull() {
			return foodSpritFull;
		}
	}
}
