package dev.lurien.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;

public interface ICommand {

    String getName();

    String getDescription();

    List<OptionData> getOptions();

    List<String> getAliases();

    void execute(SlashCommandInteractionEvent event);
}
