package me.core.item;

import me.core.util.nbt.NBTHelper;
import net.minecraft.nbt.NBTBase;
import org.bukkit.Material;

public class InventoryItem extends ServerItem {

    public InventoryItem(final Material material) {
        super(material);
    }

    public InventoryItem(final Material material, int amount) {
        super(material, amount);
    }

    public InventoryItem setTag(String key, NBTBase value) {
        NBTHelper.setTag(this, key, value);
        return this;
    }

    public InventoryItem setTag(String key, int value) {
        NBTHelper.setTag(this, key, value);
        return this;
    }

    public InventoryItem setTag(String key, String value) {
        NBTHelper.setTag(this, key, value);
        return this;
    }

    public InventoryItem setTag(String key, boolean value) {
        NBTHelper.setTag(this, key, value);
        return this;
    }

}
