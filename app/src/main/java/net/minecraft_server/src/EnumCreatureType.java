package net.minecraft_server.src;

public enum EnumCreatureType {
	monster("monster", 0, IMob.class, 70, Material.air, false),
	creature("creature", 1, EntityAnimal.class, 15, Material.air, true),
	waterCreature("waterCreature", 2, EntityWaterMob.class, 5, Material.water, true);

	private final Class creatureClass;
	private final int maxNumberOfCreature;
	private final Material creatureMaterial;
	private final boolean field_21106_g;
	private static final EnumCreatureType[] field_6155_e = new EnumCreatureType[] { monster, creature, waterCreature };

	private EnumCreatureType(String s, int i, Class class1, int j, Material material, boolean flag) {
		this.creatureClass = class1;
		this.maxNumberOfCreature = j;
		this.creatureMaterial = material;
		this.field_21106_g = flag;
	}

	public Class getCreatureClass() {
		return this.creatureClass;
	}

	public int getMaxNumberOfCreature() {
		return this.maxNumberOfCreature;
	}

	public Material getCreatureMaterial() {
		return this.creatureMaterial;
	}

	public boolean getPeacefulCreature() {
		return this.field_21106_g;
	}
}
