package evergoodteam.tradepreview.client;

import evergoodteam.chassis.client.gui.widget.WidgetBase;
import evergoodteam.chassis.util.handlers.RenderEventHandler;
import evergoodteam.tradepreview.TradePreview;
import evergoodteam.tradepreview.client.gui.OffersWidget;
import evergoodteam.tradepreview.client.gui.OverlayRenderer;
import evergoodteam.tradepreview.utils.Reference;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(EnvType.CLIENT)
public class TradePreviewClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reference.CMI + "/Client");
    public static final OverlayRenderer RENDERER = new OverlayRenderer();
    public static KeyBinding editPreview;
    public static KeyBinding preview;
    public static KeyBinding moveDown;
    public static KeyBinding moveUp;
    public static OffersWidget offersWidget = new OffersWidget(new WidgetBase.WidgetUpdateCallback() {

        @Override
        public void onPositionUpdate(int x, int y) {
            TradePreview.X_COORD.setValue(x);
            TradePreview.Y_COORD.setValue(y);
        }

        @Override
        public void onSave() {
            Reference.CONFIGS.getHandler().writeUserOptions();
        }

    }, TradePreview.X_COORD.getValue(), TradePreview.Y_COORD.getValue(), TradePreview.BACKGROUND.getIntColorValue(), TradePreview.OUTLINE.getIntColorValue(), TradePreview.GLINT_BACK.getIntColorValue(), TradePreview.GLINT_OUT.getIntColorValue());

    @Override
    public void onInitializeClient() {
        LOGGER.info("Client initialization");

        TradePreview.NETWORKING.registerClientReceiver();
        RenderEventHandler.getInstance().registerOverlayRenderer(RENDERER);

        editPreview = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.previewedit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.tradepreview.utility"
        ));

        preview = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.preview",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "category.tradepreview.utility"
        ));

        moveDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.down",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN,
                "category.tradepreview.utility"
        ));

        moveUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.up",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UP,
                "category.tradepreview.utility"
        ));
    }
}
