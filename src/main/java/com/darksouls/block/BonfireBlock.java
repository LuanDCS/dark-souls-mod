package com.darksouls.block;

import net.minecraft.block.Block;
<<<<<<< HEAD
=======
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
>>>>>>> ba47194 (Melhora no sistema de BonFire)
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
<<<<<<< HEAD
import net.minecraft.block.BlockState;

public final class BonfireBlock extends Block {
=======

public final class BonfireBlock extends Block {
    public static final BooleanProperty LIT = Properties.LIT;

>>>>>>> ba47194 (Melhora no sistema de BonFire)
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(
            0.125, 0.0, 0.125, 0.875, 0.75, 0.875);

    public BonfireBlock(Settings settings) {
        super(settings);
<<<<<<< HEAD
=======
        this.setDefaultState(this.getDefaultState().with(LIT, Boolean.FALSE));
>>>>>>> ba47194 (Melhora no sistema de BonFire)
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext ctx) {
        return SHAPE;
    }
<<<<<<< HEAD
=======

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }
>>>>>>> ba47194 (Melhora no sistema de BonFire)
}
