package dev.lurien.bot.commands.minecraft;

import dev.lurien.bot.managers.LinkedAccountsManager;
import dev.lurien.bot.model.DiscordLinkRequest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static dev.lurien.staff.lurienStaff.utils.MessagesUtils.sendMessage;

public class VinculationCommand implements TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(sender instanceof Player p){
            if(args.length == 0){
                sendMessage(sender, "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &f» &6&l! &6Usa: /vincular <código>");
                return false;
            }

            String code = args[0];

            if (!code.matches("\\d{6}")) {
                sendMessage(sender, "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &c⚠ El código es de 6 caractéres.");
                return false;
            }

            DiscordLinkRequest req = LinkedAccountsManager.getRequestByPlayer(p.getName());
            if (req == null) {
                sendMessage(sender, "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &c⚠ No hay ninguna solicitud de vinculación pendiente.");
                return false;
            }

            if (!req.code().equals(code)) {
                sendMessage(sender, "#6286FF&lD#5976F5&li#5067EB&ls#4757E1&lc#3D47D7&lo#3438CD&lr#2B28C3&ld &c⚠ El código es inválido.");
                return false;
            }

            LinkedAccountsManager.vinculate(req, p);
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
        if(args.length == 1) return List.of("Aquí el código");
        return List.of();
    }
}
