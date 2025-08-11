package net.minecraft.src;

public class ChunkBlockMap {
	private static byte[] convTable = new byte[256];

	public static void removeUnknownBlocks(byte[] abyte0) {
		for(int i = 0; i < abyte0.length; ++i) {
			abyte0[i] = convTable[abyte0[i] & 255];
		}

	}

	public static void enableBedrockConversion() {
		convTable[Block.bedrock.blockID] = (byte)Block.stone.blockID;
	}

	public static void disableBedrockConversion() {
		convTable[Block.bedrock.blockID] = (byte)Block.bedrock.blockID;
	}

	static {
		try {
			convTable[0] = 0;

			for(int exception = 1; exception < 256; ++exception) {
				byte id = (byte)exception;
				if(Block.blocksList[id & 255] == null) {
					id = 0;
				}

				convTable[exception] = id;
			}
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

	}
}
