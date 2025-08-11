package net.minecraft_server.src;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft_server.server.MinecraftServer;

public class EntityPlayerMP extends EntityPlayer implements ICrafting {
	public NetServerHandler playerNetServerHandler;
	public MinecraftServer mcServer;
	public ItemInWorldManager itemInWorldManager;
	public double lastUpdatedX;
	public double lastUpdatedZ;
	public double lastUpdatedY;
	public List chunksToLoad = new LinkedList();
	public Set field_420_ah = new HashSet();
	private int lastHealth = -99999999;
	private int ticksOfInvuln = 60;
	private ItemStack[] playerInventory = new ItemStack[] { null, null, null, null, null };
	private int currentWindowId = 0;
	public boolean isChangingQuantityOnly;

	public EntityPlayerMP(MinecraftServer minecraftserver, World world, String s,
			ItemInWorldManager iteminworldmanager) {
		super(world);
		iteminworldmanager.thisPlayer = this;
		this.itemInWorldManager = iteminworldmanager;
		ChunkCoordinates chunkcoordinates = world.getSpawnPoint();
		int i = chunkcoordinates.posX;
		int j = chunkcoordinates.posZ;
		int k = chunkcoordinates.posY;
		if (!world.worldProvider.hasNoSky) {
			i += this.rand.nextInt(20) - 10;
			k = world.findTopSolidBlock(i, j);
			j += this.rand.nextInt(20) - 10;
		}

		this.setLocationAndAngles((double) i + 0.5D, (double) k, (double) j + 0.5D, 0.0F, 0.0F);
		this.mcServer = minecraftserver;
		this.stepHeight = 0.0F;
		this.username = s;
		this.yOffset = 0.0F;
	}

	public void setWorldHandler(World world) {
		super.setWorldHandler(world);
		this.itemInWorldManager = new ItemInWorldManager((WorldServer) world);
		this.itemInWorldManager.thisPlayer = this;
	}

