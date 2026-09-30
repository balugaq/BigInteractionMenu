package com.balugaq.bim;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class BIMCommandExecutor implements TabExecutor {
    private static final Set<String> SUB_COMMANDS = Set.of("w", "h", "gap", "show", "reset");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        var option = ExampleGridOption.instance;
        var pos = BlockPos.from(player.getLocation());

        if (args.length == 0) {
            place(player, pos);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "w" -> {
                if (args.length < 2 || !parseWidth(option, args[1])) {
                    player.sendMessage("Usage: /bim w <width> (current: " + option.getWidth() + ")");
                    return true;
                }
                player.sendMessage("Width set to " + option.getWidth());
                place(player, pos);
            }
            case "h" -> {
                if (args.length < 2 || !parseHeight(option, args[1])) {
                    player.sendMessage("Usage: /bim h <height> (current: " + option.getHeight() + ")");
                    return true;
                }
                player.sendMessage("Height set to " + option.getHeight());
                place(player, pos);
            }
            case "gap" -> {
                if (args.length < 2 || !parseGap(option, args[1])) {
                    player.sendMessage("Usage: /bim gap <gap> (current: " + option.getGap() + ")");
                    return true;
                }
                player.sendMessage("Gap set to " + option.getGap());
                place(player, pos);
            }
            case "show" -> player.sendMessage("width=" + option.getWidth()
                    + ", height=" + option.getHeight()
                    + ", gap=" + option.getGap());
            case "reset" -> {
                option.setDebugWidth(9);
                option.setDebugHeight(9);
                option.setDebugGap(0.12f);
                player.sendMessage("Reset to width=9, height=9, gap=0.12");
                place(player, pos);
            }
            default -> player.sendMessage("Unknown sub-command. Available: " + String.join(", ", SUB_COMMANDS));
        }
        return true;
    }

    private void place(Player player, BlockPos pos) {
        Util.removeGrid(pos);
        Util.placeGrid(pos.toLocation(), ExampleGridOption.instance);
    }

    private boolean parseWidth(ExampleGridOption option, String raw) {
        try {
            int v = Integer.parseInt(raw);
            if (v < 1) return false;
            option.setDebugWidth(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean parseHeight(ExampleGridOption option, String raw) {
        try {
            int v = Integer.parseInt(raw);
            if (v < 1) return false;
            option.setDebugHeight(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean parseGap(ExampleGridOption option, String raw) {
        try {
            float v = Float.parseFloat(raw);
            if (v <= 0 || Float.isNaN(v) || Float.isInfinite(v)) return false;
            option.setDebugGap(v);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            return SUB_COMMANDS.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase(Locale.ROOT)))
                    .toList();
        }
        return List.of();
    }
}
