package net.minecraft.src;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BiomeGenBase {
	public static final BiomeGenBase forest = (new BiomeGenForest()).setColor(353825).setBiomeName("Forest").func_4124_a(5159473);
	public static final BiomeGenBase shrubland = (new BiomeGenBase()).setColor(10595616).setBiomeName("Shrubland");
	public static final BiomeGenBase desert = (new BiomeGenDesert()).setColor(16421912).setBiomeName("Desert").setDisableRain();
	public static final BiomeGenBase plains = (new BiomeGenDesert()).setColor(16767248).setBiomeName("Plains");
	public static final BiomeGenBase hell = (new BiomeGenHell()).setColor(16711680).setBiomeName("Hell").setDisableRain();
	public static final BiomeGenBase sky = (new BiomeGenSky()).setColor(8421631).setBiomeName("Sky").setDisableRain();
	public static final BiomeGenBase ocean = (new BiomeGenOcean()).setBiomeName("Ocean");
	public String biomeName;
	public int color;
	public byte topBlock = (byte)Block.grass.blockID;
	public byte fillerBlock = (byte)Block.dirt.blockID;
	public int field_6502_q = 5169201;
	protected List spawnableMonsterList = new ArrayList();
	protected List spawnableCreatureList = new ArrayList();
	protected List spawnableWaterCreatureList = new ArrayList();
	private boolean enableSnow;
	private boolean enableRain = true;
	private static BiomeGenBase[] biomeLookupTable = new BiomeGenBase[4096];

	protected BiomeGenBase() {
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySpider.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityZombie.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySkeleton.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntityCreeper.class, 10));
		this.spawnableMonsterList.add(new SpawnListEntry(EntitySlime.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntitySheep.class, 12));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityPig.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityChicken.class, 10));
		this.spawnableCreatureList.add(new SpawnListEntry(EntityCow.class, 8));
		this.spawnableWaterCreatureList.add(new SpawnListEntry(EntitySquid.class, 10));
	}

	private BiomeGenBase setDisableRain() {
		this.enableRain = false;
		return this;
	}

	public static void generateBiomeLookup() {
		for(int i0 = 0; i0 < 64; ++i0) {
			for(int i1 = 0; i1 < 64; ++i1) {
				biomeLookupTable[i0 + i1 * 64] = getBiome((float)i0 / 63.0F, (float)i1 / 63.0F);
			}
		}

		desert.topBlock = desert.fillerBlock = (byte)Block.sand.blockID;
	}

	public WorldGenerator getRandomWorldGenForTrees(Random random1) {
		return (WorldGenerator)(random1.nextInt(3) == 0 ? new WorldGenBigTree() : new WorldGenTrees());
	}

	protected BiomeGenBase setEnableSnow() {
		this.enableSnow = true;
		return this;
	}

	protected BiomeGenBase setBiomeName(String string1) {
		this.biomeName = string1;
		return this;
	}

	protected BiomeGenBase func_4124_a(int i1) {
		this.field_6502_q = i1;
		return this;
	}

	protected BiomeGenBase setColor(int i1) {
		this.color = i1;
		return this;
	}

	public static BiomeGenBase getBiomeFromLookup(double d0, double d2) {
		int i4 = (int)(d0 * 63.0D);
		int i5 = (int)(d2 * 63.0D);
		return biomeLookupTable[i4 + i5 * 64];
	}

	public static BiomeGenBase getBiome(float f0, float f1) {
		f1 *= f0;
		return f0 < 0.1F ? forest : (f1 < 0.2F ? (f0 < 0.5F ? plains : (f0 < 0.95F ? plains : forest)) : (f1 > 0.5F && f0 < 0.7F ? plains : (f0 < 0.5F ? forest : (f0 < 0.97F ? (f1 < 0.35F ? plains : forest) : (f1 < 0.45F ? plains : (f1 < 0.9F ? forest : forest))))));
	}

	public int getSkyColorByTemp(float f1) {
		f1 /= 3.0F;
		if(f1 < -1.0F) {
			f1 = -1.0F;
		}

		if(f1 > 1.0F) {
			f1 = 1.0F;
		}

		return Color.getHSBColor(0.62222224F - f1 * 0.05F, 0.5F + f1 * 0.1F, 1.0F).getRGB();
	}

	public List getSpawnableList(EnumCreatureType enumCreatureType1) {
		return enumCreatureType1 == EnumCreatureType.monster ? this.spawnableMonsterList : (enumCreatureType1 == EnumCreatureType.creature ? this.spawnableCreatureList : (enumCreatureType1 == EnumCreatureType.waterCreature ? this.spawnableWaterCreatureList : null));
	}

	public boolean getEnableSnow() {
		return this.enableSnow;
	}

	public boolean canSpawnLightningBolt() {
		return this.enableSnow ? false : this.enableRain;
	}

	static {
		generateBiomeLookup();
	}
}
