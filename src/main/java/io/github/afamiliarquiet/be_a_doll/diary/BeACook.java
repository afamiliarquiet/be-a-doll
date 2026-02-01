package io.github.afamiliarquiet.be_a_doll.diary;

import io.github.afamiliarquiet.be_a_doll.BeADoll;
import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class BeACook {
	// this can probably? move with the recipe if it ever moves
	public static final RecipeSerializer<EssenceArtistryRecipe> ESSENCE_ARTISTRY_SERIALIZER = Registry.register(Registries.RECIPE_SERIALIZER, BeADoll.id("crafting_special_essenceartistry"), new SpecialRecipeSerializer<>(EssenceArtistryRecipe::new));

	public static void placeOrders() {
		// hi im here to alter your essence. what can i get for you today?
	}

	// i'll move it out if i make another, same as pen pal
	public static class EssenceArtistryRecipe extends SpecialCraftingRecipe {

		public EssenceArtistryRecipe(Identifier id, CraftingRecipeCategory category) {
			super(id, category);
		}

		@Override
		public boolean matches(RecipeInputInventory input, World world) {
			List<ItemStack> inputStacks = input.getInputStacks();
			if (inputStacks.stream().filter(Predicate.not(ItemStack::isEmpty)).count() != 2) {
				return false;
			} else {
				boolean hasOneEssenceFragment = false;
				boolean hasOneDollcraftItem = false;

				for (int i = 0; i < inputStacks.size(); i++) {
					ItemStack current = inputStacks.get(i);
					if (!current.isEmpty()) {
						if (current.isIn(BeAResearcher.DOLLCRAFT_ITEMS) || current.isOf(Items.DIAMOND_PICKAXE)) {
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
		public ItemStack craft(RecipeInputInventory input, DynamicRegistryManager registries) {
			BeADoll.Variant dollVariant = null;
			ItemStack essenceFragment = ItemStack.EMPTY;

			List<ItemStack> inputStacks = input.getInputStacks();
			for (int i = 0; i < inputStacks.size(); i++) {
				ItemStack current = inputStacks.get(i);
				if (!current.isEmpty()) {
					Optional<BeADoll.Variant> variant = BeACollector.getDollVariant(current);
					if (current.isOf(BeACollector.ESSENCE_FRAGMENT)) {
						if (!essenceFragment.isEmpty()) { // no mass fabrication, one at a time
							return ItemStack.EMPTY;
						}

						essenceFragment = current;
					} else if (current.isOf(Items.DIAMOND_PICKAXE)) {
						if (dollVariant != null) {
							return ItemStack.EMPTY;
						}

						dollVariant = BeADoll.Variant.REPRESSED;
					} else if (variant.isPresent()) {
						dollVariant = variant.get();
					} else {
						// not either of the items i want? then perish
						return ItemStack.EMPTY;
					}
				}
			}

			if (!essenceFragment.isEmpty() && dollVariant != null) {
				ItemStack alteredFragment = essenceFragment.copy();
				BeACollector.setDollVariant(alteredFragment, dollVariant);
				return alteredFragment;
			} else {
				return ItemStack.EMPTY;
			}
		}

		@Override
		public DefaultedList<ItemStack> getRemainder(RecipeInputInventory input) {
			List<ItemStack> inputStacks = input.getInputStacks();
			DefaultedList<ItemStack> remainders = DefaultedList.ofSize(inputStacks.size(), ItemStack.EMPTY);

			for (int i = 0; i < remainders.size(); i++) {
				ItemStack current = inputStacks.get(i);
				Item weirdAndUnlikelyRemainder = current.getItem().getRecipeRemainder();
				if (weirdAndUnlikelyRemainder != null) {
					remainders.set(i, new ItemStack(weirdAndUnlikelyRemainder));
				} else if (current.isIn(BeAResearcher.DOLLCRAFT_ITEMS) || current.isOf(Items.DIAMOND_PICKAXE)) {
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
