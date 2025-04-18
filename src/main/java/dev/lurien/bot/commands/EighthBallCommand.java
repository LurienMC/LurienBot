package dev.lurien.bot.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.Random;

public class EighthBallCommand implements ICommand {

    private final List<String> responses = List.of(
            "Es cierto.",
            "Es decididamente así.",
            "Sin lugar a dudas.",
            "Sí - definitivamente.",
            "Puedes confiar en ello.",
            "Respuesta confusa, intenta de nuevo.",
            "Pregunta de nuevo más tarde.",
            "Mejor no decírtelo ahora.",
            "No puedo predecir ahora.",
            "Concéntrate y pregunta de nuevo.",
            "No cuentes con ello.",
            "Mi respuesta es no.",
            "Mis fuentes dicen que no.",
            "Panorama no muy bueno.",
            "Muy dudoso.",
            "Las señales apuntan a sí.",
            "Panorama bueno.",
            "Como yo lo veo, sí.",
            "Lo más probable.",
            "Sí."
    );

    @Override
    public String getName() {
        return "8ball";
    }

    @Override
    public String getDescription() {
        return "Preguntale cualquier cosa a la bola mágica";
    }

    @Override
    public List<OptionData> getOptions() {
        return List.of(
                new OptionData(OptionType.STRING, "pregunta", "La pregunta que le quieres hacer a la bola mágica", true)
        );    }

    @Override
    public List<String> getAliases() {
        return List.of("bola-magica");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        String question = event.getOption("pregunta").getAsString();
        String response = responses.get(new Random().nextInt(responses.size()));

        EmbedBuilder eb = new EmbedBuilder()
                .setColor(0x000000)
                .setTitle("🎱 La bola mágica responde:")
                .addField("Tu pregunta:", question, false)
                .addField("Respuesta:", response, false);

        event.replyEmbeds(eb.build()).queue();
    }
}
