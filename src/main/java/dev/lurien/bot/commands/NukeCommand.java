package dev.lurien.bot.commands;

import dev.lurien.bot.LurienBot;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.time.OffsetDateTime;
import java.util.*;

public class NukeCommand implements ICommand {
    @Override
    public String getName() {
        return "nuke";
    }

    @Override
    public String getDescription() {
        return "Borra todos los mensajes de un canal de texto compatible";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of();
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        MessageChannelUnion channel = event.getChannel();
        if (!Objects.requireNonNull(event.getMember()).hasPermission(Permission.MANAGE_CHANNEL)) {
            event.reply(LurienBot.getCrossEmoji() + " No tienes permisos.").setEphemeral(true).queue();
            return;
        }

        event.reply("🧹 Comenzando limpieza del canal...").submit().thenCompose(hook ->
                hook.retrieveOriginal().submit()
        ).thenAccept(botMessage -> {
            Set<String> excludeIds = new HashSet<>();
            excludeIds.add(botMessage.getId());

            clearChannel(channel, event.getHook(), excludeIds, 0, 0);
        });
        return;
    }

    private void clearChannel(MessageChannelUnion channel, InteractionHook message, Set<String> excludeIds, int recentDeleted, int oldDeleted) {
        channel.getHistory().retrievePast(100).queue(messages -> {
            if (messages.isEmpty()) {
                message.editOriginal(LurienBot.getTickEmoji() + " Limpieza completada.\n"
                        + "🆕 Mensajes recientes borrados: " + recentDeleted + "\n"
                        + "📜 Mensajes antiguos borrados: " + oldDeleted).queue();
                return;
            }

            OffsetDateTime twoWeeksAgo = OffsetDateTime.now().minusDays(14);

            List<Message> recent = new ArrayList<>();
            List<Message> old = new ArrayList<>();

            for (Message msg : messages) {
                if (excludeIds.contains(msg.getId())) continue;
                if (msg.getTimeCreated().isAfter(twoWeeksAgo)) {
                    recent.add(msg);
                } else {
                    old.add(msg);
                }
            }

            if (recent.isEmpty() && old.isEmpty()) {
                message.editOriginal(LurienBot.getTickEmoji() + " Limpieza completada.\n"
                        + "🆕 Mensajes recientes borrados: " + recentDeleted + "\n"
                        + "📜 Mensajes antiguos borrados: " + oldDeleted).queue();
                return;
            }

            if (!recent.isEmpty()) {
                deleteOneByOne(channel, recent.iterator(), message, excludeIds, recentDeleted, oldDeleted);
            } else {
                old.getFirst().delete().queue(
                        success -> clearChannel(channel, message, excludeIds, recentDeleted, oldDeleted + 1),
                        error -> message.editOriginal(LurienBot.getCrossEmoji() + " Error al borrar mensaje antiguo: " + error.getMessage()).queue()
                );
            }
        });
    }

    private void deleteOneByOne(MessageChannelUnion channel,
                                Iterator<Message> iterator,
                                InteractionHook message,
                                Set<String> excludeIds,
                                int recentDeleted,
                                int oldDeleted) {
        if (!iterator.hasNext()) {
            clearChannel(channel, message, excludeIds, recentDeleted, oldDeleted);
            return;
        }

        Message msg = iterator.next();
        msg.delete().queue(
                success -> deleteOneByOne(channel, iterator, message, excludeIds, recentDeleted + 1, oldDeleted),
                error -> message.editOriginal(LurienBot.getCrossEmoji() + " Error al borrar mensaje: " + error.getMessage()).queue()
        );
    }
}
