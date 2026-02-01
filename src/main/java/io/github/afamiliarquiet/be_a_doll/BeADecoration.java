package io.github.afamiliarquiet.be_a_doll;

import io.github.afamiliarquiet.be_a_doll.diary.BeALibrarian;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import virtuoel.pehkui.api.ScaleData;

public class BeADecoration {
	// here's where you become a shoulder decoration!
	// this is a pile of stuff for mixins to use, but having 'em here makes em reloadable for easier dev

	// todone - lots to do but firstly dismounting. the mount player never sees things dismount for some reason
	// todone - let the mount force a doll off (probably with ribbon? but maybe not. maybe pose can also do it, like crawl)
	// todone - should probably force a dismount on gamemode change n such too.. blegh. later problem
	// notodo - copy the boat yaw clamping stuffs?
	// todone - adjust for pose (crouching, crawling/swimming/gliding, sleeping, spin attack maybe, dying??)
	// todone - make riptide not hit shouldered dolls
	// todone - make sure only one doll fits
	// todone - actually. you have two shoulders, let two dolls fit
	// todone - now also should probably check on parrot shoulder slots for compat there
	// notodone - wait. two shoulders AND a head? it's possible...
	// notodo - this is extreme but.. you know how the happy ghast has seats on all 4 sides? player head is same shape..
	// todone - width/scale check?
	// todone - check to see if dolls follow with teleport, and if not try to fix. (this was a problem for spectating) (seems fine?)
	// todone - make locator bar not shiver in terror

	public static int getParrotCount(PlayerEntity playerMount) {
		int parrots = 0;
		if (!playerMount.getShoulderEntityLeft().isEmpty()) parrots++;
		if (!playerMount.getShoulderEntityRight().isEmpty()) parrots++;
		return parrots;
	}

	private static Arm getArm(PlayerEntity playerMount, PlayerEntity doll, int parrotCount) {
		int passengerIndex = playerMount.getPassengerList().indexOf(doll);
		Arm armToSitOn = playerMount.getMainArm().getOpposite();
		if (passengerIndex == 1) {// second passenger
			armToSitOn = armToSitOn.getOpposite();
		}

		if (parrotCount == 1) {
			// force seat if parrot has the other. if there's two parrots and a doll is riding then things are already broken
			if (!playerMount.getShoulderEntityLeft().isEmpty())
				armToSitOn = Arm.RIGHT; // if left is occupied, has to be right
			if (!playerMount.getShoulderEntityRight().isEmpty())
				armToSitOn = Arm.LEFT; // if right is occupied, has to be right
		}
		return armToSitOn;
	}

	// null return indicates mixin should not intervene
	public static @Nullable Vec3d getDollAttachmentPos(PlayerEntity playerMount, PlayerEntity doll, EntityDimensions dimensions, float scaleFactor) {
		int parrotCount = getParrotCount(playerMount);
		if (parrotCount > 1) {
			return null; // secret third slot. head. this shouldn't happen, but if it does, head.
		}

		Arm armToSitOn = getArm(playerMount, doll, parrotCount);
		Vec3d attachmentPos = new Vec3d((armToSitOn == Arm.LEFT ? 1 : -1) * 0.3625 * scaleFactor, dimensions.height, 0);

		switch (playerMount.getPose()) {
			case CROUCHING:
				attachmentPos = attachmentPos.add(new Vec3d(0, -0.025, 0).multiply(scaleFactor));
			case STANDING:
				attachmentPos = attachmentPos.add(new Vec3d(0, - 0.425, 0.1).multiply(scaleFactor));
				break;
			case SLEEPING:
				attachmentPos = attachmentPos.add(new Vec3d(0, -0.35, 0).multiply(scaleFactor));
				// preemptively un-rotate and then rotate to sleeping direction
				Direction sleepingDirection = playerMount.getSleepingDirection();
				if (sleepingDirection != null) { // should always be true but i'm not the assertive type
					attachmentPos = attachmentPos.rotateY(playerMount.bodyYaw * (float) (Math.PI / 180.0));
					attachmentPos = attachmentPos.rotateY((sleepingDirection.asRotation() + 180) * (float) (Math.PI / 180.0));
				}
				break;
			case SWIMMING:
				attachmentPos = attachmentPos.add(new Vec3d(0, 0.15, -1).multiply(scaleFactor));
			case SPIN_ATTACK:
			case FALL_FLYING:
				attachmentPos = attachmentPos.add(new Vec3d(0, -0.5, 1.5).multiply(scaleFactor));
				attachmentPos = attachmentPos.rotateX(-playerMount.getPitch() * (float) (Math.PI / 180.0));
				break;
		}

		return attachmentPos.rotateY(-playerMount.bodyYaw * (float) (Math.PI / 180.0));
	}

	public static boolean canAddPassenger(PlayerEntity playerMount, PlayerEntity doll) {
		int freeSlots = 2;
		freeSlots -= getParrotCount(playerMount);
		freeSlots -= playerMount.getPassengerList().size();
		ScaleData dollScale = BeALibrarian.DOLL_SCALE_TYPE.getScaleData(doll);
		ScaleData playerMountScale = BeALibrarian.DOLL_SCALE_TYPE.getScaleData(playerMount);
		return freeSlots > 0 && dollScale.getScale() / playerMountScale.getScale() <= 0.31;
	}

	public static Vec3d updatePassengerForDismount(PlayerEntity playerMount, PlayerEntity doll) {
		return new Vec3d(doll.getX(), playerMount.getBoundingBox().minY, doll.getZ());
	}

	public static boolean shoulderEntityIsEmpty(LivingEntity playerMountTrustMe, boolean parrotsEmpty, Arm testArm) {
		int likelyDollCount = (int) playerMountTrustMe.getPassengerList().stream().filter(PlayerEntity.class::isInstance).count();
		boolean dollsEmpty = true;

		if (likelyDollCount > 1) {
			dollsEmpty = false;
		} else if (likelyDollCount == 1) {
			// pain
			dollsEmpty = testArm != playerMountTrustMe.getMainArm();
		}

		return parrotsEmpty && dollsEmpty;
	}
}
