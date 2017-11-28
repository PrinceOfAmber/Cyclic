package com.lothrazar.cyclicmagic.component.wireless;
import com.lothrazar.cyclicmagic.block.base.TileEntityBaseMachineInvo;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEntityWirelessRec extends TileEntityBaseMachineInvo implements ITickable {
  public TileEntityWirelessRec() {
    super(0);
  }
  @Override
  public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate) {
    //oldState.getBlock() instanceof BlockRedstoneClock &&
    return !(newSate.getBlock() instanceof BlockRedstoneWireless);// : oldState != newSate;
  }
  @Override
  public void update() {
    //    targetPos = new BlockPos(-269, 64, 343);
    //    IBlockState target = world.getBlockState(targetPos);
    //    System.out.println(target.getBlock());
    //    if (target.getBlock() instanceof BlockRedstoneWireless) {
    //      boolean targetPowered = target.getValue(BlockRedstoneWireless.POWERED);
    //      //update target based on my state
    //      boolean isPowered = world.getBlockState(pos).getValue(BlockRedstoneWireless.POWERED);
    //      if (targetPowered != isPowered) {
    //        world.setBlockState(targetPos, target.withProperty(BlockRedstoneWireless.POWERED, isPowered));
    //      }
    //    } 
  }
  //  @Override
  //  public int getField(int id) {
  //    return 0;
  //  }
  //  @Override
  //  public void setField(int id, int value) {}
  @Override
  public NBTTagCompound writeToNBT(NBTTagCompound compound) {
    return super.writeToNBT(compound);
  }
  @Override
  public void readFromNBT(NBTTagCompound compound) {
    super.readFromNBT(compound);
  }
}
