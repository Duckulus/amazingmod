package eu.dhlserv.gabikmod;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.Display;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = GabikMod.MODID, version = GabikMod.VERSION, useMetadata = true)
public class GabikMod {
    public static final String MODID = "GabikMod";
    public static final String VERSION = "1.0";

    private static GabikMod instance;

    private int multiplier = 55;
    private long lastTimestampInGame;

    public static GabikMod inst() {
        return instance;
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
        FMLCommonHandler.instance().bus().register(this);
        ClientCommandHandler.instance.registerCommand(new ExecuteAllCommand());
    }

    public int getFps() {
        String[] split = Minecraft.getMinecraft().debug.split(" ");
        return split.length > 0 ? split[0].length() > 0 ? Integer.valueOf(split[0]) : 0 : 0;
    }

    public float getMultiplier() {
        return getFps() > 120 ? getFps() > 200 ? multiplier : 0.5F * multiplier : 0;
    }

    public float getAccumulationValue() {
        if (Minecraft.getMinecraft().theWorld == null) {
            return 0;
        }
        float percent = getMultiplier() * 10.0F;
        if (Minecraft.getMinecraft().currentScreen == null) {
            this.lastTimestampInGame = System.currentTimeMillis();
            if (percent > 996.0F) {
                percent = 996.0F;
            }
        } else {
            if (percent > 990.0F) {
                percent = 990.0F;
            }
        }

        long fadeOut = System.currentTimeMillis() - this.lastTimestampInGame;

        if (fadeOut > 10000L) {
            return 0;
        }

        if (percent < 0.0F) {
            percent = 0.0F;
        }

        return percent / 1000.0F;
    }

    private int prevFpslimit;
    private boolean wasInactive;

    @SubscribeEvent
    public void onTick(ClientTickEvent e) {
        if (Minecraft.getMinecraft().theWorld == null) {
            if (wasInactive) {
                Minecraft.getMinecraft().gameSettings.limitFramerate = prevFpslimit;
                wasInactive = false;
            }
            return;
        }

        boolean active = Display.isActive();

        if (!active && Minecraft.getMinecraft().gameSettings.limitFramerate != 30) {
            prevFpslimit = Minecraft.getMinecraft().gameSettings.limitFramerate;
            Minecraft.getMinecraft().gameSettings.limitFramerate = 30;
            wasInactive = true;
        } else if (active && wasInactive) {
            Minecraft.getMinecraft().gameSettings.limitFramerate = prevFpslimit;
            wasInactive = false;
        }
    }

    public class ExecuteAllCommand extends CommandBase {

        @Override
        public String getCommandName() {
            return "executeall";
        }

        @Override
        public String getCommandUsage(ICommandSender p_71518_1_) {
            return "";
        }

        @Override
        public int getRequiredPermissionLevel() {
            return 0;
        }

        @Override
        public void processCommand(ICommandSender sender, String[] args) {
            try {
                File textFile = new File(Minecraft.getMinecraft().mcDataDir, "commands.settings");
                if (textFile.exists()) {
                    List<String> lines = FileUtils.readLines(textFile);
                    for (String line : lines) {
                        Minecraft.getMinecraft().thePlayer.sendChatMessage("/" + line);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
