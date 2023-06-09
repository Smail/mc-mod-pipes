package com.de.smail.examplemod.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

import static com.de.smail.examplemod.ExampleMod.LOGGER;

public class TransportPipeBlock extends Block {
    private static final BooleanProperty IS_CONNECTED_NORTH = BooleanProperty.create("is_connected_north");
    private static final BooleanProperty IS_CONNECTED_EAST = BooleanProperty.create("is_connected_east");
    private static final BooleanProperty IS_CONNECTED_SOUTH = BooleanProperty.create("is_connected_south");
    private static final BooleanProperty IS_CONNECTED_WEST = BooleanProperty.create("is_connected_west");
    private static final BooleanProperty IS_CONNECTED_ABOVE = BooleanProperty.create("is_connected_above");
    private static final BooleanProperty IS_CONNECTED_BELOW = BooleanProperty.create("is_connected_below");
    private static final VoxelShape[] VOXEL_SHAPES = new VoxelShape[Types.values().length];
    @SuppressWarnings("unchecked")
    private static final HashMap<List<Boolean>, VoxelShape>[] VOXEL_SHAPE_ROTATIONS = new HashMap[VOXEL_SHAPES.length];

    static {
        for (int i = 0; i < VOXEL_SHAPES.length; i++) {
            VOXEL_SHAPES[i] = createShape(Types.from(i));
            VOXEL_SHAPE_ROTATIONS[i] = new HashMap<>();
        }
    }

