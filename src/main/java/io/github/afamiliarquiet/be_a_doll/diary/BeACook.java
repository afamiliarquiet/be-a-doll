package io.github.afamiliarquiet.be_a_doll.diary;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.fabricmc.fabric.api.recipe.v1.sync.RecipeSynchronization;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class BeACook {
	public static final RecipeSerializer<EssenceArtistryRecipe> ESSENCE_ARTISTRY_RECIPE_SERIALIZER = Registry.register(
		BuiltInRegistries.RECIPE_SERIALIZER,
		BeADoll.id("crafting_special_essence_artistry"),
		new RecipeSerializer<>(EssenceArtistryRecipe.CODEC, EssenceArtistryRecipe.STREAM_CODEC)
	);

	public static void placeOrders() {
		// hi im here to alter your essence. what can i get for you today?
		// does this actually matter? idk but it's here if you want it recipe mods
		RecipeSynchronization.synchronizeRecipeSerializer(ESSENCE_ARTISTRY_RECIPE_SERIALIZER);
	}

	// ahem. let's try this one again

	public static class EssenceArtistryRecipe extends CustomRecipe {
		public static final MapCodec<EssenceArtistryRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
			instance.group(
				ItemStackTemplate.CODEC.fieldOf("result").forGetter(EssenceArtistryRecipe::getResult),
				Ingredient.CODEC.fieldOf("essence").forGetter(EssenceArtistryRecipe::getEssence),
				Ingredient.CODEC.fieldOf("variantizer").forGetter(EssenceArtistryRecipe::getVariantizer)
			).apply(instance, EssenceArtistryRecipe::new)
		);

		public static final StreamCodec<RegistryFriendlyByteBuf, EssenceArtistryRecipe> STREAM_CODEC = StreamCodec.composite(
			ItemStackTemplate.STREAM_CODEC,
			EssenceArtistryRecipe::getResult,
			Ingredient.CONTENTS_STREAM_CODEC,
			EssenceArtistryRecipe::getEssence,
			Ingredient.CONTENTS_STREAM_CODEC,
			EssenceArtistryRecipe::getVariantizer,
			EssenceArtistryRecipe::new
		);

		private final ItemStackTemplate result;
		private final Ingredient essence;
		private final Ingredient variantizer;

		public EssenceArtistryRecipe(ItemStackTemplate result, Ingredient essence, Ingredient variantizer) {
			this.essence = essence;
			this.variantizer = variantizer;
			this.result = result;
		}

		public ItemStackTemplate getResult() {
			return result;
		}

		public Ingredient getEssence() {
			return essence;
		}

		public Ingredient getVariantizer() {
			return variantizer;
		}

		@Override
		public boolean matches(CraftingInput input, @NonNull Level level) {
			return input.ingredientCount() == 2 && essence.test(findEssence(input)) && variantizer.test(findVariantizer(input));
		}

		@Override
		public @NonNull ItemStack assemble(CraftingInput input) {
			ItemStack yourEssenceKnowsItsShape = findEssence(input).copy();
			BeADoll.Variant itsShape = findVariantizer(input).get(BeACollector.DOLL_VARIANT_COMPONENT);
			if (itsShape == null) {
				itsShape = BeADoll.Variant.REPRESSED;
			}
			yourEssenceKnowsItsShape.set(BeACollector.DOLL_VARIANT_COMPONENT, itsShape);
			return yourEssenceKnowsItsShape;
		}

		protected ItemStack findEssence(CraftingInput input) {
			for (int i = 0; i < input.size(); i++) {
				ItemStack item = input.getItem(i);
				if (item.is(BeACollector.ESSENCE_FRAGMENT)) {
					return item;
				}
			}
			return ItemStack.EMPTY;
		}

		protected ItemStack findVariantizer(CraftingInput input) {
			for (int i = 0; i < input.size(); i++) {
				ItemStack item = input.getItem(i);
				if (item.is(BeAResearcher.ESSENCE_SCENTS)) { // mojang has forced me to comply with your demand Maddy Noobulus o7
					return item;
				}
			}
			return ItemStack.EMPTY;
		}

		@Override
		public @NonNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
			NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);

			for (int i = 0; i < remainders.size(); i++) {
				ItemStack current = input.getItem(i);
				// smudged this in 26.1 port. looks maybe ok. it's weird and unlikely anyway who would dare put a remai- i start coughing up a large bucket of iron that suddenly appeared in my esophagus
				ItemStackTemplate weirdAndUnlikelyRemainder = current.getItem().getCraftingRemainder();
				if (weirdAndUnlikelyRemainder != null) {
					remainders.set(i, weirdAndUnlikelyRemainder.create());
				} else if (current.is(BeAResearcher.ESSENCE_SCENTS)) {
					remainders.set(i, current.copyWithCount(1));
					break;
				}
			}

			return remainders;
		}

		@Override
		public @NonNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
			return ESSENCE_ARTISTRY_RECIPE_SERIALIZER;
		}
	}






	// this can probably? move with the recipe if it ever moves
