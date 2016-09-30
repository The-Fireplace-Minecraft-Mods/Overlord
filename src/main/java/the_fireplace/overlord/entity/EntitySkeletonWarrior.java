package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.ai.EntityAIFollowMaster;
import the_fireplace.overlord.entity.ai.EntityAINearestNonTeamTarget;
import the_fireplace.overlord.entity.ai.EntityAIWanderBase;
import the_fireplace.overlord.entity.ai.EntityAIWarriorBow;
import the_fireplace.overlord.tools.CustomDataSerializers;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class EntitySkeletonWarrior extends EntityMob implements IEntityOwnable {

    private static final DataParameter<UUID> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntitySkeletonWarrior.class, CustomDataSerializers.UNIQUE_ID);
    private static final DataParameter<Integer> SKELETON_POWER_LEVEL = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.BOOLEAN);
    /**The attack mode. 0 is passive, 1 is defensive, 2 is aggressive*/
    private static final DataParameter<Byte> ATTACK_MODE = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.BYTE);
    /**The movement mode. 0 is stationed, 1 is follower, 2 is base*/
    private static final DataParameter<Byte> MOVEMENT_MODE = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.BYTE);
    private static final DataParameter<Integer> MILK_LEVEL = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.VARINT);
    private EntityAIWarriorBow aiArrowAttack = null;
    private EntityAIAttackMelee aiAttackOnCollide = null;

    public final InventoryBasic inventory;
    public final InventoryBasic equipInventory;

    public EntitySkeletonWarrior(World world){
        this(world, null);
    }

    public EntitySkeletonWarrior(World world, @Nullable UUID owner){
        super(world);
        if(owner != null)
            this.setOwnerId(owner);
        else
            this.setOwnerId(UUID.fromString("0b1ec5ad-cb2a-43b7-995d-889320eb2e5b"));
        this.inventory = new InventoryBasic("Items", false, 9);
        this.equipInventory = new InventoryBasic("Equipment", false, 6){
            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack)
            {
                return index >= 4 || stack != null && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[index], null);
            }
        };
        ((PathNavigateGround)this.getNavigator()).setBreakDoors(true);
        this.setCanPickUpLoot(true);
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
    protected void initEntityAI()
    {
        this.tasks.taskEntries.clear();//Clear first so this can be called when the AI Modes change
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        addMovementTasks();
        if(this.dataManager.get(ATTACK_MODE) != 0)
            addAttackTasks();
        addTargetTasks();
    }

    public void addMovementTasks(){
        switch(dataManager.get(MOVEMENT_MODE)) {
            case 1:
                this.tasks.addTask(4, new EntityAIOpenDoor(this, false));
                this.tasks.addTask(6, new EntityAIFollowMaster(this, 1.0D, 10.0F, 2.0F));
            case 0:
                this.setHomePosAndDistance(new BlockPos(this.posX, this.posY, this.posZ), -1);
                break;
            case 2:
            default:
                this.setHomePosAndDistance(new BlockPos(this.posX, this.posY, this.posZ), 20);
                this.tasks.addTask(2, new EntityAIRestrictSun(this));
                this.tasks.addTask(3, new EntityAIFleeSun(this, 1.0D));
                this.tasks.addTask(4, new EntityAIOpenDoor(this, false));
                this.tasks.addTask(7, new EntityAIWanderBase(this, 1.0D));
        }
    }

    public void addAttackTasks(){
        if(aiAttackOnCollide == null){
            aiAttackOnCollide = new EntityAIAttackMelee(this, 1.2D, false)
            {
                @Override
                public void resetTask()
                {
                    super.resetTask();
                    EntitySkeletonWarrior.this.setSwingingArms(false);
                }

                @Override
                public void startExecuting()
                {
                    super.startExecuting();
                    EntitySkeletonWarrior.this.setSwingingArms(true);
                }
            };
        }
        if(aiArrowAttack == null){
            aiArrowAttack = new EntityAIWarriorBow(this, 0.8D, 20, 15.0F);
        }
        if(this.getHeldItemMainhand() != null)
            if(this.getHeldItemMainhand().getItem() instanceof ItemBow){
               this.tasks.addTask(5, aiArrowAttack);
                return;
            }
        this.tasks.addTask(5, aiAttackOnCollide);
    }

    public void addTargetTasks(){
        switch(dataManager.get(ATTACK_MODE)) {
            case 2:
                this.targetTasks.addTask(2, new EntityAINearestNonTeamTarget(this, EntityPlayer.class, true));
            case 1:
                this.targetTasks.addTask(3, new EntityAINearestNonTeamTarget(this, EntityMob.class, true));
                this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
                break;
            case 0:
            default:
                break;
        }
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D);
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, @Nullable ItemStack stack)
    {
        if(this.getOwner() != null){
            if(this.getOwner().equals(player)){
                FMLNetworkHandler.openGui(player, Overlord.instance, hashCode(), worldObj, (int)this.posX, (int)this.posY, (int)this.posZ);
            }
        }
        return super.processInteract(player, hand, stack);
    }

    @Override
    protected void entityInit()
    {
        super.entityInit();
        this.dataManager.register(OWNER_UNIQUE_ID, UUID.fromString("0b1ec5ad-cb2a-43b7-995d-889320eb2e5b"));
        this.dataManager.register(SKELETON_POWER_LEVEL, Integer.valueOf(1));
        this.dataManager.register(MILK_LEVEL, Integer.valueOf(0));
        this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
        this.dataManager.register(ATTACK_MODE, Byte.valueOf((byte)1));
        this.dataManager.register(MOVEMENT_MODE, Byte.valueOf((byte)1));
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound()
    {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        SoundEvent soundevent = SoundEvents.ENTITY_SKELETON_STEP;
        this.playSound(soundevent, (float)(0.15F*Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))), 1.0F);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Override
    public void onLivingUpdate()
    {
        if(!this.worldObj.isRemote) {
            for(int i=0;i<this.inventory.getSizeInventory();i++){
                if(inventory.getStackInSlot(i) != null)
                    if(inventory.getStackInSlot(i).getItem() == Items.MILK_BUCKET){
                        this.increaseMilkLevel();
                        inventory.setInventorySlotContents(i, null);
                        inventory.addItem(new ItemStack(Items.BUCKET));
                    }
            }
            checkLevelUp();

            if (this.worldObj.isDaytime()) {
                float f = this.getBrightness(1.0F);
                BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ);

                if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.worldObj.canSeeSky(blockpos)) {
                    boolean flag = true;
                    ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);

                    if (itemstack != null) {
                        if(ConfigValues.HELMETDAMAGE)
                            if (itemstack.isItemStackDamageable()) {
                                itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));

                                if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
                                    this.renderBrokenItemStack(itemstack);
                                    this.setItemStackToSlot(EntityEquipmentSlot.HEAD, null);
                                }
                            }

                        flag = false;
                    }

                    if (flag) {
                        this.setFire(6);
                    }
                }
            }

            for (EntityItem entityitem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)))
            {
                if (!entityitem.isDead && entityitem.getEntityItem() != null && !entityitem.cannotPickup())
                {
                    ItemStack stack = equipInventory.addItem(entityitem.getEntityItem());
                    if(stack != null) {
                        ItemStack stack2 = inventory.addItem(stack);
                        if(stack2 != null){
                            entityitem.getEntityItem().stackSize = stack2.stackSize;
                        }else{
                            entityitem.setDead();
                        }
                    }else{
                        entityitem.setDead();
                    }
                }
            }
            //Equipment Achievements
            if(getHeldItemMainhand() != null){
                if(getOwner() != null){
                    if(getOwner() instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.armedSkeleton)) {
                            ((EntityPlayer) getOwner()).addStat(Overlord.armedSkeleton);
                        }
                }
            }
            if(getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null && getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null && getItemStackFromSlot(EntityEquipmentSlot.FEET) != null){
                if(getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.LEATHER_HELMET && getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.LEATHER_CHESTPLATE && getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.LEATHER_LEGGINGS && getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.LEATHER_BOOTS){
                    if(getOwner() != null){
                        if(getOwner() instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.sally)) {
                                ((EntityPlayer) getOwner()).addStat(Overlord.sally);
                            }
                    }
                }
                if(getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.CHAINMAIL_HELMET && getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.CHAINMAIL_CHESTPLATE && getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.CHAINMAIL_LEGGINGS && getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.CHAINMAIL_BOOTS){
                    if(getHeldItemOffhand() != null)
                        if(getHeldItemOffhand().getTagCompound() != null && getHeldItemOffhand().getItem() instanceof ItemShield)
                            if(getHeldItemOffhand().getTagCompound().equals(Overlord.shieldStack().getTagCompound()))
                    if(getOwner() != null){
                        if(getOwner() instanceof EntityPlayerMP)
                            if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.crusader)) {
                                ((EntityPlayer) getOwner()).addStat(Overlord.crusader);
                            }
                    }
                }
            }
        }

        this.setSize(0.6F, 1.99F);

        super.onLivingUpdate();
    }

    private void increaseMilkLevel(){
        int milk = dataManager.get(MILK_LEVEL);
        dataManager.set(MILK_LEVEL, ++milk);
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.firstMilk)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.firstMilk);
                    return;
                }
        }
        if((dataManager.get(SKELETON_POWER_LEVEL) == 8 && dataManager.get(MILK_LEVEL) >= 2) || dataManager.get(SKELETON_POWER_LEVEL) > 8)
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.milk256)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.milk256);
                    return;
                }
        }
        if((dataManager.get(SKELETON_POWER_LEVEL) == 12 && dataManager.get(MILK_LEVEL) >= 811) || dataManager.get(SKELETON_POWER_LEVEL) > 12)
        if(getOwner() != null){
            if(getOwner() instanceof EntityPlayerMP)
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.milk9001)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.milk9001);
                }
        }
    }

    public void checkLevelUp(){
        int level = dataManager.get(SKELETON_POWER_LEVEL);
        int milk = dataManager.get(MILK_LEVEL);
        if(milk >= Math.pow(2, level)){
            milk -= Math.pow(2, level);
            level++;
            dataManager.set(MILK_LEVEL, milk);
            dataManager.set(SKELETON_POWER_LEVEL, level);
            updateEntityAttributes();
            if(getOwner() != null){
                if(getOwner() instanceof EntityPlayerMP)
                    if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.firstLevel)) {
                        ((EntityPlayer) getOwner()).addStat(Overlord.firstLevel);
                    }
            }
        }
    }

    @Override
    public void onDeath(DamageSource cause)
    {
        super.onDeath(cause);

        if (cause.getEntity() instanceof EntityCreeper && ((EntityCreeper)cause.getEntity()).getPowered() && ((EntityCreeper)cause.getEntity()).isAIEnabled())
        {
            ((EntityCreeper)cause.getEntity()).incrementDroppedSkulls();
            this.entityDropItem(new ItemStack(Items.SKULL), 0.0F);
        }

        if (!this.worldObj.isRemote && this.worldObj.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof EntityPlayerMP)
        {
            this.getOwner().addChatMessage(this.getCombatTracker().getDeathMessage());
        }

        if(!this.worldObj.isRemote){
            for(int i=0;i<inventory.getSizeInventory();i++){
                if(inventory.getStackInSlot(i) != null){
                    EntityItem entityitem = new EntityItem(worldObj, posX, posY, posZ, inventory.getStackInSlot(i));
                    worldObj.spawnEntityInWorld(entityitem);
                }
            }
            for(int i=0;i<equipInventory.getSizeInventory();i++){
                if(equipInventory.getStackInSlot(i) != null){
                    EntityItem entityitem = new EntityItem(worldObj, posX, posY, posZ, equipInventory.getStackInSlot(i));
                    worldObj.spawnEntityInWorld(entityitem);
                }
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound)
    {
        super.readEntityFromNBT(compound);

        if (compound.hasKey("SkeletonPowerLevel"))
        {
            int i = compound.getInteger("SkeletonPowerLevel");
            this.dataManager.set(SKELETON_POWER_LEVEL, i);
        }
        if (compound.hasKey("SkeletonMilk"))
        {
            int i = compound.getInteger("SkeletonMilk");
            this.dataManager.set(MILK_LEVEL, i);
        }
        String s;
        if (compound.hasKey("OwnerUUID", 8))
        {
            s = compound.getString("OwnerUUID");
        }
        else
        {
            String s1 = compound.getString("Owner");
            s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
        }
        if (!s.isEmpty())
        {
            try
            {
                this.setOwnerId(UUID.fromString(s));
            }
            catch (Throwable var4)
            {
                var4.printStackTrace();
            }
        }
        if(compound.hasKey("AttackMode")){
            byte b = compound.getByte("AttackMode");
            this.dataManager.set(ATTACK_MODE, b);
        }
        if(compound.hasKey("MovementMode")){
            byte b = compound.getByte("MovementMode");
            this.dataManager.set(MOVEMENT_MODE, b);
        }
        NBTTagList mainInv = (NBTTagList) compound.getTag("SkeletonInventory");
        if (mainInv != null) {
            for (int i = 0; i < mainInv.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) mainInv.get(i);
                int slot = item.getByte("SlotSkeletonInventory");
                if (slot >= 0 && slot < inventory.getSizeInventory()) {
                    inventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
                }
            }
        } else {
            System.out.println("List was null when reading Skeleton Warrior's Inventory");
        }
        NBTTagList armorInv = (NBTTagList) compound.getTag("SkeletonEquipment");
        if (armorInv != null) {
            for (int i = 0; i < armorInv.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) armorInv.get(i);
                int slot = item.getByte("SlotSkeletonEquipment");
                if (slot >= 0 && slot < equipInventory.getSizeInventory()) {
                    equipInventory.setInventorySlotContents(slot, ItemStack.loadItemStackFromNBT(item));
                }
            }
        } else {
            System.out.println("List was null when reading Skeleton Warrior's Equipment");
        }
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player)
    {
        return this.isOwner(player);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("SkeletonPowerLevel", this.dataManager.get(SKELETON_POWER_LEVEL));
        updateEntityAttributes();
        compound.setInteger("SkeletonMilk", this.dataManager.get(MILK_LEVEL));
        if (this.getOwnerId() == null)
        {
            compound.setString("OwnerUUID", "0b1ec5ad-cb2a-43b7-995d-889320eb2e5b");
        }
        else
        {
            compound.setString("OwnerUUID", this.getOwnerId().toString());
        }
        compound.setByte("AttackMode", this.dataManager.get(ATTACK_MODE));
        compound.setByte("MovementMode", this.dataManager.get(MOVEMENT_MODE));

        NBTTagList mainInv = new NBTTagList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack is = inventory.getStackInSlot(i);
            if (is != null) {
                NBTTagCompound item = new NBTTagCompound();

                item.setByte("SlotSkeletonInventory", (byte) i);
                is.writeToNBT(item);

                mainInv.appendTag(item);
            }
        }
        compound.setTag("SkeletonInventory", mainInv);

        NBTTagList armorInv = new NBTTagList();
        for (int i = 0; i < equipInventory.getSizeInventory(); i++) {
            ItemStack is = equipInventory.getStackInSlot(i);
            if (is != null) {
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
        return 1.74F;
    }

    @SideOnly(Side.CLIENT)
    public boolean isSwingingArms()
    {
        return this.dataManager.get(SWINGING_ARMS).booleanValue();
    }

    public void setSwingingArms(boolean swingingArms)
    {
        this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
    }

    @Override
    public double getYOffset()
    {
        return -0.35D;
    }

    @NotNull
    @Override
    public UUID getOwnerId() {
        return this.dataManager.get(OWNER_UNIQUE_ID);
    }

    public void setOwnerId(@NotNull UUID ownerId)
    {
        this.dataManager.set(OWNER_UNIQUE_ID, ownerId);
    }

    @Nullable
    @Override
    public EntityLivingBase getOwner() {
        try
        {
            return this.worldObj.getPlayerEntityByUUID(getOwnerId());
        }
        catch (IllegalArgumentException var2)
        {
            return null;
        }
    }

    public boolean isOwner(EntityLivingBase entityIn)
    {
        return entityIn == this.getOwner();
    }

    @Override
    public Team getTeam()
    {
        EntityLivingBase entitylivingbase = this.getOwner();

        if (entitylivingbase != null)
        {
            return entitylivingbase.getTeam();
        }

        return super.getTeam();
    }

    /**
     * Returns whether this Entity is on the same team as the given Entity.
     */
    @Override
    public boolean isOnSameTeam(Entity entityIn)
    {
        EntityLivingBase entitylivingbase = this.getOwner();

        if (entityIn == entitylivingbase)
        {
            return true;
        }

        if(entitylivingbase instanceof EntitySkeletonWarrior){
            return ((EntitySkeletonWarrior) entitylivingbase).getOwnerId() == this.getOwnerId();
        }

        if (entitylivingbase != null)
        {
            return entitylivingbase.isOnSameTeam(entityIn);
        }

        return super.isOnSameTeam(entityIn);
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float p_82196_2_)
    {
        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.worldObj, this);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entitytippedarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        entitytippedarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.worldObj.getDifficulty().getDifficultyId() * 4));
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
        DifficultyInstance difficultyinstance = this.worldObj.getDifficultyForLocation(new BlockPos(this));
        entitytippedarrow.setDamage((double)(p_82196_2_ * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.worldObj.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            entitytippedarrow.setDamage(entitytippedarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entitytippedarrow.setKnockbackStrength(j);
        }

        boolean flag = this.isBurning() && difficultyinstance.func_190083_c() && this.rand.nextBoolean();
        flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0;

        if (flag)
        {
            entitytippedarrow.setFire(100);
        }

        ItemStack itemstack = this.getHeldItem(EnumHand.OFF_HAND);

        if (itemstack != null && itemstack.getItem() == Items.TIPPED_ARROW)
        {
            entitytippedarrow.setPotionEffect(itemstack);
        }

        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.worldObj.spawnEntityInWorld(entitytippedarrow);
    }

    @Override
    @Nullable
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return slotIn == EntityEquipmentSlot.MAINHAND ? equipInventory.getStackInSlot(4) : (slotIn == EntityEquipmentSlot.OFFHAND ? equipInventory.getStackInSlot(5) : (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.equipInventory.getStackInSlot(slotIn.getIndex()) : null));
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nullable ItemStack stack)
    {
        if (slotIn == EntityEquipmentSlot.MAINHAND)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(4, stack);
        }
        else if (slotIn == EntityEquipmentSlot.OFFHAND)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(5, stack);
        }
        else if (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(slotIn.getIndex(), stack);
        }
    }

    @Override
    public Iterable<ItemStack> getHeldEquipment()
    {
        return Lists.newArrayList(this.getHeldItemMainhand(), this.getHeldItemOffhand());
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return Arrays.asList(equipInventory.getStackInSlot(0), equipInventory.getStackInSlot(1), equipInventory.getStackInSlot(2), equipInventory.getStackInSlot(3));
    }

    @Override
    @Nullable
    public ItemStack getHeldItemMainhand()
    {
        if(equipInventory == null)
            return null;
        return equipInventory.getStackInSlot(4);
    }

    @Override
    @Nullable
    public ItemStack getHeldItemOffhand()
    {
        return equipInventory.getStackInSlot(5);
    }

    @Override
    @Nullable
    public ItemStack getHeldItem(EnumHand hand)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            return getHeldItemMainhand();
        }
        else if (hand == EnumHand.OFF_HAND)
        {
            return getHeldItemOffhand();
        }
        else
        {
            throw new IllegalArgumentException("Invalid hand " + hand);
        }
    }

    @Override
    public void setHeldItem(EnumHand hand, @Nullable ItemStack stack)
    {
        if (hand == EnumHand.MAIN_HAND)
        {
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
        }
        else
        {
            if (hand != EnumHand.OFF_HAND)
            {
                throw new IllegalArgumentException("Invalid hand " + hand);
            }

            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
        }
    }

    public void cycleAttackMode(){
        byte b = getAttackMode();
        if(b < 2){
            byte b1 = ++b;
            dataManager.set(ATTACK_MODE, b1);
            initEntityAI();
        }else{
            dataManager.set(ATTACK_MODE, (byte)0);
            initEntityAI();
        }
    }

    public void setAttackMode(byte b){
        dataManager.set(ATTACK_MODE, b);
        initEntityAI();
    }

    /**
     * Gets the attack mode of the skeleton
     * @return 0 for passive, 1 for defensive, 2 for aggressive
     */
    public byte getAttackMode(){
        return dataManager.get(ATTACK_MODE);
    }

    public void cycleMovementMode(){
        byte b = getMovementMode();
        if(b < 2){
            byte b1 = ++b;
            dataManager.set(MOVEMENT_MODE, b1);
            initEntityAI();
        }else{
            dataManager.set(MOVEMENT_MODE, (byte)0);
            initEntityAI();
        }
    }

    public void setMovementMode(byte b){
        dataManager.set(MOVEMENT_MODE, b);
        initEntityAI();
    }

    /**
     * Gets the movement mode of the skeleton
     * @return 0 for stationed, 1 for follower, 2 for base
     */
    public byte getMovementMode(){
        return dataManager.get(MOVEMENT_MODE);
    }

    public void updateEntityAttributes(){
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D+(4*Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/24));
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D+dataManager.get(SKELETON_POWER_LEVEL));
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/2));
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/4));
    }
}
