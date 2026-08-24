package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;

public class BeACook {
	// this can probably? move with the recipe if it ever moves
	public static final RecipeSerializer<EssenceArtistryRecipe> ESSENCE_ARTISTRY_SERIALIZER = Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, BeADoll.id("crafting_special_essenceartistry"), new CustomRecipe.Serializer<>(EssenceArtistryRecipe::new));

	public static void placeOrders() {
		// hi im here to alter your essence. what can i get for you today?
	}

	// i'll move it out if i make another, same as pen pal
	public static class EssenceArtistryRecipe extends CustomRecipe {

		public EssenceArtistryRecipe(CraftingBookCategory category) {
			super(category);
		}

		@Override
		public boolean matches(CraftingInput input, Level world) {
			if (input.ingredientCount() != 2) {
				return false;
			} else {
				boolean hasOneEssenceFragment = false;
				boolean hasOneDollcraftItem = false;

				for (int i = 0; i < input.size(); i++) {
					ItemStack current = input.getItem(i);
					if (!current.isEmpty()) {
						if (current.is(BeAResearcher.DOLLCRAFT_ITEMS) || current.is(Items.DIAMOND_PICKAXE)) {
							if (hasOneDollcraftItem) {
								return false;
							}

							hasOneDollcraftItem = true;
						} else if (current.is(BeACollector.ESSENCE_FRAGMENT)) {
							if (hasOneEssenceFragment) {
								return false;
							}

							hasOneEssenceFragment = true;
						} else {
							// not either of the items i want? then perish
							return false;
						}
					}
				}

				return hasOneDollcraftItem && hasOneEssenceFragment;
			}
		}

		@Override
		public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
			BeADoll.Variant dollVariant = null;
			ItemStack essenceFragment = ItemStack.EMPTY;

			for (int i = 0; i < input.size(); i++) {
				ItemStack current = input.getItem(i);
				if (!current.isEmpty()) {
					if (current.is(BeACollector.ESSENCE_FRAGMENT)) {
						if (!essenceFragment.isEmpty()) { // no mass fabrication, one at a time
							return ItemStack.EMPTY;
						}

						essenceFragment = current;
					} else if (current.is(Items.DIAMOND_PICKAXE)) {
						if (dollVariant != null) {
							return ItemStack.EMPTY;
						}

						dollVariant = BeADoll.Variant.REPRESSED;
					} else if (current.get(BeACollector.DOLL_VARIANT_COMPONENT) != null) {
						if (dollVariant != null) {
							return ItemStack.EMPTY;
						}

						dollVariant = current.get(BeACollector.DOLL_VARIANT_COMPONENT);
					} else {
						// not either of the items i want? then perish
						return ItemStack.EMPTY;
					}
				}
			}

			if (!essenceFragment.isEmpty() && dollVariant != null) {
				ItemStack alteredFragment = essenceFragment.copy();
				alteredFragment.set(BeACollector.DOLL_VARIANT_COMPONENT, dollVariant);
				return alteredFragment;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
			NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);

			for (int i = 0; i < remainders.size(); i++) {
				ItemStack current = input.getItem(i);
				ItemStack weirdAndUnlikelyRemainder = current.getItem().getCraftingRemainder();
				if (!weirdAndUnlikelyRemainder.isEmpty()) {
					remainders.set(i, weirdAndUnlikelyRemainder);
				} else if (current.is(BeAResearcher.DOLLCRAFT_ITEMS) || current.is(Items.DIAMOND_PICKAXE)) {
					remainders.set(i, current.copyWithCount(1));
					break;
				}
			}

			return remainders;
		}

		@Override
		public RecipeSerializer<? extends CustomRecipe> getSerializer() {
			return BeACook.ESSENCE_ARTISTRY_SERIALIZER;
		}
	}
}
