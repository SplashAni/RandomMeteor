package random.meteor.util.setting.groups;

import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import random.meteor.util.setting.GlobalSettingGroup;
import random.meteor.util.system.Mod;

import java.util.List;

public class PlaceSettingGroup extends GlobalSettingGroup {

    public Setting<List<Block>> blocks;

    public Setting<Boolean> instant;

    public Setting<Boolean> airPlace;

    public Setting<Boolean> checkEntities;
    public Setting<Boolean> attackCrystal;

    public Setting<Boolean> support;
    public Setting<Integer> supportRange;


    public PlaceSettingGroup(Mod mod) {
        super(mod, "Place");

        blocks = getSettingGroup().add(new BlockListSetting.Builder()
            .name("blocks")
            .description("What blocks to use to place.")
            .defaultValue(Blocks.OBSIDIAN, Blocks.CRYING_OBSIDIAN, Blocks.NETHERITE_BLOCK)
            .build()
        );

        instant = getSettingGroup().add(new BoolSetting.Builder()
            .name("instant")
            .defaultValue(false)
            .description("PLACES every single block at once super op but you'll probably get kicked xd")
            .build()
        );
        airPlace = getSettingGroup().add(new BoolSetting.Builder()
            .name("air-place")
            .defaultValue(true)
            .build()
        );

        checkEntities = getSettingGroup().add(new BoolSetting.Builder()
            .name("check-entities")
            .defaultValue(false)
            .build()
        );

        support = getSettingGroup().add(new BoolSetting.Builder()
            .name("support")
            .description("Places blocks near other blocks to support the goal block")
            .defaultValue(false)
            .visible(() -> !airPlace.get())
            .build()
        );

        supportRange = getSettingGroup().add(new IntSetting.Builder()
            .name("support-range")
            .description("Range in which to interact.")
            .defaultValue(4)
            .min(0)
            .sliderMax(5)
            .visible(() -> !airPlace.get() && support.get() == true)
            .build()
        );
        attackCrystal = getSettingGroup().add(new BoolSetting.Builder()
            .name("attack-crystal")
            .defaultValue(true)
            .build()
        );

    }


}
