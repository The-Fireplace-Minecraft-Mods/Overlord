package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;

import java.util.Objects;

public class OwnedSkeletonSpawnEggItem extends SpawnEggItem {
    private static final String SKELETON_DATA_TAG = "skeletonData";
    public OwnedSkeletonSpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) {
            return ActionResult.SUCCESS;
        }
        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        Direction direction = context.getSide();
        BlockState blockState = world.getBlockState(blockPos);

        //Unlike super, we don't want to put this entity in a mob spawner

        BlockPos blockPos3;
        if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
            blockPos3 = blockPos;
        } else {
            blockPos3 = blockPos.offset(direction);
        }

        EntityType<?> spawningEntityType = this.getEntityType(itemStack.getTag());
        Entity spawnedEntity = spawningEntityType.spawnFromItemStack(
            world,
            itemStack,
            context.getPlayer(),
            blockPos3,
            SpawnType.SPAWN_EGG,
            true,
            !Objects.equals(blockPos, blockPos3) && direction == Direction.UP
        );
        if (spawnedEntity != null) {
            if (spawnedEntity instanceof OwnedSkeletonEntity) {
                CompoundTag savedSkeletonData = itemStack.getSubTag(SKELETON_DATA_TAG);
                if (savedSkeletonData != null) {
                    ((OwnedSkeletonEntity) spawnedEntity).readCustomDataFromTag(savedSkeletonData);
                }
                if (context.getPlayer() != null) {
                    ((OwnedSkeletonEntity) spawnedEntity).setOwnerUuid(context.getPlayer().getUuid());
                }
                setHomeToCurrentPosition((OwnedSkeletonEntity) spawnedEntity);
            }
            itemStack.decrement(1);
        }
        return ActionResult.SUCCESS;
    }

    private void setHomeToCurrentPosition(OwnedSkeletonEntity spawnedEntity) {
        AISettings aiSettings = spawnedEntity.getAISettings();
        aiSettings.getMovement().setHome(new PositionSetting(spawnedEntity.getBlockPos().getX(), spawnedEntity.getBlockPos().getY(), spawnedEntity.getBlockPos().getZ()));
        spawnedEntity.updateAISettings(aiSettings.toTag());
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else if (world.isClient) {
            return TypedActionResult.success(itemStack);
        }
        BlockHitResult blockHitResult = (BlockHitResult)hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
            return TypedActionResult.pass(itemStack);
        } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
            EntityType<?> entityType = this.getEntityType(itemStack.getTag());
            Entity spawnedEntity = entityType.spawnFromItemStack(world, itemStack, user, blockPos, SpawnType.SPAWN_EGG, false, false);
            if (spawnedEntity == null) {
                return TypedActionResult.pass(itemStack);
            } else {
                setHomeToCurrentPosition((OwnedSkeletonEntity) spawnedEntity);
                if (!user.abilities.creativeMode) {
                    itemStack.decrement(1);
                }

                user.incrementStat(Stats.USED.getOrCreateStat(this));
                return TypedActionResult.success(itemStack);
            }
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    @Override
    public boolean useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (user.isSneaking() && entity instanceof OwnedSkeletonEntity && !user.world.isClient()) {
            entity.writeCustomDataToTag(stack.getOrCreateSubTag(SKELETON_DATA_TAG));
            if (entity.getCustomName() != null) {
                stack.setCustomName(entity.getCustomName());
            }
            user.setStackInHand(hand, stack);
            return true;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }
}
