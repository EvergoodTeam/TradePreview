package evergoodteam.tradepreview.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import evergoodteam.chassis.client.gui.screen.OverlayScreen;
import evergoodteam.chassis.client.gui.widget.OverlayWidget;
import evergoodteam.chassis.config.option.AbstractOption;
import evergoodteam.tradepreview.TradePreview;
import evergoodteam.tradepreview.client.TradePreviewClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.util.Identifier;

public class EditPositionOverlay extends OverlayScreen {

    protected static final Identifier CROSSHAIR = new Identifier("hud/crosshair");

    public EditPositionOverlay(OverlayWidget overlay) {
        super(overlay);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawCrosshair(context);
    }

    @Override
    protected void init() {
        initFooter();
    }

    public void initFooter() {
        this.addDrawableChild(ButtonWidget.builder(TradePreviewScreenTexts.RESET_P, (buttonWidget) -> {
            TradePreview.POSITION.getOptions().forEach(AbstractOption::reset);
            TradePreviewClient.offersWidget.updatePositionFromStored();
        }).position(this.width / 2 - 50, this.height - 27).size(100, 20).build());
    }

    public void drawCrosshair(DrawContext context) {
        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.ONE_MINUS_DST_COLOR, GlStateManager.DstFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );
        context.drawGuiTexture(CROSSHAIR, (context.getScaledWindowWidth() - 15) / 2, (context.getScaledWindowHeight() - 15) / 2, 15, 15);
        RenderSystem.defaultBlendFunc();
    }
}
