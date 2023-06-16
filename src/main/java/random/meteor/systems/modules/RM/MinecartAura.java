package random.meteor.systems.modules.RM;

import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import random.meteor.Main;
import random.meteor.systems.modules.utils.Utils;

import java.util.Objects;

import static meteordevelopment.meteorclient.utils.entity.TargetUtils.getPlayerTarget;
import static meteordevelopment.meteorclient.utils.entity.TargetUtils.isBadTarget;
import static meteordevelopment.meteorclient.utils.player.InvUtils.findInHotbar;
import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class MinecartAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgExplode = settings.createGroup("Explode");

    private final SettingGroup sgPause = settings.createGroup("Pause");

    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
            .name("target-range")
            .description("Range to find target.")
            .defaultValue(12)
            .range(1, 12)
            .sliderMax(12)
            .build()
    );
    private final Setting<Boolean> rotate = sgGeneral.add(new BoolSetting.Builder()
            .name("rotate")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> oldPlacements = sgGeneral.add(new BoolSetting.Builder()
            .name("1.12-placement")
            .defaultValue(true)
            .build()
    );

    private final Setting<Integer> railDelay = sgGeneral.add(new IntSetting.Builder()
            .name("rail-delay")
            .description("Delay to place rail")
            .defaultValue(15)
            .range(1, 35)
            .sliderMax(35)
            .build()
    );

    private final Setting<Integer> tntDelay = sgGeneral.add(new IntSetting.Builder()
            .name("tnt-delay")
            .description("Delay to place tnt")
            .defaultValue(15)
            .range(1, 35)
            .sliderMax(35)
            .build()
    );


    /// ignite

    private final Setting<explode> explodeMethod = sgExplode.add(new EnumSetting.Builder<explode>()
            .name("explode-method")
            .defaultValue(explode.BowFlame)
            .build()
    );
    private final Setting<Integer> explodeDelay = sgGeneral.add(new IntSetting.Builder()
            .name("explode-delay")
            .description("Delay to place tnt")
            .defaultValue(15)
            .range(1, 35)
            .sliderMax(35)
            .build()
    );
    private final Setting<Integer> pullDelay = sgExplode.add(new IntSetting.Builder()
            .name("pull-delay")
            .defaultValue(20)
            .range(5, 20)
            .sliderMax(20)
            .visible(() -> explodeMethod.get() == explode.BowFlame)
            .build()
    );
    private final Setting<Boolean> instant = sgPause.add(new BoolSetting.Builder()
            .name("instant")
            .description("")
            .defaultValue(false)
            .build()
    );


    //PAUSE

    private final Setting<Boolean> pauseMine = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-mine")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> pauseEat = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-eat")
            .description("")
            .defaultValue(true)
            .build()
    );
    private final Setting<Boolean> pauseDrink = sgPause.add(new BoolSetting.Builder()
            .name("pause-on-drink")
            .description("")
            .defaultValue(true)
            .build()
    );

    ///RENDER
    private final Setting<Boolean> swing = sgRender.add(new BoolSetting.Builder()
            .name("swing")
            .defaultValue(true)
            .build()
    );
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

    private Stage stage;
    int railTick, tntTick, explodeTicks;
    PlayerEntity target;
    BlockPos targetPos;
    float yaw, pitch;


    public MinecartAura() {
        super(Main.RM, "minecart-aura", "yess no skiddd");
    }

    @Override
    public void onActivate() {
        target = null;
        railTick = 0;
        tntTick = 0;
        explodeTicks = 0;
        stage = Stage.Preparing;
        super.onActivate();
    }

    @Override
    public void onDeactivate() {
        if (explodeMethod.get().equals(explode.BowFlame)) {
            mc.options.useKey.setPressed(false);
        }
        Rotations.rotate(yaw,pitch,50);
        super.onDeactivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {
        target = getPlayerTarget(range.get(), SortPriority.LowestDistance);
        if (isBadTarget(target, range.get())) {
            return;
        }
        if (PlayerUtils.shouldPause(pauseMine.get(), pauseEat.get(), pauseDrink.get())) return;


        switch (stage) {
            case Preparing -> {
                if (findPlacePos(target.getBlockPos()) != null) {
                    yaw = mc.player.getYaw();
                    pitch = mc.player.getPitch();
                    targetPos = findPlacePos(target.getBlockPos());
                    if (mc.world.getBlockState(targetPos.down()).getBlock() == Blocks.AIR && oldPlacements.get()) {
                        FindItemResult block = findInHotbar(itemStack -> itemStack.getItem() instanceof BlockItem);
                        BlockUtils.place(targetPos.down(1), block, rotate.get(), 50, swing.get(), true);
                    }
                    stage = Stage.Rail;
                }
            }
            case Rail -> {
                railTick++;
                if (railTick >= railDelay.get()) {
                    FindItemResult rail = findInHotbar(Items.RAIL);
                    if (BlockUtils.place(targetPos, rail, rotate.get(), 50, swing.get(), true)) {
                        yaw = mc.player.getYaw();
                        pitch = mc.player.getPitch();
                        stage = Stage.Tnt;
                    }
                }
            }
            case Tnt -> {
                tntTick++;
                if (tntTick >= tntDelay.get()) {
                    FindItemResult tnt = findInHotbar(Items.TNT_MINECART);
                    if (!tnt.isHotbar() && tnt.count() != 0) {
                        InvUtils.move().from(tnt.slot()).toHotbar(mc.player.getInventory().selectedSlot);
                    }
                    InvUtils.swap(tnt.slot(), true);
                    Objects.requireNonNull(mc.interactionManager).interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, target.getZ() + 0.5), Direction.UP, targetPos, true));
                    InvUtils.swapBack();
                    stage = Stage.Ignite;
                }
            }

            case Ignite -> {
                switch (explodeMethod.get()) {
                    case BowFlame -> {
                        explodeTicks++;
                        if (explodeTicks >= explodeDelay.get()) {
                            int delay = instant.get() ? 5 : pullDelay.get();
                            FindItemResult bow = findInHotbar(Items.BOW);
                            if (bow.isHotbar()) {
                                InvUtils.swap(bow.slot(), true);
                                assert mc.player != null;
                                ItemStack mainHandStack = mc.player.getMainHandStack();
                                if (mainHandStack.getItem() == Items.BOW && EnchantmentHelper.getLevel(Enchantments.FLAME, mainHandStack) > 0) { // aah yes my brai
                                    mc.options.useKey.setPressed(true);
                                    if (mc.player.getItemUseTime() > delay) {
                                        assert mc.interactionManager != null;
                                        Utils.rotate(EntityType.TNT_MINECART);
                                        mc.interactionManager.stopUsingItem(mc.player);
                                        mc.options.useKey.setPressed(false);
                                        InvUtils.swapBack();
                                        stage = Stage.Rail;
                                    }
                                }
                            }
                        }
                    }
                    case FlintSteel -> {
                        explodeTicks++;
                        if (explodeTicks >= explodeDelay.get()) {
                            FindItemResult flint = findInHotbar(Items.FLINT_AND_STEEL, Items.FIRE_CHARGE);
                            if (flint.isHotbar()) {
                                InvUtils.swap(flint.slot(), true);
                                mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(new Vec3d(targetPos.getX() + 0.5, targetPos.getY() + 0.5, target.getZ() + 0.5), Direction.UP, targetPos, true));
                                InvUtils.swapBack();
                                stage = Stage.Rail;
                            }
                        }
                    }
                }
            }
        }
    }

    private BlockPos findPlacePos(BlockPos targetPos) { // all credits duel to blockpostestm odule :trl:
        targetPos = new BlockPos(targetPos.down(1));

        if (canPlace(targetPos.add(0, 1, 1))) return targetPos.add(0, 1, 1);
        else if (canPlace(targetPos.add(1, 1, 0))) return targetPos.add(1, 1, 0);
        else if (canPlace(targetPos.add(1, 1, -1))) return targetPos.add(1, 1, -1);
        else if (canPlace(targetPos.add(-1, 1, 0))) return targetPos.add(-1, 1, 0);

        return null;
    }


    @EventHandler
    private void onRender(Render3DEvent event) {
        if (target == null && targetPos == null) return;

        event.renderer.box(targetPos, sideColor.get(), lineColor.get(), shapeMode.get(), 0);
    }
    public enum explode{
        BowFlame,
        FlintSteel
    }
    private enum Stage{
        Preparing,
        Rail,
        Tnt,
        Ignite
    }
}
