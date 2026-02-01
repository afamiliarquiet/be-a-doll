package io.github.afamiliarquiet.be_a_doll.item;

import io.github.afamiliarquiet.be_a_doll.BeAMaid;
import io.github.afamiliarquiet.be_a_doll.diary.BeABirdwatcher;
import io.github.afamiliarquiet.be_a_doll.mixin.synthetic_treats.FoxEntityTrustInvoker;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Collections;

public class RibbonItem extends Item {
	public RibbonItem(Settings settings) {
		super(settings);
	}

	@Override
	public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
		if (entity instanceof PlayerEntity doll && BeAMaid.isDoll(doll)) {
			if (doll.startRiding(user, false)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return ActionResult.SUCCESS;
			}
		} else {
			ActionResult tried = useToTryRiding(stack, user, entity, hand);
			if (tried.isAccepted()) {
				return tried;
			}
		}

		return super.useOnEntity(stack, user, entity, hand);
	}

	public ActionResult useToTryRiding(ItemStack stack, PlayerEntity user, Entity entity, Hand hand) {
		if (BeAMaid.isDoll(user)) {
			// ohh.. so the user was the doll!
			boolean shouldRide = false;
			if (entity instanceof Tameable tameable) {
				LivingEntity ownerRef = tameable.getOwner();
				if (ownerRef != null && ownerRef.getUuid().equals(user.getUuid()) && entity.getWidth() > user.getWidth()) {
					shouldRide = true;
				}
			} else if (entity instanceof FoxEntity foxesAreSoCool && ((FoxEntityTrustInvoker)foxesAreSoCool).invokeCanTrust(user.getUuid())) {
				shouldRide = true;
			}

			if (shouldRide && user.startRiding(entity)) {
				user.playSound(BeABirdwatcher.RAVEN_CHIRP, 1f, 1f);
				return ActionResult.SUCCESS;
			}
		}

		return ActionResult.PASS;
	}

	@Override
	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		// yeah no lol. did you not see the C2SDollDismountLetter i had to make? client's gotta hear about this
		if (/*!user.getWorld().isClient && */!user.getPassengerList().isEmpty() && user.shouldCancelInteraction()) {
//			user.removeAllPassengers();
			// intellij might tell you this can be replaced with getLast
			// it cannot. we are in java 17.
			Entity doll = user.getPassengerList().get(user.getPassengerList().size() - 1);
			BlockHitResult blockHitResult = raycast(world, user, RaycastContext.FluidHandling.NONE);
			Vec3d pos;
			// fear my mega if statement of doom! it could be worse. i'm just being a little bit silly with it.
			doll.stopRiding();
			if (!world.isClient()
				&& blockHitResult.getType() == HitResult.Type.BLOCK
				&& doll instanceof ServerPlayerEntity serverPlayerEntity
				&& (pos = getDollPlacementPos(blockHitResult, doll)) != null
			) {
				serverPlayerEntity.teleport((ServerWorld) serverPlayerEntity.getWorld(), pos.x, pos.y, pos.z, Collections.emptySet(), user.getYaw() + 180, user.getPitch() * -1);
			}
			user.playSound(BeABirdwatcher.RAVEN_CRY, 1f, 1f);
			return TypedActionResult.success(user.getStackInHand(hand));
		} else {
			return super.use(world, user, hand);
		}
	}

	public static @Nullable Vec3d getDollPlacementPos(BlockHitResult blockHitResult, Entity doll) {
		Vec3d pos = blockHitResult.getPos();
		EntityDimensions dollStanding = doll.getDimensions(EntityPose.STANDING);

		if (blockHitResult.getSide().getAxis() == Direction.Axis.Y) {
			if (blockHitResult.getSide() == Direction.DOWN) {
				pos = pos.add(0, -dollStanding.height, 0);
			}

			// just because i'm feeling extra nice, i'll give you a horizontal aim assist too.
			Vec3d firstCheck = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.X);
			if (firstCheck != null) {
				pos = firstCheck;
			} else {
				pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Z);
			}
		} else {
			Vector3f sideVec = blockHitResult.getSide().getUnitVector();
			pos = pos.add(sideVec.x * dollStanding.width / 2, 0, sideVec.z * dollStanding.width / 2);

			// adjust for being low or high enough to clip into a possible adjacent block
			pos = checkForCollisionsOnAxis(doll, dollStanding, pos, Direction.Axis.Y);
		}

		// check for any other collisions, if collide then. bad aim, sorry, just gonna drop.
		// i'll maybe update the math later.
		if (pos == null || doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos)).iterator().hasNext()) {
			return null;
		} else {
			return pos;
		}
	}

	// todo - hey quiet if you come back to use this again, maybe do it more like Entity.adjustMovementForCollisions
	//  you've really got a lot of block collision checking going on here
	//  anyway this partial aim assist only helps in the case of one collision.
	//  redoing this like entity.amfc would probably be the best way to fix that. assuming amfc is.. what this is.
	private static @Nullable Vec3d checkForCollisionsOnAxis(Entity doll, EntityDimensions dollStanding, @Nullable Vec3d pos, Direction.Axis axis) {
		if (pos == null || !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos)).iterator().hasNext()) {
			return pos;
		} else {
			double xyz = pos.getComponentAlongAxis(axis);
			double positiveEdgeCrumb = (xyz + (axis.isVertical() ? dollStanding.height : dollStanding.width / 2)) - Math.ceil(xyz);
			double negativeEdgeCrumb = Math.floor(xyz) - (xyz - (axis.isVertical() ? 0 : dollStanding.width / 2));
			if (positiveEdgeCrumb < 0.5 && positiveEdgeCrumb > 0 && !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos.withAxis(axis, xyz - positiveEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll down a bit works, try now
				return pos.withAxis(axis, xyz - positiveEdgeCrumb);
			} else if (negativeEdgeCrumb < 0.5 && negativeEdgeCrumb > 0 && !doll.getWorld().getBlockCollisions(doll, dollStanding.getBoxAt(pos.withAxis(axis, xyz + negativeEdgeCrumb))).iterator().hasNext()) {
				// yay! adjusting doll up a bit works, try now
				return pos.withAxis(axis, xyz + negativeEdgeCrumb);
			} else {
				// well. if there's gonna be collision, just drop
				return null;
			}
		}
	}
}
