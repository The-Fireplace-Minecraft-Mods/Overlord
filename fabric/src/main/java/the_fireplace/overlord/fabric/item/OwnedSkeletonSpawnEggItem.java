package the_fireplace.overlord.fabric.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.SpawnEggItem;
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
import the_fireplace.overlord.fabric.entity.OwnedSkeletonEntity;

import java.util.Objects;

public class OwnedSkeletonSpawnEggItem extends SpawnEggItem {
    public OwnedSkeletonSpawnEggItem(EntityType<?> type, int primaryColor, int secondaryColor, Settings settings) {
        super(type, primaryColor, secondaryColor, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!world.isClient) {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            //Unlike super, we don't want to put this entity in a mob spawner

            BlockPos blockPos3;
            if (blockState.getCollisionShape(world, blockPos).isEmpty())
                blockPos3 = blockPos;
            else
                blockPos3 = blockPos.offset(direction);

            EntityType<?> entityType2 = this.getEntityType(itemStack.getTag());
            Entity e = entityType2.spawnFromItemStack(world, itemStack, context.getPlayer(), blockPos3, SpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, blockPos3) && direction == Direction.UP);
            if (e != null) {
                itemStack.decrement(1);
                if(e instanceof OwnedSkeletonEntity && context.getPlayer() != null)
                    ((OwnedSkeletonEntity) e).setOwner(context.getPlayer().getUuid());
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        HitResult hitResult = rayTrace(world, user, RayTraceContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            return TypedActionResult.pass(itemStack);
        } else if (world.isClient) {
            return TypedActionResult.success(itemStack);
        } else {
            BlockHitResult blockHitResult = (BlockHitResult)hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(itemStack);
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, blockHitResult.getSide(), itemStack)) {
                EntityType<?> entityType = this.getEntityType(itemStack.getTag());
                if (entityType.spawnFromItemStack(world, itemStack, user, blockPos, SpawnType.SPAWN_EGG, false, false) == null) {
                    return TypedActionResult.pass(itemStack);
                } else {
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
    }
}
