package com.fooze.fancyitems.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;

public class Color {
    // Gets the color of an item's name or rarity
    public static Integer getColor(ItemStack stack) {
        Integer nameColor = findColor(stack.getHoverName());

        // Use the item's name color if it's not white
        if (nameColor != null && !nameColor.equals(ChatFormatting.WHITE.getColor())) {
            return nameColor;
        }

        // Ignore common items
        if (stack.getRarity() == Rarity.COMMON) {
            return null;
        }

        // Use the item's rarity color if the name color is white
        return stack.getRarity().color().getColor();
    }

    // Finds the color of a component's text
    private static Integer findColor(Component component) {
        TextColor color = component.getStyle().getColor();

        // Check if the component has a color
        if (color != null) {
            return color.getValue();
        }

        // Check if the component has any children that have a color
        for (Component child : component.getSiblings()) {
            Integer childColor = findColor(child);

            if (childColor != null) {
                return childColor;
            }
        }

        // If no color is found, return null
        return null;
    }
}