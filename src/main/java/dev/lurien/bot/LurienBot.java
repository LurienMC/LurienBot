package dev.lurien.bot;

import dev.lurien.bot.commands.*;
import dev.lurien.bot.commands.minecraft.VinculationCommand;
import dev.lurien.bot.configuration.LinkedAccountsConfig;
import dev.lurien.bot.listeners.LinkedAccountsListener;
import dev.lurien.bot.listeners.MainListener;
import dev.lurien.bot.managers.LinkedAccountsManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public final class LurienBot extends JavaPlugin {

    @Getter
    private static JDA jda;
    @Getter @Setter
    private static Guild guild;
    @Getter
    private static final CommandsManager commandsManager = new CommandsManager();
    @Getter
    private static final String tickEmoji = "<:tick_luriencito:1361495339202253012>";
    @Getter
    private static final String crossEmoji = "<:x_luriencito:1361496153643941959>";
    @Getter
    private static final String warnEmoji = "<:warn_luriencito:1361500860026192053>";
    @Getter
    private static LinkedAccountsConfig linkedAccountsConfig;

    @Getter @Setter
    private static Role headStaffRole;
    @Getter @Setter
    private static Role moderatorRole;


    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void onEnable() {
        File envFile = new File(getDataFolder(), ".env");
        if(!envFile.exists()) {
            try {
                envFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Dotenv dotenv = Dotenv.configure()
                .directory(getDataFolder().getAbsolutePath())
                .filename(".env")
                .load();

        registerCommands();

        linkedAccountsConfig = new LinkedAccountsConfig(this);
        LinkedAccountsManager.load(linkedAccountsConfig.getConfig());

        jda = JDABuilder.createDefault(dotenv.get("TOKEN"), Arrays.asList(GatewayIntent.values()))
                .setStatus(OnlineStatus.ONLINE)
                .setActivity(Activity.playing("LurienMC"))
                .addEventListeners(new MainListener(), commandsManager, new LinkedAccountsListener())
                .build();
    }

    private void registerCommands() {
        getCommand("vinculate").setExecutor(new VinculationCommand());
        getCommand("vinculate").setTabCompleter(new VinculationCommand());
        commandsManager.add(new StaffModeTopCommand());
        commandsManager.add(new NukeCommand());
        commandsManager.add(new ShipCommand());
        commandsManager.add(new EighthBallCommand());
        commandsManager.add(new LinkAccountCommand());
    }

    @Override
    public void onDisable() {
        if(jda != null) {
            jda.shutdownNow();
            jda = null;
        }
    }

    public static boolean checkGuild(Guild otherGuild){
        return otherGuild.getIdLong() == getGuild().getIdLong();
    }

    public static boolean checkGuild(Guild otherGuild, InteractionHook interaction){
        if(!checkGuild(otherGuild)){
            interaction.sendMessage(":x: Esta función no esta disponible fuera de LurienMC.").queue();
            return false;
        }
        return true;
    }
}