	public void func_20057_k() {
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public ItemStack[] getInventory() {
		return this.playerInventory;
	}

	protected void resetHeight() {
		this.yOffset = 0.0F;
	}

	public float getEyeHeight() {
		return 1.62F;
	}

	public void onUpdate() {
		this.itemInWorldManager.func_328_a();
		--this.ticksOfInvuln;
		this.currentCraftingInventory.updateCraftingMatrix();

		for (int i = 0; i < 5; ++i) {
			ItemStack itemstack = this.getEquipmentInSlot(i);
			if (itemstack != this.playerInventory[i]) {
				this.mcServer.getEntityTracker(this.dimension).sendPacketToTrackedPlayers(this,
						new Packet5PlayerInventory(this.entityId, i, itemstack));
				this.playerInventory[i] = itemstack;
			}
		}

	}

	public ItemStack getEquipmentInSlot(int i) {
		return i == 0 ? this.inventory.getCurrentItem() : this.inventory.armorInventory[i - 1];
	}

	public void onDeath(Entity entity) {
		this.inventory.dropAllItems();
	}

	public boolean attackEntityFrom(Entity entity, int i) {
		if (this.ticksOfInvuln > 0) {
			return false;
		} else {
			if (!this.mcServer.pvpOn) {
				if (entity instanceof EntityPlayer) {
					return false;
				}

				if (entity instanceof EntityArrow) {
					EntityArrow entityarrow = (EntityArrow) entity;
					if (entityarrow.owner instanceof EntityPlayer) {
						return false;
					}
				}
			}

			return super.attackEntityFrom(entity, i);
		}
	}

	protected boolean isPVPEnabled() {
		return this.mcServer.pvpOn;
	}

	public void heal(int i) {
		super.heal(i);
	}

	public void onUpdateEntity(boolean flag) {
		super.onUpdate();

		for (int coord = 0; coord < this.inventory.getSizeInventory(); ++coord) {
			ItemStack flag1 = this.inventory.getStackInSlot(coord);
			if (flag1 != null && Item.itemsList[flag1.itemID].func_28019_b()
					&& this.playerNetServerHandler.getNumChunkDataPackets() <= 2) {
				Packet worldserver = ((ItemMapBase) Item.itemsList[flag1.itemID]).func_28022_b(flag1, this.worldObj,
						this);
				if (worldserver != null) {
					this.playerNetServerHandler.sendPacket(worldserver);
				}
			}
		}

		if (flag && !this.chunksToLoad.isEmpty()) {
			ChunkCubeCoord chunkCubeCoord9 = (ChunkCubeCoord) this.chunksToLoad.get(0);
			if (chunkCubeCoord9 != null) {
				boolean z10 = false;
				if (this.playerNetServerHandler.getNumChunkDataPackets() < 4) {
					z10 = true;
				}

				if (z10) {
					WorldServer worldServer11 = this.mcServer.getWorldManager(this.dimension);
					this.chunksToLoad.remove(chunkCubeCoord9);
					boolean isInitialAir = worldServer11.isChunkCubeAir(chunkCubeCoord9.chunkXPos,
							chunkCubeCoord9.chunkYPos, chunkCubeCoord9.chunkZPos);
					int yRange = 1;
					if (!this.chunksToLoad.isEmpty()) {
						for (ChunkCubeCoord list = (ChunkCubeCoord) this.chunksToLoad
								.get(0); list.chunkYPos == chunkCubeCoord9.chunkYPos + yRange
										&& list.chunkXPos == chunkCubeCoord9.chunkXPos
										&& list.chunkZPos == chunkCubeCoord9.chunkZPos
										&& isInitialAir == worldServer11.isChunkCubeAir(list.chunkXPos, list.chunkYPos,
												list.chunkZPos); ++yRange) {
							this.chunksToLoad.remove(list);
							if (this.chunksToLoad.isEmpty()) {
								++yRange;
								break;
							}

							list = (ChunkCubeCoord) this.chunksToLoad.get(0);
						}
					}

					this.playerNetServerHandler.sendPacket(
							(new Packet51MapChunk(chunkCubeCoord9.chunkXPos * 16, chunkCubeCoord9.chunkYPos * 16,
									chunkCubeCoord9.chunkZPos * 16, 16, 16 * yRange, 16, worldServer11))
									.setIsAir(isInitialAir));
					List list12 = worldServer11.getTileEntityList(chunkCubeCoord9.chunkXPos * 16,
							chunkCubeCoord9.chunkYPos * 16, chunkCubeCoord9.chunkZPos * 16,
							chunkCubeCoord9.chunkXPos * 16 + 16, chunkCubeCoord9.chunkYPos * 16 + 16 * yRange,
							chunkCubeCoord9.chunkZPos * 16 + 16);

					for (int j = 0; j < list12.size(); ++j) {
						this.getTileEntityInfo((TileEntity) list12.get(j));
					}
				}
			}
		}

		if (this.inPortal) {
			if (this.mcServer.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				if (this.currentCraftingInventory != this.personalCraftingInventory) {
					this.usePersonalCraftingInventory();
				}

				if (this.ridingEntity != null) {
					this.mountEntity(this.ridingEntity);
				} else {
					this.timeInPortal += 0.0125F;
					if (this.timeInPortal >= 1.0F) {
						this.timeInPortal = 1.0F;
						this.timeUntilPortal = 10;
						this.mcServer.configManager.sendPlayerToOtherDimension(this);
					}
				}

				this.inPortal = false;
			}
		} else {
			if (this.timeInPortal > 0.0F) {
				this.timeInPortal -= 0.05F;
			}

			if (this.timeInPortal < 0.0F) {
				this.timeInPortal = 0.0F;
			}
		}

		if (this.timeUntilPortal > 0) {
			--this.timeUntilPortal;
		}

		if (this.health != this.lastHealth) {
			this.playerNetServerHandler.sendPacket(new Packet8UpdateHealth(this.health));
			this.lastHealth = this.health;
		}

	}

	private void getTileEntityInfo(TileEntity tileentity) {
		if (tileentity != null) {
			Packet packet = tileentity.getDescriptionPacket();
			if (packet != null) {
				this.playerNetServerHandler.sendPacket(packet);
			}
		}

	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	public void onItemPickup(Entity entity, int i) {
		if (!entity.isDead) {
			EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
			if (entity instanceof EntityItem) {
				entitytracker.sendPacketToTrackedPlayers(entity, new Packet22Collect(entity.entityId, this.entityId));
			}

			if (entity instanceof EntityArrow) {
				entitytracker.sendPacketToTrackedPlayers(entity, new Packet22Collect(entity.entityId, this.entityId));
			}
		}

		super.onItemPickup(entity, i);
		this.currentCraftingInventory.updateCraftingMatrix();
	}

	public void swingItem() {
		if (!this.isSwinging) {
			this.swingProgressInt = -1;
			this.isSwinging = true;
			EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
			entitytracker.sendPacketToTrackedPlayers(this, new Packet18Animation(this, 1));
		}

	}

	public void func_22068_s() {
	}

	public EnumStatus goToSleep(int i, int j, int k) {
		EnumStatus enumstatus = super.goToSleep(i, j, k);
		if (enumstatus == EnumStatus.OK) {
			EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
			Packet17Sleep packet17sleep = new Packet17Sleep(this, 0, i, j, k);
			entitytracker.sendPacketToTrackedPlayers(this, packet17sleep);
			this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw,
					this.rotationPitch);
			this.playerNetServerHandler.sendPacket(packet17sleep);
		}

		return enumstatus;
	}

	public void wakeUpPlayer(boolean flag, boolean flag1, boolean flag2) {
		if (this.func_22057_E()) {
			EntityTracker entitytracker = this.mcServer.getEntityTracker(this.dimension);
			entitytracker.sendPacketToTrackedPlayersAndTrackedEntity(this, new Packet18Animation(this, 3));
		}

		super.wakeUpPlayer(flag, flag1, flag2);
		if (this.playerNetServerHandler != null) {
			this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw,
					this.rotationPitch);
		}

	}

