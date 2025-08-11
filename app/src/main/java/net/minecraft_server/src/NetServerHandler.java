package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import net.minecraft_server.server.MinecraftServer;

public class NetServerHandler extends NetHandler implements ICommandListener {
	public static Logger logger = Logger.getLogger("Minecraft");
	public NetworkManager netManager;
	public boolean connectionClosed = false;
	private MinecraftServer mcServer;
	private EntityPlayerMP playerEntity;
	private int field_15_f;
	private int field_22004_g;
	private int playerInAirTime;
	private boolean field_22003_h;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private boolean hasMoved = true;
	private Map field_10_k = new HashMap();

	public NetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager,
			EntityPlayerMP entityplayermp) {
		this.mcServer = minecraftserver;
		this.netManager = networkmanager;
		networkmanager.setNetHandler(this);
		this.playerEntity = entityplayermp;
		entityplayermp.playerNetServerHandler = this;
	}

	public void handlePackets() {
		this.field_22003_h = false;
		this.netManager.processReadPackets();
		if (this.field_15_f - this.field_22004_g > 20) {
			this.sendPacket(new Packet0KeepAlive());
		}

	}

	public void kickPlayer(String s) {
		this.playerEntity.func_30002_A();
		this.sendPacket(new Packet255KickDisconnect(s));
		this.netManager.serverShutdown();
		this.mcServer.configManager
				.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
		this.mcServer.configManager.playerLoggedOut(this.playerEntity);
		this.connectionClosed = true;
	}

	public void handleMovementTypePacket(Packet27Position packet27position) {
		this.playerEntity.setMovementType(packet27position.func_22031_c(), packet27position.func_22028_e(),
				packet27position.func_22032_g(), packet27position.func_22030_h(), packet27position.func_22029_d(),
				packet27position.func_22033_f());
	}

	public void handleFlying(Packet10Flying packet10flying) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		this.field_22003_h = true;
		double d1;
		if (!this.hasMoved) {
			d1 = packet10flying.yPosition - this.lastPosY;
			if (packet10flying.xPosition == this.lastPosX && d1 * d1 < 0.01D
					&& packet10flying.zPosition == this.lastPosZ) {
				this.hasMoved = true;
			}
		}

		if (this.hasMoved) {
			double d3;
			double d5;
			double d7;
			double d11;
			if (this.playerEntity.ridingEntity != null) {
				float f26 = this.playerEntity.rotationYaw;
				float f1 = this.playerEntity.rotationPitch;
				this.playerEntity.ridingEntity.updateRiderPosition();
				d3 = this.playerEntity.posX;
				d5 = this.playerEntity.posY;
				d7 = this.playerEntity.posZ;
				double d27 = 0.0D;
				d11 = 0.0D;
				if (packet10flying.rotating) {
					f26 = packet10flying.yaw;
					f1 = packet10flying.pitch;
				}

				if (packet10flying.moving && packet10flying.yPosition == -999.0D && packet10flying.stance == -999.0D) {
					d27 = packet10flying.xPosition;
					d11 = packet10flying.zPosition;
				}

				this.playerEntity.onGround = packet10flying.onGround;
				this.playerEntity.onUpdateEntity(true);
				this.playerEntity.moveEntity(d27, 0.0D, d11);
				this.playerEntity.setPositionAndRotation(d3, d5, d7, f26, f1);
				this.playerEntity.motionX = d27;
				this.playerEntity.motionZ = d11;
				if (this.playerEntity.ridingEntity != null) {
					worldserver.func_12017_b(this.playerEntity.ridingEntity, true);
				}

				if (this.playerEntity.ridingEntity != null) {
					this.playerEntity.ridingEntity.updateRiderPosition();
				}

				this.mcServer.configManager.func_613_b(this.playerEntity);
				this.lastPosX = this.playerEntity.posX;
				this.lastPosY = this.playerEntity.posY;
				this.lastPosZ = this.playerEntity.posZ;
				worldserver.updateEntity(this.playerEntity);
				return;
			}

			if (this.playerEntity.func_22057_E()) {
				this.playerEntity.onUpdateEntity(true);
				this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ,
						this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
				worldserver.updateEntity(this.playerEntity);
				return;
			}

			d1 = this.playerEntity.posY;
			this.lastPosX = this.playerEntity.posX;
			this.lastPosY = this.playerEntity.posY;
			this.lastPosZ = this.playerEntity.posZ;
			d3 = this.playerEntity.posX;
			d5 = this.playerEntity.posY;
			d7 = this.playerEntity.posZ;
			float f2 = this.playerEntity.rotationYaw;
			float f3 = this.playerEntity.rotationPitch;
			if (packet10flying.moving && packet10flying.yPosition == -999.0D && packet10flying.stance == -999.0D) {
				packet10flying.moving = false;
			}

			if (packet10flying.moving) {
				d3 = packet10flying.xPosition;
				d5 = packet10flying.yPosition;
				d7 = packet10flying.zPosition;
				d11 = packet10flying.stance - packet10flying.yPosition;
				if (!this.playerEntity.func_22057_E() && (d11 > 1.65D || d11 < 0.1D)) {
					this.kickPlayer("Illegal stance");
					logger.warning(this.playerEntity.username + " had an illegal stance: " + d11);
					return;
				}

				if (Math.abs(packet10flying.xPosition) > 3.2E7D || Math.abs(packet10flying.zPosition) > 3.2E7D) {
					this.kickPlayer("Illegal position");
					return;
				}
			}

			if (packet10flying.rotating) {
				f2 = packet10flying.yaw;
				f3 = packet10flying.pitch;
			}

			this.playerEntity.onUpdateEntity(true);
			this.playerEntity.ySize = 0.0F;
			this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, f2, f3);
			if (!this.hasMoved) {
				return;
			}

			d11 = d3 - this.playerEntity.posX;
			double d12 = d5 - this.playerEntity.posY;
			double d13 = d7 - this.playerEntity.posZ;
			double d14 = d11 * d11 + d12 * d12 + d13 * d13;
			if (d14 > 100.0D) {
				logger.warning(this.playerEntity.username + " moved too quickly!");
				this.kickPlayer("You moved too quickly :( (Hacking?)");
				return;
			}

			float f4 = 0.0625F;
			boolean flag = worldserver.getCollidingBoundingBoxes(this.playerEntity,
					this.playerEntity.boundingBox.copy().getInsetBoundingBox((double) f4, (double) f4, (double) f4))
					.size() == 0;
			this.playerEntity.moveEntity(d11, d12, d13);
			d11 = d3 - this.playerEntity.posX;
			d12 = d5 - this.playerEntity.posY;
			if (d12 > -0.5D || d12 < 0.5D) {
				d12 = 0.0D;
			}

			d13 = d7 - this.playerEntity.posZ;
			d14 = d11 * d11 + d12 * d12 + d13 * d13;
			boolean flag1 = false;
			if (d14 > 0.0625D && !this.playerEntity.func_22057_E()) {
				flag1 = true;
				logger.warning(this.playerEntity.username + " moved wrongly!");
				System.out.println("Got position " + d3 + ", " + d5 + ", " + d7);
				System.out.println("Expected " + this.playerEntity.posX + ", " + this.playerEntity.posY + ", "
						+ this.playerEntity.posZ);
			}

			this.playerEntity.setPositionAndRotation(d3, d5, d7, f2, f3);
			boolean flag2 = worldserver.getCollidingBoundingBoxes(this.playerEntity,
					this.playerEntity.boundingBox.copy().getInsetBoundingBox((double) f4, (double) f4, (double) f4))
					.size() == 0;
			if (flag && (flag1 || !flag2) && !this.playerEntity.func_22057_E()) {
				this.teleportTo(this.lastPosX, this.lastPosY, this.lastPosZ, f2, f3);
				return;
			}

			AxisAlignedBB axisalignedbb = this.playerEntity.boundingBox.copy()
					.expand((double) f4, (double) f4, (double) f4).addCoord(0.0D, -0.55D, 0.0D);
			if (!this.mcServer.allowFlight && !worldserver.func_27069_b(axisalignedbb)) {
				if (d12 >= -0.03125D) {
					++this.playerInAirTime;
					if (this.playerInAirTime > 80) {
						logger.warning(this.playerEntity.username + " was kicked for floating too long!");
						this.kickPlayer("Flying is not enabled on this server");
						return;
					}
				}
			} else {
				this.playerInAirTime = 0;
			}

			this.playerEntity.onGround = packet10flying.onGround;
			this.mcServer.configManager.func_613_b(this.playerEntity);
			this.playerEntity.handleFalling(this.playerEntity.posY - d1, packet10flying.onGround);
		}

	}

	public void teleportTo(double d, double d1, double d2, float f, float f1) {
		this.hasMoved = false;
		this.lastPosX = d;
		this.lastPosY = d1;
		this.lastPosZ = d2;
		this.playerEntity.setPositionAndRotation(d, d1, d2, f, f1);
		this.playerEntity.playerNetServerHandler
				.sendPacket(new Packet13PlayerLookMove(d, d1 + (double) 1.62F, d1, d2, f, f1, false));
	}

	public void handleBlockDig(Packet14BlockDig packet) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		if (packet.status == 4) {
			this.playerEntity.dropCurrentItem();
		} else {
			boolean flag = worldserver.field_819_z = worldserver.worldProvider.worldType != 0
					|| this.mcServer.configManager.isOp(this.playerEntity.username);
			boolean flag1 = false;
			if (packet.status == 0) {
				flag1 = true;
			}

			if (packet.status == 2) {
				flag1 = true;
			}

			int packetX = packet.xPosition;
			int packetY = packet.yPosition;
			int packetZ = packet.zPosition;
			if (flag1) {
				double chunkcoordinates = this.playerEntity.posX - ((double) packetX + 0.5D);
				double netDist = this.playerEntity.posY - ((double) packetY + 0.5D);
				double d3 = this.playerEntity.posZ - ((double) packetZ + 0.5D);
				double d5 = chunkcoordinates * chunkcoordinates + netDist * netDist + d3 * d3;
				if (d5 > 36.0D) {
					return;
				}
			}

			ChunkCoordinates chunkcoordinates1 = worldserver.getSpawnPoint();
			int xDist = (int) MathHelper.abs((float) (packetX - chunkcoordinates1.posX));
			int netDist1 = (int) MathHelper.abs((float) (packetZ - chunkcoordinates1.posZ));
			if (xDist > netDist1) {
				netDist1 = xDist;
			}

			if (packet.status == 0) {
				if (netDist1 <= 16 && !flag) {
					this.playerEntity.playerNetServerHandler
							.sendPacket(new Packet53BlockChange(packetX, packetY, packetZ, worldserver));
				} else {
					this.playerEntity.itemInWorldManager.func_324_a(packetX, packetY, packetZ, packet.face);
				}
			} else if (packet.status == 2) {
				this.playerEntity.itemInWorldManager.func_22045_b(packetX, packetY, packetZ);
				if (worldserver.getBlockId(packetX, packetY, packetZ) != 0) {
					this.playerEntity.playerNetServerHandler
							.sendPacket(new Packet53BlockChange(packetX, packetY, packetZ, worldserver));
				}
			} else if (packet.status == 3) {
				double d2 = this.playerEntity.posX - ((double) packetX + 0.5D);
				double d4 = this.playerEntity.posY - ((double) packetY + 0.5D);
				double d6 = this.playerEntity.posZ - ((double) packetZ + 0.5D);
				double d7 = d2 * d2 + d4 * d4 + d6 * d6;
				if (d7 < 256.0D) {
					this.playerEntity.playerNetServerHandler
							.sendPacket(new Packet53BlockChange(packetX, packetY, packetZ, worldserver));
				}
			}

			worldserver.field_819_z = false;
		}
	}

	public void handlePlace(Packet15Place packet15place) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		ItemStack itemstack = this.playerEntity.inventory.getCurrentItem();
		boolean flag = worldserver.field_819_z = worldserver.worldProvider.worldType != 0
				|| this.mcServer.configManager.isOp(this.playerEntity.username);
		if (packet15place.direction == 255) {
			if (itemstack == null) {
				return;
			}

			this.playerEntity.itemInWorldManager.func_6154_a(this.playerEntity, worldserver, itemstack);
		} else {
			int slot = packet15place.xPosition;
			int j = packet15place.yPosition;
			int k = packet15place.zPosition;
			int l = packet15place.direction;
			ChunkCoordinates chunkcoordinates = worldserver.getSpawnPoint();
			int i1 = (int) MathHelper.abs((float) (slot - chunkcoordinates.posX));
			int j1 = (int) MathHelper.abs((float) (k - chunkcoordinates.posZ));
			if (i1 > j1) {
				j1 = i1;
			}

			if (this.hasMoved && this.playerEntity.getDistanceSq((double) slot + 0.5D, (double) j + 0.5D,
					(double) k + 0.5D) < 64.0D && (j1 > 16 || flag)) {
				this.playerEntity.itemInWorldManager.activeBlockOrUseItem(this.playerEntity, worldserver, itemstack,
						slot, j, k, l);
			}

			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(slot, j, k, worldserver));
			if (l == 0) {
				--j;
			}

			if (l == 1) {
				++j;
			}

			if (l == 2) {
				--k;
			}

			if (l == 3) {
				++k;
			}

			if (l == 4) {
				--slot;
			}

			if (l == 5) {
				++slot;
			}

			this.playerEntity.playerNetServerHandler.sendPacket(new Packet53BlockChange(slot, j, k, worldserver));
		}

		itemstack = this.playerEntity.inventory.getCurrentItem();
		if (itemstack != null && itemstack.stackSize == 0) {
			this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
		}

		this.playerEntity.isChangingQuantityOnly = true;
		this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack
				.func_20117_a(this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]);
		Slot slot12 = this.playerEntity.currentCraftingInventory.func_20127_a(this.playerEntity.inventory,
				this.playerEntity.inventory.currentItem);
		this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
		this.playerEntity.isChangingQuantityOnly = false;
		if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packet15place.itemStack)) {
			this.sendPacket(new Packet103SetSlot(this.playerEntity.currentCraftingInventory.windowId, slot12.id,
					this.playerEntity.inventory.getCurrentItem()));
		}

		worldserver.field_819_z = false;
	}

	public void handleErrorMessage(String s, Object[] aobj) {
		logger.info(this.playerEntity.username + " lost connection: " + s);
		this.mcServer.configManager
				.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + this.playerEntity.username + " left the game."));
		this.mcServer.configManager.playerLoggedOut(this.playerEntity);
		this.connectionClosed = true;
	}

	public void registerPacket(Packet packet) {
		logger.warning(this.getClass() + " wasn\'t prepared to deal with a " + packet.getClass());
		this.kickPlayer("Protocol error, unexpected packet");
	}

	public void sendPacket(Packet packet) {
		this.netManager.addToSendQueue(packet);
		this.field_22004_g = this.field_15_f;
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch packet16blockitemswitch) {
		if (packet16blockitemswitch.id >= 0 && packet16blockitemswitch.id <= InventoryPlayer.func_25054_e()) {
			this.playerEntity.inventory.currentItem = packet16blockitemswitch.id;
		} else {
			logger.warning(this.playerEntity.username + " tried to set an invalid carried item");
		}
	}

	public void handleChat(Packet3Chat packet3chat) {
		String s = packet3chat.message;
		if (s.length() > 100) {
			this.kickPlayer("Chat message too long");
		} else {
			s = s.trim();

			for (int i = 0; i < s.length(); ++i) {
				if (ChatAllowedCharacters.allowedCharacters.indexOf(s.charAt(i)) < 0) {
					this.kickPlayer("Illegal characters in chat");
					return;
				}
			}

			if (s.startsWith("/")) {
				this.handleSlashCommand(s);
			} else {
				s = "<" + this.playerEntity.username + "> " + s;
				logger.info(s);
				this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(s));
			}

		}
	}

	private void handleSlashCommand(String s) {
		if (s.toLowerCase().startsWith("/me ")) {
			s = "* " + this.playerEntity.username + " " + s.substring(s.indexOf(" ")).trim();
			logger.info(s);
			this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat(s));
		} else if (s.toLowerCase().startsWith("/kill")) {
			this.playerEntity.attackEntityFrom((Entity) null, 1000);
		} else if (s.toLowerCase().startsWith("/tell ")) {
			String[] s2 = s.split(" ");
			if (s2.length >= 3) {
				s = s.substring(s.indexOf(" ")).trim();
				s = s.substring(s.indexOf(" ")).trim();
				s = "\u00a77" + this.playerEntity.username + " whispers " + s;
				logger.info(s + " to " + s2[1]);
				if (!this.mcServer.configManager.sendPacketToPlayer(s2[1], new Packet3Chat(s))) {
					this.sendPacket(new Packet3Chat("\u00a7cThere\'s no player by that name online."));
				}
			}
		} else {
			String s21;
			if (this.mcServer.configManager.isOp(this.playerEntity.username)) {
				s21 = s.substring(1);
				logger.info(this.playerEntity.username + " issued server command: " + s21);
				this.mcServer.addCommand(s21, this);
			} else {
				s21 = s.substring(1);
				logger.info(this.playerEntity.username + " tried command: " + s21);
			}
		}

	}

	public void handleArmAnimation(Packet18Animation packet18animation) {
		if (packet18animation.animate == 1) {
			this.playerEntity.swingItem();
		}

	}

	public void func_21001_a(Packet19EntityAction packet19entityaction) {
		if (packet19entityaction.state == 1) {
			this.playerEntity.setSneaking(true);
		} else if (packet19entityaction.state == 2) {
			this.playerEntity.setSneaking(false);
		} else if (packet19entityaction.state == 3) {
			this.playerEntity.wakeUpPlayer(false, true, true);
			this.hasMoved = false;
		}

	}

	public void handleKickDisconnect(Packet255KickDisconnect packet255kickdisconnect) {
		this.netManager.networkShutdown("disconnect.quitting", new Object[0]);
	}

	public int getNumChunkDataPackets() {
		return this.netManager.getNumChunkDataPackets();
	}

	public void log(String s) {
		this.sendPacket(new Packet3Chat("\u00a77" + s));
	}

	public String getUsername() {
		return this.playerEntity.username;
	}

	public void func_6006_a(Packet7UseEntity packet7useentity) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		Entity entity = worldserver.func_6158_a(packet7useentity.targetEntity);
		if (entity != null && this.playerEntity.canEntityBeSeen(entity)
				&& this.playerEntity.getDistanceSqToEntity(entity) < 36.0D) {
			if (packet7useentity.isLeftClick == 0) {
				this.playerEntity.useCurrentItemOnEntity(entity);
			} else if (packet7useentity.isLeftClick == 1) {
				this.playerEntity.attackTargetEntityWithCurrentItem(entity);
			}
		}

	}

	public void handleRespawnPacket(Packet9Respawn packet9respawn) {
		if (this.playerEntity.health <= 0) {
			this.playerEntity = this.mcServer.configManager.recreatePlayerEntity(this.playerEntity, 0);
		}
	}

	public void handleCubeRequest(Packet59RequestCube packet) {
		int distSq = packet.xPosition - this.playerEntity.chunkCoordX
				^ 2 + (packet.yPosition - this.playerEntity.chunkCoordY)
				^ 2 + (packet.zPosition - this.playerEntity.chunkCoordZ) ^ 2;
		if (distSq <= 64) {
			boolean isAir = this.playerEntity.worldObj.isChunkCubeAir(packet.xPosition, packet.yPosition,
					packet.zPosition);
			this.playerEntity.playerNetServerHandler.sendPacket(
					new Packet50PreChunk(packet.xPosition, packet.yPosition, packet.zPosition, true, isAir));
			int xBlock = packet.xPosition * 16;
			int yBlock = packet.yPosition * 16;
			int zBlock = packet.zPosition * 16;
			this.playerEntity.playerNetServerHandler.sendPacket((new Packet51MapChunk(xBlock, yBlock, zBlock,
					xBlock + 16, yBlock + 16, zBlock + 16, this.playerEntity.worldObj)).setIsAir(isAir));
		}
	}

	public void handleCraftingGuiClosedPacked(Packet101CloseWindow packet) {
		this.playerEntity.closeCraftingGui();
	}

	public void func_20007_a(Packet102WindowClick packet102windowclick) {
		if (this.playerEntity.currentCraftingInventory.windowId == packet102windowclick.window_Id
				&& this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
			ItemStack itemstack = this.playerEntity.currentCraftingInventory.func_27085_a(
					packet102windowclick.inventorySlot, packet102windowclick.mouseClick,
					packet102windowclick.field_27039_f, this.playerEntity);
			if (ItemStack.areItemStacksEqual(packet102windowclick.itemStack, itemstack)) {
				this.playerEntity.playerNetServerHandler.sendPacket(
						new Packet106Transaction(packet102windowclick.window_Id, packet102windowclick.action, true));
				this.playerEntity.isChangingQuantityOnly = true;
				this.playerEntity.currentCraftingInventory.updateCraftingMatrix();
				this.playerEntity.updateHeldItem();
				this.playerEntity.isChangingQuantityOnly = false;
			} else {
				this.field_10_k.put(this.playerEntity.currentCraftingInventory.windowId, packet102windowclick.action);
				this.playerEntity.playerNetServerHandler.sendPacket(
						new Packet106Transaction(packet102windowclick.window_Id, packet102windowclick.action, false));
				this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, false);
				ArrayList arraylist = new ArrayList();

				for (int i = 0; i < this.playerEntity.currentCraftingInventory.inventorySlots.size(); ++i) {
					arraylist.add(((Slot) this.playerEntity.currentCraftingInventory.inventorySlots.get(i)).getStack());
				}

				this.playerEntity.updateCraftingInventory(this.playerEntity.currentCraftingInventory, arraylist);
			}
		}

	}

	public void func_20008_a(Packet106Transaction packet106transaction) {
		Short short1 = (Short) this.field_10_k.get(this.playerEntity.currentCraftingInventory.windowId);
		if (short1 != null && packet106transaction.shortWindowId == short1.shortValue()
				&& this.playerEntity.currentCraftingInventory.windowId == packet106transaction.windowId
				&& !this.playerEntity.currentCraftingInventory.getCanCraft(this.playerEntity)) {
			this.playerEntity.currentCraftingInventory.setCanCraft(this.playerEntity, true);
		}

	}

	public void handleUpdateSign(Packet130UpdateSign packet130updatesign) {
		WorldServer worldserver = this.mcServer.getWorldManager(this.playerEntity.dimension);
		if (worldserver.blockExists(packet130updatesign.xPosition, packet130updatesign.yPosition,
				packet130updatesign.zPosition)) {
			TileEntity tileentity = worldserver.getBlockTileEntity(packet130updatesign.xPosition,
					packet130updatesign.yPosition, packet130updatesign.zPosition);
			if (tileentity instanceof TileEntitySign) {
				TileEntitySign j = (TileEntitySign) tileentity;
				if (!j.getIsEditAble()) {
					this.mcServer.logWarning(
							"Player " + this.playerEntity.username + " just tried to change non-editable sign");
					return;
				}
			}

			int i1;
			int i9;
			for (i9 = 0; i9 < 4; ++i9) {
				boolean k = true;
				if (packet130updatesign.signLines[i9].length() > 15) {
					k = false;
				} else {
					for (i1 = 0; i1 < packet130updatesign.signLines[i9].length(); ++i1) {
						if (ChatAllowedCharacters.allowedCharacters
								.indexOf(packet130updatesign.signLines[i9].charAt(i1)) < 0) {
							k = false;
						}
					}
				}

				if (!k) {
					packet130updatesign.signLines[i9] = "!?";
				}
			}

			if (tileentity instanceof TileEntitySign) {
				i9 = packet130updatesign.xPosition;
				int i10 = packet130updatesign.yPosition;
				i1 = packet130updatesign.zPosition;
				TileEntitySign tileentitysign1 = (TileEntitySign) tileentity;

				for (int j1 = 0; j1 < 4; ++j1) {
					tileentitysign1.signText[j1] = packet130updatesign.signLines[j1];
				}

				tileentitysign1.func_32001_a(false);
				tileentitysign1.onInventoryChanged();
				worldserver.markBlockNeedsUpdate(i9, i10, i1);
			}
		}

	}

	public boolean isServerHandler() {
		return true;
	}
}
