package ink.iridith.be_a_doll.client.personal_diary;

import ink.iridith.be_a_doll.diary.BeABug;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber
public class BeALocalBug {
	public static void lookAtBug() {
//		ParticleFactoryRegistry bugParty = ParticleFactoryRegistry.getInstance();
//		bugParty.register(BeABug.FRAGMENTED.get(), FragmentedParticle.Factory::new);
	}

	@SubscribeEvent
	public static void bugsnax(RegisterParticleProvidersEvent event) {
		// sorry excuse me intellij. you're calling that a typo of "Bugsnag"? what kind of word is that- NOT A WORD!!! A BRAND! intellij DIE
		event.registerSpriteSet(BeABug.FRAGMENTED.get(), FragmentedParticle.Factory::new);
	}

	public static class FragmentedParticle extends TextureSheetParticle {
		private static final RandomSource RANDOM = RandomSource.create();
		private final int fromColor;
		private final int toColor;

		protected FragmentedParticle(ClientLevel clientWorld, double x, double y, double z, int fromColor, int toColor, double xr, double zr) {
			// this is silly. 3 layers to avoid being restricted by super() on line 1 and ignore half of it anyway
			this(clientWorld, x, y, z, 0.05 - xr * 0.1, 0.0125 - RANDOM.nextDouble() * 0.025, 0.05 - zr  * 0.1, fromColor, toColor);
		}

		protected FragmentedParticle(ClientLevel clientWorld, double x, double y, double z, double vx, double vy, double vz, int fromColor, int toColor) {
			super(clientWorld, x+vx*2, y+vy*2, z+vz*2, 0, 0, 0);

			this.xd = vx;
			this.yd = vy;
			this.zd = vz;

			this.quadSize = (this.random.nextFloat() * 0.01f + 0.02f);
			this.friction = 0.87F;
			this.gravity = 0;

			this.fromColor = fromColor;
			this.toColor = toColor;
			setColorSimpler(this.fromColor);
		}

		@Override
		public void tick() {
			this.xo = this.x;
			this.yo = this.y;
			this.zo = this.z;
			if (this.age++ >= this.lifetime) {
				this.remove();
			} else {
				this.move(this.xd, this.yd, this.zd);

				this.xd = this.xd * this.friction;
				this.yd = this.yd * this.friction;
				this.zd = this.zd * this.friction;
				setColorSimpler(FastColor.ARGB32.lerp((float)this.age / this.lifetime, this.fromColor, this.toColor));
			}
		}

		private void setColorSimpler(int color) {
			this.setColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F, FastColor.ARGB32.blue(color) / 255.0F);
			this.setAlpha(FastColor.ARGB32.alpha(color) / 255.0F);
		}

		@Override
		public @NotNull ParticleRenderType getRenderType() {
			return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
		}

		public static class Factory implements ParticleProvider<SimpleParticleType> {
			private final SpriteSet spriteProvider;

			public Factory(SpriteSet spriteProvider) {
				this.spriteProvider = spriteProvider;
			}

			public Particle createParticle(@NotNull SimpleParticleType simpleParticleType, @NotNull ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
				FragmentedParticle bug = new FragmentedParticle(clientWorld, d, e, f, 0xff95a5e9, RANDOM.nextBoolean() ? 0xfff77490 : 0xfffab598, RANDOM.nextDouble(), RANDOM.nextDouble());
				bug.scale(Mth.randomBetween(clientWorld.getRandom(), 3.0F, 5.0F));
				bug.pickSprite(this.spriteProvider);
				return bug;
			}
		}
	}
}
