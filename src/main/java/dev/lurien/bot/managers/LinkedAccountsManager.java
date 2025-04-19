package dev.lurien.bot.managers;

import dev.lurien.bot.LurienBot;
import dev.lurien.bot.model.DiscordLinkRequest;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.interactions.InteractionHook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

import static dev.lurien.staff.lurienStaff.utils.MessagesUtils.sendMessage;

public class LinkedAccountsManager {

    private static final String LINKED_ACCOUNTS_PATH = "cuentas-vinculadas";
    private static final String PENDING_LINKS_PATH = "por-vincular";

    public static final Map<String, Long> accountsLinked = new HashMap<>();
    public static final List<DiscordLinkRequest> toLink = new ArrayList<>();

    public static void load(ConfigurationSection section) {
        ConfigurationSection linked = section.getConfigurationSection(LINKED_ACCOUNTS_PATH);
        if (linked != null) {
            for (String key : linked.getKeys(false)) {
                accountsLinked.put(key, linked.getLong(key));
            }
        }

        ConfigurationSection pending = section.getConfigurationSection(PENDING_LINKS_PATH);
        if (pending != null) {
            for (String key : pending.getKeys(false)) {
                ConfigurationSection s = pending.getConfigurationSection(key);
                assert s != null;
                toLink.add(new DiscordLinkRequest(s.getLong("memberID"), s.getString("nick"), s.getString("code")));
            }
        }
    }

    public static void save(ConfigurationSection section) {
        section.set(LINKED_ACCOUNTS_PATH, null);
        accountsLinked.forEach((nick, memberID) ->
                section.set(LINKED_ACCOUNTS_PATH + "." + nick, memberID)
        );

        section.set(PENDING_LINKS_PATH, null);
        for (int i = 0; i < toLink.size(); i++) {
            DiscordLinkRequest req = toLink.get(i);
            section.set(PENDING_LINKS_PATH + "." + i + ".memberID", req.memberID());
            section.set(PENDING_LINKS_PATH + "." + i + ".nick", req.nick());
            section.set(PENDING_LINKS_PATH + "." + i + ".code", req.code());
        }

        LurienBot.getLinkedAccountsConfig().save();
    }

    public static boolean isLinked(String player) {
        return accountsLinked.containsKey(player);
    }

    public static boolean isLinked(Long memberID) {
        return accountsLinked.containsValue(memberID);
    }

    public static boolean isRequesting(long memberID) {
        return toLink.stream().anyMatch(req -> req.memberID() == memberID);
    }

    public static DiscordLinkRequest getRequestByPlayer(String name) {
        return toLink.stream().filter(req -> req.nick().equals(name)).findFirst().orElse(null);
    }

    public static void linkAccount(String player, Long memberID) {
        accountsLinked.put(player, memberID);
        save(LurienBot.getLinkedAccountsConfig().getConfig());
    }

    public static boolean isLinkedAccountInDiscordServer(String player) {
        return LurienBot.getGuild().getMemberById(accountsLinked.get(player)) != null;
    }

    public static void toLink(Member member, String nick, InteractionHook hook) {
        String codeStr = String.format("%06d", new Random().nextInt(1_000_000));
        DiscordLinkRequest dlr = new DiscordLinkRequest(member.getIdLong(), nick, codeStr);
        toLink.add(dlr);
        save(LurienBot.getLinkedAccountsConfig().getConfig());

        if (Bukkit.getOfflinePlayer(nick).isOnline()) {
            sendMessage(Objects.requireNonNull(Bukkit.getPlayer(nick)),
                    "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &f» &6&l! &fUna cuenta de Discord intenta vincularse con esta cuenta de Minecraft (&6"
                            + member.getEffectiveName() + "&f). Para vincular usa el comando:\n&e/vincular <código>");
        }

        hook.sendMessage(LurienBot.getTickEmoji() + " Para continuar el proceso, mira tus mensajes privados.").queue();

        member.getUser().openPrivateChannel().queue(channel ->
                channel.sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("Solicitud de vinculación creada")
                        .setDescription("Ingresa al servidor de Minecraft con tu cuenta y usa el comando:\n**/vincular " + codeStr + "**")
                        .setThumbnail("https://cdn.discordapp.com/attachments/882789546339741716/1363213381145989200/raw.png")
                        .setFooter("Created by @octdamfar", Objects.requireNonNull(hook.getInteraction().getGuild()).getIconUrl())
                        .build()).queue());
    }

    public static void vinculate(DiscordLinkRequest req, Player player) {
        toLink.remove(req);
        Member m = LurienBot.getGuild().getMemberById(req.memberID());

        if (m != null) {
            m.getUser().openPrivateChannel().queue(channel ->
                    channel.sendMessage(LurienBot.getTickEmoji() + " Te vinculaste a la cuenta de Minecraft **" + player.getName() + "**").queue());

            sendMessage(player, "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &f» &a&l✔ &aTe vinculaste a la cuenta de Discord " + m.getEffectiveName() + " (" + m.getUser().getIdLong() + ")");
        }

        linkAccount(player.getName(), req.memberID()); // Incluye save
    }

    public static String getNick(long memberID) {
        return accountsLinked.entrySet().stream()
                .filter(e -> e.getValue().equals(memberID))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Ninguno");
    }

    public static Member getMember(String nick) {
        if (isLinkedAccountInDiscordServer(nick)) {
            return LurienBot.getGuild().getMemberById(accountsLinked.get(nick));
        }
        return null;
    }
}
