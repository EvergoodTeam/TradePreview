package evergoodteam.tradepreview.client.gui;

import evergoodteam.chassis.client.gui.screen.OverlayScreen;
import evergoodteam.chassis.client.gui.widget.OverlayWidget;
import evergoodteam.chassis.config.option.AbstractOption;
import evergoodteam.tradepreview.client.TradePreviewClient;
import evergoodteam.tradepreview.utils.Reference;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
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
        this.addDrawableChild(ButtonWidget.builder(Text.literal("Reset Coordinates"), (buttonWidget) -> {
            Reference.CONFIGS.getOptionStorage().getOptions().forEach(AbstractOption::reset);
            TradePreviewClient.offersWidget.updateCoordFromOptions();

            //LOGGER.debug("Reset config options for \"{}\"", Reference.CONFIGS.namespace);
        }).position(this.width / 2 - 50, this.height - 27).size(100, 20).build());
    }

    public void drawCrosshair(DrawContext context){
        context.drawGuiTexture(CROSSHAIR, (context.getScaledWindowWidth() - 15) / 2, (context.getScaledWindowHeight() - 15) / 2, 15, 15);
    }
}
