package dev.lurien.bot.commands;

import dev.lurien.bot.LurienBot;
import dev.lurien.staff.lurienStaff.managers.StaffModeManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StaffModeTopCommand implements ICommand{
    @Override
    public String getName() {
        return "staffmodetop";
    }

    @Override
    public String getDescription() {
        return "Enseña el top staffs en servicio";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.INTEGER, "limite", "Limite de entradas", true));
    }

    @Override
    public List<String> getAliases() {
        return List.of("topstaffmode", "topstaffsenservicio");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(false).queue();

        if(!Objects.requireNonNull(event.getMember()).getRoles().contains(LurienBot.getHeadStaffRole())){
            event.getHook().sendMessage(LurienBot.getCrossEmoji()+" No tienes permisos.").queue();
            return;
        }

        int limit = Objects.requireNonNull(event.getOption("limite")).getAsInt();

        Map<String, String> top = StaffModeManager.getTopStaffsMode(limit);

        if(top.isEmpty()){
            event.getHook().sendMessage(LurienBot.getWarnEmoji()+" El top está vacío.").queue();
            return;
        }

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(0xAE3DFF)
                .setTitle("Top "+limit+" staffs con más tiempo en servicio")
                .setFooter("Created by @octdamfar", event.getGuild().getIconUrl());

        int i = 1;
        for (Map.Entry<String, String> entry : top.entrySet()) {
            if(i == 1){
                eb.setThumbnail("https://visage.surgeplay.com/full/"+entry.getKey());
            }
            eb.appendDescription("**#"+i+" "+entry.getKey()+"**: "+entry.getValue());
            i++;
        }

        eb.appendDescription("\n\n__Staff de LurienMC__");
        event.getHook().sendMessageEmbeds(eb.build()).queue();
    }
}
