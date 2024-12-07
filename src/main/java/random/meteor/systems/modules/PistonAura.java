package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.Mod;
import random.meteor.utils.PistonUtils;
import random.meteor.utils.Utils;

import java.util.Map;

import static random.meteor.systems.modules.PistonPush.pistonYaw;

public class PistonAura extends Mod {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    public final Setting<Mode> mode = sgGeneral.add(new EnumSetting.Builder<Mode>()
        .name("mode")
        .defaultValue(Mode.RedstoneBlock)
        .build()
    );
    public final Setting<PistonRotate> rotateMode = sgGeneral.add(new EnumSetting.Builder<PistonRotate>()
        .name("rotation-mode")
        .defaultValue(PistonRotate.None)
        .build()
    );
    public final Setting<SwapMode> swapMode = sgGeneral.add(new EnumSetting.Builder<SwapMode>()
        .name("swap-mode")
        .defaultValue(SwapMode.Silent)
        .build()
    );

    private final SettingGroup sgTrap = settings.createGroup("Trap");

    private final SettingGroup sgRender = settings.createGroup("Render");
    private final Setting<Integer> pistonDelay = sgGeneral.add(new IntSetting.Builder()
        .name("piston-delay")
        .defaultValue(5)
        .min(0)
        .build()
    );
    private final Setting<Integer> crystalDelay = sgGeneral.add(new IntSetting.Builder()
        .name("crystal-delay")
        .defaultValue(5)
        .min(0)
        .build()
    );
    private final Setting<Integer> redstoneDelay = sgGeneral.add(new IntSetting.Builder()
        .name("redstone-delay")
        .defaultValue(5)
        .min(0)
        .build()
    );
    private final Setting<Integer> attackDelay = sgGeneral.add(new IntSetting.Builder()
        .name("attack-delay")
        .defaultValue(5)
        .min(0)
        .build()
    );
    // render
    private final Setting<Boolean> renderPiston = sgRender.add(new BoolSetting.Builder()
        .name("render-piston")
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> pistonShapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("piston-shape-mode")
        .defaultValue(ShapeMode.Both)
        .visible(renderPiston::get)
        .build()
    );
    private final Setting<SettingColor> pistonSideColor = sgRender.add(new ColorSetting.Builder()
        .name("piston-side-color")
        .defaultValue(new SettingColor(225,211,0,75))
        .visible(renderPiston::get)
        .build()
    );
    private final Setting<SettingColor> pistonLineColor = sgRender.add(new ColorSetting.Builder()
        .name("piston-line-color")
        .description("The line color of the piston rendering.")
        .defaultValue(new SettingColor(210,225,0,255))
        .visible(renderPiston::get)
        .build()
    );
    private final Setting<Boolean> renderPistonHead = sgGeneral.add(new BoolSetting.Builder()
        .name("render-piston-head")
        .defaultValue(true)
            .visible(renderPiston::get)
        .build()
    );
    private final Setting<Boolean> renderCrystalBase = sgRender.add(new BoolSetting.Builder()
        .name("render-crystal-base")
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> crystalBaseShapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("crystal-base-shape-mode")
        .defaultValue(ShapeMode.Both)
        .visible(renderCrystalBase::get)
        .build()
    );
    private final Setting<SettingColor> crystalBaseSideColor = sgRender.add(new ColorSetting.Builder()
        .name("crystal-base-side-color")
        .defaultValue(new SettingColor(225,0,179,75))
        .visible(renderCrystalBase::get)
        .build()
    );
    private final Setting<SettingColor> crystalBaseLineColor = sgRender.add(new ColorSetting.Builder()
        .name("crystal-base-line-color")
        .defaultValue(new SettingColor(225,0,161,255))
        .visible(renderCrystalBase::get)
        .build()
    );

