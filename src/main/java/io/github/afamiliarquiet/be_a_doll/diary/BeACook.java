package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.recipe.input.CraftingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class BeACook {
	// this can probably? move with the recipe if it ever moves
	public static final RecipeSerializer<EssenceArtistryRecipe> ESSENCE_ARTISTRY_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, BeADoll.id("crafting_special_essenceartistry"), new SpecialRecipeSerializer<>(EssenceArtistryRecipe::new));

	public static void placeOrders() {
		// hi im here to alter your essence. what can i get for you today?
	}

	// i'll move it out if i make another, same as pen pal
	public static class EssenceArtistryRecipe extends SpecialCraftingRecipe {

		public EssenceArtistryRecipe(CraftingRecipeCategory category) {
			super(category);
		}

		@Override
		public boolean matches(CraftingRecipeInput input, World world) {
			if (input.getStackCount() != 2) {
				return false;
			} else {
				boolean hasOneEssenceFragment = false;
				boolean hasOneDollcraftItem = false;

				for (int i = 0; i < input.getSize(); i++) {
					ItemStack current = input.getStackInSlot(i);
					if (!current.isEmpty()) {
						if (current.isIn(BeAResearcher.ESSENCE_SCENTS)) {
							if (hasOneDollcraftItem) {
								return false;
							}

							hasOneDollcraftItem = true;
						} else if (current.isOf(BeACollector.ESSENCE_FRAGMENT)) {
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
		public ItemStack craft(CraftingRecipeInput input, RegistryWrapper.WrapperLookup registries) {
			BeADoll.Variant dollVariant = null;
			ItemStack essenceFragment = ItemStack.EMPTY;

			for (int i = 0; i < input.getSize(); i++) {
				ItemStack current = input.getStackInSlot(i);
				if (!current.isEmpty()) {
					if (current.isOf(BeACollector.ESSENCE_FRAGMENT)) {
						if (!essenceFragment.isEmpty()) { // no mass fabrication, one at a time
							return ItemStack.EMPTY;
						}

						essenceFragment = current;
					} else if (current.isIn(BeAResearcher.PLAYER_ITEMS)) {
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
		public DefaultedList<ItemStack> getRemainder(CraftingRecipeInput input) {
			DefaultedList<ItemStack> remainders = DefaultedList.ofSize(input.getSize(), ItemStack.EMPTY);

			for (int i = 0; i < remainders.size(); i++) {
				ItemStack current = input.getStackInSlot(i);
				Item weirdAndUnlikelyRemainder = current.getItem().getRecipeRemainder();
				if (weirdAndUnlikelyRemainder != null) {
					remainders.set(i, new ItemStack(weirdAndUnlikelyRemainder));
				} else if (current.isIn(BeAResearcher.ESSENCE_SCENTS)) {
					remainders.set(i, current.copyWithCount(1));
					break;
				}
			}

			return remainders;
		}

		@Override
		public RecipeSerializer<? extends SpecialCraftingRecipe> getSerializer() {
			return BeACook.ESSENCE_ARTISTRY_SERIALIZER;
		}

		@Override
		public boolean fits(int width, int height) {
			return width * height >= 2;
		}
	}
}
