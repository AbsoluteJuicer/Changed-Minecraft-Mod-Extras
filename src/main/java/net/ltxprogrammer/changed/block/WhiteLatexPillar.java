package net.ltxprogrammer.changed.block;

import net.ltxprogrammer.changed.entity.ChangedEntity;
import net.ltxprogrammer.changed.entity.GooType;
import net.ltxprogrammer.changed.entity.TransfurCause;
import net.ltxprogrammer.changed.entity.TransfurContext;
import net.ltxprogrammer.changed.entity.variant.TransfurVariant;
import net.ltxprogrammer.changed.entity.variant.TransfurVariantInstance;
import net.ltxprogrammer.changed.init.ChangedBlocks;
import net.ltxprogrammer.changed.init.ChangedDamageSources;
import net.ltxprogrammer.changed.init.ChangedSounds;
import net.ltxprogrammer.changed.process.ProcessTransfur;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Random;

import static net.ltxprogrammer.changed.block.AbstractLatexBlock.COVERED;

public class WhiteLatexPillar extends AbstractCustomShapeTallBlock implements WhiteLatexTransportInterface {
    public static final VoxelShape SHAPE_WHOLE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 30.0D, 14.0D);
    public static final VoxelShape SHAPE_SMALL = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 1.0D, 14.0D);
    public static final VoxelShape SHAPE_OCC = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 32.0D, 16.0D);

    public static final BooleanProperty EXTENDED = BlockStateProperties.EXTENDED;

    public WhiteLatexPillar(Properties properties) {
        super(properties.randomTicks().isSuffocating(ChangedBlocks::never).isViewBlocking(ChangedBlocks::never));
    }

    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        TransfurVariantInstance variant = ProcessTransfur.getPlayerTransfurVariant(player);
        if (variant != null && variant.getGooType() == GooType.PURE_WHITE_GOO &&
                /*player.isShiftKeyDown() && */player.getItemInHand(player.getUsedItemHand()).isEmpty() && !WhiteLatexTransportInterface.isEntityInWhiteLatex(player)) { // Empty-handed RMB
            if (pos.distSqr(new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ())) > 4.0)
                return super.use(state, level, pos, player, hand, hitResult);

            WhiteLatexTransportInterface.entityEnterLatex(player, pos);
            return InteractionResult.CONSUME;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos blockPos) {
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER)
            return super.canSurvive(blockState, level, blockPos);
        else {
            BlockState belowState = level.getBlockState(blockPos.below());
            if (belowState.is(ChangedBlocks.WHITE_LATEX_BLOCK.get()) ||
                    (belowState.getProperties().contains(COVERED) && belowState.getValue(COVERED) == GooType.PURE_WHITE_GOO))
                return super.canSurvive(blockState, level, blockPos);
            else
                return false;
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityCollisionContext && entityCollisionContext.getEntity() != null)
            if (GooType.getEntityGooType(entityCollisionContext.getEntity()) == GooType.PURE_WHITE_GOO)
                return Shapes.empty();

        return getInteractionShape(blockState, blockGetter, blockPos);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        if (p_60547_.getValue(EXTENDED)) {

            if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
                return SHAPE_WHOLE;
            else
                return SHAPE_WHOLE.move(0, -1, 0);
        }
        else {
            if (p_60547_.getValue(HALF) == DoubleBlockHalf.LOWER)
                return SHAPE_SMALL;
            else
                return SHAPE_SMALL.move(0, -1, 0);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return blockState.getValue(EXTENDED) ? RenderShape.MODEL : RenderShape.INVISIBLE;
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EXTENDED);
    }

    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (!state.getValue(EXTENDED))
            return;

        if (entity instanceof LivingEntity le && !(entity instanceof ChangedEntity)) {
            if (entity instanceof Player player && ProcessTransfur.isPlayerLatex(player)) {
                var latexType = ProcessTransfur.getPlayerTransfurVariant(player).getGooType();

                if (latexType == GooType.PURE_WHITE_GOO) {
                    WhiteLatexTransportInterface.entityEnterLatex(player, pos);
                    return;
                }

                else if (latexType.isHostileTo(GooType.PURE_WHITE_GOO)) {
                    player.hurt(ChangedDamageSources.WHITE_LATEX, 3.0f);
                }
            }
            else {
                ProcessTransfur.progressTransfur(le, 4.8f, TransfurVariant.PURE_WHITE_GOO_WOLF, TransfurContext.hazard(TransfurCause.WHITE_LATEX));
            }
        } else if (entity instanceof ChangedEntity changedEntity) {
            if (changedEntity.getGooType().isHostileTo(GooType.PURE_WHITE_GOO))
                changedEntity.hurt(ChangedDamageSources.WHITE_LATEX, 3.0f);
        }
        entity.makeStuckInBlock(state, new Vec3((double)0.8F, 0.75D, (double)0.8F));
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, Random random) {
        super.randomTick(blockState, level, blockPos, random);
        if (blockState.getValue(HALF) == DoubleBlockHalf.UPPER)
            return;

        boolean wantToAppear = WhiteLatexBlock.targetNearby(level, blockPos);
        if (blockState.getValue(EXTENDED) != wantToAppear) {
            ChangedSounds.broadcastSound(level.getServer(), ChangedSounds.POISON, blockPos, 1, 1);
            level.setBlockAndUpdate(blockPos, blockState.setValue(EXTENDED, wantToAppear));
            level.setBlockAndUpdate(blockPos.above(), blockState.setValue(HALF, DoubleBlockHalf.UPPER).setValue(EXTENDED, wantToAppear));
        }
    }

    @Override
    public boolean allowTransport(BlockState blockState) {
        return blockState.getValue(EXTENDED);
    }
}