    private final Setting<Boolean> renderActivator = sgRender.add(new BoolSetting.Builder()
        .name("render-redstone")
        .defaultValue(true)
        .build()
    );
    private final Setting<ShapeMode> activatorShapeMode = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("redstone-shape-mode")
        .defaultValue(ShapeMode.Both)
        .visible(renderActivator::get)
        .build()
    );
    private final Setting<SettingColor> activatorSideColor = sgRender.add(new ColorSetting.Builder()
        .name("redstone-side-color")
        .defaultValue(new SettingColor(225,0,0,75))
        .visible(renderActivator::get)
        .build()
    );
    private final Setting<SettingColor> activatorLineColor = sgRender.add(new ColorSetting.Builder()
        .name("redstone-line-color")
        .defaultValue(new SettingColor(225,0,0,255))
        .visible(renderActivator::get)
        .build()
    );


    AuraPosition auraPosition;
    PistonUtils pistonUtils;
    PlayerEntity target;
    FindItemResult piston, activatorItem, crystal;
    Stage stage;
    EndCrystalEntity crystalEntity;
    int pistonTick, crystalTick, redstoneTick, attackTick;
    private boolean stopRecursion = false;

    public PistonAura() {
        super("piston-aura", "pisaton moment");
    }

    @Override
    public void onActivate() {
        pistonUtils = new PistonUtils();
        auraPosition = null;


        setStage(Stage.Prepare);
        super.onActivate();
    }

    @EventHandler
    public void onTick(TickEvent.Pre event) {

        crystal = InvUtils.findInHotbar(Items.END_CRYSTAL);
        piston = InvUtils.findInHotbar(Items.PISTON, Items.STICKY_PISTON);
        activatorItem = switch (mode.get()) {
            case Button -> InvUtils.findInHotbar(i -> Block.getBlockFromItem(i.getItem()) instanceof ButtonBlock);
            case RedstoneBlock -> InvUtils.findInHotbar(Items.REDSTONE_BLOCK);
        };

        target = TargetUtils.getPlayerTarget(5, SortPriority.ClosestAngle);

        if (target == null || !crystal.isHotbar() || !piston.isHotbar() || !activatorItem.isHotbar()) return;


        if (auraPosition == null) {
            calc();
        }

        switch (this.stage) {
            case Prepare -> {
                if (auraPosition != null) {
                    setStage(Stage.Piston);
                }
            }
            case Piston -> {
                if (mc.world.getBlockState(auraPosition.redstonePos).getBlock() == Blocks.REDSTONE_BLOCK) {
                    BlockUtils.breakBlock(auraPosition.redstonePos, true);
                    return;
                }

                switch (rotateMode.get()) {

                    case None ->
                        mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(pistonYaw(auraPosition.pistonBlock.direction()), 0, true));
                    case Full -> Rotations.rotate(pistonYaw(auraPosition.pistonBlock.direction()), 0);
                }


                if (pistonTick > 0) {
                    pistonTick--;
                    return;
                }


                if (BlockUtils.place(auraPosition.pistonBlock.pos, piston,
                    false, 50, true, false, swapMode.get() == SwapMode.Silent) || pistonUtils.isPiston(auraPosition.pistonBlock.pos)) {
                    setStage(Stage.Crystal);

                }
            }
            case Crystal -> {

                if (crystalTick > 0) {
                    crystalTick--;
                    return;
                }

                if (mc.world.getBlockState(auraPosition.crystalPos.up()).isAir()) {

                    InvUtils.swap(crystal.slot(), swapMode.get() == SwapMode.Silent);

                    mc.interactionManager.interactBlock(
                        mc.player, Hand.MAIN_HAND, new BlockHitResult(auraPosition.crystalPos.toCenterPos(),
                            Direction.UP, auraPosition.crystalPos, true));

                    if (swapMode.get() == SwapMode.Silent) InvUtils.swapBack();
                }

                if (crystalEntity != null) setStage(Stage.Activate);

            }
            case Activate -> {
                if (redstoneTick > 0) {
                    redstoneTick--;
                    return;
                }


                switch (mode.get()) {
                    case Button -> {

                        boolean interact = Utils.state(auraPosition.redstonePos.up(1)) instanceof ButtonBlock
                            || BlockUtils.place(auraPosition.redstonePos.up(1), activatorItem,
                            false, 50, true,
                            false, swapMode.get() == SwapMode.Silent);

                        if (interact) {
                            mc.interactionManager.interactBlock(
                                mc.player, Hand.MAIN_HAND,
                                new BlockHitResult(
                                    auraPosition.redstonePos.up().toCenterPos(),
                                    Direction.UP,
                                    auraPosition.redstonePos.up(),
                                    true
                                )
                            );
                            setStage(Stage.Attack);
                        }

                    }
                    case RedstoneBlock -> {
                        if (BlockUtils.place(auraPosition.redstonePos, activatorItem,
                            false, 50, true,
                            false, swapMode.get() == SwapMode.Silent)) {
                            setStage(Stage.Attack);
                        }
                    }
                }

            }
            case Attack -> {
                if (attackTick > 0) {
                    attackTick--;
                    return;
                }
                if (crystalEntity != null) {
                    mc.interactionManager.attackEntity(mc.player, crystalEntity);
                }
                setStage(Stage.Prepare);
            }
        }
    }

    public void setStage(Stage newStage) {
        pistonTick = pistonDelay.get();
        crystalTick = crystalDelay.get();
        redstoneTick = redstoneDelay.get();
        attackTick = attackDelay.get();


        this.stage = newStage;
        if (newStage == Stage.Prepare && crystalEntity != null) {
            crystalEntity = null;
        }
    }

    @Override
    public String getInfoString() {
        return stage == null ? "No target" : stage.toString();
    }

    @EventHandler
    public void onSpawn(EntityAddedEvent event) {
        if (event.entity instanceof EndCrystalEntity crystal) {
            if (auraPosition == null) return;
            if (crystal.getBlockPos().equals(auraPosition.crystalPos.up(1))) {
                crystalEntity = (EndCrystalEntity) event.entity;
            }
        }
    }

    @EventHandler
    public void onPacket(PacketEvent.Send event) {
        if (event.packet instanceof PlayerMoveC2SPacket.Full packet) {
            if (stage == Stage.Piston && !stopRecursion) {

                float modifiedYaw = pistonYaw(auraPosition.pistonBlock.direction);

                stopRecursion = true;

                event.cancel();

                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(
                    packet.getX(-1), packet.getY(-1), packet.getZ(-1),
                    modifiedYaw, 0, packet.isOnGround()
                ));

                stopRecursion = false;
            }
        }
    }


    public void calc() {

        Mode mode = this.mode.get();
        BlockPos crystalPos, redstonePos, pistonPos;

        Map<Direction, BlockPos> placeableMap = pistonUtils.getValidPosition(target, false, mode);

        if (placeableMap == null || placeableMap.isEmpty()) {
            placeableMap = pistonUtils.getValidPosition(target, true, mode);
        }

        if (placeableMap != null && !placeableMap.isEmpty()) {
            Map.Entry<Direction, BlockPos> entry = placeableMap.entrySet().stream().findFirst().orElse(null);

            crystalPos = entry.getValue();
            Direction direction = entry.getKey();
            pistonPos = crystalPos.offset(direction).up();

            redstonePos = switch (mode) {
                case Button -> pistonUtils.getButtonPos(pistonPos, direction);
                case RedstoneBlock -> pistonPos.offset(direction);
            };

            AuraPosition aura = new AuraPosition(
                crystalPos, new PistonInfo(pistonPos, direction), redstonePos
            );

            if (this.auraPosition == null) {
                setAuraPosition(aura);
            }

            if (!aura.equals(auraPosition)) {
                setAuraPosition(aura);
            }

        } else {
            auraPosition = null;
        }

    }

    public void setAuraPosition(AuraPosition auraPosition) {
        this.auraPosition = auraPosition;

        setStage(Stage.Prepare);
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        if (auraPosition == null) return;

        if (renderCrystalBase.get())
            event.renderer.box(auraPosition.crystalPos, crystalBaseSideColor.get(), crystalBaseLineColor.get(), crystalBaseShapeMode.get(), 0);

        if (renderPiston.get()) {
            event.renderer.box(auraPosition.pistonBlock.pos,
                pistonSideColor.get(), pistonLineColor.get(), pistonShapeMode.get(), 0);

            if (renderPiston.get() && renderPistonHead.get()) pistonUtils.renderPistonHead(event, auraPosition.pistonBlock.pos,
                pistonSideColor.get(), pistonLineColor.get(), pistonShapeMode.get());

        }


        if (renderActivator.get())
            event.renderer.box(auraPosition.redstonePos, activatorSideColor.get(), activatorLineColor.get(), activatorShapeMode.get(), 0);
    }


    public enum Stage {
        Prepare,
        Piston,
        Crystal,
        Activate,
        Attack
    }

    public enum PistonRotate {
        None,
        Full
    }

    public enum SwapMode {
        Normal,
        Silent
    }

    public enum Mode {
        Button,
        RedstoneBlock
    }

    public record AuraPosition(BlockPos crystalPos, PistonInfo pistonBlock, BlockPos redstonePos) {
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;

            AuraPosition auraPosition1 = (AuraPosition) obj;
            return crystalPos.equals(auraPosition1.crystalPos) &&
                pistonBlock.equals(auraPosition1.pistonBlock) &&
                redstonePos.equals(auraPosition1.redstonePos);
        }
    }

    public record PistonInfo(BlockPos pos, Direction direction) {

    }

}
