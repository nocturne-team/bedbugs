package com.github._0x00acab.bedbugs;

//? if >=1.17 {
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//?} else {
/*import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
*///?}

public class BedbugsMod {
    //? if >=1.17 {
    public static final Logger LOGGER = LoggerFactory.getLogger("bedbugs");
    //?} else
    /*public static final Logger LOGGER = LogManager.getLogger("bedbugs");*/
    public static final String VERSION = /*$ mod_version*/ "0.1.0";
    public static final String MINECRAFT = /*$ minecraft*/ "26.3";
}
