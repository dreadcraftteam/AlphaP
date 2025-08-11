package net.minecraft_server.src;

public class ItemLeaves extends ItemBlock {
	public ItemLeaves(int i1) {
		super(i1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int i1) {
		return i1 | 8;
	}
}
