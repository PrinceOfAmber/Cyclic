package com.lothrazar.cyclic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.lothrazar.cyclic.event.ClientInputEvents;
import com.lothrazar.cyclic.event.ItemEvents;
import com.lothrazar.cyclic.event.PotionEvents;
import com.lothrazar.cyclic.registry.CuriosRegistry;
import com.lothrazar.cyclic.registry.PacketRegistry;
import com.lothrazar.cyclic.registry.PotionRegistry;
import com.lothrazar.cyclic.setup.ClientProxy;
import com.lothrazar.cyclic.setup.ConfigHandler;
import com.lothrazar.cyclic.setup.IProxy;
import com.lothrazar.cyclic.setup.ServerProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ModCyclic.MODID)
public class ModCyclic {

  public static final String certificateFingerprint = "@FINGERPRINT@";
  public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
  public static final String MODID = "cyclic";
  private static final Logger LOGGER = LogManager.getLogger();

  public ModCyclic() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    ConfigHandler.loadConfig(FMLPaths.CONFIGDIR.get().resolve(MODID + ".toml"));
  }

  private void setup(final FMLCommonSetupEvent event) {
    //now all blocks/items exist
    CuriosRegistry.setup(event);
    PotionRegistry.setup(event);
    PacketRegistry.setup();
    proxy.setup();
    MinecraftForge.EVENT_BUS.register(new ClientInputEvents());
    MinecraftForge.EVENT_BUS.register(new PotionEvents());
    MinecraftForge.EVENT_BUS.register(new ItemEvents());
  }

  public static void error(Object... list) {
    LOGGER.error(list);
  }

  public static void log(Object... list) {
    LOGGER.info(list);
  }
}
