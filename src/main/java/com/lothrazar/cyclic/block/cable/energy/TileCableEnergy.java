package com.lothrazar.cyclic.block.cable.energy;

import com.google.common.collect.Maps;
import com.lothrazar.cyclic.base.TileEntityBase;
import com.lothrazar.cyclic.block.cable.CableBase;
import com.lothrazar.cyclic.block.cable.EnumConnectType;
import com.lothrazar.cyclic.capability.CustomEnergyStorage;
import com.lothrazar.cyclic.registry.TileRegistry;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileCableEnergy extends TileEntityBase implements ITickableTileEntity {

  private static final int MAX = 32000;
  CustomEnergyStorage energy = new CustomEnergyStorage(MAX, MAX);
  private LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> energy);
  private Map<Direction, Integer> mapIncomingEnergy = Maps.newHashMap();

  public TileCableEnergy() {
    super(TileRegistry.energy_pipeTile);
    for (Direction f : Direction.values()) {
      mapIncomingEnergy.put(f, 0);
    }
  }

  @Override
  public void tick() {
    this.syncEnergy();
    this.tickDownIncomingPowerFaces();
    this.tickCableFlow();
    //extract mode conditionally
    for (Direction side : Direction.values()) {
      EnumConnectType connection = this.getBlockState().get(CableBase.FACING_TO_PROPERTY_MAP.get(side));
      if (connection.isExtraction()) {
        tryExtract(side);
      }
    }
  }

  @SuppressWarnings("unused")
  private void tryExtract(Direction extractSide) {
    if (extractSide == null) {
      return;
    }
    BlockPos posTarget = this.pos.offset(extractSide);
    TileEntity tile = world.getTileEntity(posTarget);
    if (tile != null) {
      IEnergyStorage itemHandlerFrom = tile.getCapability(CapabilityEnergy.ENERGY, extractSide.getOpposite()).orElse(null);
      if (itemHandlerFrom != null) {
        //ok go
        //
        int extractSim = itemHandlerFrom.extractEnergy(MAX, true);
        if (extractSim > 0 && energy.receiveEnergy(extractSim, true) > 0) {
          //actually extract energy for real, whatever it accepted 
          int actuallyEx = itemHandlerFrom.extractEnergy(energy.receiveEnergy(extractSim, false), false);
          //          ModCyclic.LOGGER.info("TEX from to me" + actuallyEx);
        }
      }
    }
  }

  private void tickCableFlow() {
    List<Integer> rawList = IntStream.rangeClosed(0, 5).boxed().collect(Collectors.toList());
    Collections.shuffle(rawList);
    for (Integer i : rawList) {
      Direction outgoingSide = Direction.values()[i];
      EnumConnectType connection = this.getBlockState().get(CableBase.FACING_TO_PROPERTY_MAP.get(outgoingSide));
      if (connection.isExtraction() || connection.isBlocked()) {
        continue;
      }
      if (this.isEnergyIncomingFromFace(outgoingSide) == false) {
        moveEnergy(outgoingSide, MAX);
      }
    }
  }

  public void tickDownIncomingPowerFaces() {
    for (Direction f : Direction.values()) {
      if (mapIncomingEnergy.get(f) > 0) {
        mapIncomingEnergy.put(f, mapIncomingEnergy.get(f) - 1);
      }
    }
  }

  @Override
  public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == CapabilityEnergy.ENERGY) {
      if (!CableBase.isCableBlocked(this.getBlockState(), side)) {
        return energyCap.cast();
      }
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void read(BlockState bs, CompoundNBT tag) {
    for (Direction f : Direction.values()) {
      mapIncomingEnergy.put(f, tag.getInt(f.getString() + "_incenergy"));
    }
    energy.deserializeNBT(tag.getCompound(NBTENERGY));
    super.read(bs, tag);
  }

  @Override
  public CompoundNBT write(CompoundNBT tag) {
    for (Direction f : Direction.values()) {
      tag.putInt(f.getString() + "_incenergy", mapIncomingEnergy.get(f));
    }
    tag.put(NBTENERGY, energy.serializeNBT());
    return super.write(tag);
  }

  private static final int TIMER_SIDE_INPUT = 15;

  private boolean isEnergyIncomingFromFace(Direction face) {
    return mapIncomingEnergy.get(face) > 0;
  }

  public void updateIncomingEnergyFace(Direction inputFrom) {
    mapIncomingEnergy.put(inputFrom, TIMER_SIDE_INPUT);
  }

  @Override
  public void setField(int field, int value) {}

  @Override
  public int getField(int field) {
    return 0;
  }
}
