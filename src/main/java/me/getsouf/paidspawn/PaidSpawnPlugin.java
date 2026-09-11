package me.getsouf.paidspawn;

import me.getsouf.paidspawn.command.SpawnCommand;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class PaidSpawnPlugin extends JavaPlugin {

    private Economy economy;
    private double price;
    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadSettings();

        if (!setupEconomy()) {
            getLogger().severe("Провайдер экономики Vault не найден. Плагин отключён.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        PluginCommand spawnCommand = Objects.requireNonNull(
                getCommand("spawn"),
                "Команда /spawn не объявлена в plugin.yml"
        );
        spawnCommand.setExecutor(new SpawnCommand(this));
        getLogger().info("PaidSpawn включён. Цена /spawn: " + formatMoney(price));
    }

    private void loadSettings() {
        price = getConfig().getDouble("price", 5.0D);
        if (price < 0.0D) {
            getLogger().warning("Цена не может быть отрицательной. Используется $5.00.");
            price = 5.0D;
        }
        messages = new Messages(getConfig());
    }

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> registration = getServer().getServicesManager().getRegistration(Economy.class);
        if (registration == null) {
            return false;
        }
        economy = registration.getProvider();
        return economy != null;
    }

    public Economy getEconomy() { return economy; }
    public double getPrice() { return price; }
    public Messages getMessages() { return messages; }

    public String formatMoney(double amount) {
        String formatted = economy.format(amount);
        return formatted == null || formatted.isBlank() ? String.format("$%.2f", amount) : formatted;
    }
}
