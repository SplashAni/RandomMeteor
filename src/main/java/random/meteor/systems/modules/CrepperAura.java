package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.game.OpenScreenEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.Main;
import random.meteor.systems.Mod;
import random.meteor.utils.Utils;

import java.util.stream.IntStream;

public class CrepperAura extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Double> targetRange = sgGeneral.add(new DoubleSetting.Builder().name("target-range").description("The radius in which players get targeted.").defaultValue(5.5).min(0).sliderMax(7).build());
    private final Setting<SortPriority> priority = sgGeneral.add(new EnumSetting.Builder<SortPriority>().name("target-priority").description("How to filter targets within range.").defaultValue(SortPriority.LowestDistance).build());
    private final Setting<Integer> delay = sgGeneral.add(new IntSetting.Builder().name("place-delay").description("How many ticks between block placements.").defaultValue(1).build());
    private final Setting<Boolean> silentSwap = sgGeneral.add(new BoolSetting.Builder().name("silent-swap").defaultValue(true).build());
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder().name("rotate").description("Automatically rotates you towards the city block.").defaultValue(true).build());
    private final Setting<Integer> extraPackets = sgGeneral.add(new IntSetting.Builder().name("extra-packets").defaultValue(0).range(0, 15).sliderMax(15).build());

    /*render*/
    private final Setting<Boolean> swingHand = sgRender.add(new BoolSetting.Builder().name("swing-hand").description("Whether to render your hand swinging.").defaultValue(false).build());
    private final Setting<Boolean> renderBlock = sgRender.add(new BoolSetting.Builder().name("render-block").description("Whether to render the block being broken.").defaultValue(true).build());
    private final Setting<ShapeMode> shapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>().name("shape-mode").description("How the shapes are rendered.").defaultValue(ShapeMode.Both).visible(renderBlock::get).build());
    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder().name("side-color").description("The side color of the rendering.").defaultValue(new SettingColor(225, 0, 0, 75)).visible(renderBlock::get).build());
    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder().name("line-color").description("The line color of the rendering.").defaultValue(new SettingColor(225, 0, 0, 255)).visible(renderBlock::get).build());
    private final Setting<Integer> renderTime = sgRender.add(new IntSetting.Builder().name("render-time").description("How long to render placements.").defaultValue(10).min(0).sliderMax(20).visible(renderBlock::get).build());
    private final Setting<Boolean> shrink = sgRender.add(new BoolSetting.Builder().name("shrink").defaultValue(false).visible(renderBlock::get).build());
    private final Setting<Boolean> fade = sgRender.add(new BoolSetting.Builder().name("fade").defaultValue(false).visible(renderBlock::get).build());

    int ticks;
    PlayerEntity target;
    BlockPos targetPos;

    @Override
    public void onActivate() {
        ticks = 0;
        target = null;
        targetPos = null;
        super.onActivate();
    }

    public CrepperAura() {
        super( "creeper-aura", "Places creeper eggs on target (for 8b8t)");
    }
    @EventHandler
    private void onOpenScreen(OpenScreenEvent event) {
        if (event.screen instanceof AnvilScreen) event.cancel(); /*some clever fag found this out :skull:*/
    }

    @EventHandler
    public void tick(TickEvent.Pre event) {

        if (TargetUtils.isBadTarget(target, targetRange.get())) {
            target = TargetUtils.getPlayerTarget(targetRange.get(), priority.get());
        }

        FindItemResult egg = InvUtils.findInHotbar(Items.CREEPER_SPAWN_EGG);

        if (!egg.isHotbar() || TargetUtils.isBadTarget(target, targetRange.get())) return;

        targetPos = getTargetPos();

        if (targetPos == null) return;

        if (ticks > 0) {
            ticks--;
            return;
        }


        InvUtils.swap(egg.slot(), silentSwap.get());


        if (rotate.get()) {
            Rotations.rotate(Rotations.getYaw(targetPos), Rotations.getPitch(targetPos));
        }

        RenderUtils.renderTickingBlock(targetPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0, renderTime.get(), fade.get(), shrink.get());


        assert mc.interactionManager != null;
        ActionResult result = mc.interactionManager.interactBlock(mc.player, egg.getHand(), new BlockHitResult(targetPos.toCenterPos(), Direction.UP, targetPos, false));

        IntStream.iterate(0, i -> i <= extraPackets.get(), i -> i + 1) /*gud damn this is op*/.filter(i -> result.isAccepted()).filter(i -> swingHand.get()).forEach(i -> mc.player.swingHand(Hand.MAIN_HAND));

        if (silentSwap.get()) InvUtils.swapBack();

        targetPos = null;

        ticks = delay.get();
    }


    public BlockPos getTargetPos() {
        BlockPos pos = target.getBlockPos();
        if (Utils.state(pos.down()) != Blocks.AIR) return pos.down();
        if (Utils.state(pos.down(2)) != Blocks.AIR) return pos.down(2);
        return null;
    }
}
