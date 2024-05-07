package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.mixin.ClientPlayerInteractionManagerAccessor;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import random.meteor.Main;
import random.meteor.systems.Mod;
import random.meteor.utils.Utils;

import java.util.Arrays;


public class AutoDupe extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> range = sgGeneral.add(new DoubleSetting.Builder()
        .name("range")
        .description("Range to find an itemframe.")
        .defaultValue(4.5)
        .min(0)
        .sliderMax(6)
        .build()
    );

    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder()
        .name("delay")
        .description("amount of ticks to wait before duping")
        .defaultValue(5)
        .min(0)
        .sliderMax(30)
        .build()
    );
    private final Setting<Integer> spinAttempts = sgGeneral.add(new IntSetting.Builder()
        .name("spin-attempts")
        .defaultValue(1)
        .min(1)
        .sliderMax(15)
        .build()
    );
    private final SettingGroup sgRender = settings.createGroup("Render");

    // Render

    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode")
        .description("How the shapes are rendered.")
        .defaultValue(ShapeMode.Both)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
        .name("side-color")
        .description("The side color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 75))
        .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
        .name("line-color")
        .description("The line color of the rendering.")
        .defaultValue(new SettingColor(225, 0, 0, 255))
        .build()
    );

    public AutoDupe() {
        super( "auto-dupe", "Item frame dupe");
    }


    Entity itemFrame;
    int ticks, spins;

    @Override
    public void onActivate() {
        itemFrame = null;
        spins = 0;
        ticks = delay.get();
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        itemFrame = null;

        for (Entity entity : mc.world.getEntities()) {
            if (!PlayerUtils.isWithin(entity, range.get())) continue;
            if (!(entity instanceof ItemFrameEntity)) continue;
            itemFrame = entity;
            break;
        }

        if (itemFrame == null) {
            error("No nearby item-frames found... toggling");
            toggle();
            return;
        }

        FindItemResult shulker = InvUtils.findInHotbar(itemStack -> Arrays.stream(Utils.SHULKER_BLOCKS).toList().contains(Block.getBlockFromItem(itemStack.getItem())));

        if (!shulker.isHotbar()) return;

        if (ticks > 0) {
            ticks--;
        } else {
            ItemFrameEntity entity = (ItemFrameEntity) itemFrame;

            if (entity.getHeldItemStack().isEmpty()) {
                if (spins < spinAttempts.get()) {
                    InvUtils.swap(shulker.slot(), true);

                    mc.interactionManager.interactEntity(mc.player, itemFrame, shulker.getHand());

                    InvUtils.swapBack();
                    spins++;
                } else {
                    spins = 0;
                    ticks = delay.get();
                }

            } else {
                ticks = delay.get();
                mc.interactionManager.attackEntity(mc.player, itemFrame);
            }
        }
    }


    @EventHandler
    public void render(Render3DEvent event) {
        if (itemFrame == null) return;

        BlockPos pos = itemFrame.getBlockPos();
        Box box = new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 0.2, pos.getZ() + 1);

        event.renderer.box(box, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
}
