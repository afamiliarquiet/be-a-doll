package ink.iridith.be_a_doll.diary;

import ink.iridith.be_a_doll.BeADoll;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BeACook {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, BeADoll.MOD_ID);

	// this can probably? move with the recipe if it ever moves
	public static final Supplier<RecipeSerializer<EssenceArtistryRecipe>> ESSENCE_ARTISTRY_SERIALIZER = RECIPE_SERIALIZERS.register("crafting_special_essenceartistry", () -> new SimpleCraftingRecipeSerializer<>(EssenceArtistryRecipe::new));

	public static void placeOrders(IEventBus modBus) {
		// hi im here to alter your essence. what can i get for you today?
		RECIPE_SERIALIZERS.register(modBus);
	}

	// i'll move it out if i make another, same as pen pal
	public static class EssenceArtistryRecipe extends CustomRecipe {

		public EssenceArtistryRecipe(CraftingBookCategory category) {
			super(category);
		}

		@Override
		public boolean matches(CraftingInput input, @NotNull Level world) {
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
						} else if (current.is(BeACollector.ESSENCE_FRAGMENT.get())) {
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
		public @NotNull ItemStack assemble(CraftingInput input, HolderLookup.@NotNull Provider registries) {
			BeADoll.Variant dollVariant = null;
			ItemStack essenceFragment = ItemStack.EMPTY;

			for (int i = 0; i < input.size(); i++) {
				ItemStack current = input.getItem(i);
				if (!current.isEmpty()) {
					if (current.is(BeACollector.ESSENCE_FRAGMENT.get())) {
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
		public @NotNull NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
			NonNullList<ItemStack> remainders = NonNullList.withSize(input.size(), ItemStack.EMPTY);

			for (int i = 0; i < remainders.size(); i++) {
				ItemStack current = input.getItem(i);
				ItemStack weirdAndUnlikelyRemainder = current.getCraftingRemainingItem();
				if (!weirdAndUnlikelyRemainder.isEmpty()) {
					remainders.set(i, weirdAndUnlikelyRemainder.copy());
				} else if (current.is(BeAResearcher.DOLLCRAFT_ITEMS) || current.is(Items.DIAMOND_PICKAXE)) {
					remainders.set(i, current.copyWithCount(1));
					break;
				}
			}

			return remainders;
		}

		@Override
		public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
			return BeACook.ESSENCE_ARTISTRY_SERIALIZER.get();
		}

		@Override
		public boolean canCraftInDimensions(int width, int height) {
			return width * height >= 2;
		}
	}
}
