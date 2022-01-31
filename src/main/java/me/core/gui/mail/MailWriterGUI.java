package me.core.gui.mail;

import me.core.gui.GUIBase;
import me.core.item.InventoryItem;
import me.core.item.MCServerItems;
import me.core.mail.NewMail;
import me.core.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;

public class MailWriterGUI extends GUIBase {

    public static HashMap<Player, MailWriterGUI> VIEW_MAP = new HashMap<>();
    private final NewMail mail;

    public static void setup() {
        VIEW_MAP = new HashMap<>();
    }

    public MailWriterGUI(Player player, NewMail mail) {
        super(player);
        this.mail = mail;
        VIEW_MAP.put(player, this);
    }

    @Override
    public void setInventory() {
        for (int i = 0; i < 8; i++) {
            this.inventory.setItem(i + 37, this.mail.getItemStacks()[i]);
        }
        this.inventory.setItem(8, send());
        this.inventory.setItem(9, title());
        this.inventory.setItem(18, to());
        this.inventory.setItem(27, text());
        this.inventory.setItem(36, attachment());
    }

    @Override
    public Component getGUIName() {
        return Component.translatable("gui.mail.writer.menu");
    }

    @Contract(" -> new")
    private @NotNull InventoryItem title() {
        InventoryItem item = new InventoryItem(Material.PAPER).setTag("ItemTag", "gui.mail.writer.title");
        item.setDisplayName(ComponentUtil.translate(ChatColor.GREEN, "gui.mail.title"));
        item.addLore(ComponentUtil.translate(ChatColor.YELLOW, mail.getTitle()));
        item.addLore(Component.translatable("gui.mail.edit"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem to() {
        InventoryItem item = new InventoryItem(Material.PLAYER_HEAD).setTag("ItemTag", "gui.mail.writer.to");
        item.setDisplayName(ComponentUtil.translate(ChatColor.GREEN, "gui.mail.to"));
        TextComponent.Builder builder = Component.text();
        if (this.mail.getAddressee().length > 0) {
            for (int i = 0; i < this.mail.getAddressee().length; i++) {
                OfflinePlayer op = this.mail.getAddressee()[i];
                builder.append(ComponentUtil.text(ChatColor.YELLOW, Objects.requireNonNull(op.getName())));
                if (i < this.mail.getAddressee().length - 1) builder.append(Component.text(", "));
            }
        } else {
            builder.append(ComponentUtil.translate(ChatColor.GRAY, "gui.none"));
        }
        item.addLore(builder.build());
        item.addLore(Component.translatable("gui.mail.edit"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem text() {
        InventoryItem item = new InventoryItem(Material.PAPER).setTag("ItemTag", "gui.mail.writer.text");
        item.setDisplayName(ComponentUtil.translate(ChatColor.GREEN, "gui.mail.text"));
        if (this.mail.getText().equals("gui.mail.no_text")) {
            item.addLore(ComponentUtil.translate(ChatColor.GRAY, mail.getText()).decoration(TextDecoration.ITALIC, true));
        } else {
            String[] sa = (this.mail.getText()).split("\\\\n");
            for (String s : sa) {
                item.addLore(Component.text(ChatColor.GRAY + "§o" + s));
            }
        }
        item.addLore(Component.translatable("gui.mail.edit"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem attachment() {
        InventoryItem item = new InventoryItem(Material.PAPER).setTag("ItemTag", "gui.mail.writer.attachment");
        item.setDisplayName(ComponentUtil.translate(ChatColor.GREEN, "gui.mail.attachment"));
        item.addLore(Component.translatable("gui.mail.attachment_edit_lore1"));
        item.addLore(Component.translatable("gui.mail.attachment_edit_lore2"));
        item.addLore(Component.translatable("gui.mail.edit"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem send() {
        InventoryItem item = new InventoryItem(Material.GREEN_CONCRETE).setTag("ItemTag", "gui.mail.writer.send");
        item.setDisplayName(Component.translatable("gui.mail.writer.menu.send"));
        return item;
    }

    public NewMail getMail() {
        return this.mail;
    }

    public void update() {
        for (int i = 0; i < 8; i++) {
            this.inventory.setItem(i + 37, this.mail.getItemStacks()[i]);
        }
        this.inventory.setItem(8, send());
        this.inventory.setItem(9, title());
        this.inventory.setItem(18, to());
        this.inventory.setItem(27, text());
        this.inventory.setItem(36, attachment());
        this.inventory.setItem(48, MCServerItems.back);
    }
}
