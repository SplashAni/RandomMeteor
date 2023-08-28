package random.meteor.systems.modules.RM.PistonAura;

import meteordevelopment.meteorclient.utils.world.BlockUtils;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import random.meteor.systems.modules.RM.Blockpos;
import random.meteor.systems.modules.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import static meteordevelopment.meteorclient.utils.world.BlockUtils.canPlace;

public class PistonPositions {
    boolean checkExisting;
    BlockPos pos;
    /* returning poses*/
    BlockPos pistonPos,redstonPos,basePos;

    public Position pistonPostion(BlockPos pos, boolean checkExisting) {
        this.checkExisting = checkExisting;
        this.pos = pos;

    }

    public Position calcPosition() {

        BlockPos pp = getPistonPosition();
        basePosition bp = getBasePosition();

        if(pp == null || bp == null) return null;


        List<BlockPos> redstonePoses = new ArrayList<>();


        for(Direction d : Direction.values()){
            if(canPlace(getPistonPosition().offset(d)))  redstonePoses.add(pistonPos);
        }

        if(redstonePoses.isEmpty() || redstonePoses.size() > 2){
            return null;
        } else {
            return new Position(bp.createBase,bp.pos,pp,redstonePoses.get(0));
        }
    }

    public BlockPos getPistonPosition() {
        basePosition o = getBasePosition();

        if (o == null) return null;

        BlockPos p = switch (o.d) {
            case NORTH -> new BlockPos(o.pos.getX(), o.pos.getY(), o.pos.getZ() - 1);
            case SOUTH -> new BlockPos(o.pos.getX(), o.pos.getY(), o.pos.getZ() + 1);
            case WEST -> new BlockPos(o.pos.getX() - 1, o.pos.getY(), o.pos.getZ());
            case EAST -> new BlockPos(o.pos.getX() + 1, o.pos.getY(), o.pos.getZ());
            default -> null;
        };

        return isValidPiston(p) ? p : null;
    }



    public basePosition getBasePosition(){
        List<basePosition> p = new ArrayList<>();

        for(Direction d : Direction.values()){
            if(d == Direction.UP || d == Direction.DOWN) continue;
            BlockPos p1  = pos.offset(d);

            Block state = Utils.state(p1);

            if(state == Blocks.OBSIDIAN || state == Blocks.BEDROCK){

                p.add(new basePosition(p1,d,false));

            } else if (state == Blocks.AIR){

                p.add(new basePosition(p1,d,true));
            }

        }


        if(p.isEmpty() || p.size() > 2) return null;

        return p.get(1);

    }

    public boolean isValidPiston(BlockPos p) {

        if (checkExisting) {
            Block block = Utils.state(p);
            return block == Blocks.PISTON || block == Blocks.STICKY_PISTON;
        }

        return BlockUtils.canPlace(p);
    }
    public record Position(boolean createBase,BlockPos obsidianPos, BlockPos pistonPos, BlockPos redstonePos){

    }
    public record basePosition(BlockPos pos, Direction d,boolean createBase){

    }


}

