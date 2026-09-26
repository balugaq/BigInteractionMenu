package com.balugaq.bim;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class ExampleGridOption extends GridOption {
    static ExampleGridOption instance = new ExampleGridOption();

    public ExampleGridOption() {
        super("example", 9, 9);
    }

    public void load() {
        GridOptionRegistry.registerOption(this);
    }

    @Override
    public void initialize(int idx, InteractUnit unit) {
        var stack = new ItemStack(Material.STONE, idx + 1);
        unit.itemDisplay.setItemStack(stack);
        unit.itemDisplay.setTransformation(TransformationBuilder.create().scale(0.25f).build());
        unit.titleDisplay.text(stack.effectiveName());
        unit.titleDisplay.setAlignment(TextDisplay.TextAlignment.CENTER);
        unit.titleDisplay.setInvisible(true);
        var s = Util.formatAmount(stack.getAmount());
        unit.amountDisplay.setAlignment(TextDisplay.TextAlignment.RIGHT);
        unit.amountDisplay.text(Component.text().color(NamedTextColor.WHITE).append(Component.text(s)).build());
        unit.amountDisplay.setDisplayWidth(0.1f * s.length());
        unit.amountDisplay.setDisplayHeight(0.1f);
    }

    @Override
    public void interact(InteractUnit unit, PlayerInteractEvent event) {
        event.getPlayer().sendMessage("Performed " + event.getAction() + " at idx:" + unit.getIdx());
    }

    @Override
    public void hover(InteractUnit unit, Player player) {
        unit.amountDisplay.setInvisible(false);
        Bukkit.getScheduler().runTaskLater(MyPluginMain.instance(), () -> {
            if (unit.amountDisplay.isValid()) {
                unit.amountDisplay.setInvisible(true);
            }
        }, tickInterval());
    }

    @Override
    public void scroll(ScrollResult result, PlayerItemHeldEvent event) {

    }

    @Override
    public void tick() {

    }

    @Override
    public int tickInterval() {
        return 1;
    }

    @Override
    public int entriesSize() {
        return 92;
    }
}
