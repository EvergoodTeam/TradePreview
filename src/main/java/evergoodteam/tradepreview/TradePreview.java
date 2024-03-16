package evergoodteam.tradepreview;

import evergoodteam.chassis.client.gui.text.GradientText;
import evergoodteam.chassis.config.option.AbstractOption;
import evergoodteam.chassis.config.option.CategoryOption;
import evergoodteam.chassis.config.option.ColorOption;
import evergoodteam.chassis.config.option.IntegerSliderOption;
import evergoodteam.chassis.util.gui.ColorUtils;
import evergoodteam.tradepreview.client.TradePreviewClient;
import evergoodteam.tradepreview.client.gui.TradePreviewScreenTexts;
import evergoodteam.tradepreview.networking.TradePreviewNetworking;
import evergoodteam.tradepreview.utils.Reference;
import net.fabricmc.api.ModInitializer;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

import static evergoodteam.tradepreview.utils.Reference.CONFIGS;

public class TradePreview implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);
    public static final TradePreviewNetworking NETWORKING = new TradePreviewNetworking();
    public static final CategoryOption POSITION = new CategoryOption(CONFIGS, "position", "Overlay's coordinates on the screen");
    public static final CategoryOption COLORS = new CategoryOption(CONFIGS, "colors", "Overlay's colours");
    public static final IntegerSliderOption X_COORD = new IntegerSliderOption("xCoordinate", 0, 3840, 0).getBuilder()
            .addUpdateCallback(getIntCallback(newVal -> TradePreviewClient.offersWidget.x = newVal)).build();
    public static final IntegerSliderOption Y_COORD = new IntegerSliderOption("yCoordinate", 0, 2160, 0).getBuilder()
            .addUpdateCallback(getIntCallback(newVal -> TradePreviewClient.offersWidget.y = newVal)).build();
    public static final ColorOption BACKGROUND = new ColorOption(getStringCallback(newVal -> TradePreviewClient.offersWidget.backgroundColor = newVal), "backgroundColor", "6D272B45");
    public static final ColorOption OUTLINE = new ColorOption(getStringCallback(newVal -> TradePreviewClient.offersWidget.outlineColor = newVal), "outlineColor", "6D272B45");
    public static final ColorOption GLINT_BACK = new ColorOption(getStringCallback(newVal -> TradePreviewClient.offersWidget.glintBack = newVal), "glintBack", "6D55FFFF");
    public static final ColorOption GLINT_OUT = new ColorOption(getStringCallback(newVal -> TradePreviewClient.offersWidget.glintOut = newVal), "glintOut", "6D55FFFF");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing TradePreview");

        initConfig();

        NETWORKING.registerSyncServerReceiver();
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

    public void initConfig() {
        CONFIGS.getNetworkHandler().registerServerConnectionListener();
        CONFIGS.getNetworkHandler().registerHandshakeReceiver();

        CONFIGS.setDisplayTitle(GradientText.copyOf(Text.literal("Trade Preview"))
                .setColorPoints(50, "937fb8", "dcccfa", "6942e2", "937fb8")
                .setScrollDelay(4));

        CONFIGS.addCategory(POSITION.getBuilder().setDisplayName(GradientText.copyOf(TradePreviewScreenTexts.POSITION).setColorPoints("dcccfa")).build()
                        .addIntegerOption(X_COORD.getBuilder()
                                .setDisplayName(Text.translatable("config.tradepreview.x"))
                                .setComment("The x coordinate for the trade preview overlay. Reads any value from the config file, while the slider is capped at 3840.")
                                .setTooltip(TradePreviewScreenTexts.X_TOOLTIP).build())
                        .addIntegerOption(Y_COORD.getBuilder()
                                .setDisplayName(Text.translatable("config.tradepreview.y"))
                                .setComment("The y coordinate for the trade preview overlay. Reads any value from the config file, while the slider is capped at 2160.")
                                .setTooltip(TradePreviewScreenTexts.Y_TOOLTIP).build())
                ).addCategory(COLORS.getBuilder().setDisplayName(GradientText.copyOf(TradePreviewScreenTexts.COLORS).setColorPoints("dcccfa")).build()
                        .addStringSetOption(BACKGROUND.getBuilder()
                                .setDisplayName(Text.literal("Background"))
                                .setComment("Colour used for the overlay's background. Uses the AARRGGBB format.").build())
                        .addStringSetOption(OUTLINE.getBuilder()
                                .setDisplayName(Text.literal("Outline"))
                                .setComment("Colour used for the overlay's outline. Uses the AARRGGBB format.").build())
                        .addStringSetOption(GLINT_BACK.getBuilder()
                                .setDisplayName(Text.literal("Selection background"))
                                .setComment("Background colour for the selection of enchanted items. Uses the AARRGGBB format.").build())
                        .addStringSetOption(GLINT_OUT.getBuilder()
                                .setDisplayName(Text.literal("Selection outline"))
                                .setComment("Outline colour for the selection of enchanted items. Uses the AARRGGBB format.").build())
                )
                .registerProperties();
    }
}
