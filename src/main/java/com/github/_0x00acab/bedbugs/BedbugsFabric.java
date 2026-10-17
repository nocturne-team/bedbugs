package com.github._0x00acab.bedbugs;

//? if fabric {
import net.fabricmc.api.ModInitializer;

import static com.github._0x00acab.bedbugs.BedbugsMod.*;

public class BedbugsFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        LOGGER.info("Bedbugs loaded");
    }
}
//?}
