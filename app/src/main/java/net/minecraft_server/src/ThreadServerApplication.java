package net.minecraft_server.src;

import net.minecraft_server.server.MinecraftServer;

public final class ThreadServerApplication extends Thread {
	final MinecraftServer mcServer;

	public ThreadServerApplication(String string1, MinecraftServer minecraftServer2) {
		super(string1);
		this.mcServer = minecraftServer2;
	}

	public void run() {
		this.mcServer.run();
	}
}
