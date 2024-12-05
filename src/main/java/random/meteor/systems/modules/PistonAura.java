package random.meteor.systems.modules;

import meteordevelopment.meteorclient.events.entity.EntityAddedEvent;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.entity.SortPriority;
import meteordevelopment.meteorclient.utils.entity.TargetUtils;
import meteordevelopment.meteorclient.utils.player.FindItemResult;
import meteordevelopment.meteorclient.utils.player.InvUtils;
import meteordevelopment.meteorclient.utils.player.Rotations;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.BlockUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.block.Blocks;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.Mod;
import random.meteor.utils.PistonUtils;

import java.util.Map;
import java.util.Objects;

import static random.meteor.systems.modules.PistonPush.pistonYaw;

public class PistonAura extends Mod {
    private final SettingGroup sgRender = settings.createGroup("Render");
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

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
    );   private final Setting<Integer> redstoneDelay = sgGeneral.add(new IntSetting.Builder()
        .name("redstone-delay")
        .defaultValue(5)
        .min(0)
        .build()
    );   private final Setting<Integer> attackDelay = sgGeneral.add(new IntSetting.Builder()
        .name("attack-delay")
        .defaultValue(5)
        .min(0)
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

    AuraPosition auraPosition;
    PistonUtils pistonUtils;
    PlayerEntity target;
    FindItemResult piston, redstone, crystal;
    Stage stage;
    EndCrystalEntity crystalEntity;
    int pistonTick, crystalTick, redstoneTick, attackTick;

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
        redstone = InvUtils.findInHotbar(Items.REDSTONE_BLOCK);

        target = TargetUtils.getPlayerTarget(5, SortPriority.ClosestAngle);

        if (target == null || !crystal.isHotbar() || !piston.isHotbar() || !redstone.isHotbar()) return;


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
                if(mc.world.getBlockState(auraPosition.redstonePos).getBlock() == Blocks.REDSTONE_BLOCK){
                    BlockUtils.breakBlock(auraPosition.redstonePos,true);
                    return;
                }
                Rotations.rotate(pistonYaw(auraPosition.pistonBlock.direction()), 0);

                if (pistonTick > 0) {
                    pistonTick--;
                    return;
                }

                if (BlockUtils.place(auraPosition.pistonBlock.pos, piston,
                    false, 50, true, false, false)) {
                    setStage(Stage.Crystal);

                }
            }
            case Crystal -> {

                if (crystalTick > 0) {
                    crystalTick--;
                    return;
                }

                InvUtils.swap(crystal.slot(), false);
                mc.interactionManager.interactBlock(
                    mc.player, Hand.MAIN_HAND, new BlockHitResult(auraPosition.crystalPos.toCenterPos(),
                        Direction.UP, auraPosition.crystalPos, true));

                setStage(Stage.Redstone);

            }
            case Redstone -> {
                if (redstoneTick > 0) {
                    redstoneTick--;
                    return;
                }

                if (BlockUtils.place(auraPosition.redstonePos, redstone,
                    false, 50, true, false, false)) {

                    setStage(Stage.Attack);
                }

            }
            case Attack -> {
                if (attackTick > 0) {
                    attackTick--;
                    return;
                }
                if (crystalEntity != null) {
                    mc.interactionManager.attackEntity(mc.player, crystalEntity);
                    info("not null");
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

        info("Stage changed to: " + newStage);
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

    public void calc() {

        BlockPos crystalPos, redstonePos, pistonPos;

        Map<Direction, BlockPos> placeableMap = pistonUtils.getValidPosition(Objects.requireNonNull(target));

        if (placeableMap != null && !placeableMap.isEmpty()) {
            Map.Entry<Direction, BlockPos> entry = placeableMap.entrySet().stream().findFirst().orElse(null);

            crystalPos = entry.getValue();
            Direction direction = entry.getKey();
            pistonPos = crystalPos.offset(direction).up();
            redstonePos = pistonPos.offset(direction);

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

        event.renderer.box(auraPosition.crystalPos, new Color(77, 9, 255, 131), lineColor.get(), shapeMode.get(), 0);
        event.renderer.box(auraPosition.pistonBlock.pos, new Color(210, 255, 9, 131), lineColor.get(), shapeMode.get(), 0);

        event.renderer.box(auraPosition.redstonePos, new Color(255, 9, 9, 131), lineColor.get(), shapeMode.get(), 0);

    }

    public enum Stage {
        Prepare,
        Piston,
        Crystal,
        Redstone,
        Attack
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
