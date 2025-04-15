package dev.lurien.bot.commands;

import dev.lurien.bot.LurienBot;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager extends ListenerAdapter {
    private final List<ICommand> commands = new ArrayList<>();

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        for(ICommand command : commands) {
            if(command.getOptions() == null) {
                LurienBot.getGuild().upsertCommand(command.getName(), command.getDescription()).queue();
            } else {
                LurienBot.getGuild().upsertCommand(command.getName(), command.getDescription()).addOptions(command.getOptions()).queue();
            }
            for (String alias : command.getAliases()) {
                if(command.getOptions() == null) {
                    LurienBot.getGuild().upsertCommand(alias, command.getDescription()).queue();
                } else {
                    LurienBot.getGuild().upsertCommand(alias, command.getDescription()).addOptions(command.getOptions()).queue();
                }
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        if(event.getMember() == null || event.getGuild() == null) return;
        if(!LurienBot.checkGuild(event.getGuild(), event.getInteraction().getHook())) return;

        for(ICommand command : commands) {
            if(command.getName().equals(event.getName())) {
                command.execute(event);
                return;
            }
            for (String alias : command.getAliases()) {
                if(command.getName().equals(alias)){
                    command.execute(event);
                    return;
                }
            }
        }
    }

    public void add(ICommand command) {
        commands.add(command);
    }
}
