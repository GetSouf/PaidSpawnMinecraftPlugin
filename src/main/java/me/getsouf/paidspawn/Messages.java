package me.getsouf.paidspawn;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public final class Messages {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final FileConfiguration config;

    public Messages(FileConfiguration config) {
        this.config = config;
    }

    public void send(CommandSender recipient, String key, Map<String, String> placeholders) {
        String message = config.getString("messages." + key, "&cСообщение не настроено: " + key);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        recipient.sendMessage(MINI_MESSAGE.deserialize(message));
    }

    public void send(CommandSender recipient, String key) {
        send(recipient, key, Map.of());
    }
}
