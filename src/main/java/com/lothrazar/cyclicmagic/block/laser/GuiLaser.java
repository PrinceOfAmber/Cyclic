/*******************************************************************************
 * The MIT License (MIT)
 * 
 * Copyright (C) 2014-2018 Sam Bassett (aka Lothrazar)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.lothrazar.cyclicmagic.block.laser;

import com.lothrazar.cyclicmagic.core.gui.GuiBaseContainer;
import com.lothrazar.cyclicmagic.core.util.Const;
import com.lothrazar.cyclicmagic.core.util.Const.ScreenSize;
import com.lothrazar.cyclicmagic.core.util.UtilChat;
import com.lothrazar.cyclicmagic.gui.GuiSliderInteger;
import com.lothrazar.cyclicmagic.gui.button.ButtonTileEntityField;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiLaser extends GuiBaseContainer {

  private ButtonTileEntityField btnPulsing;

  public GuiLaser(InventoryPlayer inventoryPlayer, TileEntityLaser te) {
    super(new ContainerLaser(inventoryPlayer, te), te);
    this.setScreenSize(ScreenSize.LARGE);
    this.fieldRedstoneBtn = TileEntityLaser.Fields.REDSTONE.ordinal();
  }

  @Override
  public void initGui() {
    super.initGui();
    int id = 0, x = guiLeft + 48, y = guiTop + 20, width = 120, h = 12;
    GuiSliderInteger sliderX = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 255, TileEntityLaser.Fields.R.ordinal());
    sliderX.setTooltip("screen.red");
    this.addButton(sliderX);
    y += h + 4;
    sliderX = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 255, TileEntityLaser.Fields.G.ordinal());
    sliderX.setTooltip("screen.green");
    this.addButton(sliderX);
    y += h + 4;
    sliderX = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 255, TileEntityLaser.Fields.B.ordinal());
    sliderX.setTooltip("screen.blue");
    this.addButton(sliderX);
    y += h + 4;
    sliderX = new GuiSliderInteger(tile, id++, x, y, width, h, 0, 100, TileEntityLaser.Fields.ALPHA.ordinal());
    sliderX.setTooltip("screen.alpha");
    this.addButton(sliderX);
    //
    y += 24;
    btnPulsing = new ButtonTileEntityField(id++, x, y, this.tile.getPos(), TileEntityLaser.Fields.PULSE.ordinal());
    btnPulsing.width = 64;
    btnPulsing.setTooltip("button.pulsing.tooltip");
    this.addButton(btnPulsing);
  }

  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    btnPulsing.displayString = UtilChat.lang("button.pulsing.name" + this.tile.getField(TileEntityLaser.Fields.PULSE.ordinal()));
    int u = 0, v = 0, x, y;
    this.mc.getTextureManager().bindTexture(Const.Res.SLOT_GPS);
    for (int i = 0; i < tile.getSizeInventory(); i++) {
      x = this.guiLeft + 7;
      y = this.guiTop + 42 + i * Const.SQ;
      Gui.drawModalRectWithCustomSizedTexture(
          x, y,
          u, v, Const.SQ, Const.SQ, Const.SQ, Const.SQ);
    }
  }
}
