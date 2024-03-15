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

import static evergoodteam.tradepreview.utils.Reference.CONFIGS;

public class TradePreview implements ModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger(Reference.MOD_ID);
    public static final TradePreviewNetworking NETWORKING = new TradePreviewNetworking();
    public static final IntegerSliderOption X_COORD = new IntegerSliderOption("xCoordinate", 0, 3840, 0);
    public static final IntegerSliderOption Y_COORD = new IntegerSliderOption("yCoordinate", 0, 2160, 0);
    public static ColorOption BACKGROUND;
    public static ColorOption OUTLINE;

    @Override
    public void onInitialize() {

        LOGGER.info("Initializing TradePreview");

        X_COORD.addUpdateCallback(new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onUpdate(Integer newValue) {
                TradePreviewClient.offersWidget.x = newValue;
            }
        });

        Y_COORD.addUpdateCallback(new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onUpdate(Integer newValue) {
                TradePreviewClient.offersWidget.y = newValue;
            }
        });

        BACKGROUND = new ColorOption(new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onAnyUpdate(String newValue) {
                TradePreviewClient.offersWidget.backgroundColor = ColorUtils.ARGB.getIntFromHexARGB(newValue);
            }
        }, "backgroundColor", "6D272B45");

        OUTLINE = new ColorOption(new AbstractOption.OptionUpdateCallback<>() {
            @Override
            public void onAnyUpdate(String newValue) {
                TradePreviewClient.offersWidget.outlineColor = ColorUtils.ARGB.getIntFromHexARGB(newValue);
            }
        }, "outlineColor", "6D272B45");

        NETWORKING.registerSyncServerReceiver();

        initConfig();
    }

    public void initConfig() {
        CONFIGS.getNetworkHandler().registerServerConnectionListener();
        CONFIGS.getNetworkHandler().registerHandshakeReceiver();

        CONFIGS.setDisplayTitle(GradientText.copyOf(Text.literal("Trade Preview"))
                .setColorPoints(50, "937fb8", "dcccfa", "6942E2", "937fb8")
                .setScrollDelay(4));

        CategoryOption GENERAL = new CategoryOption(CONFIGS, "clientOptions", "Everything that is related to the client").getBuilder()
                .setDisplayName(TradePreviewScreenTexts.CAT).build();

        CONFIGS.addCategory(GENERAL
                        .addIntegerOption(X_COORD.getBuilder()
                                .setDisplayName(Text.translatable("config.tradepreview.x"))
                                .setComment("The x coordinate for the trade preview overlay. Reads any value from the config file, while the slider is capped at 3840.")
                                .setTooltip(TradePreviewScreenTexts.X_TOOLTIP).build())
                        .addIntegerOption(Y_COORD.getBuilder()
                                .setDisplayName(Text.translatable("config.tradepreview.y"))
                                .setComment("The y coordinate for the trade preview overlay. Reads any value from the config file, while the slider is capped at 2160.")
                                .setTooltip(TradePreviewScreenTexts.Y_TOOLTIP).build())
                        .addStringSetOption(BACKGROUND.getBuilder()
                                .setDisplayName(Text.literal("Background"))
                                .setComment("Colour used for the overlay's background. Uses the AARRGGBB format.").build())
                        .addStringSetOption(OUTLINE.getBuilder()
                                .setDisplayName(Text.literal("Outline"))
                                .setComment("Colour used for the overlay's outline. Uses the AARRGGBB format.").build())
                )
                .registerProperties();
    }
}
