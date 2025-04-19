package dev.lurien.bot.commands;

import dev.lurien.bot.LurienBot;
import dev.lurien.bot.managers.LinkedAccountsManager;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.Objects;

public class LinkAccountCommand implements ICommand {
    @Override
    public String getName() {
        return "link-account";
    }

    @Override
    public String getDescription() {
        return "Vincula tu cuenta de Minecraft con Discord";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(new OptionData(OptionType.STRING, "nick-de-minecraft", "Nick de minecraft al cual vincularse", true));
    }

    @Override
    public List<String> getAliases() {
        return List.of("vincular-cuenta");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply(true).queue();
        String nick = Objects.requireNonNull(event.getOption("nick-de-minecraft")).getAsString();

        if (!nick.matches("^[a-zA-Z0-9_]{3,16}$")) {
            event.getHook().sendMessage(LurienBot.getCrossEmoji() + " Ese nombre no parece válido.").setEphemeral(true).queue();
            return;
        }

        if (!Bukkit.getOfflinePlayer(nick).hasPlayedBefore()) {
            event.getHook().sendMessage(LurienBot.getCrossEmoji() + " Ese jugador nunca se ha unido al servidor.").setEphemeral(true).queue();
            return;
        }

        if (LinkedAccountsManager.isLinked(Objects.requireNonNull(event.getMember()).getIdLong())) {
            event.getHook().sendMessage(LurienBot.getWarnEmoji() + " Tu cuenta de Discord ya se vinculó con una cuenta de Minecraft (" +
                    LinkedAccountsManager.getNick(event.getMember().getIdLong()) + "). Si necesitas cambiar de cuenta, abre un ticket.").setEphemeral(true).queue();
            return;
        }

        if (LinkedAccountsManager.isLinked(nick)) {
            Member m = LinkedAccountsManager.getMember(nick);
            String name = m != null ? m.getEffectiveName() : "desconocido";
            event.getHook().sendMessage(LurienBot.getWarnEmoji() + " Esa cuenta de Minecraft ya está vinculada con Discord (" +
                    name + "). Si necesitas cambiar de cuenta, abre un ticket.").setEphemeral(true).queue();
            return;
        }

        if (LinkedAccountsManager.isRequesting(event.getMember().getIdLong())) {
            event.getHook().sendMessage(LurienBot.getCrossEmoji() + " Ya solicitaste una vinculación. Si te equivocaste, cancela e intenta de nuevo.").setEphemeral(true).queue();
            return;
        }

        LinkedAccountsManager.toLink(event.getMember(), nick, event.getHook());
    }
}
