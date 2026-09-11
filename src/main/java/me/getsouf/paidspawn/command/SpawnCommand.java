package me.getsouf.paidspawn.command;

import me.getsouf.paidspawn.PaidSpawnPlugin;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.Map;

public final class SpawnCommand implements CommandExecutor {

    private final PaidSpawnPlugin plugin;

    public SpawnCommand(PaidSpawnPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            plugin.getMessages().send(sender, "player-only");
            return true;
        }

        double price = plugin.getPrice();
        double balance = plugin.getEconomy().getBalance(player);
        if (balance < price) {
            plugin.getMessages().send(player, "no-money", Map.of(
                    "price", plugin.formatMoney(price),
                    "balance", plugin.formatMoney(balance)
            ));
            return true;
        }

        EconomyResponse withdrawal = plugin.getEconomy().withdrawPlayer(player, price);
        if (!withdrawal.transactionSuccess()) {
            plugin.getMessages().send(player, "withdrawal-failed");
            return true;
        }

        boolean teleported = player.teleport(player.getWorld().getSpawnLocation(), TeleportCause.COMMAND);
        if (!teleported) {
            plugin.getEconomy().depositPlayer(player, price);
            plugin.getMessages().send(player, "teleport-failed");
            return true;
        }

        plugin.getMessages().send(player, "teleported", Map.of("price", plugin.formatMoney(price)));
        return true;
    }
}
