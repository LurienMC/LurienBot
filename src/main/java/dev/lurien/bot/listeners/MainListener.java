package dev.lurien.bot.listeners;

import dev.lurien.bot.LurienBot;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MainListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if(event.getGuild().getIdLong() == 1162834261011857568L){
            LurienBot.setGuild(event.getGuild());
            LurienBot.setHeadStaffRole(event.getGuild().getRoleById(1361506899853312050L));
        }
    }
}
