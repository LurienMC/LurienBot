package dev.lurien.bot.listeners;

import dev.lurien.bot.LurienBot;
import dev.lurien.staff.lurienStaff.LurienStaff;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class MainListener extends ListenerAdapter {

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        if(event.getGuild().getIdLong() == 1162834261011857568L){
            LurienBot.setGuild(event.getGuild());
            LurienBot.setHeadStaffRole(event.getGuild().getRoleById(1361506899853312050L));
            LurienStaff.setStaffModeChannel(event.getGuild().getNewsChannelById(1361900553587921027L));
            LurienStaff.setChatChannel(event.getGuild().getNewsChannelById(1361906583118020779L));
            LurienStaff.setActivityChannel(event.getGuild().getNewsChannelById(1361900904554696825L));
        }
    }
}
