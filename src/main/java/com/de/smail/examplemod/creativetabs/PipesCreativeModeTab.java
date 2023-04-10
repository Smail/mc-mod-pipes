package com.de.smail.examplemod.creativetabs;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class PipesCreativeModeTab extends CreativeModeTab {
    public static final PipesCreativeModeTab INSTANCE = new PipesCreativeModeTab(CreativeModeTab.TABS.length, "examplemod");

    private PipesCreativeModeTab(int index, String label) {
        super(index, label);
    }

    @Override
    public @NotNull ItemStack makeIcon() {
        return new ItemStack(Items.DIAMOND);
    }
}
