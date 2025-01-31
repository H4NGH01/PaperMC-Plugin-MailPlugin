package me.core.gui.mail;

import me.core.enchantments.PluginEnchantments;
import me.core.gui.GUIBase;
import me.core.gui.MultiplePageGUI;
import me.core.gui.Updatable;
import me.core.items.InventoryItem;
import me.core.mail.Mail;
import me.core.mail.MailManager;
import me.core.utils.ComponentUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MailBoxGUI extends MultiplePageGUI implements MailGUIInterface, Updatable {

    private static final HashMap<Player, MailBoxGUI> VIEW_MAP = new HashMap<>();
    private final HashSet<Mail> selectedMail = new HashSet<>();

    public MailBoxGUI(@NotNull Player player) {
        super(player);
        this.setDefault();
    }

    @Override
    public void setInventory() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Mail mail : MailManager.getMailList(this.player)) {
            if (!mail.isDeleted()) {
                stacks.add(mailStack(mail, this.selectedMail.contains(mail)));
            }
        }
        this.setContents(stacks);
        this.toArray(VIEW_MAP.containsKey(this.getPlayer()) ? VIEW_MAP.get(this.player).getPage() : 1);
        this.inventory.setItem(0, info(this.player));
        this.inventory.setItem(1, writeMail());
        this.inventory.setItem(2, sentMail());
        this.inventory.setItem(3, mailBin());
        this.inventory.setItem(4, deleteMail());
    }

    @Override
    public Component getGUIName() {
        return Component.translatable("gui.mail.box");
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends GUIBase> HashMap<Player, T> getViewMap() {
        return (HashMap<Player, T>) VIEW_MAP;
    }

    public static HashMap<Player, MailBoxGUI> getViews() {
        return VIEW_MAP;
    }

    @Override
    public void openToPlayer() {
        super.openToPlayer();
        this.player.playSound(this.player.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 0.7f, 1f);
    }

    @Contract("_ -> new")
    private @NotNull InventoryItem info(Player p) {
        InventoryItem item = new InventoryItem(Material.PAPER).setTag("ItemTag", "gui.mail.box.info");
        item.setDisplayName(Component.translatable("gui.mail.box.info"));
        item.addLore(ComponentUtil.component(Component.translatable("gui.mail.box.info_total").args(Component.text(ChatColor.YELLOW.toString() + MailManager.getMailCount(p)))));
        item.addLore(ComponentUtil.component(Component.translatable("gui.mail.box.info_new").args(Component.text(ChatColor.YELLOW.toString() + MailManager.getUnreadMail(p).size()))));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem writeMail() {
        InventoryItem item = new InventoryItem(Material.WRITABLE_BOOK).setTag("ItemTag", "gui.mail.box.write");
        item.setDisplayName(Component.translatable("gui.mail.box.write"));
        item.addLore(Component.translatable("gui.mail.box.write_lore"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem sentMail() {
        InventoryItem item = new InventoryItem(Material.WRITTEN_BOOK).setTag("ItemTag", "gui.mail.box.sent");
        item.setDisplayName(Component.translatable("gui.mail.box.sent"));
        item.addLore(Component.translatable("gui.mail.box.sent_lore"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem mailBin() {
        InventoryItem item = new InventoryItem(Material.BUCKET).setTag("ItemTag", "gui.mail.box.bin");
        item.setDisplayName(Component.translatable("gui.mail.box.bin"));
        item.addLore(Component.translatable("gui.mail.box.bin_lore"));
        return item;
    }

    @Contract(" -> new")
    private @NotNull InventoryItem deleteMail() {
        InventoryItem item = new InventoryItem(Material.CAULDRON).setTag("ItemTag", "gui.mail.box.delete");
        item.setDisplayName(Component.translatable("gui.mail.box.delete"));
        item.addLore(Component.translatable("gui.mail.box.delete_lore1"));
        item.addLore(Component.translatable("gui.mail.box.delete_lore2"));
        return item;
    }

    private @NotNull InventoryItem mailStack(@NotNull Mail mail, boolean selected) {
        InventoryItem item = new InventoryItem(Material.PAPER).setTag("ItemTag", "gui.mail.box.mail").setTag("MailID", mail.getMailID());
        item.setDisplayName(ComponentUtil.translate(NamedTextColor.YELLOW, mail.getTitle()));
        String sender = mail.getSender().startsWith("player@") ? plugin.getServer().getOfflinePlayer(UUID.fromString(mail.getSender().substring(7))).getName() : mail.getSender();
        item.addLore(ComponentUtil.component(NamedTextColor.GRAY, Component.translatable("gui.mail.from"), Component.text(": " + Objects.requireNonNull(sender))));
        if (mail.getText().equals("gui.mail.no_text")) {
            item.addLore(ComponentUtil.component(NamedTextColor.GRAY, Component.translatable(mail.getText()).decoration(TextDecoration.ITALIC, true)));
        } else {
            String[] sa = (mail.getText()).split("\\\\n");
            for (String s : sa) {
                item.addLore(Component.text(ChatColor.GRAY + "§o" + s));
            }
        }
        item.addLore(ComponentUtil.component(NamedTextColor.GRAY, Component.translatable("gui.mail.attachment")));
        if (mail.getItemList().size() == 0) {
            item.addLore(ComponentUtil.component(NamedTextColor.GRAY, Component.translatable("gui.none")));
        } else {
            for (ItemStack stack : mail.getItemList()) {
                item.addLore(ComponentUtil.component(NamedTextColor.GRAY, Component.text("- "), stack.displayName()));
            }
        }
        item.addLore(ComponentUtil.component(NamedTextColor.GREEN, Component.translatable("gui.mail.date"), Component.text(": " + mail.getDate())));
        if (mail.isReceived()) {
            item.addLore(Component.translatable("gui.mail.received"));
            item.setType(Material.MAP);
        }
        item.addLore(Component.translatable(selected ? "gui.unselect" : "gui.select"));
        item.addLore(Component.translatable("gui.show_details"));
        if (selected) item.addUnsafeEnchantment(PluginEnchantments.WRAPPER, 0);
        return item;
    }

    public HashSet<Mail> getSelectedMail() {
        return this.selectedMail;
    }

    @Override
    public void update() {
        List<ItemStack> stacks = new ArrayList<>();
        for (Mail mail : MailManager.getMailList(this.player)) {
            if (!mail.isDeleted()) {
                stacks.add(mailStack(mail, this.selectedMail.contains(mail)));
            }
        }
        this.setContents(stacks);
        this.toArray(VIEW_MAP.containsKey(this.player) ? VIEW_MAP.get(this.player).getPage() : 1);
        this.inventory.setItem(0, info(this.player));
    }
}
