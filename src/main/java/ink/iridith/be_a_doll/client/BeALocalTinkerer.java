package ink.iridith.be_a_doll.client;

import ink.iridith.be_a_doll.BeADoll;
import ink.iridith.be_a_doll.diary.BeALibrarian;
import ink.iridith.be_a_doll.letters.C2SKeysmashConfigSyncLetter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@EventBusSubscriber(modid = BeADoll.MOD_ID, value = Dist.CLIENT)
public class BeALocalTinkerer {
	private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

	public static C2SKeysmashConfigSyncLetter writtenForAFriend() {
		return new C2SKeysmashConfigSyncLetter(USE_KEYSMASHING.get(), READABLE_SELF.get(), ALWAYS_READABLE_OTHERS.get(), LETTER_POOL_OVERRIDE.get(), RESTOCK_THRESHOLD.get(), USE_ORDERED_SPOOLING.get(), BASE_CLARITY_CHANCE.get(), STARTING_CLARITY_SCORE.get(), KEYSMASHED_MULTIPLIER.get(), SPOKEN_LOUDLY_CLARITY.get(), NON_LETTER_CLARITY.get());
	}

	@SubscribeEvent
	private static void whoTouchedSasha(ModConfigEvent.Reloading event) {
		// wait hang on if i just do the dirty thing again but have it start dirty i .. no hang on that doesnt work
		// what if i have the dirty be attached to the player entity on the client. is that possible? hmmmmmmmm
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) { // i see the player you mean
			BeALibrarian.markConfigDirty(player);
//			PacketDistributor.sendToServer(writtenForAFriend());
		}
	}

	public static final ModConfigSpec.BooleanValue USE_KEYSMASHING = BUILDER
		.comment("Set this to false if you do not want your speech inhibited as a doll.")
		.comment("Only affects the client's sent messages, not the messages of other players")
		.define("useKeysmashing", true);

	public static final ModConfigSpec.BooleanValue READABLE_SELF = BUILDER
		.comment("Set this to true if you want to read your own messages through the filter.")
		.define("readableSelf", false);

	public static final ModConfigSpec.BooleanValue ALWAYS_READABLE_OTHERS = BUILDER
		.comment("Set this to false if you don't want to read others' messages through the filter.")
		.comment("This only affects the check for doll-to-doll communication, not the proximity check.")
		.define("alwaysReadableOthers", true);

	public static final ModConfigSpec.ConfigValue<String> LETTER_POOL_OVERRIDE = BUILDER
		.comment("If not empty, this will be the pool of letters that your keysmashes consist of.")
		.comment("By default, there's a few different options that are selected from based on your UUID.") // not true? lol
		.define("letterPoolOverride", "");

	public static final ModConfigSpec.ConfigValue<Float> RESTOCK_THRESHOLD = BUILDER
		.comment("Controls the percentage of your pool that must have been used in keysmash before letters can repeat")
		.comment("The pool must be this % full or less to trigger restock")
		.define("restockThreshold", 0.13f);

	public static final ModConfigSpec.BooleanValue USE_ORDERED_SPOOLING = BUILDER
		.comment("If true, your letter pool will be chosen from in order instead of randomly.")
		.comment("This will also trigger a restock every time the filter processes a non-letter.")
		.define("useOrderedSpooling", false);

	public static final ModConfigSpec.ConfigValue<Float> BASE_CLARITY_CHANCE = BUILDER
		.comment("Any non-letter characters only have a chance to be included based on a 'clarity' level of your message.")
		.comment("This controls the minimum chance that non-letters are included")
		.define("baseClarityChance", 0.31f);

	public static final ModConfigSpec.ConfigValue<Float> STARTING_CLARITY_SCORE = BUILDER
		.comment("The clarity score is what becomes the chance of inclusion when adding a non-letter")
		.comment("This chance is calculated as (clarityScore / (1 + keysmash length)) - so the first character starts at (1 + baseClarity)")
		.define("startingClarityScore", 1f);

	public static final ModConfigSpec.ConfigValue<Float> KEYSMASHED_MULTIPLIER = BUILDER
		.comment("When a character is added that is converted to keysmashing, the clarity score is /multiplied/ by this value")
		.define("keysmashedMultiplier", 0.8f);

	public static final ModConfigSpec.ConfigValue<Float> SPOKEN_LOUDLY_CLARITY = BUILDER
		.comment("When a character is added that is not converted to keysmashing, this is added to the clarity score")
		.define("spokenLoudlyClarity", 1.3f);

	public static final ModConfigSpec.ConfigValue<Float> NON_LETTER_CLARITY = BUILDER
		.comment("When a non-letter is added, this is added to the clarity score")
		.comment("(for smileys and symbols and non-english messages, it's probably best to have this at a neutral 1)")
		.define("nonLetterClarity", 1f);

	static final ModConfigSpec SPEC = BUILDER.build();
}