	public void mountEntity(Entity entity) {
		super.mountEntity(entity);
		this.playerNetServerHandler.sendPacket(new Packet39AttachEntity(this, this.ridingEntity));
		this.playerNetServerHandler.teleportTo(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
	}

	protected void updateFallState(double d, boolean flag) {
	}

	public void handleFalling(double d, boolean flag) {
		super.updateFallState(d, flag);
	}

	private void getNextWidowId() {
		this.currentWindowId = this.currentWindowId % 100 + 1;
	}

	public void displayWorkbenchGUI(int i, int j, int k) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 1, "Crafting", 9));
		this.currentCraftingInventory = new ContainerWorkbench(this.inventory, this.worldObj, i, j, k);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIChest(IInventory iinventory) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 0, iinventory.getInvName(),
				iinventory.getSizeInventory()));
		this.currentCraftingInventory = new ContainerChest(this.inventory, iinventory);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIFurnace(TileEntityFurnace tileentityfurnace) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 2,
				tileentityfurnace.getInvName(), tileentityfurnace.getSizeInventory()));
		this.currentCraftingInventory = new ContainerFurnace(this.inventory, tileentityfurnace);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void displayGUIDispenser(TileEntityDispenser tileentitydispenser) {
		this.getNextWidowId();
		this.playerNetServerHandler.sendPacket(new Packet100OpenWindow(this.currentWindowId, 3,
				tileentitydispenser.getInvName(), tileentitydispenser.getSizeInventory()));
		this.currentCraftingInventory = new ContainerDispenser(this.inventory, tileentitydispenser);
		this.currentCraftingInventory.windowId = this.currentWindowId;
		this.currentCraftingInventory.onCraftGuiOpened(this);
	}

	public void updateCraftingInventorySlot(Container container, int i, ItemStack itemstack) {
		if (!(container.getSlot(i) instanceof SlotCrafting)) {
			if (!this.isChangingQuantityOnly) {
				this.playerNetServerHandler.sendPacket(new Packet103SetSlot(container.windowId, i, itemstack));
			}
		}
	}

	public void func_28017_a(Container container) {
		this.updateCraftingInventory(container, container.func_28127_b());
	}

	public void updateCraftingInventory(Container container, List list) {
		this.playerNetServerHandler.sendPacket(new Packet104WindowItems(container.windowId, list));
		this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
	}

	public void updateCraftingInventoryInfo(Container container, int i, int j) {
		this.playerNetServerHandler.sendPacket(new Packet105UpdateProgressbar(container.windowId, i, j));
	}

	public void onItemStackChanged(ItemStack itemstack) {
	}

	public void usePersonalCraftingInventory() {
		this.playerNetServerHandler.sendPacket(new Packet101CloseWindow(this.currentCraftingInventory.windowId));
		this.closeCraftingGui();
	}

	public void updateHeldItem() {
		if (!this.isChangingQuantityOnly) {
			this.playerNetServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getItemStack()));
		}
	}

	public void closeCraftingGui() {
		this.currentCraftingInventory.onCraftGuiClosed(this);
		this.currentCraftingInventory = this.personalCraftingInventory;
	}

	public void setMovementType(float f, float f1, boolean flag, boolean flag1, float f2, float f3) {
		this.moveStrafing = f;
		this.moveForward = f1;
		this.isJumping = flag;
		this.setSneaking(flag1);
		this.rotationPitch = f2;
		this.rotationYaw = f3;
	}

	public void func_30002_A() {
		if (this.ridingEntity != null) {
			this.mountEntity(this.ridingEntity);
		}

		if (this.riddenByEntity != null) {
			this.riddenByEntity.mountEntity(this);
		}

		if (this.sleeping) {
			this.wakeUpPlayer(true, false, false);
		}

	}

	public void func_30001_B() {
		this.lastHealth = -99999999;
	}

	public void func_22061_a(String s) {
		StringTranslate stringtranslate = StringTranslate.getInstance();
		String s1 = stringtranslate.translateKey(s);
		this.playerNetServerHandler.sendPacket(new Packet3Chat(s1));
	}
}
