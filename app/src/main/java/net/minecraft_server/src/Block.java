package net.minecraft_server.src;

import java.util.ArrayList;
import java.util.Random;

public class Block {
	public static final StepSound soundPowderFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundWoodFootstep = new StepSound("wood", 1.0F, 1.0F);
	public static final StepSound soundGravelFootstep = new StepSound("gravel", 1.0F, 1.0F);
	public static final StepSound soundGrassFootstep = new StepSound("grass", 1.0F, 1.0F);
	public static final StepSound soundStoneFootstep = new StepSound("stone", 1.0F, 1.0F);
	public static final StepSound soundMetalFootstep = new StepSound("stone", 1.0F, 1.5F);
	public static final StepSound soundGlassFootstep = new StepSoundStone("stone", 1.0F, 1.0F);
	public static final StepSound soundClothFootstep = new StepSound("cloth", 1.0F, 1.0F);
	public static final StepSound soundSandFootstep = new StepSoundSand("sand", 1.0F, 1.0F);
	public static final Block[] blocksList = new Block[256];
	public static final boolean[] tickOnLoad = new boolean[256];
	public static final boolean[] opaqueCubeLookup = new boolean[256];
	public static final boolean[] isBlockContainer = new boolean[256];
	public static final int[] lightOpacity = new int[256];
	public static final boolean[] canBlockGrass = new boolean[256];
	public static final int[] lightValue = new int[256];
	public static final boolean[] requiresSelfNotify = new boolean[256];
	public static final Block stone = (new BlockStone(1, 1)).setHardness(1.5F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("stone");
	public static final BlockGrass grass = (BlockGrass) (new BlockGrass(2)).setHardness(0.6F)
			.setStepSound(soundGrassFootstep).setBlockName("grass");
	public static final Block dirt = (new BlockDirt(3, 2)).setHardness(0.5F).setStepSound(soundGravelFootstep)
			.setBlockName("dirt");
	public static final Block cobblestone = (new Block(4, 16, Material.rock)).setHardness(2.0F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("stonebrick");
	public static final Block planks = (new Block(5, 4, Material.wood)).setHardness(2.0F).setResistance(5.0F)
			.setStepSound(soundWoodFootstep).setBlockName("wood").setRequiresSelfNotify();
	public static final Block sapling = (new BlockSapling(6, 15)).setHardness(0.0F).setStepSound(soundGrassFootstep)
			.setBlockName("sapling").setRequiresSelfNotify();
	public static final Block bedrock = (new Block(7, 17, Material.rock)).setBlockUnbreakable()
			.setResistance(6000000.0F).setStepSound(soundStoneFootstep).setBlockName("bedrock").disableStats();
	public static final Block waterMoving = (new BlockFlowing(8, Material.water)).setHardness(100.0F).setLightOpacity(3)
			.setBlockName("water").disableStats().setRequiresSelfNotify();
	public static final Block waterStill = (new BlockStationary(9, Material.water)).setHardness(100.0F)
			.setLightOpacity(3).setBlockName("water").disableStats().setRequiresSelfNotify();
	public static final Block lavaMoving = (new BlockFlowing(10, Material.lava)).setHardness(0.0F).setLightValue(1.0F)
			.setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
	public static final Block lavaStill = (new BlockStationary(11, Material.lava)).setHardness(100.0F)
			.setLightValue(1.0F).setLightOpacity(255).setBlockName("lava").disableStats().setRequiresSelfNotify();
	public static final Block sand = (new BlockSand(12, 18)).setHardness(0.5F).setStepSound(soundSandFootstep)
			.setBlockName("sand");
	public static final Block gravel = (new BlockGravel(13, 19)).setHardness(0.6F).setStepSound(soundGravelFootstep)
			.setBlockName("gravel");
	public static final Block oreGold = (new BlockOre(14, 32)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreGold");
	public static final Block oreIron = (new BlockOre(15, 33)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreIron");
	public static final Block oreCoal = (new BlockOre(16, 34)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreCoal");
	public static final Block wood = (new BlockLog(17)).setHardness(2.0F).setStepSound(soundWoodFootstep)
			.setBlockName("log").setRequiresSelfNotify();
	public static final BlockLeaves leaves = (BlockLeaves) (new BlockLeaves(18, 52)).setHardness(0.2F)
			.setLightOpacity(1).setStepSound(soundGrassFootstep).setBlockName("leaves").disableStats()
			.setRequiresSelfNotify();
	public static final Block sponge = (new BlockSponge(19)).setHardness(0.6F).setStepSound(soundGrassFootstep)
			.setBlockName("sponge");
	public static final Block glass = (new BlockGlass(20, 49, Material.glass, false)).setHardness(0.3F)
			.setStepSound(soundGlassFootstep).setBlockName("glass");
	public static final Block oreLapis = (new BlockOre(21, 160)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreLapis");
	public static final Block blockLapis = (new Block(22, 144, Material.rock)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("blockLapis");
	public static final Block dispenser = (new BlockDispenser(23)).setHardness(3.5F).setStepSound(soundStoneFootstep)
			.setBlockName("dispenser").setRequiresSelfNotify();
	public static final Block sandStone = (new BlockSandStone(24)).setStepSound(soundStoneFootstep).setHardness(0.8F)
			.setBlockName("sandStone");
	public static final Block musicBlock = (new BlockNote(25)).setHardness(0.8F).setBlockName("musicBlock")
			.setRequiresSelfNotify();
	public static final Block bed = (new BlockBed(26)).setHardness(0.2F).setBlockName("bed").disableStats()
			.setRequiresSelfNotify();
	public static final Block railPowered = (new BlockRail(27, 179, true)).setHardness(0.7F)
			.setStepSound(soundMetalFootstep).setBlockName("goldenRail").setRequiresSelfNotify();
	public static final Block railDetector = (new BlockDetectorRail(28, 195)).setHardness(0.7F)
			.setStepSound(soundMetalFootstep).setBlockName("detectorRail").setRequiresSelfNotify();
	public static final Block pistonStickyBase = (new BlockPistonBase(29, 106, true)).setBlockName("pistonStickyBase")
			.setRequiresSelfNotify();
	public static final Block web = (new BlockWeb(30, 11)).setLightOpacity(1).setHardness(4.0F).setBlockName("web");
	public static final BlockTallGrass tallGrass = (BlockTallGrass) (new BlockTallGrass(31, 39)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setBlockName("tallgrass");
	public static final BlockDeadBush deadBush = (BlockDeadBush) (new BlockDeadBush(32, 55)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setBlockName("deadbush");
	public static final Block pistonBase = (new BlockPistonBase(33, 107, false)).setBlockName("pistonBase")
			.setRequiresSelfNotify();
	public static final BlockPistonExtension pistonExtension = (BlockPistonExtension) (new BlockPistonExtension(34,
			107)).setRequiresSelfNotify();
	public static final Block cloth = (new BlockCloth()).setHardness(0.8F).setStepSound(soundClothFootstep)
			.setBlockName("cloth").setRequiresSelfNotify();
	public static final BlockPistonMoving pistonMoving = new BlockPistonMoving(36);
	public static final BlockFlower plantYellow = (BlockFlower) (new BlockFlower(37, 13)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setBlockName("flower");
	public static final BlockFlower plantRed = (BlockFlower) (new BlockFlower(38, 12)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setBlockName("rose");
	public static final BlockFlower mushroomBrown = (BlockFlower) (new BlockMushroom(39, 29)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setLightValue(0.125F).setBlockName("mushroom");
	public static final BlockFlower mushroomRed = (BlockFlower) (new BlockMushroom(40, 28)).setHardness(0.0F)
			.setStepSound(soundGrassFootstep).setBlockName("mushroom");
	public static final Block blockGold = (new BlockOreStorage(41, 23)).setHardness(3.0F).setResistance(10.0F)
			.setStepSound(soundMetalFootstep).setBlockName("blockGold");
	public static final Block blockSteel = (new BlockOreStorage(42, 22)).setHardness(5.0F).setResistance(10.0F)
			.setStepSound(soundMetalFootstep).setBlockName("blockIron");
	public static final Block stairDouble = (new BlockStep(43, true)).setHardness(2.0F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
	public static final Block stairSingle = (new BlockStep(44, false)).setHardness(2.0F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("stoneSlab");
	public static final Block brick = (new Block(45, 7, Material.rock)).setHardness(2.0F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("brick");
	public static final Block tnt = (new BlockTNT(46, 8)).setHardness(0.0F).setStepSound(soundGrassFootstep)
			.setBlockName("tnt");
	public static final Block bookShelf = (new BlockBookshelf(47, 35)).setHardness(1.5F).setStepSound(soundWoodFootstep)
			.setBlockName("bookshelf");
	public static final Block cobblestoneMossy = (new Block(48, 36, Material.rock)).setHardness(2.0F)
			.setResistance(10.0F).setStepSound(soundStoneFootstep).setBlockName("stoneMoss");
	public static final Block obsidian = (new BlockObsidian(49, 37)).setHardness(10.0F).setResistance(2000.0F)
			.setStepSound(soundStoneFootstep).setBlockName("obsidian");
	public static final Block torchWood = (new BlockTorch(50, 80)).setHardness(0.0F).setLightValue(0.9375F)
			.setStepSound(soundWoodFootstep).setBlockName("torch").setRequiresSelfNotify();
	public static final BlockFire fire = (BlockFire) (new BlockFire(51, 31)).setHardness(0.0F).setLightValue(1.0F)
			.setStepSound(soundWoodFootstep).setBlockName("fire").disableStats().setRequiresSelfNotify();
	public static final Block mobSpawner = (new BlockMobSpawner(52, 65)).setHardness(5.0F)
			.setStepSound(soundMetalFootstep).setBlockName("mobSpawner").disableStats();
	public static final Block stairCompactPlanks = (new BlockStairs(53, planks)).setBlockName("stairsWood")
			.setRequiresSelfNotify();
	public static final Block chest = (new BlockChest(54)).setHardness(2.5F).setStepSound(soundWoodFootstep)
			.setBlockName("chest").setRequiresSelfNotify();
	public static final Block redstoneWire = (new BlockRedstoneWire(55, 164)).setHardness(0.0F)
			.setStepSound(soundPowderFootstep).setBlockName("redstoneDust").disableStats().setRequiresSelfNotify();
	public static final Block oreDiamond = (new BlockOre(56, 50)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreDiamond");
	public static final Block blockDiamond = (new BlockOreStorage(57, 24)).setHardness(5.0F).setResistance(10.0F)
			.setStepSound(soundMetalFootstep).setBlockName("blockDiamond");
	public static final Block workbench = (new BlockWorkbench(58)).setHardness(2.5F).setStepSound(soundWoodFootstep)
			.setBlockName("workbench");
	public static final Block crops = (new BlockCrops(59, 88)).setHardness(0.0F).setStepSound(soundGrassFootstep)
			.setBlockName("crops").disableStats().setRequiresSelfNotify();
	public static final Block tilledField = (new BlockFarmland(60)).setHardness(0.6F).setStepSound(soundGravelFootstep)
			.setBlockName("farmland");
	public static final Block stoneOvenIdle = (new BlockFurnace(61, false)).setHardness(3.5F)
			.setStepSound(soundStoneFootstep).setBlockName("furnace").setRequiresSelfNotify();
	public static final Block stoneOvenActive = (new BlockFurnace(62, true)).setHardness(3.5F)
			.setStepSound(soundStoneFootstep).setLightValue(0.875F).setBlockName("furnace").setRequiresSelfNotify();
	public static final Block signPost = (new BlockSign(63, TileEntitySign.class, true)).setHardness(1.0F)
			.setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
	public static final Block doorWood = (new BlockDoor(64, Material.wood)).setHardness(3.0F)
			.setStepSound(soundWoodFootstep).setBlockName("doorWood").disableStats().setRequiresSelfNotify();
	public static final Block ladder = (new BlockLadder(65, 83)).setHardness(0.4F).setStepSound(soundWoodFootstep)
			.setBlockName("ladder").setRequiresSelfNotify();
	public static final Block minecartTrack = (new BlockRail(66, 128, false)).setHardness(0.7F)
			.setStepSound(soundMetalFootstep).setBlockName("rail").setRequiresSelfNotify();
	public static final Block stairCompactCobblestone = (new BlockStairs(67, cobblestone)).setBlockName("stairsStone")
			.setRequiresSelfNotify();
	public static final Block signWall = (new BlockSign(68, TileEntitySign.class, false)).setHardness(1.0F)
			.setStepSound(soundWoodFootstep).setBlockName("sign").disableStats().setRequiresSelfNotify();
	public static final Block lever = (new BlockLever(69, 96)).setHardness(0.5F).setStepSound(soundWoodFootstep)
			.setBlockName("lever").setRequiresSelfNotify();
	public static final Block pressurePlateStone = (new BlockPressurePlate(70, stone.blockIndexInTexture,
			EnumMobType.mobs, Material.rock)).setHardness(0.5F).setStepSound(soundStoneFootstep)
			.setBlockName("pressurePlate").setRequiresSelfNotify();
	public static final Block doorSteel = (new BlockDoor(71, Material.iron)).setHardness(5.0F)
			.setStepSound(soundMetalFootstep).setBlockName("doorIron").disableStats().setRequiresSelfNotify();
	public static final Block pressurePlatePlanks = (new BlockPressurePlate(72, planks.blockIndexInTexture,
			EnumMobType.everything, Material.wood)).setHardness(0.5F).setStepSound(soundWoodFootstep)
			.setBlockName("pressurePlate").setRequiresSelfNotify();
	public static final Block oreRedstone = (new BlockRedstoneOre(73, 51, false)).setHardness(3.0F).setResistance(5.0F)
			.setStepSound(soundStoneFootstep).setBlockName("oreRedstone").setRequiresSelfNotify();
	public static final Block oreRedstoneGlowing = (new BlockRedstoneOre(74, 51, true)).setLightValue(0.625F)
			.setHardness(3.0F).setResistance(5.0F).setStepSound(soundStoneFootstep).setBlockName("oreRedstone")
			.setRequiresSelfNotify();
	public static final Block torchRedstoneIdle = (new BlockRedstoneTorch(75, 115, false)).setHardness(0.0F)
			.setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
	public static final Block torchRedstoneActive = (new BlockRedstoneTorch(76, 99, true)).setHardness(0.0F)
			.setLightValue(0.5F).setStepSound(soundWoodFootstep).setBlockName("notGate").setRequiresSelfNotify();
	public static final Block button = (new BlockButton(77, stone.blockIndexInTexture)).setHardness(0.5F)
			.setStepSound(soundStoneFootstep).setBlockName("button").setRequiresSelfNotify();
	public static final Block snow = (new BlockSnow(78, 66)).setHardness(0.1F).setStepSound(soundClothFootstep)
			.setBlockName("snow");
	public static final Block ice = (new BlockIce(79, 67)).setHardness(0.5F).setLightOpacity(3)
			.setStepSound(soundGlassFootstep).setBlockName("ice");
	public static final Block blockSnow = (new BlockSnowBlock(80, 66)).setHardness(0.2F)
			.setStepSound(soundClothFootstep).setBlockName("snow");
	public static final Block cactus = (new BlockCactus(81, 70)).setHardness(0.4F).setStepSound(soundClothFootstep)
			.setBlockName("cactus");
	public static final Block blockClay = (new BlockClay(82, 72)).setHardness(0.6F).setStepSound(soundGravelFootstep)
			.setBlockName("clay");
	public static final Block reed = (new BlockReed(83, 73)).setHardness(0.0F).setStepSound(soundGrassFootstep)
			.setBlockName("reeds").disableStats();
	public static final Block jukebox = (new BlockJukeBox(84, 74)).setHardness(2.0F).setResistance(10.0F)
			.setStepSound(soundStoneFootstep).setBlockName("jukebox").setRequiresSelfNotify();
	public static final Block fence = (new BlockFence(85, 4)).setHardness(2.0F).setResistance(5.0F)
			.setStepSound(soundWoodFootstep).setBlockName("fence").setRequiresSelfNotify();
	public static final Block pumpkin = (new BlockPumpkin(86, 102, false)).setHardness(1.0F)
			.setStepSound(soundWoodFootstep).setBlockName("pumpkin").setRequiresSelfNotify();
	public static final Block bloodStone = (new BlockNetherrack(87, 103)).setHardness(0.4F)
			.setStepSound(soundStoneFootstep).setBlockName("hellrock");
	public static final Block slowSand = (new BlockSoulSand(88, 104)).setHardness(0.5F).setStepSound(soundSandFootstep)
			.setBlockName("hellsand");
	public static final Block glowStone = (new BlockGlowStone(89, 105, Material.rock)).setHardness(0.3F)
			.setStepSound(soundGlassFootstep).setLightValue(1.0F).setBlockName("lightgem");
	public static final BlockPortal portal = (BlockPortal) (new BlockPortal(90, 14)).setHardness(-1.0F)
			.setStepSound(soundGlassFootstep).setLightValue(0.75F).setBlockName("portal");
	public static final Block pumpkinLantern = (new BlockPumpkin(91, 102, true)).setHardness(1.0F)
			.setStepSound(soundWoodFootstep).setLightValue(1.0F).setBlockName("litpumpkin").setRequiresSelfNotify();
	public static final Block cake = (new BlockCake(92, 121)).setHardness(0.5F).setStepSound(soundClothFootstep)
			.setBlockName("cake").disableStats().setRequiresSelfNotify();
	public static final Block redstoneRepeaterIdle = (new BlockRedstoneRepeater(93, false)).setHardness(0.0F)
			.setStepSound(soundWoodFootstep).setBlockName("diode").disableStats().setRequiresSelfNotify();
	public static final Block redstoneRepeaterActive = (new BlockRedstoneRepeater(94, true)).setHardness(0.0F)
			.setLightValue(0.625F).setStepSound(soundWoodFootstep).setBlockName("diode").disableStats()
			.setRequiresSelfNotify();
	public static final Block lockedChest = (new BlockLockedChest(95)).setHardness(0.0F).setLightValue(1.0F)
			.setStepSound(soundWoodFootstep).setBlockName("lockedchest").setTickOnLoad(true).setRequiresSelfNotify();
	public static final Block trapdoor = (new BlockTrapDoor(96, Material.wood)).setHardness(3.0F)
			.setStepSound(soundWoodFootstep).setBlockName("trapdoor").disableStats().setRequiresSelfNotify();
	public int blockIndexInTexture;
	public final int blockID;
	protected float blockHardness;
	protected float blockResistance;
	protected boolean blockConstructorCalled;
	protected boolean enableStats;
	public double minX;
	public double minY;
	public double minZ;
	public double maxX;
	public double maxY;
	public double maxZ;
	public StepSound stepSound;
	public float blockParticleGravity;
	public final Material blockMaterial;
	public float slipperiness;
	private String blockName;

	protected Block(int i, Material material) {
		this.blockConstructorCalled = true;
		this.enableStats = true;
		this.stepSound = soundPowderFootstep;
		this.blockParticleGravity = 1.0F;
		this.slipperiness = 0.6F;
		if (blocksList[i] != null) {
			throw new IllegalArgumentException(
					"Slot " + i + " is already occupied by " + blocksList[i] + " when adding " + this);
		} else {
			this.blockMaterial = material;
			blocksList[i] = this;
			this.blockID = i;
			this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
			opaqueCubeLookup[i] = this.isOpaqueCube();
			lightOpacity[i] = this.isOpaqueCube() ? 255 : 0;
			canBlockGrass[i] = !material.getCanBlockGrass();
			isBlockContainer[i] = false;
		}
	}

	protected Block setRequiresSelfNotify() {
		requiresSelfNotify[this.blockID] = true;
		return this;
	}

	protected void setFireBurnRates() {
	}

	protected Block(int i, int j, Material material) {
		this(i, material);
		this.blockIndexInTexture = j;
	}

	protected Block setStepSound(StepSound stepsound) {
		this.stepSound = stepsound;
		return this;
	}

	protected Block setLightOpacity(int i) {
		lightOpacity[this.blockID] = i;
		return this;
	}

	protected Block setLightValue(float f) {
		lightValue[this.blockID] = (int) (15.0F * f);
		return this;
	}

	protected Block setResistance(float f) {
		this.blockResistance = f * 3.0F;
		return this;
	}

	public boolean isACube() {
		return true;
	}

	protected Block setHardness(float f) {
		this.blockHardness = f;
		if (this.blockResistance < f * 5.0F) {
			this.blockResistance = f * 5.0F;
		}

		return this;
	}

	protected Block setBlockUnbreakable() {
		this.setHardness(-1.0F);
		return this;
	}

	public float getHardness() {
		return this.blockHardness;
	}

	protected Block setTickOnLoad(boolean flag) {
		tickOnLoad[this.blockID] = flag;
		return this;
	}

	public void setBlockBounds(float f, float f1, float f2, float f3, float f4, float f5) {
		this.minX = (double) f;
		this.minY = (double) f1;
		this.minZ = (double) f2;
		this.maxX = (double) f3;
		this.maxY = (double) f4;
		this.maxZ = (double) f5;
	}

	public boolean shouldSideBeRendered(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return iblockaccess.getBlockMaterial(i, j, k).isSolid();
	}

	public int getBlockTextureFromSideAndMetadata(int i, int j) {
		return this.getBlockTextureFromSide(i);
	}

	public int getBlockTextureFromSide(int i) {
		return this.blockIndexInTexture;
	}

	public void getCollidingBoundingBoxes(World world, int i, int j, int k, AxisAlignedBB axisalignedbb,
			ArrayList arraylist) {
		AxisAlignedBB axisalignedbb1 = this.getCollisionBoundingBoxFromPool(world, i, j, k);
		if (axisalignedbb1 != null && axisalignedbb.intersectsWith(axisalignedbb1)) {
			arraylist.add(axisalignedbb1);
		}

	}

	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int i, int j, int k) {
		return AxisAlignedBB.getBoundingBoxFromPool((double) i + this.minX, (double) j + this.minY,
				(double) k + this.minZ, (double) i + this.maxX, (double) j + this.maxY, (double) k + this.maxZ);
	}

	public boolean isOpaqueCube() {
		return true;
	}

	public boolean canCollideCheck(int i, boolean flag) {
		return this.isCollidable();
	}

	public boolean isCollidable() {
		return true;
	}

	public void updateTick(World world, int i, int j, int k, Random random) {
	}

	public void onBlockDestroyedByPlayer(World world, int i, int j, int k, int l) {
	}

	public void onNeighborBlockChange(World world, int i, int j, int k, int l) {
	}

	public int tickRate() {
		return 10;
	}

	public void onBlockAdded(World world, int i, int j, int k) {
	}

	public void onBlockRemoval(World world, int i, int j, int k) {
	}

	public int quantityDropped(Random random) {
		return 1;
	}

	public int idDropped(int i, Random random) {
		return this.blockID;
	}

	public float blockStrength(EntityPlayer entityplayer) {
		return this.blockHardness < 0.0F ? 0.0F
				: (!entityplayer.canHarvestBlock(this) ? 1.0F / this.blockHardness / 100.0F
						: entityplayer.getCurrentPlayerStrVsBlock(this) / this.blockHardness / 30.0F);
	}

	public final void dropBlockAsItem(World world, int i, int j, int k, int l) {
		this.dropBlockAsItemWithChance(world, i, j, k, l, 1.0F);
	}

	public void dropBlockAsItemWithChance(World world, int i, int j, int k, int l, float f) {
		if (!world.multiplayerWorld) {
			int i1 = this.quantityDropped(world.rand);

			for (int j1 = 0; j1 < i1; ++j1) {
				if (world.rand.nextFloat() <= f) {
					int k1 = this.idDropped(l, world.rand);
					if (k1 > 0) {
						this.dropBlockAsItem_do(world, i, j, k, new ItemStack(k1, 1, this.damageDropped(l)));
					}
				}
			}

		}
	}

	protected void dropBlockAsItem_do(World world, int i, int j, int k, ItemStack itemstack) {
		if (!world.multiplayerWorld) {
			float f = 0.7F;
			double d = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d1 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			double d2 = (double) (world.rand.nextFloat() * f) + (double) (1.0F - f) * 0.5D;
			EntityItem entityitem = new EntityItem(world, (double) i + d, (double) j + d1, (double) k + d2, itemstack);
			entityitem.delayBeforeCanPickup = 10;
			world.entityJoinedWorld(entityitem);
		}
	}

	protected int damageDropped(int i) {
		return 0;
	}

	public float getExplosionResistance(Entity entity) {
		return this.blockResistance / 5.0F;
	}

	public MovingObjectPosition collisionRayTrace(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
		this.setBlockBoundsBasedOnState(world, i, j, k);
		vec3d = vec3d.addVector((double) (-i), (double) (-j), (double) (-k));
		vec3d1 = vec3d1.addVector((double) (-i), (double) (-j), (double) (-k));
		Vec3D vec3d2 = vec3d.getIntermediateWithXValue(vec3d1, this.minX);
		Vec3D vec3d3 = vec3d.getIntermediateWithXValue(vec3d1, this.maxX);
		Vec3D vec3d4 = vec3d.getIntermediateWithYValue(vec3d1, this.minY);
		Vec3D vec3d5 = vec3d.getIntermediateWithYValue(vec3d1, this.maxY);
		Vec3D vec3d6 = vec3d.getIntermediateWithZValue(vec3d1, this.minZ);
		Vec3D vec3d7 = vec3d.getIntermediateWithZValue(vec3d1, this.maxZ);
		if (!this.isVecInsideYZBounds(vec3d2)) {
			vec3d2 = null;
		}

		if (!this.isVecInsideYZBounds(vec3d3)) {
			vec3d3 = null;
		}

		if (!this.isVecInsideXZBounds(vec3d4)) {
			vec3d4 = null;
		}

		if (!this.isVecInsideXZBounds(vec3d5)) {
			vec3d5 = null;
		}

		if (!this.isVecInsideXYBounds(vec3d6)) {
			vec3d6 = null;
		}

		if (!this.isVecInsideXYBounds(vec3d7)) {
			vec3d7 = null;
		}

		Vec3D vec3d8 = null;
		if (vec3d2 != null && (vec3d8 == null || vec3d.distanceTo(vec3d2) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d2;
		}

		if (vec3d3 != null && (vec3d8 == null || vec3d.distanceTo(vec3d3) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d3;
		}

		if (vec3d4 != null && (vec3d8 == null || vec3d.distanceTo(vec3d4) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d4;
		}

		if (vec3d5 != null && (vec3d8 == null || vec3d.distanceTo(vec3d5) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d5;
		}

		if (vec3d6 != null && (vec3d8 == null || vec3d.distanceTo(vec3d6) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d6;
		}

		if (vec3d7 != null && (vec3d8 == null || vec3d.distanceTo(vec3d7) < vec3d.distanceTo(vec3d8))) {
			vec3d8 = vec3d7;
		}

		if (vec3d8 == null) {
			return null;
		} else {
			byte byte0 = -1;
			if (vec3d8 == vec3d2) {
				byte0 = 4;
			}

			if (vec3d8 == vec3d3) {
				byte0 = 5;
			}

			if (vec3d8 == vec3d4) {
				byte0 = 0;
			}

			if (vec3d8 == vec3d5) {
				byte0 = 1;
			}

			if (vec3d8 == vec3d6) {
				byte0 = 2;
			}

			if (vec3d8 == vec3d7) {
				byte0 = 3;
			}

			return new MovingObjectPosition(i, j, k, byte0, vec3d8.addVector((double) i, (double) j, (double) k));
		}
	}

	private boolean isVecInsideYZBounds(Vec3D vec3d) {
		return vec3d == null ? false
				: vec3d.yCoord >= this.minY && vec3d.yCoord <= this.maxY && vec3d.zCoord >= this.minZ
						&& vec3d.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXZBounds(Vec3D vec3d) {
		return vec3d == null ? false
				: vec3d.xCoord >= this.minX && vec3d.xCoord <= this.maxX && vec3d.zCoord >= this.minZ
						&& vec3d.zCoord <= this.maxZ;
	}

	private boolean isVecInsideXYBounds(Vec3D vec3d) {
		return vec3d == null ? false
				: vec3d.xCoord >= this.minX && vec3d.xCoord <= this.maxX && vec3d.yCoord >= this.minY
						&& vec3d.yCoord <= this.maxY;
	}

	public void onBlockDestroyedByExplosion(World world, int i, int j, int k) {
	}

	public boolean canPlaceBlockOnSide(World world, int i, int j, int k, int l) {
		return this.canPlaceBlockAt(world, i, j, k);
	}

	public boolean canPlaceBlockAt(World world, int i, int j, int k) {
		int l = world.getBlockId(i, j, k);
		return l == 0 || blocksList[l].blockMaterial.func_27090_g();
	}

	public boolean blockActivated(World world, int i, int j, int k, EntityPlayer entityplayer) {
		return false;
	}

	public void onEntityWalking(World world, int i, int j, int k, Entity entity) {
	}

	public void onBlockPlaced(World world, int i, int j, int k, int l) {
	}

	public void onBlockClicked(World world, int i, int j, int k, EntityPlayer entityplayer) {
	}

	public void velocityToAddToEntity(World world, int i, int j, int k, Entity entity, Vec3D vec3d) {
	}

	public void setBlockBoundsBasedOnState(IBlockAccess iblockaccess, int i, int j, int k) {
	}

	public boolean isPoweringTo(IBlockAccess iblockaccess, int i, int j, int k, int l) {
		return false;
	}

	public boolean canProvidePower() {
		return false;
	}

	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
	}

	public boolean isIndirectlyPoweringTo(World world, int i, int j, int k, int l) {
		return false;
	}

	public void harvestBlock(World world, EntityPlayer entityplayer, int i, int j, int k, int l) {
		this.dropBlockAsItem(world, i, j, k, l);
	}

	public boolean canBlockStay(World world, int i, int j, int k) {
		return true;
	}

	public void onBlockPlacedBy(World world, int i, int j, int k, EntityLiving entityliving) {
	}

	public Block setBlockName(String s) {
		this.blockName = "tile." + s;
		return this;
	}

	public String getBlockName() {
		return this.blockName;
	}

	public void playBlock(World world, int i, int j, int k, int l, int i1) {
	}

	public boolean getEnableStats() {
		return this.enableStats;
	}

	protected Block disableStats() {
		this.enableStats = false;
		return this;
	}

	public int getMobilityFlag() {
		return this.blockMaterial.getMaterialMobility();
	}

	static Class _mthclass$(String s) {
		try {
			return Class.forName(s);
		} catch (ClassNotFoundException classNotFoundException2) {
			throw new NoClassDefFoundError(classNotFoundException2.getMessage());
		}
	}

	static {
		Item.itemsList[cloth.blockID] = (new ItemCloth(cloth.blockID - 256)).setItemName("cloth");
		Item.itemsList[wood.blockID] = (new ItemLog(wood.blockID - 256)).setItemName("log");
		Item.itemsList[stairSingle.blockID] = (new ItemSlab(stairSingle.blockID - 256)).setItemName("stoneSlab");
		Item.itemsList[sapling.blockID] = (new ItemSapling(sapling.blockID - 256)).setItemName("sapling");
		Item.itemsList[leaves.blockID] = (new ItemLeaves(leaves.blockID - 256)).setItemName("leaves");
		Item.itemsList[pistonBase.blockID] = new ItemPiston(pistonBase.blockID - 256);
		Item.itemsList[pistonStickyBase.blockID] = new ItemPiston(pistonStickyBase.blockID - 256);

		for (int i = 0; i < 256; ++i) {
			if (blocksList[i] != null && Item.itemsList[i] == null) {
				Item.itemsList[i] = new ItemBlock(i - 256);
				blocksList[i].setFireBurnRates();
			}
		}

		canBlockGrass[0] = true;
	}
}
