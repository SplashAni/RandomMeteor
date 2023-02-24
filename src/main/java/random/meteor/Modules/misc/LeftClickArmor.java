package random.meteor.Modules.misc;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import random.meteor.Main;

import static random.meteor.Utils.MiscUtils.shouldTrigger;

public class LeftClickArmor extends Module {
    private final MinecraftClient mc = MinecraftClient.getInstance();

    public LeftClickArmor() {
        super(Main.MISC, "left-click-armor", "Automatically equips armor when you left-click with it in your hand");
    }

    @EventHandler
    private void onTick(TickEvent.Pre event) {
        if (shouldTrigger()) {
            ItemStack pain = mc.player.getMainHandStack();
            if (pain == null || pain.isEmpty()) return;

            if (pain.getItem() == Items.LEATHER_HELMET || pain.getItem() == Items.CHAINMAIL_HELMET ||
                    pain.getItem() == Items.IRON_HELMET || pain.getItem() == Items.GOLDEN_HELMET ||
                    pain.getItem() == Items.DIAMOND_HELMET || pain.getItem() == Items.NETHERITE_HELMET) {

                ItemStack helmet = mc.player.getInventory().armor.get(3);
                mc.player.getInventory().armor.set(3, pain);
                mc.player.setStackInHand(mc.player.getActiveHand(), helmet);
            }
            else if (pain.getItem() == Items.LEATHER_CHESTPLATE || pain.getItem() == Items.CHAINMAIL_CHESTPLATE ||
                    pain.getItem() == Items.IRON_CHESTPLATE || pain.getItem() == Items.GOLDEN_CHESTPLATE ||
                    pain.getItem() == Items.DIAMOND_CHESTPLATE || pain.getItem() == Items.NETHERITE_CHESTPLATE) {

                ItemStack chestplate = mc.player.getInventory().armor.get(2);
                mc.player.getInventory().armor.set(2, pain);
                mc.player.setStackInHand(mc.player.getActiveHand(), chestplate);
            }
            else if (pain.getItem() == Items.LEATHER_LEGGINGS || pain.getItem() == Items.CHAINMAIL_LEGGINGS ||
                    pain.getItem() == Items.IRON_LEGGINGS || pain.getItem() == Items.GOLDEN_LEGGINGS ||
                    pain.getItem() == Items.DIAMOND_LEGGINGS || pain.getItem() == Items.NETHERITE_LEGGINGS) {

                ItemStack leggings = mc.player.getInventory().armor.get(1);
                mc.player.getInventory().armor.set(1, pain);
                mc.player.setStackInHand(mc.player.getActiveHand(), leggings);
            }
            else if (pain.getItem() == Items.LEATHER_BOOTS || pain.getItem() == Items.CHAINMAIL_BOOTS ||
                    pain.getItem() == Items.IRON_BOOTS || pain.getItem() == Items.GOLDEN_BOOTS ||
                    pain.getItem() == Items.DIAMOND_BOOTS || pain.getItem() == Items.NETHERITE_CHESTPLATE) {

                ItemStack boots = mc.player.getInventory().armor.get(0);
                mc.player.getInventory().armor.set(0, pain);
                mc.player.setStackInHand(mc.player.getActiveHand(), boots);
            }
        }
    }
}
