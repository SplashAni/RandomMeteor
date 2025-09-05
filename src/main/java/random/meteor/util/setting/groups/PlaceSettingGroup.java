package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import random.meteor.util.setting.DefaultSettingGroup;
import random.meteor.util.system.Mod;

import java.util.List;

public class PlaceSettingGroup extends DefaultSettingGroup {

    Setting<List<Block>> blocks;

    Setting<Boolean> airPlace;
    Setting<Boolean> attackCrystal;
    Setting<Boolean> createBase;
    Setting<Double> supportRange;

    public PlaceSettingGroup(Mod mod) {
        super(mod);

        blocks = getSettingGroup().add(new BlockListSetting.Builder()
            .name("blocks")
            .description("What blocks to use to place.")
            .defaultValue(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK)
            .build()
        );

        airPlace = getSettingGroup().add(new BoolSetting.Builder()
            .name("air-place")
            .defaultValue(true)
            .build()
        );

        createBase = getSettingGroup().add(new BoolSetting.Builder()
            .name("create-support")
            .description("Places blocks near other blocks to support the goal block")
            .defaultValue(false)
            .visible(() -> !airPlace.get())
            .build()
        );

        supportRange = getSettingGroup().add(new DoubleSetting.Builder()
            .name("support-range")
            .description("Range in which to interact.")
            .defaultValue(4.5)
            .min(0)
            .sliderMax(6)
            .visible(() -> !airPlace.get() && createBase.get() == true)
            .build()
        );
        attackCrystal = getSettingGroup().add(new BoolSetting.Builder()
            .name("attack-crystal")
            .defaultValue(true)
            .build()
        );

    }

    public enum WorldHeight {
        Old,
        New,
    }
}
