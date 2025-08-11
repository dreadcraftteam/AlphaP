package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.imageio.ImageIO;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public final class ModLoader {
	private static final List animList = new LinkedList();
	private static final Map blockModels = new HashMap();
	private static final Map blockSpecialInv = new HashMap();
	private static final File cfgdir = new File(Minecraft.getMinecraftDir(), "/config/");
	private static final File cfgfile = new File(cfgdir, "ModLoader.cfg");
	public static Level cfgLoggingLevel = Level.FINER;
	private static Map classMap = null;
	private static long clock = 0L;
	public static final boolean DEBUG = false;
	private static Field field_animList = null;
	private static Field field_armorList = null;
	private static Field field_blockList = null;
	private static Field field_modifiers = null;
	private static Field field_TileEntityRenderers = null;
	private static boolean hasInit = false;
	private static int highestEntityId = 3000;
	private static final Map inGameHooks = new HashMap();
	private static final Map inGUIHooks = new HashMap();
	private static Minecraft instance = null;
	private static int itemSpriteIndex = 0;
	private static int itemSpritesLeft = 0;
	private static final Map keyList = new HashMap();
	private static final File logfile = new File(Minecraft.getMinecraftDir(), "ModLoader.txt");
	private static final Logger logger = Logger.getLogger("ModLoader");
	private static FileHandler logHandler = null;
	private static Method method_RegisterEntityID = null;
	private static Method method_RegisterTileEntity = null;
	private static final File modDir = new File(Minecraft.getMinecraftDir(), "/mods/");
	private static final LinkedList modList = new LinkedList();
	private static int nextBlockModelID = 1000;
	private static final Map overrides = new HashMap();
	public static final Properties props = new Properties();
	private static BiomeGenBase[] standardBiomes;
	private static int terrainSpriteIndex = 0;
	private static int terrainSpritesLeft = 0;
	private static String texPack = null;
	private static boolean texturesAdded = false;
	private static final boolean[] usedItemSprites = new boolean[256];
	private static final boolean[] usedTerrainSprites = new boolean[256];
	public static final String VERSION = "ModLoader Beta 1.7.3";

	// public static void AddAchievementDesc(Achievement achievement, String s, String s1) {
	// 	try {
	// 		if(achievement.statName.contains(".")) {
	// 			String[] nosuchfieldexception = achievement.statName.split("\\.");
	// 			if(nosuchfieldexception.length == 2) {
	// 				String s2 = nosuchfieldexception[1];
	// 				AddLocalization("achievement." + s2, s);
	// 				AddLocalization("achievement." + s2 + ".desc", s1);
	// 				setPrivateValue(StatBase.class, achievement, 1, StringTranslate.getInstance().translateKey("achievement." + s2));
	// 				setPrivateValue(Achievement.class, achievement, 3, StringTranslate.getInstance().translateKey("achievement." + s2 + ".desc"));
	// 			} else {
	// 				setPrivateValue(StatBase.class, achievement, 1, s);
	// 				setPrivateValue(Achievement.class, achievement, 3, s1);
	// 			}
	// 		} else {
	// 			setPrivateValue(StatBase.class, achievement, 1, s);
	// 			setPrivateValue(Achievement.class, achievement, 3, s1);
	// 		}
	// 	} catch (IllegalArgumentException illegalArgumentException5) {
	// 		logger.throwing("ModLoader", "AddAchievementDesc", illegalArgumentException5);
	// 		ThrowException(illegalArgumentException5);
	// 	} catch (SecurityException securityException6) {
	// 		logger.throwing("ModLoader", "AddAchievementDesc", securityException6);
	// 		ThrowException(securityException6);
	// 	} catch (NoSuchFieldException noSuchFieldException7) {
	// 		logger.throwing("ModLoader", "AddAchievementDesc", noSuchFieldException7);
	// 		ThrowException(noSuchFieldException7);
	// 	}

	// }

	public static int AddAllFuel(int i) {
		logger.finest("Finding fuel for " + i);
		int j = 0;

		for(Iterator iterator = modList.iterator(); iterator.hasNext() && j == 0; j = ((BaseMod)iterator.next()).AddFuel(i)) {
		}

		if(j != 0) {
			logger.finest("Returned " + j);
		}

		return j;
	}

	public static void AddAllRenderers(Map map) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Iterator iterator = modList.iterator();

		while(iterator.hasNext()) {
			BaseMod basemod = (BaseMod)iterator.next();
			basemod.AddRenderer(map);
		}

	}

	public static void addAnimation(TextureFX texturefx) {
		logger.finest("Adding animation " + texturefx.toString());
		Iterator iterator = animList.iterator();

		while(iterator.hasNext()) {
			TextureFX texturefx1 = (TextureFX)iterator.next();
			if(texturefx1.tileImage == texturefx.tileImage && texturefx1.iconIndex == texturefx.iconIndex) {
				animList.remove(texturefx);
				break;
			}
		}

		animList.add(texturefx);
	}

	public static int AddArmor(String s) {
		try {
			String[] illegalaccessexception = (String[])((String[])field_armorList.get((Object)null));
			List list = Arrays.asList(illegalaccessexception);
			ArrayList arraylist = new ArrayList();
			arraylist.addAll(list);
			if(!arraylist.contains(s)) {
				arraylist.add(s);
			}

			int i = arraylist.indexOf(s);
			field_armorList.set((Object)null, arraylist.toArray(new String[0]));
			return i;
		} catch (IllegalArgumentException illegalArgumentException5) {
			logger.throwing("ModLoader", "AddArmor", illegalArgumentException5);
			ThrowException("An impossible error has occured!", illegalArgumentException5);
		} catch (IllegalAccessException illegalAccessException6) {
			logger.throwing("ModLoader", "AddArmor", illegalAccessException6);
			ThrowException("An impossible error has occured!", illegalAccessException6);
		}

		return -1;
	}

	public static void AddLocalization(String s, String s1) {
		Properties properties = null;

		try {
			properties = (Properties)getPrivateValue(StringTranslate.class, StringTranslate.getInstance(), 1);
		} catch (SecurityException securityException4) {
			logger.throwing("ModLoader", "AddLocalization", securityException4);
			ThrowException(securityException4);
		} catch (NoSuchFieldException noSuchFieldException5) {
			logger.throwing("ModLoader", "AddLocalization", noSuchFieldException5);
			ThrowException(noSuchFieldException5);
		}

		if(properties != null) {
			properties.put(s, s1);
		}

	}

	private static void addMod(ClassLoader classloader, String s) {
		try {
			String throwable = s.split("\\.")[0];
			if(throwable.contains("$")) {
				return;
			}

			if(props.containsKey(throwable) && (props.getProperty(throwable).equalsIgnoreCase("no") || props.getProperty(throwable).equalsIgnoreCase("off"))) {
				return;
			}

			Package package1 = ModLoader.class.getPackage();
			if(package1 != null) {
				throwable = package1.getName() + "." + throwable;
			}

			Class class1 = classloader.loadClass(throwable);
			if(!BaseMod.class.isAssignableFrom(class1)) {
				return;
			}

			setupProperties(class1);
			BaseMod basemod = (BaseMod)class1.newInstance();
			if(basemod != null) {
				modList.add(basemod);
				logger.fine("Mod Loaded: \"" + basemod.toString() + "\" from " + s);
				System.out.println("Mod Loaded: " + basemod.toString());
			}
		} catch (Throwable throwable6) {
			logger.fine("Failed to load mod from \"" + s + "\"");
			System.out.println("Failed to load mod from \"" + s + "\"");
			logger.throwing("ModLoader", "addMod", throwable6);
			ThrowException(throwable6);
		}

	}

	public static void AddName(Object obj, String s) {
		String s1 = null;
		Exception exception13;
		if(obj instanceof Item) {
			Item exception1 = (Item)obj;
			if(exception1.getItemName() != null) {
				s1 = exception1.getItemName() + ".name";
			}
		} else if(obj instanceof Block) {
			Block exception11 = (Block)obj;
			if(exception11.getBlockName() != null) {
				s1 = exception11.getBlockName() + ".name";
			}
		} else if(obj instanceof ItemStack) {
			ItemStack exception12 = (ItemStack)obj;
			if(exception12.getItemName() != null) {
				s1 = exception12.getItemName() + ".name";
			}
		} else {
			exception13 = new Exception(obj.getClass().getName() + " cannot have name attached to it!");
			logger.throwing("ModLoader", "AddName", exception13);
			ThrowException(exception13);
		}

		if(s1 != null) {
			AddLocalization(s1, s);
		} else {
			exception13 = new Exception(obj + " is missing name tag!");
			logger.throwing("ModLoader", "AddName", exception13);
			ThrowException(exception13);
		}

	}

	public static int addOverride(String s, String s1) {
		try {
			int throwable = getUniqueSpriteIndex(s);
			addOverride(s, s1, throwable);
			return throwable;
		} catch (Throwable throwable3) {
			logger.throwing("ModLoader", "addOverride", throwable3);
			ThrowException(throwable3);
			throw new RuntimeException(throwable3);
		}
	}

	public static void addOverride(String s, String s1, int i) {
		boolean j = true;
		boolean k = false;
		byte j1;
		int k1;
		if(s.equals("/terrain.png")) {
			j1 = 0;
			k1 = terrainSpritesLeft;
		} else {
			if(!s.equals("/gui/items.png")) {
				return;
			}

			j1 = 1;
			k1 = itemSpritesLeft;
		}

		System.out.println("Overriding " + s + " with " + s1 + " @ " + i + ". " + k1 + " left.");
		logger.finer("addOverride(" + s + "," + s1 + "," + i + "). " + k1 + " left.");
		Object obj = (Map)overrides.get(Integer.valueOf(j1));
		if(obj == null) {
			obj = new HashMap();
			overrides.put(Integer.valueOf(j1), obj);
		}

		((Map)((Map)obj)).put(s1, i);
	}

	public static void AddRecipe(ItemStack itemstack, Object[] aobj) {
		CraftingManager.getInstance().addRecipe(itemstack, aobj);
	}

	public static void AddShapelessRecipe(ItemStack itemstack, Object[] aobj) {
		CraftingManager.getInstance().addShapelessRecipe(itemstack, aobj);
	}

	public static void AddSmelting(int i, ItemStack itemstack) {
		FurnaceRecipes.smelting().addSmelting(i, itemstack);
	}

	public static void AddSpawn(Class class1, int i, EnumCreatureType enumcreaturetype) {
		AddSpawn((Class)class1, i, enumcreaturetype, (BiomeGenBase[])null);
	}

	public static void AddSpawn(Class class1, int i, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
		if(class1 == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(enumcreaturetype == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(abiomegenbase == null) {
				abiomegenbase = standardBiomes;
			}

			for(int j = 0; j < abiomegenbase.length; ++j) {
				List list = abiomegenbase[j].getSpawnableList(enumcreaturetype);
				if(list != null) {
					boolean flag = false;
					Iterator iterator = list.iterator();

					while(iterator.hasNext()) {
						SpawnListEntry spawnlistentry = (SpawnListEntry)iterator.next();
						if(spawnlistentry.entityClass == class1) {
							spawnlistentry.spawnRarityRate = i;
							flag = true;
							break;
						}
					}

					if(!flag) {
						list.add(new SpawnListEntry(class1, i));
					}
				}
			}

		}
	}

	public static void AddSpawn(String s, int i, EnumCreatureType enumcreaturetype) {
		AddSpawn((String)s, i, enumcreaturetype, (BiomeGenBase[])null);
	}

	public static void AddSpawn(String s, int i, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
		Class class1 = (Class)classMap.get(s);
		if(class1 != null && EntityLiving.class.isAssignableFrom(class1)) {
			AddSpawn(class1, i, enumcreaturetype, abiomegenbase);
		}

	}

	public static boolean DispenseEntity(World world, double d, double d1, double d2, int i, int j, ItemStack itemstack) {
		boolean flag = false;

		for(Iterator iterator = modList.iterator(); iterator.hasNext() && !flag; flag = ((BaseMod)iterator.next()).DispenseEntity(world, d, d1, d2, i, j, itemstack)) {
		}

		return flag;
	}

	public static List getLoadedMods() {
		return Collections.unmodifiableList(modList);
	}

	public static Logger getLogger() {
		return logger;
	}

	public static Minecraft getMinecraftInstance() {
		if(instance == null) {
			try {
				ThreadGroup nosuchfieldexception = Thread.currentThread().getThreadGroup();
				int i = nosuchfieldexception.activeCount();
				Thread[] athread = new Thread[i];
				nosuchfieldexception.enumerate(athread);

				for(int j = 0; j < athread.length; ++j) {
					if(athread[j].getName().equals("AlphaPlus main thread")) {
						instance = (Minecraft)getPrivateValue(Thread.class, athread[j], "target");
						break;
					}
				}
			} catch (SecurityException securityException4) {
				logger.throwing("ModLoader", "getMinecraftInstance", securityException4);
				throw new RuntimeException(securityException4);
			} catch (NoSuchFieldException noSuchFieldException5) {
				logger.throwing("ModLoader", "getMinecraftInstance", noSuchFieldException5);
				throw new RuntimeException(noSuchFieldException5);
			}
		}

		return instance;
	}

	public static Object getPrivateValue(Class class1, Object obj, int i) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field illegalaccessexception = class1.getDeclaredFields()[i];
			illegalaccessexception.setAccessible(true);
			return illegalaccessexception.get(obj);
		} catch (IllegalAccessException illegalAccessException4) {
			logger.throwing("ModLoader", "getPrivateValue", illegalAccessException4);
			ThrowException("An impossible error has occured!", illegalAccessException4);
			return null;
		}
	}

	public static Object getPrivateValue(Class class1, Object obj, String s) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field illegalaccessexception = class1.getDeclaredField(s);
			illegalaccessexception.setAccessible(true);
			return illegalaccessexception.get(obj);
		} catch (IllegalAccessException illegalAccessException4) {
			logger.throwing("ModLoader", "getPrivateValue", illegalAccessException4);
			ThrowException("An impossible error has occured!", illegalAccessException4);
			return null;
		}
	}

	public static int getUniqueBlockModelID(BaseMod basemod, boolean flag) {
		int i = nextBlockModelID++;
		blockModels.put(i, basemod);
		blockSpecialInv.put(i, flag);
		return i;
	}

	public static int getUniqueEntityId() {
		return highestEntityId++;
	}

	private static int getUniqueItemSpriteIndex() {
		while(itemSpriteIndex < usedItemSprites.length) {
			if(!usedItemSprites[itemSpriteIndex]) {
				usedItemSprites[itemSpriteIndex] = true;
				--itemSpritesLeft;
				return itemSpriteIndex++;
			}

			++itemSpriteIndex;
		}

		Exception exception = new Exception("No more empty item sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", exception);
		ThrowException(exception);
		return 0;
	}

	public static int getUniqueSpriteIndex(String s) {
		if(s.equals("/gui/items.png")) {
			return getUniqueItemSpriteIndex();
		} else if(s.equals("/terrain.png")) {
			return getUniqueTerrainSpriteIndex();
		} else {
			Exception exception = new Exception("No registry for this texture: " + s);
			logger.throwing("ModLoader", "getUniqueItemSpriteIndex", exception);
			ThrowException(exception);
			return 0;
		}
	}

	private static int getUniqueTerrainSpriteIndex() {
		while(terrainSpriteIndex < usedTerrainSprites.length) {
			if(!usedTerrainSprites[terrainSpriteIndex]) {
				usedTerrainSprites[terrainSpriteIndex] = true;
				--terrainSpritesLeft;
				return terrainSpriteIndex++;
			}

			++terrainSpriteIndex;
		}

		Exception exception = new Exception("No more empty terrain sprite indices left!");
		logger.throwing("ModLoader", "getUniqueItemSpriteIndex", exception);
		ThrowException(exception);
		return 0;
	}

	private static void init() {
		hasInit = true;
		String s = "1111111111111111111111111111111111111101111111011111111111111001111111111111111111111111111011111111100110000011111110000000001111111001100000110000000100000011000000010000001100000000000000110000000000000000000000000000000000000000000000001100000000000000";
		String s1 = "1111111111111111111111111111110111111111111111111111110111111111111111111111000111111011111111111111001111111110111111111111100011111111000010001111011110000000111111000000000011111100000000001111000000000111111000000000001101000000000001111111111111000011";

		for(int throwable = 0; throwable < 256; ++throwable) {
			usedItemSprites[throwable] = s.charAt(throwable) == 49;
			if(!usedItemSprites[throwable]) {
				++itemSpritesLeft;
			}

			usedTerrainSprites[throwable] = s1.charAt(throwable) == 49;
			if(!usedTerrainSprites[throwable]) {
				++terrainSpritesLeft;
			}
		}

		try {
			instance = (Minecraft)getPrivateValue(Minecraft.class, (Object)null, 1);
			instance.entityRenderer = new EntityRendererProxy(instance);
			classMap = (Map)getPrivateValue(EntityList.class, (Object)null, 0);
			field_modifiers = Field.class.getDeclaredField("modifiers");
			field_modifiers.setAccessible(true);
			field_blockList = Session.class.getDeclaredFields()[0];
			field_blockList.setAccessible(true);
			field_TileEntityRenderers = TileEntityRenderer.class.getDeclaredFields()[0];
			field_TileEntityRenderers.setAccessible(true);
			field_armorList = RenderPlayer.class.getDeclaredFields()[3];
			field_modifiers.setInt(field_armorList, field_armorList.getModifiers() & -17);
			field_armorList.setAccessible(true);
			field_animList = RenderEngine.class.getDeclaredFields()[6];
			field_animList.setAccessible(true);
			Field[] field16 = BiomeGenBase.class.getDeclaredFields();
			LinkedList iterator = new LinkedList();

			for(int basemod = 0; basemod < field16.length; ++basemod) {
				Class class1 = field16[basemod].getType();
				if((field16[basemod].getModifiers() & 8) != 0 && class1.isAssignableFrom(BiomeGenBase.class)) {
					BiomeGenBase biomegenbase = (BiomeGenBase)field16[basemod].get((Object)null);
					if(!(biomegenbase instanceof BiomeGenHell) && !(biomegenbase instanceof BiomeGenSky)) {
						iterator.add(biomegenbase);
					}
				}
			}

			standardBiomes = (BiomeGenBase[])((BiomeGenBase[])iterator.toArray(new BiomeGenBase[0]));

			try {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("a", new Class[]{Class.class, String.class});
			} catch (NoSuchMethodException noSuchMethodException8) {
				method_RegisterTileEntity = TileEntity.class.getDeclaredMethod("addMapping", new Class[]{Class.class, String.class});
			}

			method_RegisterTileEntity.setAccessible(true);

			try {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("a", new Class[]{Class.class, String.class, Integer.TYPE});
			} catch (NoSuchMethodException noSuchMethodException7) {
				method_RegisterEntityID = EntityList.class.getDeclaredMethod("addMapping", new Class[]{Class.class, String.class, Integer.TYPE});
			}

			method_RegisterEntityID.setAccessible(true);
		} catch (SecurityException securityException11) {
			logger.throwing("ModLoader", "init", securityException11);
			ThrowException(securityException11);
			throw new RuntimeException(securityException11);
		} catch (NoSuchFieldException noSuchFieldException12) {
			logger.throwing("ModLoader", "init", noSuchFieldException12);
			ThrowException(noSuchFieldException12);
			throw new RuntimeException(noSuchFieldException12);
		} catch (NoSuchMethodException noSuchMethodException13) {
			logger.throwing("ModLoader", "init", noSuchMethodException13);
			ThrowException(noSuchMethodException13);
			throw new RuntimeException(noSuchMethodException13);
		} catch (IllegalArgumentException illegalArgumentException14) {
			logger.throwing("ModLoader", "init", illegalArgumentException14);
			ThrowException(illegalArgumentException14);
			throw new RuntimeException(illegalArgumentException14);
		} catch (IllegalAccessException illegalAccessException15) {
			logger.throwing("ModLoader", "init", illegalAccessException15);
			ThrowException(illegalAccessException15);
			throw new RuntimeException(illegalAccessException15);
		}

		try {
			loadConfig();
			if(props.containsKey("loggingLevel")) {
				cfgLoggingLevel = Level.parse(props.getProperty("loggingLevel"));
			}

			if(props.containsKey("grassFix")) {
				RenderBlocks.cfgGrassFix = Boolean.parseBoolean(props.getProperty("grassFix"));
			}

			logger.setLevel(cfgLoggingLevel);
			if((logfile.exists() || logfile.createNewFile()) && logfile.canWrite() && logHandler == null) {
				logHandler = new FileHandler(logfile.getPath());
				logHandler.setFormatter(new SimpleFormatter());
				logger.addHandler(logHandler);
			}

			logger.fine("ModLoader Beta 1.7.3 Initializing...");
			System.out.println("ModLoader Beta 1.7.3 Initializing...");
			System.out.println("***** Modloader Fix by coffeenotfound @ 2017 ~ https://github.com/coffeenotfound ~ https://bitangent.net/ ****");

			File file17;
			try {
				String string18 = URLDecoder.decode(ModLoader.class.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
				string18 = string18.replace("jar:", "").replace("file:/", "").replace("file:\\", "");
				if(string18.contains(".jar!")) {
					string18 = string18.substring(0, string18.lastIndexOf(".jar!") + ".jar".length());
				}

				string18 = (new File(string18)).getAbsolutePath();
				System.out.println("[Modloader Fix] original jar path = " + ModLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
				System.out.println("[Modloader Fix] new jar path = " + string18);
				file17 = new File(string18);
			} catch (Exception exception9) {
				throw new RuntimeException("Failed to resolve minecraft jar path; this is the fixed code, if this exception ever gets thrown I owe you a beer.", exception9);
			}

			modDir.mkdirs();
			readFromModFolder(modDir);
			readFromClassPath(file17);
			System.out.println("Done.");
			props.setProperty("loggingLevel", cfgLoggingLevel.getName());
			props.setProperty("grassFix", Boolean.toString(RenderBlocks.cfgGrassFix));
			Iterator iterator20 = modList.iterator();

			while(iterator20.hasNext()) {
				BaseMod baseMod19 = (BaseMod)iterator20.next();
				baseMod19.ModsLoaded();
				if(!props.containsKey(baseMod19.getClass().getName())) {
					props.setProperty(baseMod19.getClass().getName(), "on");
				}
			}

			instance.gameSettings.keyBindings = RegisterAllKeys(instance.gameSettings.keyBindings);
			instance.gameSettings.loadOptions();
			//initStats();
			saveConfig();
		} catch (Throwable throwable10) {
			logger.throwing("ModLoader", "init", throwable10);
			ThrowException("ModLoader has failed to initialize.", throwable10);
			if(logHandler != null) {
				logHandler.close();
			}

			throw new RuntimeException(throwable10);
		}
	}

	// private static void initStats() {
	// 	int hashset;
	// 	String obj;
	// 	for(hashset = 0; hashset < Block.blocksList.length; ++hashset) {
	// 		if(!StatList.field_25169_C.containsKey(16777216 + hashset) && Block.blocksList[hashset] != null && Block.blocksList[hashset].getEnableStats()) {
	// 			obj = StringTranslate.getInstance().translateKeyFormat("stat.mineBlock", new Object[]{Block.blocksList[hashset].translateBlockName()});
	// 			StatList.mineBlockStatArray[hashset] = (new StatCrafting(16777216 + hashset, obj, hashset)).registerStat();
	// 			StatList.field_25185_d.add(StatList.mineBlockStatArray[hashset]);
	// 		}
	// 	}

	// 	for(hashset = 0; hashset < Item.itemsList.length; ++hashset) {
	// 		if(!StatList.field_25169_C.containsKey(16908288 + hashset) && Item.itemsList[hashset] != null) {
	// 			obj = StringTranslate.getInstance().translateKeyFormat("stat.useItem", new Object[]{Item.itemsList[hashset].getStatName()});
	// 			StatList.field_25172_A[hashset] = (new StatCrafting(16908288 + hashset, obj, hashset)).registerStat();
	// 			if(hashset >= Block.blocksList.length) {
	// 				StatList.field_25186_c.add(StatList.field_25172_A[hashset]);
	// 			}
	// 		}

	// 		if(!StatList.field_25169_C.containsKey(16973824 + hashset) && Item.itemsList[hashset] != null && Item.itemsList[hashset].isDamagable()) {
	// 			obj = StringTranslate.getInstance().translateKeyFormat("stat.breakItem", new Object[]{Item.itemsList[hashset].getStatName()});
	// 			StatList.field_25170_B[hashset] = (new StatCrafting(16973824 + hashset, obj, hashset)).registerStat();
	// 		}
	// 	}

	// 	HashSet hashSet6 = new HashSet();
	// 	Iterator obj1 = CraftingManager.getInstance().getRecipeList().iterator();

	// 	while(obj1.hasNext()) {
	// 		Object object7 = obj1.next();
	// 		hashSet6.add(((IRecipe)object7).getRecipeOutput().itemID);
	// 	}

	// 	Iterator iterator2 = FurnaceRecipes.smelting().getSmeltingList().values().iterator();

	// 	while(iterator2.hasNext()) {
	// 		Object object8 = iterator2.next();
	// 		hashSet6.add(((ItemStack)object8).itemID);
	// 	}

	// 	iterator2 = hashSet6.iterator();

	// 	while(iterator2.hasNext()) {
	// 		int k = ((Integer)iterator2.next()).intValue();
	// 		if(!StatList.field_25169_C.containsKey(16842752 + k) && Item.itemsList[k] != null) {
	// 			String s3 = StringTranslate.getInstance().translateKeyFormat("stat.craftItem", new Object[]{Item.itemsList[k].getStatName()});
	// 			StatList.field_25158_z[k] = (new StatCrafting(16842752 + k, s3, k)).registerStat();
	// 		}
	// 	}

	// }

	public static boolean isGUIOpen(Class class1) {
		Minecraft minecraft = getMinecraftInstance();
		return class1 == null ? minecraft.currentScreen == null : (minecraft.currentScreen == null && class1 != null ? false : class1.isInstance(minecraft.currentScreen));
	}

	public static boolean isModLoaded(String s) {
		Class class1 = null;

		try {
			class1 = Class.forName(s);
		} catch (ClassNotFoundException classNotFoundException4) {
			return false;
		}

		if(class1 != null) {
			Iterator iterator = modList.iterator();

			while(iterator.hasNext()) {
				BaseMod basemod = (BaseMod)iterator.next();
				if(class1.isInstance(basemod)) {
					return true;
				}
			}
		}

		return false;
	}

	public static void loadConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canRead()) {
				FileInputStream fileinputstream = new FileInputStream(cfgfile);
				props.load(fileinputstream);
				fileinputstream.close();
			}

		}
	}

	public static BufferedImage loadImage(RenderEngine renderengine, String s) throws Exception {
		TexturePackList texturepacklist = (TexturePackList)getPrivateValue(RenderEngine.class, renderengine, 11);
		InputStream inputstream = texturepacklist.selectedTexturePack.getResourceAsStream(s);
		if(inputstream == null) {
			throw new Exception("Image not found: " + s);
		} else {
			BufferedImage bufferedimage = ImageIO.read(inputstream);
			if(bufferedimage == null) {
				throw new Exception("Image corrupted: " + s);
			} else {
				return bufferedimage;
			}
		}
	}

	public static void OnItemPickup(EntityPlayer entityplayer, ItemStack itemstack) {
		Iterator iterator = modList.iterator();

		while(iterator.hasNext()) {
			BaseMod basemod = (BaseMod)iterator.next();
			basemod.OnItemPickup(entityplayer, itemstack);
		}

	}

	public static void OnTick(Minecraft minecraft) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		if(texPack == null || minecraft.gameSettings.skin != texPack) {
			texturesAdded = false;
			texPack = minecraft.gameSettings.skin;
		}

		if(!texturesAdded && minecraft.renderEngine != null) {
			RegisterAllTextureOverrides(minecraft.renderEngine);
			texturesAdded = true;
		}

		long l = 0L;
		Iterator iterator2;
		Entry entry;
		if(minecraft.theWorld != null) {
			l = minecraft.theWorld.getWorldTime();
			iterator2 = inGameHooks.entrySet().iterator();

			label94:
			while(true) {
				do {
					if(!iterator2.hasNext()) {
						break label94;
					}

					entry = (Entry)iterator2.next();
				} while(clock == l && ((Boolean)entry.getValue()).booleanValue());

				if(!((BaseMod)entry.getKey()).OnTickInGame(minecraft)) {
					iterator2.remove();
				}
			}
		}

		if(minecraft.currentScreen != null) {
			iterator2 = inGUIHooks.entrySet().iterator();

			label81:
			while(true) {
				do {
					if(!iterator2.hasNext()) {
						break label81;
					}

					entry = (Entry)iterator2.next();
				} while(clock == l && ((Boolean)entry.getValue()).booleanValue() & minecraft.theWorld != null);

				if(!((BaseMod)entry.getKey()).OnTickInGUI(minecraft, minecraft.currentScreen)) {
					iterator2.remove();
				}
			}
		}

		if(clock != l) {
			iterator2 = keyList.entrySet().iterator();

			label67:
			while(iterator2.hasNext()) {
				entry = (Entry)iterator2.next();
				Iterator iterator3 = ((Map)entry.getValue()).entrySet().iterator();

				while(true) {
					Entry entry3;
					boolean flag;
					boolean[] aflag;
					boolean flag1;
					do {
						do {
							if(!iterator3.hasNext()) {
								continue label67;
							}

							entry3 = (Entry)iterator3.next();
							flag = Keyboard.isKeyDown(((KeyBinding)entry3.getKey()).keyCode);
							aflag = (boolean[])((boolean[])entry3.getValue());
							flag1 = aflag[1];
							aflag[1] = flag;
						} while(!flag);
					} while(flag1 && !aflag[0]);

					((BaseMod)entry.getKey()).KeyboardEvent((KeyBinding)entry3.getKey());
				}
			}
		}

		clock = l;
	}

	public static void OpenGUI(EntityPlayer entityplayer, GuiScreen guiscreen) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Minecraft minecraft = getMinecraftInstance();
		if(minecraft.thePlayer == entityplayer) {
			if(guiscreen != null) {
				minecraft.displayGuiScreen(guiscreen);
			}

		}
	}

	public static void PopulateChunk(IChunkProvider ichunkprovider, int i, int j, World world) {
		if(!hasInit) {
			init();
			logger.fine("Initialized");
		}

		Random random = new Random(world.getRandomSeed());
		long l = random.nextLong() / 2L * 2L + 1L;
		long l1 = random.nextLong() / 2L * 2L + 1L;
		random.setSeed((long)i * l + (long)j * l1 ^ world.getRandomSeed());
		Iterator iterator = modList.iterator();

		while(iterator.hasNext()) {
			BaseMod basemod = (BaseMod)iterator.next();
			if(ichunkprovider.makeString().equals("RandomLevelSource")) {
				basemod.GenerateSurface(world, random, i << 4, j << 4);
			} else if(ichunkprovider.makeString().equals("HellRandomLevelSource")) {
				basemod.GenerateNether(world, random, i << 4, j << 4);
			}
		}

	}

	private static void readFromClassPath(File file) throws FileNotFoundException, IOException {
		logger.finer("Adding mods from " + file.getCanonicalPath());
		ClassLoader classloader = ModLoader.class.getClassLoader();
		if(!file.isFile() || !file.getName().endsWith(".jar") && !file.getName().endsWith(".zip")) {
			if(file.isDirectory()) {
				Package package7 = ModLoader.class.getPackage();
				if(package7 != null) {
					String string8 = package7.getName().replace('.', File.separatorChar);
					file = new File(file, string8);
				}

				logger.finer("Directory found.");
				File[] file9 = file.listFiles();
				if(file9 != null) {
					for(int i10 = 0; i10 < file9.length; ++i10) {
						String string11 = file9[i10].getName();
						if(file9[i10].isFile() && string11.startsWith("mod_") && string11.endsWith(".class")) {
							addMod(classloader, string11);
						}
					}
				}
			}
		} else {
			logger.finer("Zip found.");
			FileInputStream package1 = new FileInputStream(file);
			ZipInputStream afile = new ZipInputStream(package1);
			Object i = null;

			while(true) {
				ZipEntry s2 = afile.getNextEntry();
				if(s2 == null) {
					package1.close();
					break;
				}

				String s1 = s2.getName();
				if(!s2.isDirectory() && s1.startsWith("mod_") && s1.endsWith(".class")) {
					addMod(classloader, s1);
				}
			}
		}

	}

	private static void readFromModFolder(File file) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		ClassLoader classloader = Minecraft.class.getClassLoader();
		Method method = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		method.setAccessible(true);
		if(!file.isDirectory()) {
			throw new IllegalArgumentException("folder must be a Directory.");
		} else {
			File[] afile = file.listFiles();
			int j;
			File file2;
			if(classloader instanceof URLClassLoader) {
				for(j = 0; j < afile.length; ++j) {
					file2 = afile[j];
					if(file2.isDirectory() || file2.isFile() && (file2.getName().endsWith(".jar") || file2.getName().endsWith(".zip"))) {
						method.invoke(classloader, new Object[]{file2.toURI().toURL()});
					}
				}
			}

			for(j = 0; j < afile.length; ++j) {
				file2 = afile[j];
				if(file2.isDirectory() || file2.isFile() && (file2.getName().endsWith(".jar") || file2.getName().endsWith(".zip"))) {
					logger.finer("Adding mods from " + file2.getCanonicalPath());
					if(!file2.isFile()) {
						if(file2.isDirectory()) {
							Package package11 = ModLoader.class.getPackage();
							if(package11 != null) {
								String string12 = package11.getName().replace('.', File.separatorChar);
								file2 = new File(file2, string12);
							}

							logger.finer("Directory found.");
							File[] file13 = file2.listFiles();
							if(file13 != null) {
								for(int i14 = 0; i14 < file13.length; ++i14) {
									String string15 = file13[i14].getName();
									if(file13[i14].isFile() && string15.startsWith("mod_") && string15.endsWith(".class")) {
										addMod(classloader, string15);
									}
								}
							}
						}
					} else {
						logger.finer("Zip found.");
						FileInputStream package1 = new FileInputStream(file2);
						ZipInputStream afile1 = new ZipInputStream(package1);
						Object k = null;

						while(true) {
							ZipEntry s2 = afile1.getNextEntry();
							if(s2 == null) {
								afile1.close();
								package1.close();
								break;
							}

							String s1 = s2.getName();
							if(!s2.isDirectory() && s1.startsWith("mod_") && s1.endsWith(".class")) {
								addMod(classloader, s1);
							}
						}
					}
				}
			}

		}
	}

	public static KeyBinding[] RegisterAllKeys(KeyBinding[] akeybinding) {
		LinkedList linkedlist = new LinkedList();
		linkedlist.addAll(Arrays.asList(akeybinding));
		Iterator iterator = keyList.values().iterator();

		while(iterator.hasNext()) {
			Map map = (Map)iterator.next();
			linkedlist.addAll(map.keySet());
		}

		return (KeyBinding[])((KeyBinding[])linkedlist.toArray(new KeyBinding[0]));
	}

	public static void RegisterAllTextureOverrides(RenderEngine renderengine) {
		animList.clear();
		Minecraft minecraft = getMinecraftInstance();
		Iterator texturefx = modList.iterator();

		while(texturefx.hasNext()) {
			BaseMod basemod = (BaseMod)texturefx.next();
			basemod.RegisterAnimation(minecraft);
		}

		Iterator iterator2 = animList.iterator();

		while(iterator2.hasNext()) {
			TextureFX texturefx1 = (TextureFX)iterator2.next();
			renderengine.registerTextureFX(texturefx1);
		}

		iterator2 = overrides.entrySet().iterator();

		while(iterator2.hasNext()) {
			Entry entry = (Entry)iterator2.next();
			Iterator iterator3 = ((Map)entry.getValue()).entrySet().iterator();

			while(iterator3.hasNext()) {
				Entry entry1 = (Entry)iterator3.next();
				String s = (String)entry1.getKey();
				int i = ((Integer)entry1.getValue()).intValue();
				int j = ((Integer)entry.getKey()).intValue();

				try {
					BufferedImage exception = loadImage(renderengine, s);
					ModTextureStatic modtexturestatic = new ModTextureStatic(i, j, exception);
					renderengine.registerTextureFX(modtexturestatic);
				} catch (Exception exception13) {
					logger.throwing("ModLoader", "RegisterAllTextureOverrides", exception13);
					ThrowException(exception13);
					throw new RuntimeException(exception13);
				}
			}
		}

	}

	public static void RegisterBlock(Block block) {
		RegisterBlock(block, (Class)null);
	}

	public static void RegisterBlock(Block block, Class class1) {
		try {
			if(block == null) {
				throw new IllegalArgumentException("block parameter cannot be null.");
			}

			List nosuchmethodexception = (List)field_blockList.get((Object)null);
			nosuchmethodexception.add(block);
			int i = block.blockID;
			ItemBlock itemblock = null;
			if(class1 != null) {
				itemblock = (ItemBlock)class1.getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{i - 256});
			} else {
				itemblock = new ItemBlock(i - 256);
			}

			if(Block.blocksList[i] != null && Item.itemsList[i] == null) {
				Item.itemsList[i] = itemblock;
			}
		} catch (IllegalArgumentException illegalArgumentException5) {
			logger.throwing("ModLoader", "RegisterBlock", illegalArgumentException5);
			ThrowException(illegalArgumentException5);
		} catch (IllegalAccessException illegalAccessException6) {
			logger.throwing("ModLoader", "RegisterBlock", illegalAccessException6);
			ThrowException(illegalAccessException6);
		} catch (SecurityException securityException7) {
			logger.throwing("ModLoader", "RegisterBlock", securityException7);
			ThrowException(securityException7);
		} catch (InstantiationException instantiationException8) {
			logger.throwing("ModLoader", "RegisterBlock", instantiationException8);
			ThrowException(instantiationException8);
		} catch (InvocationTargetException invocationTargetException9) {
			logger.throwing("ModLoader", "RegisterBlock", invocationTargetException9);
			ThrowException(invocationTargetException9);
		} catch (NoSuchMethodException noSuchMethodException10) {
			logger.throwing("ModLoader", "RegisterBlock", noSuchMethodException10);
			ThrowException(noSuchMethodException10);
		}

	}

	public static void RegisterEntityID(Class class1, String s, int i) {
		try {
			method_RegisterEntityID.invoke((Object)null, new Object[]{class1, s, i});
		} catch (IllegalArgumentException illegalArgumentException4) {
			logger.throwing("ModLoader", "RegisterEntityID", illegalArgumentException4);
			ThrowException(illegalArgumentException4);
		} catch (IllegalAccessException illegalAccessException5) {
			logger.throwing("ModLoader", "RegisterEntityID", illegalAccessException5);
			ThrowException(illegalAccessException5);
		} catch (InvocationTargetException invocationTargetException6) {
			logger.throwing("ModLoader", "RegisterEntityID", invocationTargetException6);
			ThrowException(invocationTargetException6);
		}

	}

	public static void RegisterKey(BaseMod basemod, KeyBinding keybinding, boolean flag) {
		Object obj = (Map)keyList.get(basemod);
		if(obj == null) {
			obj = new HashMap();
		}

		boolean[] aflag = new boolean[]{flag, false};
		((Map)((Map)obj)).put(keybinding, aflag);
		keyList.put(basemod, obj);
	}

	public static void RegisterTileEntity(Class class1, String s) {
		RegisterTileEntity(class1, s, (TileEntitySpecialRenderer)null);
	}

	public static void RegisterTileEntity(Class class1, String s, TileEntitySpecialRenderer tileentityspecialrenderer) {
		try {
			method_RegisterTileEntity.invoke((Object)null, new Object[]{class1, s});
			if(tileentityspecialrenderer != null) {
				TileEntityRenderer invocationtargetexception = TileEntityRenderer.instance;
				Map map = (Map)field_TileEntityRenderers.get(invocationtargetexception);
				map.put(class1, tileentityspecialrenderer);
				tileentityspecialrenderer.setTileEntityRenderer(invocationtargetexception);
			}
		} catch (IllegalArgumentException illegalArgumentException5) {
			logger.throwing("ModLoader", "RegisterTileEntity", illegalArgumentException5);
			ThrowException(illegalArgumentException5);
		} catch (IllegalAccessException illegalAccessException6) {
			logger.throwing("ModLoader", "RegisterTileEntity", illegalAccessException6);
			ThrowException(illegalAccessException6);
		} catch (InvocationTargetException invocationTargetException7) {
			logger.throwing("ModLoader", "RegisterTileEntity", invocationTargetException7);
			ThrowException(invocationTargetException7);
		}

	}

	public static void RemoveSpawn(Class class1, EnumCreatureType enumcreaturetype) {
		RemoveSpawn((Class)class1, enumcreaturetype, (BiomeGenBase[])null);
	}

	public static void RemoveSpawn(Class class1, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
		if(class1 == null) {
			throw new IllegalArgumentException("entityClass cannot be null");
		} else if(enumcreaturetype == null) {
			throw new IllegalArgumentException("spawnList cannot be null");
		} else {
			if(abiomegenbase == null) {
				abiomegenbase = standardBiomes;
			}

			for(int i = 0; i < abiomegenbase.length; ++i) {
				List list = abiomegenbase[i].getSpawnableList(enumcreaturetype);
				if(list != null) {
					Iterator iterator = list.iterator();

					while(iterator.hasNext()) {
						SpawnListEntry spawnlistentry = (SpawnListEntry)iterator.next();
						if(spawnlistentry.entityClass == class1) {
							iterator.remove();
						}
					}
				}
			}

		}
	}

	public static void RemoveSpawn(String s, EnumCreatureType enumcreaturetype) {
		RemoveSpawn((String)s, enumcreaturetype, (BiomeGenBase[])null);
	}

	public static void RemoveSpawn(String s, EnumCreatureType enumcreaturetype, BiomeGenBase[] abiomegenbase) {
		Class class1 = (Class)classMap.get(s);
		if(class1 != null && EntityLiving.class.isAssignableFrom(class1)) {
			RemoveSpawn(class1, enumcreaturetype, abiomegenbase);
		}

	}

	public static boolean RenderBlockIsItemFull3D(int i) {
		return !blockSpecialInv.containsKey(i) ? i == 16 : ((Boolean)blockSpecialInv.get(i)).booleanValue();
	}

	public static void RenderInvBlock(RenderBlocks renderblocks, Block block, int i, int j) {
		BaseMod basemod = (BaseMod)blockModels.get(j);
		if(basemod != null) {
			basemod.RenderInvBlock(renderblocks, block, i, j);
		}
	}

	public static boolean RenderWorldBlock(RenderBlocks renderblocks, IBlockAccess iblockaccess, int i, int j, int k, Block block, int l) {
		BaseMod basemod = (BaseMod)blockModels.get(l);
		return basemod == null ? false : basemod.RenderWorldBlock(renderblocks, iblockaccess, i, j, k, block, l);
	}

	public static void saveConfig() throws IOException {
		cfgdir.mkdir();
		if(cfgfile.exists() || cfgfile.createNewFile()) {
			if(cfgfile.canWrite()) {
				FileOutputStream fileoutputstream = new FileOutputStream(cfgfile);
				props.store(fileoutputstream, "ModLoader Config");
				fileoutputstream.close();
			}

		}
	}

	public static void SetInGameHook(BaseMod basemod, boolean flag, boolean flag1) {
		if(flag) {
			inGameHooks.put(basemod, flag1);
		} else {
			inGameHooks.remove(basemod);
		}

	}

	public static void SetInGUIHook(BaseMod basemod, boolean flag, boolean flag1) {
		if(flag) {
			inGUIHooks.put(basemod, flag1);
		} else {
			inGUIHooks.remove(basemod);
		}

	}

	public static void setPrivateValue(Class class1, Object obj, int i, Object obj1) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field illegalaccessexception = class1.getDeclaredFields()[i];
			illegalaccessexception.setAccessible(true);
			int j = field_modifiers.getInt(illegalaccessexception);
			if((j & 16) != 0) {
				field_modifiers.setInt(illegalaccessexception, j & -17);
			}

			illegalaccessexception.set(obj, obj1);
		} catch (IllegalAccessException illegalAccessException6) {
			logger.throwing("ModLoader", "setPrivateValue", illegalAccessException6);
			ThrowException("An impossible error has occured!", illegalAccessException6);
		}

	}

	public static void setPrivateValue(Class class1, Object obj, String s, Object obj1) throws IllegalArgumentException, SecurityException, NoSuchFieldException {
		try {
			Field illegalaccessexception = class1.getDeclaredField(s);
			int i = field_modifiers.getInt(illegalaccessexception);
			if((i & 16) != 0) {
				field_modifiers.setInt(illegalaccessexception, i & -17);
			}

			illegalaccessexception.setAccessible(true);
			illegalaccessexception.set(obj, obj1);
		} catch (IllegalAccessException illegalAccessException6) {
			logger.throwing("ModLoader", "setPrivateValue", illegalAccessException6);
			ThrowException("An impossible error has occured!", illegalAccessException6);
		}

	}

	private static void setupProperties(Class class1) throws IllegalArgumentException, IllegalAccessException, IOException, SecurityException, NoSuchFieldException {
		Properties properties = new Properties();
		File file = new File(cfgdir, class1.getName() + ".cfg");
		if(file.exists() && file.canRead()) {
			properties.load(new FileInputStream(file));
		}

		StringBuilder stringbuilder = new StringBuilder();
		Field[] afield;
		int j = (afield = class1.getFields()).length;

		for(int i = 0; i < j; ++i) {
			Field field = afield[i];
			if((field.getModifiers() & 8) != 0 && field.isAnnotationPresent(MLProp.class)) {
				Class class2 = field.getType();
				MLProp mlprop = (MLProp)field.getAnnotation(MLProp.class);
				String s = mlprop.name().length() != 0 ? mlprop.name() : field.getName();
				Object obj = field.get((Object)null);
				StringBuilder stringbuilder1 = new StringBuilder();
				if(mlprop.min() != Double.NEGATIVE_INFINITY) {
					stringbuilder1.append(String.format(",>=%.1f", new Object[]{mlprop.min()}));
				}

				if(mlprop.max() != Double.POSITIVE_INFINITY) {
					stringbuilder1.append(String.format(",<=%.1f", new Object[]{mlprop.max()}));
				}

				StringBuilder stringbuilder2 = new StringBuilder();
				if(mlprop.info().length() > 0) {
					stringbuilder2.append(" -- ");
					stringbuilder2.append(mlprop.info());
				}

				stringbuilder.append(String.format("%s (%s:%s%s)%s\n", new Object[]{s, class2.getName(), obj, stringbuilder1, stringbuilder2}));
				if(properties.containsKey(s)) {
					String s1 = properties.getProperty(s);
					Object obj1 = null;
					if(class2.isAssignableFrom(String.class)) {
						obj1 = s1;
					} else if(class2.isAssignableFrom(Integer.TYPE)) {
						obj1 = Integer.parseInt(s1);
					} else if(class2.isAssignableFrom(Short.TYPE)) {
						obj1 = Short.parseShort(s1);
					} else if(class2.isAssignableFrom(Byte.TYPE)) {
						obj1 = Byte.parseByte(s1);
					} else if(class2.isAssignableFrom(Boolean.TYPE)) {
						obj1 = Boolean.parseBoolean(s1);
					} else if(class2.isAssignableFrom(Float.TYPE)) {
						obj1 = Float.parseFloat(s1);
					} else if(class2.isAssignableFrom(Double.TYPE)) {
						obj1 = Double.parseDouble(s1);
					}

					if(obj1 != null) {
						if(obj1 instanceof Number) {
							double d = ((Number)obj1).doubleValue();
							if(mlprop.min() != Double.NEGATIVE_INFINITY && d < mlprop.min() || mlprop.max() != Double.POSITIVE_INFINITY && d > mlprop.max()) {
								continue;
							}
						}

						logger.finer(s + " set to " + obj1);
						if(!obj1.equals(obj)) {
							field.set((Object)null, obj1);
						}
					}
				} else {
					logger.finer(s + " not in config, using default: " + obj);
					properties.setProperty(s, obj.toString());
				}
			}
		}

		if(!properties.isEmpty() && (file.exists() || file.createNewFile()) && file.canWrite()) {
			properties.store(new FileOutputStream(file), stringbuilder.toString());
		}

	}

	public static void TakenFromCrafting(EntityPlayer entityplayer, ItemStack itemstack) {
		Iterator iterator = modList.iterator();

		while(iterator.hasNext()) {
			BaseMod basemod = (BaseMod)iterator.next();
			basemod.TakenFromCrafting(entityplayer, itemstack);
		}

	}

	public static void TakenFromFurnace(EntityPlayer entityplayer, ItemStack itemstack) {
		Iterator iterator = modList.iterator();

		while(iterator.hasNext()) {
			BaseMod basemod = (BaseMod)iterator.next();
			basemod.TakenFromFurnace(entityplayer, itemstack);
		}

	}

	public static void ThrowException(String s, Throwable throwable) {
		Minecraft minecraft = getMinecraftInstance();
		if(minecraft != null) {
			minecraft.displayUnexpectedThrowable(new UnexpectedThrowable(s, throwable));
		} else {
			throw new RuntimeException(throwable);
		}
	}

	private static void ThrowException(Throwable throwable) {
		ThrowException("Exception occured in ModLoader", throwable);
	}
}