//	public static final RecipeSerializer<EssenceArtistryRecipe> ESSENCE_ARTISTRY_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BeADoll.id("crafting_special_essenceartistry"), new CustomRecipe.Serializer<>(EssenceArtistryRecipe::new));
//
//	// i'll move it out if i make another, same as pen pal
//	public static class EssenceArtistryRecipe extends CustomRecipe {
//
//		@Override
//		public boolean matches(CraftingInput input, @NonNull Level world) {
//			if (input.ingredientCount() != 2) {
//				return false;
//			} else {
//				boolean hasOneEssenceFragment = false;
//				boolean hasOneDollcraftItem = false;
//
//				for (int i = 0; i < input.size(); i++) {
//					ItemStack current = input.getItem(i);
//					if (!current.isEmpty()) {
//						if (current.is(BeAResearcher.DOLLCRAFT_ITEMS) || current.is(Items.DIAMOND_PICKAXE)) {
//							if (hasOneDollcraftItem) {
//								return false;
//							}
//
//							hasOneDollcraftItem = true;
//						} else if (current.is(BeACollector.ESSENCE_FRAGMENT)) {
//							if (hasOneEssenceFragment) {
//								return false;
//							}
//
//							hasOneEssenceFragment = true;
//						} else {
//							// not either of the items i want? then perish
//							return false;
//						}
//					}
//				}
//
//				return hasOneDollcraftItem && hasOneEssenceFragment;
//			}
//		}
//
//		@Override
//		public @NonNull ItemStack assemble(CraftingInput input) {
//			BeADoll.Variant dollVariant = null;
//			ItemStack essenceFragment = ItemStack.EMPTY;
//
//			for (int i = 0; i < input.size(); i++) {
//				ItemStack current = input.getItem(i);
//				if (!current.isEmpty()) {
//					if (current.is(BeACollector.ESSENCE_FRAGMENT)) {
//						if (!essenceFragment.isEmpty()) { // no mass fabrication, one at a time
//							return ItemStack.EMPTY;
//						}
//
//						essenceFragment = current;
//					} else if (current.is(Items.DIAMOND_PICKAXE)) {
//						if (dollVariant != null) {
//							return ItemStack.EMPTY;
//						}
//
//						dollVariant = BeADoll.Variant.REPRESSED;
//					} else if (current.get(BeACollector.DOLL_VARIANT_COMPONENT) != null) {
//						if (dollVariant != null) {
//							return ItemStack.EMPTY;
//						}
//
//						dollVariant = current.get(BeACollector.DOLL_VARIANT_COMPONENT);
//					} else {
//						// not either of the items i want? then perish
//						return ItemStack.EMPTY;
//					}
//				}
//			}
//
//			if (!essenceFragment.isEmpty() && dollVariant != null) {
//				ItemStack alteredFragment = essenceFragment.copy();
//				alteredFragment.set(BeACollector.DOLL_VARIANT_COMPONENT, dollVariant);
//				return alteredFragment;
//			} else {
//				return ItemStack.EMPTY;
//			}
//		}
//
//		@Override
//		public @NonNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
//			NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);
//
//			for (int i = 0; i < remainders.size(); i++) {
//				ItemStack current = input.getItem(i);
//				// smudged this in 26.1 port. looks maybe ok. it's weird and unlikely anyway who would dare put a remai- i start coughing up a large bucket of iron that suddenly appeared in my esophagus
//				ItemStackTemplate weirdAndUnlikelyRemainder = current.getItem().getCraftingRemainder();
//				if (weirdAndUnlikelyRemainder != null) {
//					remainders.set(i, weirdAndUnlikelyRemainder.create());
//				} else if (current.is(BeAResearcher.DOLLCRAFT_ITEMS) || current.is(Items.DIAMOND_PICKAXE)) {
//					remainders.set(i, current.copyWithCount(1));
//					break;
//				}
//			}
//
//			return remainders;
//		}
//
//		@Override
//		public @NonNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
//			return BeACook.ESSENCE_ARTISTRY_SERIALIZER;
//		}
//	}
}
