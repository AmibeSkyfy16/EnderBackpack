package ch.skyfy.enderbackpack.client.screen;

import ch.skyfy.enderbackpack.EnderBackpack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackpackScreen extends HandledScreen<BackpackScreenHandler> {

    private final int row;

    public BackpackScreen(BackpackScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        row = handler.row;
        backgroundHeight = 114 + row * 18;
        playerInventoryTitleY = backgroundHeight - 94;
        EnderBackpack.LOGGER.info("[BackpackScreen.class] CLIENT SIDE, row is : " + handler.row);
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
//        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new Identifier(EnderBackpack.MODID, "textures/gui/container_54.png"));
        int i = (width - backgroundWidth) / 2;
        int j = (height - backgroundHeight) / 2;
        drawTexture(matrices, i, j, 0, 0, backgroundWidth, row * 18 + 17);
        drawTexture(matrices, i, j + row * 18 + 17, 0, 126, backgroundWidth, 96);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }
}
