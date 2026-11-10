package com.github._0x00acab.bedbugs;

import java.nio.file.Path;

public sealed interface ModLoaderAccess {
	ModLoaderAccess INSTANCE =
		//? if fabric {
		new FabricLoaderAccess();
		//?} elif neoforge {
		/*new NeoForgeLoaderAccess();
		*///?} else {
		/*new ForgeLoaderAccess();
		*///?}

	Path getConfigDir();

	//? if fabric {
	final class FabricLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.fabricmc.loader.api.FabricLoader.getInstance().getConfigDir();
		}
	}
	//?} elif neoforge {
	/*final class NeoForgeLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.neoforged.fml.loading.FMLPaths.CONFIGDIR.get();
		}
	}
	*///?} elif forge {
	/*final class ForgeLoaderAccess implements ModLoaderAccess {
		@Override
		public Path getConfigDir() {
			return net.minecraftforge.fml.loading.FMLPaths.CONFIGDIR.get();
		}
	}
	*///?}
}
