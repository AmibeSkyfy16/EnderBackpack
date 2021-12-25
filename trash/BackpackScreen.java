package ch.skyfy.singlebackpack.client.screen;

import ch.skyfy.singlebackpack.BackpacksManager;
import ch.skyfy.singlebackpack.SingleBackpack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

//renders your screen
@Environment(EnvType.CLIENT)
public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 114 + BackpacksManager.current.rows * 18;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new Identifier(SingleBackpack.MODID, BackpacksManager.current.texturePath));
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        this.drawTexture(matrices, i, j, 0, 0, this.backgroundWidth, BackpacksManager.current.rows * 18 + 17);
        this.drawTexture(matrices, i, j + BackpacksManager.current.rows * 18 + 17, 0, 126, this.backgroundWidth, 96);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}