package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import the_fireplace.overlord.Overlord;
import the_fireplace.overlord.config.ConfigValues;
import the_fireplace.overlord.entity.ai.EntityAIWarriorBow;
import the_fireplace.overlord.network.PacketDispatcher;
import the_fireplace.overlord.network.packets.RequestAugmentMessage;
import the_fireplace.overlord.registry.AugmentRegistry;
import the_fireplace.overlord.tools.Augment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.UUID;

/**
 * @author The_Fireplace
 */
public class EntitySkeletonWarrior extends EntityArmyMember {

    private static final DataParameter<Integer> SKELETON_POWER_LEVEL = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TOTAL_MILK_LEVEL = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> XP = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.VARINT);
    private static final DataParameter<String> SKINSUIT_NAME = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> HAS_SKINSUIT = EntityDataManager.createKey(EntitySkeletonWarrior.class, DataSerializers.BOOLEAN);
    private EntityAIWarriorBow aiArrowAttack = null;

    public final InventoryBasic inventory;
    public final InventoryBasic equipInventory;

    public boolean cachedClientAugment = false;
    public Augment clientAugment = null;

    public EntitySkeletonWarrior instance;

    public EntitySkeletonWarrior(World world){
        this(world, null);
    }

    public EntitySkeletonWarrior(World world, @Nullable UUID owner){
        super(world, owner);
        instance = this;
        this.inventory = new InventoryBasic("Items", false, 9);
        this.equipInventory = new InventoryBasic("Equipment", false, 7){
            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack)
            {
                return (index >= 4 && index < 6) || (index == 6 && AugmentRegistry.getAugment(stack) != null) || !stack.isEmpty() && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[index], null);
            }
            @Override
            public void setInventorySlotContents(int index, ItemStack stack){
                super.setInventorySlotContents(index, stack);
                if(world.isRemote && index == 6)
                    PacketDispatcher.sendToServer(new RequestAugmentMessage(instance));
            }
            @Override
            @Nonnull
            public ItemStack removeStackFromSlot(int index)
            {
                ItemStack stack = super.removeStackFromSlot(index);
                if(world.isRemote && index == 6)
                    PacketDispatcher.sendToServer(new RequestAugmentMessage(instance));
                return stack;
            }
            @Override
            @Nonnull
            public ItemStack decrStackSize(int index, int count)
            {
                ItemStack stack = super.decrStackSize(index, count);
                if(world.isRemote && index == 6)
                    PacketDispatcher.sendToServer(new RequestAugmentMessage(instance));
                return stack;
            }
        };
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

                @Override
                public void updateTask(){
                    if(!this.attacker.getHeldItemMainhand().isEmpty() && !this.attacker.getHeldItemOffhand().isEmpty() && this.attacker.getHeldItemMainhand().getItem() instanceof ItemBow && this.attacker.getHeldItemOffhand().getItem() instanceof ItemArrow) {
                        ((EntitySkeletonWarrior) this.attacker).initEntityAI();
                        return;
                    }
                    if(continueExecuting())
                        super.updateTask();
                }
            };
        }
        if(aiArrowAttack == null){
            aiArrowAttack = new EntityAIWarriorBow(this, 0.8D, 20, 30.0F);
        }
        if(!this.getHeldItemMainhand().isEmpty())
            if(this.getHeldItemMainhand().getItem() instanceof ItemBow){
                this.tasks.addTask(5, aiArrowAttack);
                return;
            }
        if(this.dataManager.get(MOVEMENT_MODE) > 0)
            this.tasks.addTask(5, aiAttackOnCollide);
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
    public boolean processInteract(EntityPlayer player, EnumHand hand)
    {
        if(this.getOwner() != null){
            if(this.getOwner().equals(player)){
                if(!player.isSneaking()) {
                    FMLNetworkHandler.openGui(player, Overlord.instance, hashCode(), world, (int) this.posX, (int) this.posY, (int) this.posZ);
                    return true;
                }else {
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
                            } else if (stack.getItem() == Overlord.warrior_spawner) {
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

    public void applySkinsuit(@Nonnull ItemStack stack){
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
        this.dataManager.register(SKELETON_POWER_LEVEL, Integer.valueOf(1));
        this.dataManager.register(TOTAL_MILK_LEVEL, Integer.valueOf(0));
        this.dataManager.register(HAS_SKINSUIT, Boolean.valueOf(false));
        this.dataManager.register(SKINSUIT_NAME, String.valueOf(""));
        this.dataManager.register(XP, Integer.valueOf(0));
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn)
    {
        SoundEvent soundevent = SoundEvents.ENTITY_SKELETON_STEP;
        this.playSound(soundevent, (float)(0.15F*Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))), 1.0F);
    }

    @Override
    public Augment getAugment(){
        if(equipInventory == null)
            return null;
        if(AugmentRegistry.getAugment(equipInventory.getStackInSlot(6)) == null && world.isRemote)
            return clientAugment;
        return AugmentRegistry.getAugment(equipInventory.getStackInSlot(6));
    }

    @Override
    @Nonnull
    public EnumCreatureAttribute getCreatureAttribute()
    {
        return EnumCreatureAttribute.UNDEAD;
    }

    ItemStack bucket = new ItemStack(Items.BUCKET);
    ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
    @Override
    public void onLivingUpdate()
    {
        if(!this.world.isRemote) {
            for(int i=0;i<this.inventory.getSizeInventory();i++){
                if(!inventory.getStackInSlot(i).isEmpty()) {
                    if (inventory.getStackInSlot(i).getItem() == Items.MILK_BUCKET) {
                        this.increaseMilkLevel(true);
                        if (inventory.getStackInSlot(i).getCount() > 1)
                            inventory.getStackInSlot(i).shrink(1);
                        else
                            inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        inventory.addItem(bucket);
                    }else if(inventory.getStackInSlot(i).getItem() == Overlord.milk_bottle){
                        this.increaseMilkLevel(true);
                        if(inventory.getStackInSlot(i).getCount() > 1)
                            inventory.getStackInSlot(i).shrink(1);
                        else
                            inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                        inventory.addItem(bottle);
                    }
                }
            }
            checkLevelUp();

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

            this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)).stream().filter(entityitem -> !entityitem.isDead && !entityitem.getEntityItem().isEmpty() && !entityitem.cannotPickup()).forEach(entityitem -> {
                ItemStack stack2 = inventory.addItem(entityitem.getEntityItem());
                if (!stack2.isEmpty()) {
                    if(stack2.getCount() != entityitem.getEntityItem().getCount())
                        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    entityitem.getEntityItem().setCount(stack2.getCount());
                    if(stack2.getItem() == Items.MILK_BUCKET || stack2.getItem() == Overlord.milk_bottle){
                        for(int i=0;i<inventory.getSizeInventory();i++){
                            if(!inventory.getStackInSlot(i).isEmpty() && (inventory.getStackInSlot(i).getItem() == Items.BUCKET || inventory.getStackInSlot(i).getItem() == Items.GLASS_BOTTLE)) {
                                entityDropItem(inventory.getStackInSlot(i), 0.1F);
                                inventory.setInventorySlotContents(i, ItemStack.EMPTY);
                            }
                        }
                    }
                } else {
                    entityitem.setDead();
                    playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
            });

            for(EntityXPOrb xp:world.getEntitiesWithinAABB(EntityXPOrb.class, this.getEntityBoundingBox().expand(8, 5, 8))){
                if (!xp.hasNoGravity())
                {
                    xp.motionY -= 0.029999999329447746D;
                }

                if (xp.world.getBlockState(new BlockPos(this)).getMaterial() == Material.LAVA)
                {
                    xp.motionY = 0.20000000298023224D;
                    xp.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    xp.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
                    xp.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
                }

                this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0D, this.posZ);
                double d1 = (this.posX - xp.posX) / 8.0D;
                double d2 = (this.posY + (double)this.getEyeHeight() / 2.0D - xp.posY) / 8.0D;
                double d3 = (this.posZ - xp.posZ) / 8.0D;
                double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
                double d5 = 1.0D - d4;

                if (d5 > 0.0D)
                {
                    d5 = d5 * d5;
                    xp.motionX += d1 / d4 * d5 * 0.1D;
                    xp.motionY += d2 / d4 * d5 * 0.1D;
                    xp.motionZ += d3 / d4 * d5 * 0.1D;
                }

                xp.move(MoverType.SELF, xp.motionX, xp.motionY, xp.motionZ);
                float f = 0.98F;

                if (this.onGround)
                {
                    f = xp.world.getBlockState(new BlockPos(MathHelper.floor(xp.posX), MathHelper.floor(xp.getEntityBoundingBox().minY) - 1, MathHelper.floor(xp.posZ))).getBlock().slipperiness * 0.98F;
                }

                xp.motionX *= (double)f;
                xp.motionY *= 0.9800000190734863D;
                xp.motionZ *= (double)f;

                if (xp.onGround)
                {
                    xp.motionY *= -0.8999999761581421D;
                }
            }
            world.getEntitiesWithinAABB(EntityXPOrb.class, this.getEntityBoundingBox()).stream().filter(xp -> xp.delayBeforeCanPickup <= 0).forEach(xp -> {
                this.addXP(xp.getXpValue());
                xp.setDead();
            });
            //Bow stuffs
            if(!getHeldItemMainhand().isEmpty()){
                if(getHeldItemMainhand().getItem() instanceof ItemBow)
                    if((!getHeldItemOffhand().isEmpty() && !(getHeldItemOffhand().getItem() instanceof ItemArrow)) || getHeldItemOffhand().isEmpty()){
                        boolean swapWeapon=true;
                        for(int i=0;i<inventory.getSizeInventory();i++){
                            if(!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).getItem() instanceof ItemArrow){
                                ItemStack offhand = ItemStack.EMPTY;
                                if(!getHeldItemOffhand().isEmpty())
                                    offhand=getHeldItemOffhand().copy();
                                ItemStack arrows = inventory.getStackInSlot(i).copy();
                                setHeldItem(EnumHand.OFF_HAND, arrows);
                                inventory.setInventorySlotContents(i, offhand);
                                swapWeapon=false;
                                break;
                            }
                        }
                        if(swapWeapon)
                        for(int i=0;i<inventory.getSizeInventory();i++){
                            if(!inventory.getStackInSlot(i).isEmpty() && inventory.getStackInSlot(i).getItem() instanceof ItemSword){
                                ItemStack clone = inventory.getStackInSlot(i).copy();
                                inventory.setInventorySlotContents(i, getHeldItemMainhand());
                                setHeldItem(EnumHand.MAIN_HAND, clone);
                                break;
                            }
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
            if(!getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty() && !getItemStackFromSlot(EntityEquipmentSlot.CHEST).isEmpty() && !getItemStackFromSlot(EntityEquipmentSlot.LEGS).isEmpty() && !getItemStackFromSlot(EntityEquipmentSlot.FEET).isEmpty()){
                if(getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.CHAINMAIL_HELMET && getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.CHAINMAIL_CHESTPLATE && getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.CHAINMAIL_LEGGINGS && getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.CHAINMAIL_BOOTS){
                    if(!getHeldItemOffhand().isEmpty())
                        if(getHeldItemOffhand().getTagCompound() != null && getHeldItemOffhand().getItem() instanceof ItemShield)
                            if(getHeldItemOffhand().getTagCompound().equals(Overlord.crusaderShield().getTagCompound()))
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

        float f = this.getBrightness(1.0F);

        if (f > 0.5F && !this.hasSkinsuit())
        {
            this.entityAge += 1;
        }
        super.onLivingUpdate();
    }

    public void addXP(int amount){
        int xp = amount + dataManager.get(XP);
        dataManager.set(XP, Integer.valueOf(xp));
    }

    public void increaseMilkLevel(boolean addXp){
        int milk = getTotalMilkConsumed();
        dataManager.set(TOTAL_MILK_LEVEL, ++milk);
        if(addXp) {
            int xp = getXP();
            dataManager.set(XP, ++xp);
        }
        if(getOwner() != null)
            if(getOwner() instanceof EntityPlayerMP)
                ((EntityPlayer) getOwner()).addStat(Overlord.firstMilk);
        if(getTotalMilkConsumed() >= 256)
            if(getOwner() != null)
                ((EntityPlayer) getOwner()).addStat(Overlord.milk256);
        if(getTotalMilkConsumed() > 9000)
            if(getOwner() != null)
                ((EntityPlayer) getOwner()).addStat(Overlord.milk9001);
    }

    public void checkLevelUp(){
        int level = dataManager.get(SKELETON_POWER_LEVEL);
        int xp = getXP();
        if(xp >= Math.pow(2, level)){
            xp -= Math.pow(2, level);
            level++;
            dataManager.set(XP, xp);
            dataManager.set(SKELETON_POWER_LEVEL, level);
            updateEntityAttributes();
            if(getHealth() < getMaxHealth())
                heal(getMaxHealth()-getHealth());
            if(getOwner() != null){
                if(getOwner() instanceof EntityPlayerMP)
                    if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.firstLevel)) {
                        ((EntityPlayer) getOwner()).addStat(Overlord.firstLevel);
                    }
            }
        }
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
            for(int i=0;i<inventory.getSizeInventory();i++){
                if(!inventory.getStackInSlot(i).isEmpty()){
                    EntityItem entityitem = new EntityItem(world, posX, posY, posZ, inventory.getStackInSlot(i));
                    entityitem.setDefaultPickupDelay();
                    world.spawnEntity(entityitem);
                }
            }
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

        if (compound.hasKey("SkeletonPowerLevel"))
        {
            int i = compound.getInteger("SkeletonPowerLevel");
            this.dataManager.set(SKELETON_POWER_LEVEL, i);
        }
        if (compound.hasKey("TotalMilkLevel"))
        {
            int i = compound.getInteger("TotalMilkLevel");
            this.dataManager.set(TOTAL_MILK_LEVEL, i);
        } else if (compound.hasKey("SkeletonMilk")) {
            int i = compound.getInteger("SkeletonMilk");
            this.dataManager.set(XP, i);
            for(int j=1;j<=this.dataManager.get(SKELETON_POWER_LEVEL);j++)
                i += Math.pow(2, j);
            this.dataManager.set(TOTAL_MILK_LEVEL, i);
            int l = compound.getInteger("SkeletonPowerLevel");
            l *= 2;
            this.dataManager.set(SKELETON_POWER_LEVEL, l);
        }

        if(compound.hasKey("XP")){
            int i = compound.getInteger("XP");
            this.dataManager.set(XP, i);
        }

        if(compound.hasKey("SkinsuitName")){
            String s = compound.getString("SkinsuitName");
            this.dataManager.set(SKINSUIT_NAME, s);
        }
        if(compound.hasKey("HasSkinsuit")){
            boolean b = compound.getBoolean("HasSkinsuit");
            this.dataManager.set(HAS_SKINSUIT, b);
        }
        NBTTagList mainInv = (NBTTagList) compound.getTag("SkeletonInventory");
        if (mainInv != null) {
            for (int i = 0; i < mainInv.tagCount(); i++) {
                NBTTagCompound item = (NBTTagCompound) mainInv.get(i);
                int slot = item.getByte("SlotSkeletonInventory");
                if (slot >= 0 && slot < inventory.getSizeInventory()) {
                    inventory.setInventorySlotContents(slot, new ItemStack(item));
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
                    equipInventory.setInventorySlotContents(slot, new ItemStack(item));
                }
            }
        } else {
            System.out.println("List was null when reading Skeleton Warrior's Equipment");
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setInteger("SkeletonPowerLevel", this.dataManager.get(SKELETON_POWER_LEVEL));
        updateEntityAttributes();
        compound.setInteger("TotalMilkLevel", this.dataManager.get(TOTAL_MILK_LEVEL));
        compound.setBoolean("HasSkinsuit", this.dataManager.get(HAS_SKINSUIT));
        compound.setString("SkinsuitName", this.dataManager.get(SKINSUIT_NAME));
        compound.setInteger("XP", this.dataManager.get(XP));

        NBTTagList mainInv = new NBTTagList();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack is = inventory.getStackInSlot(i);
            if (!is.isEmpty()) {
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
        return 1.74F;
    }

    @Override
    public double getYOffset()
    {
        return -0.35D;
    }

    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {
        ItemStack itemstack = this.getHeldItemOffhand();
        if(itemstack.isEmpty() || !(itemstack.getItem() instanceof ItemArrow))
            return;

        EntityArrow entityarrow = getArrow(distanceFactor);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entityarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        entityarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getDifficultyId() * 4));

        if(EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.INFINITY, this) <= 0) {
            if (itemstack.getCount() > 1)
                itemstack.shrink(1);
            else
                setHeldItem(EnumHand.OFF_HAND, ItemStack.EMPTY);
            entityarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }

        if(!getHeldItemMainhand().isEmpty())
            getHeldItemMainhand().damageItem(1, this);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entityarrow);
    }

    protected EntityArrow getArrow(float distanceFactor)
    {
        ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);

        if (itemstack.getItem() == Items.SPECTRAL_ARROW)
        {
            EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(this.world, this);
            entityspectralarrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
            return entityspectralarrow;
        }
        else
        {
            EntityTippedArrow entityarrow = new EntityTippedArrow(this.world, this);
            entityarrow.setEnchantmentEffectsFromEntity(this, distanceFactor);

            if (itemstack.getItem() == Items.TIPPED_ARROW)
            {
                entityarrow.setPotionEffect(itemstack);
            }

            return entityarrow;
        }
    }

    @Override
    @Nonnull
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return slotIn == EntityEquipmentSlot.MAINHAND ? equipInventory.getStackInSlot(4) : (slotIn == EntityEquipmentSlot.OFFHAND ? equipInventory.getStackInSlot(5) : (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.equipInventory.getStackInSlot(slotIn.getIndex()) : ItemStack.EMPTY));
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, @Nonnull ItemStack stack)
    {
        if (slotIn == EntityEquipmentSlot.MAINHAND)
        {
            this.playEquipSound(stack);
            this.equipInventory.setInventorySlotContents(4, stack);
            initEntityAI();
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
    @Nonnull
    public Iterable<ItemStack> getHeldEquipment()
    {
        return Lists.newArrayList(this.getHeldItemMainhand(), this.getHeldItemOffhand());
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
        if(equipInventory == null)
            return ItemStack.EMPTY;
        return equipInventory.getStackInSlot(5);
    }

    @Override
    public boolean isPlayer(){
        return true;
    }

    @SideOnly(Side.CLIENT)
    public void setAugment(@Nullable String augment){
        clientAugment = AugmentRegistry.getAugment(augment);
        cachedClientAugment = true;
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
            return getHeldItemOffhand();
        }
        else
        {
            throw new IllegalArgumentException("Invalid hand " + hand);
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
                throw new IllegalArgumentException("Invalid hand " + hand);
            }

            this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
        }
    }

    public void updateEntityAttributes(){
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16.0D+(4*Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))));
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.23D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/32));
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D+dataManager.get(SKELETON_POWER_LEVEL));
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/2));
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(1.0D+(Math.sqrt(dataManager.get(SKELETON_POWER_LEVEL))/4));
    }

    public int getTotalMilkConsumed(){
        return dataManager.get(TOTAL_MILK_LEVEL);
    }

    public int getXP(){
        return dataManager.get(XP);
    }

    public int getLevel(){
        return dataManager.get(SKELETON_POWER_LEVEL);
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
}
