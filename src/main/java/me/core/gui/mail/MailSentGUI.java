package me.core.gui.mail;

import me.core.gui.MultiplePageGUI;
import me.core.item.InventoryItem;
import me.core.item.MCServerItems;
import me.core.mail.Mail;
import me.core.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MailSentGUI extends MultiplePageGUI {

    public static HashMap<Player, MailSentGUI> VIEW_MAP = new HashMap<>();

    public static void setup() {
        VIEW_MAP = new HashMap<>();
    }

    public MailSentGUI(Player player) {
        super(player);
        VIEW_MAP.put(player, this);
    }

    @Override
    public void setInventory() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Mail mail : plugin.getMailManager().getMailListBySender(this.getPlayer())) {
            stacks.add(mail(mail));
        }
        this.setContents(stacks);
        this.toArray(VIEW_MAP.containsKey(this.getPlayer()) ? VIEW_MAP.get(this.getPlayer()).getPage() : 1);

        this.inventory.setItem(0, info(this.getPlayer()));
        this.inventory.setItem(48, MCServerItems.back);
        this.inventory.setItem(45, MCServerItems.prev);
        this.inventory.setItem(53, MCServerItems.next);

        this.updateGUIName();
    }

    @Override
    public String getGUIName() {
        return "gui.mail.sent";
    }

    @Contract("_ -> new")
    private @NotNull InventoryItem info(Player p) {
        InventoryItem item = new InventoryItem(Material.PAPER).addTag("ItemTag", "gui.mail.sent.info");
        item.setDisplayName(Component.translatable("gui.mail.sent.info"));
        item.addLore(Component.translatable("gui.mail.sent.info_total").append(Component.text(ChatColor.YELLOW.toString() + plugin.getMailManager().getMailListBySender(p).size())));
        int i = 0;
        for (Mail mail : plugin.getMailManager().getMailListBySender(p)) {
            if (mail.isReceived()) i++;
        }
        item.addLore(Component.translatable("gui.mail.sent.info_read").append(Component.text(ChatColor.YELLOW.toString() + Component.text(i))));
        return item;
    }

    private @NotNull ItemStack mail(@NotNull Mail mail) {
        InventoryItem item = new InventoryItem(Material.PAPER).addTag("ItemTag", "gui.mail.sent.mail").addTag("MailID", mail.getMailID());
        item.setDisplayName(ComponentUtil.translate(ChatColor.YELLOW, mail.getTitle()));
        item.addLore(ComponentUtil.translate(ChatColor.GRAY, "gui.mail.to").append(Component.text(ChatColor.GRAY + ": " + Objects.requireNonNull(plugin.getServer().getOfflinePlayer(mail.getAddressee()).getName()))));

        if (mail.getText().equals("gui.mail.no_text")) {
            item.addLore(ComponentUtil.translate(ChatColor.GRAY, mail.getText()).decoration(TextDecoration.ITALIC, true));
        } else {
            String[] sa = (mail.getText()).split("\\\\n");
            for (String s : sa) {
                item.addLore(Component.text(ChatColor.GRAY + "§o" + s));
            }
        }
        item.addLore(ComponentUtil.translate(ChatColor.GRAY, "gui.mail.attachment"));
        if (mail.getItemList().size() == 0) {
            item.addLore(ComponentUtil.translate(ChatColor.GRAY, "gui.none"));
        } else {
            for (ItemStack stack : mail.getItemList()) {
                item.addLore(ComponentUtil.component(Component.text(ChatColor.GRAY + "- "), stack.displayName()));
            }
        }
        item.addLore(ComponentUtil.translate(ChatColor.GREEN, "gui.mail.date").append(Component.text(": " + mail.getDate())));
        if (mail.isReceived()) {
            item.addLore(Component.translatable("gui.mail.received"));
            item.setType(Material.MAP);
        }
        if (mail.isDeleted()) {
            item.addLore(Component.translatable("gui.mail.deleted"));
            item.setType(Material.CAULDRON);
        }
        item.addLore(Component.translatable("gui.mail.show_details"));
        return item;
    }
}
