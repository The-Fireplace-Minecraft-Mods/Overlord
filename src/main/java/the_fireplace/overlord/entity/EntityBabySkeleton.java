package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class EntityBabySkeleton extends EntityArmyMember {

    private static final DataParameter<String> SKINSUIT_NAME = EntityDataManager.createKey(EntityBabySkeleton.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> HAS_SKINSUIT = EntityDataManager.createKey(EntityBabySkeleton.class, DataSerializers.BOOLEAN);

    public final InventoryBasic equipInventory;

    public EntityBabySkeleton(World world){
        this(world, null);
    }

    public EntityBabySkeleton(World world, @Nullable UUID owner){
        super(world, owner);
        this.equipInventory = new InventoryBasic("Equipment", false, 5){
            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack)
            {
                return index >= 4 || !stack.isEmpty() && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[index], null);
            }
        };
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.firstBaby)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.firstBaby);
                }
        }
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.firstSkeleton)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.firstSkeleton);
                    return;
                }
        }
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.secondSkeleton)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.secondSkeleton);
                }
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.255D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(25.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.5D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if(this.getOwner() != null){
            if(this.getOwner().equals(player)){
                if(!player.isSneaking()) {
                    FMLNetworkHandler.openGui(player, Overlord.instance, hashCode(), world, (int) this.posX, (int) this.posY, (int) this.posZ);
                    return true;
                } else {
                    if (!world.isRemote){
                        ItemStack stack = player.getHeldItem(hand);
                        if (!stack.isEmpty()) {
                            if (stack.getItem() == Overlord.skinsuit && !this.hasSkinsuit()) {
                                applySkinsuit(stack);
                                if (!player.isCreative())
                                    stack.shrink(1);
                            } else if (stack.getItem() == Items.SHEARS && this.hasSkinsuit()) {
                                if (!player.isCreative()) {
                                    stack.damageItem(1, player);
                                    entityDropItem(new ItemStack(Overlord.skinsuit).setStackDisplayName(getSkinsuitName()), 0.1F);
                                }
                                if (ConfigValues.SKINSUITNAMETAGS && this.hasCustomName()) {
                                    if (this.getCustomNameTag().equals(getSkinsuitName()))
                                        this.setCustomNameTag("");
                                }
                                this.dataManager.set(HAS_SKINSUIT, Boolean.valueOf(false));
                                this.dataManager.set(SKINSUIT_NAME, String.valueOf(""));
                            } else if (stack.getItem() == Overlord.baby_spawner) {
                                NBTTagCompound compound = new NBTTagCompound();
                                this.writeEntityToNBT(compound);
                                stack.setTagCompound(compound);
                            }
                        }
                    }
                }
            }
        }
        return super.processInteract(player, hand);
    }

    public void applySkinsuit(ItemStack stack){
        this.dataManager.set(HAS_SKINSUIT, Boolean.valueOf(true));
        if(stack.hasDisplayName()) {
            this.dataManager.set(SKINSUIT_NAME, String.valueOf(stack.getDisplayName()));
            if(ConfigValues.SKINSUITNAMETAGS && !this.hasCustomName())
                setCustomNameTag(stack.getDisplayName());
        }else
            this.dataManager.set(SKINSUIT_NAME, String.valueOf(""));
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(HAS_SKINSUIT, Boolean.valueOf(false));
        this.dataManager.register(SKINSUIT_NAME, String.valueOf(""));
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        SoundEvent soundevent = SoundEvents.ENTITY_SKELETON_STEP;
        this.playSound(soundevent, 0.15F, 1.0F);
    }

    @Override
    @Nonnull
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void onLivingUpdate()
    {
        if(!this.world.isRemote) {
            if (this.world.isDaytime()) {
                float f = this.getBrightness(1.0F);
                BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ);

                if(!hasSkinsuit())
                if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos)) {
                    boolean flag = true;
                    ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                    if (!itemstack.isEmpty()) {
                        if(ConfigValues.HELMETDAMAGE)
                            if (itemstack.isItemStackDamageable()) {
                                itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));

                                if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
                                    this.renderBrokenItemStack(itemstack);
                                    this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                                }
                            }

                        flag = false;
                    }

                    if (flag) {
                        this.setFire(6);
                    }
                }
            }
            //Equipment Achievements
            if(!getHeldItemMainhand().isEmpty()){
                if(getOwner() != null){
                    if(getOwner() instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.armedSkeleton)) {
                            ((EntityPlayer) getOwner()).addStat(Overlord.armedSkeleton);
                        }
                }
            }
            if(hasSkinsuit()){
                if(getOwner() != null){
                    if(getOwner() instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.sally)) {
                            ((EntityPlayer) getOwner()).addStat(Overlord.sally);
                        }
                }
            }
        }

        this.setSize(0.3F, 0.995F);

        float f = this.getBrightness(1.0F);

        if (f > 0.5F && !this.hasSkinsuit())
        {
            this.entityAge += 1;
        }
        super.onLivingUpdate();
    }

    @Override
    public boolean isChild(){
        return true;
    }

    @Override
    public void onDeath(@Nonnull DamageSource cause)
    {
        super.onDeath(cause);

        if (cause.getEntity() instanceof EntityCreeper && ((EntityCreeper)cause.getEntity()).getPowered() && ((EntityCreeper)cause.getEntity()).isAIEnabled())
        {
            ((EntityCreeper)cause.getEntity()).incrementDroppedSkulls();
            this.entityDropItem(new ItemStack(Items.SKULL), 0.0F);
        }

        if(!this.world.isRemote){
            for(int i=0;i<equipInventory.getSizeInventory();i++){
                if(!equipInventory.getStackInSlot(i).isEmpty()){
                    EntityItem entityitem = new EntityItem(world, posX, posY, posZ, equipInventory.getStackInSlot(i));
                    entityitem.setDefaultPickupDelay();
                    world.spawnEntity(entityitem);
                }
            }
            if(hasSkinsuit()){
                ItemStack stack = new ItemStack(Overlord.skinsuit).setStackDisplayName(getSkinsuitName());
                EntityItem entityitem = new EntityItem(world, posX, posY, posZ, stack);
                entityitem.setDefaultPickupDelay();
                world.spawnEntity(entityitem);
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);
        if(compound.hasKey("SkinsuitName")){
            String s = compound.getString("SkinsuitName");
            this.dataManager.set(SKINSUIT_NAME, s);
        }
        if(compound.hasKey("HasSkinsuit")){
            boolean b = compound.getBoolean("HasSkinsuit");
            this.dataManager.set(HAS_SKINSUIT, b);
        }
        NBTTagList armorInv = (NBTTagList) compound.getTag("SkeletonEquipment");
        if (armorInv != null) {
            for (int i = 0; i < armorInv.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) armorInv.get(i);
                int slot = item.getByte("SlotSkeletonEquipment");
                if (slot >= 0 && slot < equipInventory.getSizeInventory()) {
                    equipInventory.setInventorySlotContents(slot, new ItemStack(item));
                }
            }
        } else {
            Overlord.logWarn("List was null when reading Baby Skeleton's Equipment");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("HasSkinsuit", this.dataManager.get(HAS_SKINSUIT));
        compound.setString("SkinsuitName", this.dataManager.get(SKINSUIT_NAME));

        NBTTagList armorInv = new NBTTagList();
        for (int i = 0; i < equipInventory.getSizeInventory(); i++) {
            ItemStack is = equipInventory.getStackInSlot(i);
            if (!is.isEmpty()) {
                NBTTagCompound item = new NBTTagCompound();

                item.setByte("SlotSkeletonEquipment", (byte) i);
                is.writeToNBT(item);

                armorInv.appendTag(item);
            }
        }
        compound.setTag("SkeletonEquipment", armorInv);
    }

    @Override
    public float getEyeHeight()
    {
        return 0.93F;
    }

    @Override
    @Nonnull
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return slotIn == EntityEquipmentSlot.MAINHAND ? equipInventory.getStackInSlot(4) : (slotIn == EntityEquipmentSlot.OFFHAND ? ItemStack.EMPTY : (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.equipInventory.getStackInSlot(slotIn.getIndex()) : ItemStack.EMPTY));
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nonnull ItemStack stack)
    {
        if (slotIn == EntityEquipmentSlot.MAINHAND)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(4, stack);
        }
        else if (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(slotIn.getIndex(), stack);
        }
    }

    @Override
    @Nonnull
    public Iterable<ItemStack> getHeldEquipment()
    {
        return Lists.newArrayList(this.getHeldItemMainhand());
    }

    @Override
    @Nonnull
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return Arrays.asList(equipInventory.getStackInSlot(0), equipInventory.getStackInSlot(1), equipInventory.getStackInSlot(2), equipInventory.getStackInSlot(3));
    }

    @Override
    @Nonnull
    public ItemStack getHeldItemMainhand()
    {
        if(equipInventory == null)
            return ItemStack.EMPTY;
        return equipInventory.getStackInSlot(4);
    }

    @Override
    @Nonnull
    public ItemStack getHeldItemOffhand()
    {
        return ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack getHeldItem(EnumHand hand)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            return getHeldItemMainhand();
        }
        else if (hand == EnumHand.OFF_HAND)
        {
            return ItemStack.EMPTY;
        }
        else
        {
            throw new IllegalArgumentException("Invalid hand: " + hand);
        }
    }

    @Override
    public void setHeldItem(EnumHand hand, @Nonnull ItemStack stack)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
        }
        else
        {
            if (hand != EnumHand.OFF_HAND)
            {
                throw new IllegalArgumentException("Invalid hand: " + hand);
            }
        }
    }

    public boolean hasSkinsuit(){
        return this.dataManager.get(HAS_SKINSUIT);
    }

    public String getSkinsuitName(){
        return this.dataManager.get(SKINSUIT_NAME);
    }

    @Override
    public float getBlockPathWeight(BlockPos pos)
    {
        if(!this.hasSkinsuit())
            return 0.5F - this.world.getLightBrightness(pos);
        else
            return super.getBlockPathWeight(pos);
    }

    @Override
    public boolean shouldMobAttack(@Nonnull EntityLiving mob) {
        return this.hasSkinsuit() && (getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() || getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() != Items.SKULL || getItemStackFromSlot(EntityEquipmentSlot.HEAD).getMetadata() == 3);
    }
}
