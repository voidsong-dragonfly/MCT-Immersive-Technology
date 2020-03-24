package mctmods.immersivetechnology;

//import blusunrize.immersiveengineering.common.Config;
import mctmods.immersivetechnology.common.CommonProxy;
import mctmods.immersivetechnology.common.ITContent;
import mctmods.immersivetechnology.common.util.ITLogger;
import mctmods.immersivetechnology.common.util.ITSounds;
import mctmods.immersivetechnology.common.util.compat.ITCompatModule;
//import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
//import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.common.Mod.EventHandler;
//import net.minecraftforge.fml.common.Mod.Instance;
//import net.minecraftforge.fml.common.SidedProxy;
//import net.minecraftforge.fml.common.event.*;
//import net.minecraftforge.fml.common.network.NetworkRegistry;
//import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(ImmersiveTechnology.MODID)
public class ImmersiveTechnology {

	public static final String MODID = "immersivetechnology";
	public static final String NAME = "Immersive Technology";
	public static final String VERSION = "${version}";
	
}
/*
	@SidedProxy(clientSide = "mctmods.immersivetechnology.client.ClientProxy" , serverSide = "mctmods.immersivetechnology.common.CommonProxy")
	public static CommonProxy proxy;
	public static final SimpleNetworkWrapper packetHandler = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

	public void preInit(FMLPreInitializationEvent event) {
		ITLogger.logger = event.getModLog();
		Config.preInit(event);
		ITContent.preInit();
		proxy.preInit();
		ITCompatModule.doModulesPreInit();
	}

	public void init(FMLInitializationEvent event) {
		ITContent.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		proxy.preInitEnd();
		proxy.init();
		ITSounds.init();
		ITCompatModule.doModulesInit();
		proxy.initEnd();
	}

	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit();
		ITCompatModule.doModulesPostInit();
		proxy.postInitEnd();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		ITCompatModule.doModulesLoadComplete();
	}

	public void serverStarted(FMLServerStartedEvent event) {
	}

	public static CreativeTabs creativeTab = new CreativeTabs(MODID) {
		@Override
		public ItemStack getTabIconItem() {
			return ItemStack.EMPTY;
		}
		@Override
		public ItemStack getIconItemStack() {
			return new ItemStack(ITContent.blockValve, 1, 0);
		}
	};

}*/