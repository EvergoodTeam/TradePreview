package evergoodteam.tradepreview.client;

import evergoodteam.chassis.client.gui.widget.WidgetBase;
import evergoodteam.chassis.config.option.AbstractOption;
import evergoodteam.chassis.util.gui.ColorUtils;
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

import java.util.function.Consumer;

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

        TradePreview.X_COORD.addUpdateCallback(getIntCallback(newVal -> TradePreviewClient.offersWidget.x = newVal));
        TradePreview.Y_COORD.addUpdateCallback(getIntCallback(newVal -> TradePreviewClient.offersWidget.y = newVal));
        TradePreview.BACKGROUND.addUpdateCallback(getStringCallback(newVal -> TradePreviewClient.offersWidget.backgroundColor = newVal));
        TradePreview.OUTLINE.addUpdateCallback(getStringCallback(newVal -> TradePreviewClient.offersWidget.outlineColor = newVal));
        TradePreview.GLINT_BACK.addUpdateCallback(getStringCallback(newVal -> TradePreviewClient.offersWidget.glintBack = newVal));
        TradePreview.GLINT_OUT.addUpdateCallback(getStringCallback(newVal -> TradePreviewClient.offersWidget.glintOut = newVal));

        TradePreview.NETWORKING.registerClientReceiver();
        RenderEventHandler.getInstance().registerOverlayRenderer(RENDERER);

        editPreview = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.previewedit",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "key.tradepreview.category"
        ));

        preview = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.preview",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                "key.tradepreview.category"
        ));

        moveDown = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.down",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_DOWN,
                "key.tradepreview.category"
        ));

        moveUp = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.tradepreview.up",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UP,
                "key.tradepreview.category"
        ));
    }

    private static AbstractOption.OptionUpdateCallback<Integer> getIntCallback(Consumer<Integer> consumer) {
        return new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onAnyUpdate(Integer newValue) {
                consumer.accept(newValue);
            }
        };
    }

    private static AbstractOption.OptionUpdateCallback<String> getStringCallback(Consumer<Integer> consumer) {
        return new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onAnyUpdate(String newValue) {
                consumer.accept(ColorUtils.ARGB.getIntFromHexARGB(newValue));
            }
        };
    }
}