    public TransportPipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(IS_CONNECTED_NORTH, false)
                .setValue(IS_CONNECTED_EAST, false)
                .setValue(IS_CONNECTED_SOUTH, false)
                .setValue(IS_CONNECTED_WEST, false)
                .setValue(IS_CONNECTED_ABOVE, false)
                .setValue(IS_CONNECTED_BELOW, false)
        );
    }

    public TransportPipeBlock() {
        this(BlockBehaviour.Properties.of(Material.STONE)
                .dynamicShape()
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F));
    }

    private static VoxelShape createShape(Types connections) {
        return switch (connections) {
            case ZERO_UNCONNECTED -> Shapes.join(Shapes.empty(),
                    Shapes.box(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875), BooleanOp.OR);
            case ONE_DEAD_END -> Shapes.join(Shapes.empty(),
                    Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.6875), BooleanOp.OR);
            case TWO_PIPE -> Shapes.join(Shapes.empty(),
                    Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 1), BooleanOp.OR);
            case TWO_CORNER -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1), BooleanOp.OR);
            }
            case THREE_T -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.3125), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0, 0.3125, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);
            }
            case THREE_T_ROTATED -> {
                // TODO case 4 and 5 are basically the same, but rotated
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0, 0.3125, 0.6875, 1, 0.6875), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.3125), BooleanOp.OR);
            }
            case THREE_CORNER -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0, 0.3125, 0.3125, 0.3125, 0.6875, 0.6875), BooleanOp.OR);
            }
            case FOUR_T -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0, 0.3125, 0.6875, 0.6875, 0.6875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 0.3125), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0.6875, 0.6875, 0.6875, 1), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0.6875, 0.3125, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);
            }
            case FOUR_PLANE -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 1), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0, 0.3125, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);
            }
            case FIVE_T -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 1), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0, 0.3125, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0.3125, 0.6875, 0.3125, 0.6875, 1, 0.6875), BooleanOp.OR);
            }
            case SIX_ALL -> {
                var shape = Shapes.join(Shapes.empty(), Shapes.box(0.3125, 0, 0.3125, 0.6875, 1, 0.6875), BooleanOp.OR);
                shape = Shapes.join(shape, Shapes.box(0.3125, 0.3125, 0, 0.6875, 0.6875, 1), BooleanOp.OR);

                yield Shapes.join(shape, Shapes.box(0, 0.3125, 0.3125, 1, 0.6875, 0.6875), BooleanOp.OR);
            }
        };
    }

    private static VoxelShape rotateShapeHorizontally(Direction to, VoxelShape shape) {
        assert (to == Direction.NORTH || to == Direction.SOUTH || to == Direction.WEST || to == Direction.EAST);

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        int times = (to.ordinal() - Direction.NORTH.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.create(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    /**
     * Rotate a VoxelShape vertically. Implicitly rotates from a horizontal direction.
     *
     * @param to    The Direction to rotate the VoxelShape to.
     * @param shape The VoxelShape to rotate.
     * @return the rotated VoxelShape.
     */
    private static VoxelShape rotateShapeVertical(Direction to, VoxelShape shape) {
        assert (to == Direction.UP || to == Direction.DOWN);

        VoxelShape[] buffer = new VoxelShape[]{shape, Shapes.empty()};
        // When the `from` value is a horizontal value, i.e., NORTH, SOUTH, etc.,
        // then we need to turn it either 1 time or 3 times, depending on if `to` is UP or DOWN.
        // The ordinal value of DOWN is 0 and of UP is 1.
        // The expression 2 * to.ordinal() transforms the value range from [0, 1] to [0, 2], then add 1 so it's [1, 3].
        int times = 2 * to.ordinal() + 1;

        // DOWN: 0 UP: 1 NORTH: 2 SOUTH: 3 WEST: 4 EAST: 5
        // DOWN: -1 UP: -1 NORTH: 2 SOUTH: 0 WEST: 1 EAST: 3

        for (int i = 0; i < times; i++) {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] =
                    Shapes.or(buffer[1], Shapes.create(minX, minZ, 1 - maxY, maxX, maxZ, 1 - minY)));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    private static void updateState(BlockPos blockPos, Level level) {
        BlockState blockState = level.getBlockState(blockPos);
        if (!(blockState.getBlock() instanceof TransportPipeBlock)) return;

        boolean isAbove = level.getBlockState(blockPos.above()).getBlock() instanceof TransportPipeBlock;
        boolean isBelow = level.getBlockState(blockPos.below()).getBlock() instanceof TransportPipeBlock;
        boolean isNorth = level.getBlockState(blockPos.north()).getBlock() instanceof TransportPipeBlock;
        boolean isEast = level.getBlockState(blockPos.east()).getBlock() instanceof TransportPipeBlock;
        boolean isSouth = level.getBlockState(blockPos.south()).getBlock() instanceof TransportPipeBlock;
        boolean isWest = level.getBlockState(blockPos.west()).getBlock() instanceof TransportPipeBlock;

        level.setBlock(blockPos, level.getBlockState(blockPos)
                        .setValue(IS_CONNECTED_NORTH, isNorth)
                        .setValue(IS_CONNECTED_EAST, isEast)
                        .setValue(IS_CONNECTED_SOUTH, isSouth)
                        .setValue(IS_CONNECTED_WEST, isWest)
                        .setValue(IS_CONNECTED_ABOVE, isAbove)
                        .setValue(IS_CONNECTED_BELOW, isBelow),
                2);
    }

    public static Types getState(boolean isAbove, boolean isBelow, boolean isNorth,
                                 boolean isEast, boolean isSouth, boolean isWest) {
        int numConnected = (int) Arrays.stream(new Boolean[]{isAbove, isBelow, isNorth, isEast, isSouth, isWest})
                .filter(x -> x).count();

        if (numConnected == 0) return Types.ZERO_UNCONNECTED;
        if (numConnected == 1) return Types.ONE_DEAD_END;
        if (numConnected == 5) return Types.FIVE_T;
        if (numConnected == 6) return Types.SIX_ALL;
        if (numConnected == 2 && (isWest && isEast || isNorth && isSouth || isAbove && isBelow)) return Types.TWO_PIPE;
        if (numConnected == 2 && (isWest && isNorth || isWest && isSouth || isWest && isAbove || isWest && isBelow ||
                isEast && isNorth || isEast && isSouth || isEast && isAbove || isEast && isBelow ||
                isNorth && isAbove || isNorth && isBelow || isSouth && isAbove || isSouth && isBelow)) {
            return Types.TWO_CORNER;
        } else if (numConnected == 3 && (isWest && isNorth && isEast || isWest && isSouth && isEast ||
                isNorth && isEast && isSouth || isNorth && isWest && isSouth ||
                isWest && isAbove && isEast || isWest && isBelow && isEast ||
                isNorth && isAbove && isSouth || isNorth && isBelow && isSouth)) {
            return Types.THREE_T;
        } else if (numConnected == 3 && (isAbove && isBelow)) {
            return Types.THREE_T_ROTATED;
        } else if (numConnected == 3 && (isWest && isNorth && isBelow || isWest && isNorth && isAbove ||
                isEast && isNorth && isBelow || isEast && isNorth && isAbove ||
                isWest && isSouth && isBelow || isWest && isSouth && isAbove ||
                isEast && isSouth && isBelow || isEast && isSouth && isAbove)) {
            return Types.THREE_CORNER;
        } else if (numConnected == 4 && (isWest && isNorth && isEast && isAbove ||
                isWest && isNorth && isEast && isBelow || isSouth && isWest && isNorth && isAbove ||
                isSouth && isWest && isNorth && isBelow || isEast && isSouth && isWest && isAbove ||
                isEast && isSouth && isWest && isBelow || isNorth && isEast && isSouth && isAbove ||
                isNorth && isEast && isSouth && isBelow)) {
            return Types.FOUR_T;
        } else if (numConnected == 4 && (isWest && isNorth && isEast && isSouth ||
                isNorth && isSouth && isAbove && isBelow || isEast && isWest && isAbove && isBelow)) {
            return Types.FOUR_PLANE;
        } else if (numConnected == 4 && isAbove && isBelow &&
                (isNorth && isEast || isNorth && isWest || isSouth && isEast || isSouth && isWest)) {
            return Types.FOUR_T;
        }

        LOGGER.error("UNKNOWN STATE - Defaulting to " + Types.TWO_PIPE);
        return Types.TWO_PIPE;
    }

    public Types getState(BlockState blockState) {
        return getState(blockState.getValue(IS_CONNECTED_ABOVE), blockState.getValue(IS_CONNECTED_BELOW),
                blockState.getValue(IS_CONNECTED_NORTH), blockState.getValue(IS_CONNECTED_EAST),
                blockState.getValue(IS_CONNECTED_SOUTH), blockState.getValue(IS_CONNECTED_WEST));
    }

    private VoxelShape getProperRotation(BlockState blockState) {
        boolean isAbove = blockState.getValue(IS_CONNECTED_ABOVE);
        boolean isBelow = blockState.getValue(IS_CONNECTED_BELOW);
        boolean isNorth = blockState.getValue(IS_CONNECTED_NORTH);
        boolean isEast = blockState.getValue(IS_CONNECTED_EAST);
        boolean isSouth = blockState.getValue(IS_CONNECTED_SOUTH);
        boolean isWest = blockState.getValue(IS_CONNECTED_WEST);

        Types connections = getState(blockState);
        VoxelShape shape = VOXEL_SHAPES[connections.getValue()];

        return switch (connections) {
            case ZERO_UNCONNECTED, SIX_ALL -> shape;
            case ONE_DEAD_END -> {
                if (isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);
                else if (isSouth) shape = rotateShapeHorizontally(Direction.WEST, shape);
                else if (isEast) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (isAbove) shape = rotateShapeVertical(Direction.UP, shape);
                else if (isBelow) shape = rotateShapeVertical(Direction.DOWN, shape);

                yield shape;
            }
            case TWO_PIPE -> {
                if (isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);
                else if (isAbove) shape = rotateShapeVertical(Direction.UP, shape);

                yield shape;
            }
            case TWO_CORNER -> {
                if (isWest && isNorth) {
                    shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    shape = rotateShapeVertical(Direction.UP, shape);
                } else if (isEast && isNorth) {
                    shape = rotateShapeVertical(Direction.UP, shape);
                    shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    shape = rotateShapeVertical(Direction.UP, shape);
                } else if (isEast && isSouth) {
                    shape = rotateShapeVertical(Direction.UP, shape);
                    shape = rotateShapeVertical(Direction.UP, shape);
                    shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    shape = rotateShapeVertical(Direction.UP, shape);
                } else if (isWest && isSouth) {
                    shape = rotateShapeVertical(Direction.DOWN, shape);
                    shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    shape = rotateShapeVertical(Direction.UP, shape);
                } else if (isAbove || isBelow) {
                    // Rotate first vertically and then horizontally
                    if (isAbove) shape = rotateShapeVertical(Direction.DOWN, shape);
                    if (isNorth) shape = rotateShapeHorizontally(Direction.WEST, shape);
                    else if (isWest) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    else if (isEast) shape = rotateShapeHorizontally(Direction.EAST, shape);
                }

                yield shape;
            }
            case THREE_T -> {
                // Rotate vertically if a pipe is connected above or below
                boolean isVertical = isAbove || isBelow;
                if (isAbove && !isBelow) shape = rotateShapeVertical(Direction.UP, shape);
                else if (isBelow && !isAbove) shape = rotateShapeVertical(Direction.DOWN, shape);

                if (isVertical && isNorth && isSouth) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (isEast && isSouth && isWest) shape = rotateShapeHorizontally(Direction.WEST, shape);
                else if (isEast && isSouth && isNorth) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (isNorth && isSouth && isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);

                yield shape;
            }
            case THREE_T_ROTATED -> {
                assert isAbove && isBelow;

                if (isSouth) shape = rotateShapeHorizontally(Direction.WEST, shape);
                else if (isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);
                else if (isEast) shape = rotateShapeHorizontally(Direction.SOUTH, shape);

                yield shape;
            }
            case THREE_CORNER -> {
                // Default voxel shape is with a connected pipe below
                if (isAbove) shape = rotateShapeVertical(Direction.DOWN, shape);

                if (isNorth && isWest) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (isNorth && isEast) shape = rotateShapeHorizontally(Direction.WEST, shape);
                else if (isSouth && isEast) shape = rotateShapeHorizontally(Direction.EAST, shape);

                yield shape;
            }
            case FOUR_T -> {
                // Default voxel shape is with a connected pipe below
                if (isAbove) {
                    shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                    shape = rotateShapeVertical(Direction.DOWN, shape);
                }

                if (isEast && isSouth && isWest) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (isNorth && isSouth && isWest) shape = rotateShapeHorizontally(Direction.WEST, shape);
                else if (isEast && isNorth && isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);

                if (isAbove) shape = rotateShapeHorizontally(Direction.EAST, shape);

                yield shape;
            }
            case FOUR_PLANE -> {
                if (isAbove) shape = rotateShapeVertical(Direction.DOWN, shape);
                if (isNorth) shape = rotateShapeHorizontally(Direction.SOUTH, shape);

                yield shape;
            }
            case FIVE_T -> {
                if (!isSouth) shape = rotateShapeVertical(Direction.DOWN, shape);
                else if (!isNorth || !isWest || !isEast || !isAbove) shape = rotateShapeVertical(Direction.UP, shape);

                if (!isWest) shape = rotateShapeHorizontally(Direction.EAST, shape);
                else if (!isEast) shape = rotateShapeHorizontally(Direction.SOUTH, shape);
                else if (!isAbove) shape = rotateShapeVertical(Direction.UP, shape);

                yield shape;
            }
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_CONNECTED_NORTH)
                .add(IS_CONNECTED_EAST)
                .add(IS_CONNECTED_SOUTH)
                .add(IS_CONNECTED_WEST)
                .add(IS_CONNECTED_ABOVE)
                .add(IS_CONNECTED_BELOW);
    }

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(@NotNull BlockState blockState, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext ctx) {
        List<Boolean> bs = Stream.of(IS_CONNECTED_ABOVE, IS_CONNECTED_BELOW, IS_CONNECTED_NORTH,
                IS_CONNECTED_EAST, IS_CONNECTED_SOUTH, IS_CONNECTED_WEST).map(blockState::getValue).toList();
        HashMap<List<Boolean>, VoxelShape> voxelShapeRotationMap = VOXEL_SHAPE_ROTATIONS[getState(blockState).getValue()];

        // Lazy initialize rotations
        if (!voxelShapeRotationMap.containsKey(bs))
            voxelShapeRotationMap.put(bs, getProperRotation(blockState));
        return getProperRotation(blockState);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(@NotNull BlockState blockState, Level level, BlockPos thisPos, Block fromBlock,
                                @NotNull BlockPos fromPos, boolean isMoving) {
        System.err.println(fromBlock.getClass().getSimpleName());
        System.err.println(level.getBlockState(fromPos).getBlock().getClass().getSimpleName());

        Stream.of(thisPos.above(), thisPos.below(), thisPos.north(), thisPos.east(), thisPos.south(), thisPos.west())
                .forEach(pos -> updateState(pos, level));
        updateState(thisPos, level);

        super.neighborChanged(blockState, level, thisPos, fromBlock, fromPos, isMoving);
    }

    enum Types {
        ZERO_UNCONNECTED,
        ONE_DEAD_END,
        TWO_PIPE, TWO_CORNER,
        THREE_T, THREE_T_ROTATED, THREE_CORNER,
        FOUR_T, FOUR_PLANE,
        FIVE_T,
        SIX_ALL;

        public static Types from(int x) {
            return Types.values()[x];
        }

        public int getValue() {
            return this.ordinal();
        }
    }
}
