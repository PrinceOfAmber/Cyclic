package com.lothrazar.cyclic.block.cable;

import com.google.common.collect.Maps;
import com.lothrazar.cyclic.base.BlockBase;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.World;

public abstract class CableBase extends BlockBase {

  //regular connections
  public static final EnumProperty<EnumConnectType> DOWN = EnumProperty.create("down", EnumConnectType.class);
  public static final EnumProperty<EnumConnectType> UP = EnumProperty.create("up", EnumConnectType.class);
  public static final EnumProperty<EnumConnectType> NORTH = EnumProperty.create("north", EnumConnectType.class);
  public static final EnumProperty<EnumConnectType> SOUTH = EnumProperty.create("south", EnumConnectType.class);
  public static final EnumProperty<EnumConnectType> WEST = EnumProperty.create("west", EnumConnectType.class);
  public static final EnumProperty<EnumConnectType> EAST = EnumProperty.create("east", EnumConnectType.class);
  public static final Map<Direction, EnumProperty<EnumConnectType>> FACING_TO_PROPERTY_MAP = Util.make(Maps.newEnumMap(Direction.class), (p) -> {
    p.put(Direction.NORTH, NORTH);
    p.put(Direction.EAST, EAST);
    p.put(Direction.SOUTH, SOUTH);
    p.put(Direction.WEST, WEST);
    p.put(Direction.UP, UP);
    p.put(Direction.DOWN, DOWN);
  });
  private static final double top = 16;
  private static final double bot = 0;
  private static final double C = 8;
  private static final double w = 2;
  private static final double sm = C - w;
  private static final double lg = C + w;
  protected static final VoxelShape AABB = Block.makeCuboidShape(sm, sm, sm, lg, lg, lg);
  //Y for updown
  protected static final VoxelShape AABB_UP = Block.makeCuboidShape(sm, sm, sm, lg, top, lg);
  protected static final VoxelShape AABB_DOWN = Block.makeCuboidShape(sm, bot, sm, lg, lg, lg);
  //Z for n-s
  protected static final VoxelShape AABB_NORTH = Block.makeCuboidShape(sm, sm, bot, lg, lg, lg);
  protected static final VoxelShape AABB_SOUTH = Block.makeCuboidShape(sm, sm, sm, lg, lg, top);
  //X for e-w
  protected static final VoxelShape AABB_WEST = Block.makeCuboidShape(bot, sm, sm, lg, lg, lg);
  protected static final VoxelShape AABB_EAST = Block.makeCuboidShape(sm, sm, sm, top, lg, lg);

  static boolean shapeConnects(BlockState state, EnumProperty<EnumConnectType> dirctionProperty) {
    return state.get(dirctionProperty).isConnected();
  }

  public static VoxelShape createShape(BlockState state) {
    VoxelShape shape = AABB;
    if (shapeConnects(state, UP)) {
      shape = VoxelShapes.combine(shape, AABB_UP, IBooleanFunction.OR);
    }
    if (shapeConnects(state, DOWN)) {
      shape = VoxelShapes.combine(shape, AABB_DOWN, IBooleanFunction.OR);
    }
    if (shapeConnects(state, WEST)) {
      shape = VoxelShapes.combine(shape, AABB_WEST, IBooleanFunction.OR);
    }
    if (shapeConnects(state, EAST)) {
      shape = VoxelShapes.combine(shape, AABB_EAST, IBooleanFunction.OR);
    }
    if (shapeConnects(state, NORTH)) {
      shape = VoxelShapes.combine(shape, AABB_NORTH, IBooleanFunction.OR);
    }
    if (shapeConnects(state, SOUTH)) {
      shape = VoxelShapes.combine(shape, AABB_SOUTH, IBooleanFunction.OR);
    }
    return shape;
  }

  public CableBase(Properties properties) {
    super(properties);
  }

  @Override
  public ActionResultType onBlockActivated(BlockState state, World world,
      BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (hit.getFace() == null) {
      return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }
    ItemStack stack = player.getHeldItem(handIn);
    if (!stack.getItem().isIn(CableWrench.WRENCH)) {
      return super.onBlockActivated(state, world, pos, player, handIn, hit);
    }
    final float hitLimit = 0.28F;
    Direction sideToToggle = hit.getFace();
    //hitX y and Z from old onBlockActivated 
    double hitX = hit.getHitVec().x - pos.getX();
    double hitY = hit.getHitVec().y - pos.getY();
    double hitZ = hit.getHitVec().z - pos.getZ();
    if (hitX < hitLimit) {
      sideToToggle = Direction.WEST;
    }
    else if (hitX > 1 - hitLimit) {
      sideToToggle = Direction.EAST;
    }
    else if (hitY < hitLimit) {
      sideToToggle = Direction.DOWN;
    }
    else if (hitY > 1 - hitLimit) {
      sideToToggle = Direction.UP;
    }
    else if (hitZ < hitLimit) {
      sideToToggle = Direction.NORTH;
    }
    else if (hitZ > 1 - hitLimit) {
      sideToToggle = Direction.SOUTH;
    }
    //now we have the same data that onBlockActivated used
    //    WrenchActionType type = WrenchActionType.getType(stack);
    //    if (type == WrenchActionType.EXTRACT) {
    //      if (state.getBlock() == BlockRegistry.fluid_pipe
    //          || state.getBlock() == BlockRegistry.item_pipe) {
    //        if (state.hasProperty(BlockCableFluid.EXTR)) {
    //          DirectionNullable current = state.get(BlockCableFluid.EXTR);
    //          DirectionNullable newextr = current.toggle(sideToToggle);
    //          world.setBlockState(pos, state.with(BlockCableFluid.EXTR, newextr));
    //        }
    //      }
    //    }
    //    else if (type == WrenchActionType.DISABLE && state.getBlock() instanceof CableBase) {
    EnumProperty<EnumConnectType> prop = CableBase.FACING_TO_PROPERTY_MAP.get(sideToToggle);
    if (state.hasProperty(prop)) {
      EnumConnectType status = state.get(prop);
      //inventory is decided not by wrench but by normal mode
      //so it rotates: 
      // INVENTORY// NONE -> CABLE(extract) -> BLOCKED -> and back to none again
      switch (status) {
        case BLOCKED:
          //unblock it go back to none (dont know where connection would be if any)
          world.setBlockState(pos, state.with(prop, EnumConnectType.NONE));
        break;
        case INVENTORY: // inventory connection or
        case NONE: // no connection
          world.setBlockState(pos, state.with(prop, EnumConnectType.CABLE)); //try to extract
        break;
        case CABLE: // extract
          // extract to blocked
          world.setBlockState(pos, state.with(prop, EnumConnectType.BLOCKED));
        break;
      }
      //else state does not have prop . ttreat it teh same as (getBlock is not a CableBase)
    }
    return super.onBlockActivated(state, world, pos, player, handIn, hit);
  }

  /**
   * True means cable is blocked. false means its not a cable at all, or its unblocked
   * 
   * @param blockState
   * @param side
   * @return
   */
  public static boolean isCableBlocked(BlockState blockState, Direction side) {
    if (side == null) {
      return false;
    }
    EnumProperty<EnumConnectType> property = CableBase.FACING_TO_PROPERTY_MAP.get(side);
    return blockState.getBlock() instanceof CableBase
        && blockState.hasProperty(property)
        && blockState.get(property).isUnBlocked() == false;
  }
}
