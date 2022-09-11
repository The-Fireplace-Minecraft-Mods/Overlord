package dev.the_fireplace.overlord.item;

import dev.the_fireplace.overlord.entity.OwnedSkeletonEntity;
import dev.the_fireplace.overlord.entity.ai.aiconfig.AISettings;
import dev.the_fireplace.overlord.entity.ai.aiconfig.movement.PositionSetting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;

public class OwnedSkeletonSpawnEggItem extends SpawnEggItem
{
    private static final String SKELETON_DATA_TAG = "skeletonData";

    public OwnedSkeletonSpawnEggItem(EntityType<? extends Mob> type, int primaryColor, int secondaryColor, Properties settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        ItemStack itemStack = context.getItemInHand();
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockState blockState = world.getBlockState(blockPos);

        //Unlike super, we don't want to put this entity in a mob spawner

        BlockPos blockPos3;
        if (blockState.getCollisionShape(world, blockPos).isEmpty()) {
            blockPos3 = blockPos;
        } else {
            blockPos3 = blockPos.relative(direction);
        }

        EntityType<?> spawningEntityType = this.getType(itemStack.getTag());
        Entity spawnedEntity = spawningEntityType.spawn(
            (ServerLevel) world,
            itemStack,
            context.getPlayer(),
            blockPos3,
            MobSpawnType.SPAWN_EGG,
            true,
            !Objects.equals(blockPos, blockPos3) && direction == Direction.UP
        );
        if (spawnedEntity != null) {
            if (spawnedEntity instanceof OwnedSkeletonEntity) {
                CompoundTag savedSkeletonData = itemStack.getTagElement(SKELETON_DATA_TAG);
                if (savedSkeletonData != null) {
                    ((OwnedSkeletonEntity) spawnedEntity).readAdditionalSaveData(savedSkeletonData);
                }
                if (context.getPlayer() != null) {
                    ((OwnedSkeletonEntity) spawnedEntity).setOwnerUUID(context.getPlayer().getUUID());
                }
                setHomeToCurrentPosition((OwnedSkeletonEntity) spawnedEntity);
            }
            itemStack.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    private void setHomeToCurrentPosition(OwnedSkeletonEntity spawnedEntity) {
        AISettings aiSettings = spawnedEntity.getAISettings();
        aiSettings.getMovement().setHome(new PositionSetting((int) spawnedEntity.position().x(), (int) spawnedEntity.position().y(), (int) spawnedEntity.position().z()));
        spawnedEntity.updateAISettings(aiSettings.toTag());
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        HitResult hitResult = getPlayerPOVHitResult(world, user, ClipContext.Fluid.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        } else if (world.isClientSide) {
            return InteractionResultHolder.success(itemStack);
        }
        BlockHitResult blockHitResult = (BlockHitResult) hitResult;
        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!(world.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(itemStack);
        } else if (world.mayInteract(user, blockPos) && user.mayUseItemAt(blockPos, blockHitResult.getDirection(), itemStack)) {
            EntityType<?> entityType = this.getType(itemStack.getTag());
            Entity spawnedEntity = entityType.spawn((ServerLevel) world, itemStack, user, blockPos, MobSpawnType.SPAWN_EGG, false, false);
            if (spawnedEntity == null) {
                return InteractionResultHolder.pass(itemStack);
            } else {
                setHomeToCurrentPosition((OwnedSkeletonEntity) spawnedEntity);
                if (!user.abilities.instabuild) {
                    itemStack.shrink(1);
                }

                user.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResultHolder.success(itemStack);
            }
        } else {
            return InteractionResultHolder.fail(itemStack);
        }
    }

    @Override
    public boolean interactEnemy(ItemStack stack, Player user, LivingEntity entity, InteractionHand hand) {
        if (user.isShiftKeyDown() && entity instanceof OwnedSkeletonEntity && !user.level.isClientSide()) {
            entity.addAdditionalSaveData(stack.getOrCreateTagElement(SKELETON_DATA_TAG));
            if (entity.getCustomName() != null) {
                stack.setHoverName(entity.getCustomName());
            }
            user.setItemInHand(hand, stack);
            return true;
        }
        return super.interactEnemy(stack, user, entity, hand);
    }
}
