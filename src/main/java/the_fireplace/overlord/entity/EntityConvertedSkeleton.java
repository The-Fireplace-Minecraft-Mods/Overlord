package the_fireplace.overlord.entity;

import com.google.common.collect.Lists;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
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
public class EntityConvertedSkeleton extends EntityArmyMember {
    private static final DataParameter<String> SKINSUIT_NAME = EntityDataManager.createKey(EntityConvertedSkeleton.class, DataSerializers.STRING);
    private static final DataParameter<Boolean> HAS_SKINSUIT = EntityDataManager.createKey(EntityConvertedSkeleton.class, DataSerializers.BOOLEAN);
    private EntityAIWarriorBow aiArrowAttack = null;
    public final InventoryBasic inventory;
    public final InventoryBasic equipInventory;
    public boolean cachedClientAugment = false;
    public Augment clientAugment = null;
    public EntityConvertedSkeleton instance;
    public EntityConvertedSkeleton(World world) {
        this(world, null);
    }
    public EntityConvertedSkeleton(World world, @Nullable UUID owner) {
        super(world, owner);
        instance = this;
        this.inventory = new InventoryBasic("Items", false, 9);
        this.equipInventory = new InventoryBasic("Equipment", false, 7){
            @Override
            public boolean isItemValidForSlot(int index, ItemStack stack)
            {
                return (index >= 4 && index < 6) || (index == 6 && AugmentRegistry.getAugment(stack) != null) || stack != null && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.values()[index], null);
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
                if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.converter)) {
                    ((EntityPlayer) getOwner()).addStat(Overlord.converter);
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
                    EntityConvertedSkeleton.this.setSwingingArms(false);
                }
                @Override
                public void startExecuting()
                {
                    super.startExecuting();
                    EntityConvertedSkeleton.this.setSwingingArms(true);
                }
                @Override
                public void updateTask(){
                    if(this.attacker.getHeldItemMainhand() != null && this.attacker.getHeldItemOffhand() != null && this.attacker.getHeldItemMainhand().getItem() instanceof ItemBow && this.attacker.getHeldItemOffhand().getItem() instanceof ItemArrow) {
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
        if(this.getHeldItemMainhand() != null)
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
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack is)
    {
        if(this.getOwner() != null){
            if(this.getOwner().equals(player)){
                if(!player.isSneaking()) {
                    FMLNetworkHandler.openGui(player, Overlord.instance, hashCode(), world, (int) this.posX, (int) this.posY, (int) this.posZ);
                    return true;
                }else {
                    if (!world.isRemote){
                        ItemStack stack = player.getHeldItem(hand);
                        if (stack != null) {
                            if (stack.getItem() == Overlord.skinsuit && !this.hasSkinsuit()) {
                                applySkinsuit(stack);
                                if (!player.isCreative())
                                    stack.stackSize--;
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
                            } else if (stack.getItem() == Overlord.converted_spawner) {
                                NBTTagCompound compound = new NBTTagCompound();
                                this.writeEntityToNBT(compound);
                                stack.setTagCompound(compound);
                            }
                        }
                    }
                }
            }
        }
        return super.processInteract(player, hand, is);
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
        this.dataManager.register(HAS_SKINSUIT, Boolean.valueOf(false));
        this.dataManager.register(SKINSUIT_NAME, String.valueOf(""));
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
                if(inventory.getStackInSlot(i) != null && this.getHealth() < this.getMaxHealth()) {
                    if (inventory.getStackInSlot(i).getItem() == Items.MILK_BUCKET) {
                        this.heal(1);
                        if (inventory.getStackInSlot(i).stackSize > 1)
                            inventory.getStackInSlot(i).stackSize--;
                        else
                            inventory.setInventorySlotContents(i, null);
                        inventory.addItem(bucket);
                    }else if(inventory.getStackInSlot(i).getItem() == Overlord.milk_bottle){
                        this.heal(1);
                        if(inventory.getStackInSlot(i).stackSize > 1)
                            inventory.getStackInSlot(i).stackSize--;
                        else
                            inventory.setInventorySlotContents(i, null);
                        inventory.addItem(bottle);
                    }
                }
            }
            if (this.world.isDaytime()) {
                float f = this.getBrightness(1.0F);
                BlockPos blockpos = this.getRidingEntity() instanceof EntityBoat ? (new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ)).up() : new BlockPos(this.posX, (double) Math.round(this.posY), this.posZ);
                if(!hasSkinsuit())
                    if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(blockpos)) {
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
            this.world.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(1.0D, 0.0D, 1.0D)).stream().filter(entityitem -> !entityitem.isDead && entityitem.getEntityItem() != null && !entityitem.cannotPickup()).forEach(entityitem -> {
                ItemStack stack2 = inventory.addItem(entityitem.getEntityItem());
                if (stack2 != null) {
                    if(stack2.stackSize != entityitem.getEntityItem().stackSize)
                        playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                    entityitem.getEntityItem().stackSize=stack2.stackSize;
                    if(stack2.getItem() == Items.MILK_BUCKET || stack2.getItem() == Overlord.milk_bottle){
                        for(int i=0;i<inventory.getSizeInventory();i++){
                            if(inventory.getStackInSlot(i) != null && (inventory.getStackInSlot(i).getItem() == Items.BUCKET || inventory.getStackInSlot(i).getItem() == Items.GLASS_BOTTLE)) {
                                entityDropItem(inventory.getStackInSlot(i), 0.1F);
                                inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                } else {
                    entityitem.setDead();
                    playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.2F, ((rand.nextFloat() - rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                }
            });
            //Bow stuffs
            if(getHeldItemMainhand() != null){
                if(getHeldItemMainhand().getItem() instanceof ItemBow)
                    if((getHeldItemOffhand() != null && !(getHeldItemOffhand().getItem() instanceof ItemArrow)) || getHeldItemOffhand() == null){
                        boolean swapWeapon=true;
                        for(int i=0;i<inventory.getSizeInventory();i++){
                            if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() instanceof ItemArrow){
                                ItemStack offhand = null;
                                if(getHeldItemOffhand() != null)
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
                                if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() instanceof ItemSword){
                                    ItemStack clone = inventory.getStackInSlot(i).copy();
                                    inventory.setInventorySlotContents(i, getHeldItemMainhand());
                                    setHeldItem(EnumHand.MAIN_HAND, clone);
                                    break;
                                }
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
            if(hasSkinsuit()){
                if(getOwner() != null){
                    if(getOwner() instanceof EntityPlayerMP)
                        if(((EntityPlayerMP) getOwner()).getStatFile().canUnlockAchievement(Overlord.sally)) {
                            ((EntityPlayer) getOwner()).addStat(Overlord.sally);
                        }
                }
            }
            if(getItemStackFromSlot(EntityEquipmentSlot.HEAD) != null && getItemStackFromSlot(EntityEquipmentSlot.CHEST) != null && getItemStackFromSlot(EntityEquipmentSlot.LEGS) != null && getItemStackFromSlot(EntityEquipmentSlot.FEET) != null){
                if(getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.CHAINMAIL_HELMET && getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.CHAINMAIL_CHESTPLATE && getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.CHAINMAIL_LEGGINGS && getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.CHAINMAIL_BOOTS){
                    if(getHeldItemOffhand() != null)
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
                if(inventory.getStackInSlot(i) != null){
                    EntityItem entityitem = new EntityItem(world, posX, posY, posZ, inventory.getStackInSlot(i));
                    entityitem.setDefaultPickupDelay();
                    world.spawnEntity(entityitem);
                }
            }
            for(int i=0;i<equipInventory.getSizeInventory();i++){
                if(equipInventory.getStackInSlot(i) != null){
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
            System.out.println("List was null when reading Converted Skeleton's Inventory");
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
            System.out.println("List was null when reading Converted Skeleton's Equipment");
        }
    }
    @Override
    public void writeEntityToNBT(NBTTagCompound compound)
    {
        super.writeEntityToNBT(compound);
        compound.setBoolean("HasSkinsuit", this.dataManager.get(HAS_SKINSUIT));
        compound.setString("SkinsuitName", this.dataManager.get(SKINSUIT_NAME));
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
    @Override
    public double getYOffset()
    {
        return -0.35D;
    }
    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor)
    {
        ItemStack itemstack = this.getHeldItemOffhand();
        if(itemstack == null || !(itemstack.getItem() instanceof ItemArrow))
            return;

        EntityTippedArrow entitytippedarrow = new EntityTippedArrow(this.world, this);
        double d0 = target.posX - this.posX;
        double d1 = target.getEntityBoundingBox().minY + (double)(target.height / 3.0F) - entitytippedarrow.posY;
        double d2 = target.posZ - this.posZ;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
        entitytippedarrow.setThrowableHeading(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float)(14 - this.world.getDifficulty().getDifficultyId() * 4));
        int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.POWER, this);
        int j = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.PUNCH, this);
        DifficultyInstance difficultyinstance = this.world.getDifficultyForLocation(new BlockPos(this));
        entitytippedarrow.setDamage((double)(distanceFactor * 2.0F) + this.rand.nextGaussian() * 0.25D + (double)((float)this.world.getDifficulty().getDifficultyId() * 0.11F));

        if (i > 0)
        {
            entitytippedarrow.setDamage(entitytippedarrow.getDamage() + (double)i * 0.5D + 0.5D);
        }

        if (j > 0)
        {
            entitytippedarrow.setKnockbackStrength(j);
        }

        boolean flag = this.isBurning() && difficultyinstance.isHard() && this.rand.nextBoolean();
        flag = flag || EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FLAME, this) > 0;

        if (flag)
        {
            entitytippedarrow.setFire(100);
        }

        if (itemstack.getItem() == Items.TIPPED_ARROW)
        {
            entitytippedarrow.setPotionEffect(itemstack);
        }

        if(EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.INFINITY, this) <= 0) {
            if (itemstack.stackSize > 1)
                itemstack.stackSize--;
            else
                setHeldItem(EnumHand.OFF_HAND, null);
            entitytippedarrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
        }

        if(getHeldItemMainhand() != null)
            getHeldItemMainhand().damageItem(1, this);
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(entitytippedarrow);
    }
    @Override
    @Nullable
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return slotIn == EntityEquipmentSlot.MAINHAND ? equipInventory.getStackInSlot(4) : (slotIn == EntityEquipmentSlot.OFFHAND ? equipInventory.getStackInSlot(5) : (slotIn.getSlotType() == EntityEquipmentSlot.Type.ARMOR ? this.equipInventory.getStackInSlot(slotIn.getIndex()) : null));
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
        if(equipInventory == null)
            return null;
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