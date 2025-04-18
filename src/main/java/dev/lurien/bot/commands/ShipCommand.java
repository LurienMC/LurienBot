package dev.lurien.bot.commands;

import dev.lurien.bot.LurienBot;
import lombok.Getter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class ShipCommand implements ICommand{
    @Override
    public String getName() {
        return "ship";
    }

    @Override
    public String getDescription() {
        return "Mide el porcentaje de pareja de dos usuarios";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.USER, "primer-usuario", "El primer usuario del ship", true),
                new OptionData(OptionType.USER, "segundo-usuario", "El segundo usuario del ship", true));
    }

    @Override
    public List<String> getAliases() {
        return List.of("pareja");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        User user1 = Objects.requireNonNull(event.getOption("primer-usuario")).getAsUser();
        User user2 = Objects.requireNonNull(event.getOption("segundo-usuario")).getAsUser();

        event.deferReply().setEphemeral(false).queue();
        InteractionHook hook = event.getHook();

        if(user1.getId().contains(user2.getId())){
            if(user1.getId().contains(event.getJDA().getSelfUser().getId())){
                hook.sendMessage(LurienBot.getTickEmoji()+" Yo ya me amo a mí mismo.").queue();
                return;
            }
            send(hook, 1, Objects.requireNonNull(event.getMember()), user1, user2, ShipType.SELF);
            return;
        }

        if(user1.getId().contains(event.getJDA().getSelfUser().getId()) || user2.getId().contains(event.getJDA().getSelfUser().getId())){
            send(hook, 1, Objects.requireNonNull(event.getMember()), user1, user2, ShipType.WITH_ME);
            return;
        }

        send(hook, new Random().nextDouble(), Objects.requireNonNull(event.getMember()), user1, user2, ShipType.NORMAL);
    }

    private void send(InteractionHook hook, double percentage, Member owner, User user1, User user2, ShipType type) {
        int clearPercentage = Math.round((float)percentage*100);
        EmbedBuilder eb = new EmbedBuilder()
                .setTitle("\uD83D\uDC98 "+user1.getEffectiveName()+" x "+(type == ShipType.SELF ? "sí mismo" : user2.getEffectiveName()))
                .setColor(percentageToIntColor(clearPercentage))
                .setThumbnail("https://cdn.discordapp.com/attachments/882789546339741716/1362612355137015979/1.png?ex=68030725&is=6801b5a5&hm=f427a6ea4f86619c70a728b9f749df39119d8cddd96cf92e330944afadd64baa&")
                .setImage("https://cdn.discordapp.com/attachments/882789546339741716/1362621182888640512/SHIPS2.png?ex=68030f5e&is=6801bdde&hm=8f8e32a1e4ddefc6c2d799e9da5f7ba73e4f7371c69dbaa85d6e5dc5e7d46b58&?w=200&h=150&fit=crop")
                .setAuthor("Pedido por: "+owner.getEffectiveName(), null, owner.getEffectiveAvatarUrl())
                .setFooter("Created by: @octdamfar", hook.getInteraction().getGuild().getIconUrl())
                .setDescription("__Su compatibilidad es de__ "+clearPercentage+"%\n-# "+getStringPerPercentage(clearPercentage, type));

        hook.sendMessageEmbeds(eb.build()).queue();
    }

    private String getStringPerPercentage(int p, ShipType t){
        if(p < 35){
            return (t.badStrings != null ? t.badStrings.get(new Random().nextInt(t.badStrings.size())) : "Mal ahí");
        }else if(p < 65){
            return (t.mediumStrings != null ? t.mediumStrings.get(new Random().nextInt(t.mediumStrings.size())) : "Maso che");
        }else{
            return (t.goodStrings != null ? t.goodStrings.get(new Random().nextInt(t.goodStrings.size())) : "Bien ahí");
        }
    }

    private int percentageToIntColor(int percentage) {
        int[] strongRed = {255, 0, 0};
        int[] pastelPink = {255, 203, 219};

        double t = (double) percentage / 100.0;

        int red = (int) (strongRed[0] + (0) * t);
        int green = (int) (strongRed[1] + (pastelPink[1] - strongRed[1]) * t);
        int blue = (int) (strongRed[2] + (pastelPink[2] - strongRed[2]) * t);

        red = Math.max(0, Math.min(255, red));
        green = Math.max(0, Math.min(255, green));
        blue = Math.max(0, Math.min(255, blue));

        return (red << 16) | (green << 8) | blue;
    }

    private enum ShipType {
        NORMAL(List.of("Vaya, no está nada mal",
                "Se nota que hay algo ahí, no?", "QUE GANADOOOOR"),
                List.of("Peor es ser fan de Taylor Swift", "Peor es nada", "Mejor nadota bro"),
                List.of("UFFF casi \uD83D\uDD25", "Tienen que cerrar el estadio!!", "Por eso mejor trava feo")),
        SELF(List.of("Tienes que amarte siempre corazón", "\uD83D\uDD25\uD83D\uDD25", "La autoestíma es lo más importante \uD83D\uDCAA"), null, null),
        WITH_ME(List.of("Como no voy a amarte mi vida", "Eso que, si yo te re amo", "Ay.. \uD83E\uDEE6", "Ti amo"), null, null);

        @Nullable
        @Getter
        private final List<String> goodStrings, mediumStrings, badStrings;

        ShipType(@Nullable List<String> goodStrings, @Nullable List<String> mediumStrings, @Nullable List<String> badStrings) {
            this.goodStrings = goodStrings;
            this.mediumStrings = mediumStrings;
            this.badStrings = badStrings;
        }
    }
}
