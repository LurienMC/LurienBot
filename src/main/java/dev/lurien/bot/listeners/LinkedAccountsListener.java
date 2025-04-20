package dev.lurien.bot.listeners;

import dev.lurien.bot.LurienBot;
import dev.lurien.bot.managers.LinkedAccountsManager;
import dev.lurien.bot.model.DiscordLinkRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class LinkedAccountsListener extends ListenerAdapter {

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (Objects.requireNonNull(event.getButton().getId()).startsWith("cvi;")) {
            String[] args = event.getButton().getId().split(";");
            String code = args[1];

            Optional<DiscordLinkRequest> match = LinkedAccountsManager.toLink.stream()
                    .filter(req -> req.code().equals(code))
                    .findFirst();

            if (match.isPresent()) {
                LinkedAccountsManager.toLink.remove(match.get());
                LinkedAccountsManager.save(LurienBot.getLinkedAccountsConfig().getConfig());

                event.reply(LurienBot.getTickEmoji() + " La solicitud de vinculación fue cancelada.").setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }else{
                event.reply(LurienBot.getCrossEmoji() + " La solicitud ya fue cancelada.").setEphemeral(true).queue();
                event.getMessage().delete().queue();
            }
        }
        // STAFF
        else if(Objects.requireNonNull(event.getButton().getId()).startsWith("cvis;")){

            if(!Objects.requireNonNull(event.getMember()).getRoles().contains(LurienBot.getHeadStaffRole())){
                event.reply(LurienBot.getCrossEmoji()+" No tienes permisos.").queue();
                return;
            }

            String[] args = event.getButton().getId().split(";");
            String code = args[1];

            Optional<DiscordLinkRequest> match = LinkedAccountsManager.toLink.stream()
                    .filter(req -> req.code().equals(code))
                    .findFirst();

            if (match.isPresent()) {
                LinkedAccountsManager.toLink.remove(match.get());
                LinkedAccountsManager.save(LurienBot.getLinkedAccountsConfig().getConfig());

                if(Objects.requireNonNull(event.getGuild()).getMemberById(match.get().memberID()) != null){
                    Objects.requireNonNull(event.getGuild().getMemberById(match.get().memberID())).getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessageEmbeds(new EmbedBuilder()
                                .setColor(0xf90003)
                                .setAuthor("Tu vinculación a "+match.get().nick()+" fue cancelada por un staff.").build()).queue();
                    });
                }

                event.reply(LurienBot.getTickEmoji() + " La solicitud de vinculación fue cancelada.").setEphemeral(false).queue();
            }else{
                event.reply(LurienBot.getCrossEmoji() + " La solicitud ya fue cancelada.").setEphemeral(true).queue();
            }
        }else if(event.getButton().getId().startsWith("fvi;")){

            if(!Objects.requireNonNull(event.getMember()).getRoles().contains(LurienBot.getHeadStaffRole())){
                event.reply(LurienBot.getCrossEmoji()+" No tienes permisos.").queue();
                return;
            }

            String[] args = event.getButton().getId().split(";");
            String code = args[1];

            Optional<DiscordLinkRequest> match = LinkedAccountsManager.toLink.stream()
                    .filter(req -> req.code().equals(code))
                    .findFirst();

            if (match.isPresent()) {

                if(LinkedAccountsManager.isLinked(match.get().nick()) || LinkedAccountsManager.isLinked(match.get().memberID())){
                    event.reply(LurienBot.getCrossEmoji()+" Una de las dos cuentas ya se vinculó con otra.").queue();
                    return;
                }

                if(Objects.requireNonNull(event.getGuild()).getMemberById(match.get().memberID()) != null){
                    Objects.requireNonNull(event.getGuild().getMemberById(match.get().memberID())).getUser().openPrivateChannel().queue(privateChannel -> {
                        privateChannel.sendMessageEmbeds(new EmbedBuilder()
                                .setColor(0xf90003)
                                .setAuthor("Tu vinculación a "+match.get().nick()+" ha sido forzada por un administrador.").build()).queue();
                    });
                }

                LinkedAccountsManager.vinculateOffline(match.get(), match.get().nick());
            }else{
                event.reply(LurienBot.getCrossEmoji() + " La cuenta ya fue vinculada o fue cancelada..").setEphemeral(true).queue();
            }
        }
    }
}
