package evergoodteam.tradepreview.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import evergoodteam.chassis.client.gui.widget.OverlayWidget;
import evergoodteam.tradepreview.TradePreview;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;

import java.util.List;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class OffersWidget extends OverlayWidget {

    private static final Identifier TEXTURE = new Identifier("textures/gui/container/villager2.png");
    private int itemSize = 16;
    private int arrowWidth = 10;
    private int space = 8;
    private int interline = 4;
    private int lineWidth = space + itemSize + space + itemSize + space + arrowWidth + space + itemSize + space;
    private int glintIndex = 0;
    public int glintBack;
    public int glintOut;

    public OffersWidget(WidgetUpdateCallback callback, int x, int y, int color, int outlineColor, int glintBack, int glintOut) {
        super(callback, x, y, 0, 0, color, outlineColor);
        this.width = lineWidth;
        this.glintBack = glintBack;
        this.glintOut = glintOut;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        renderExampleOffers(context);
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        //renderHeader(context);
        renderContainer(context);
    }

    public void renderExampleOffers(DrawContext context) {
        //context.drawCenteredTextWithShadow(client.textRenderer, Text.literal("Profession"), x + width / 2, y + 7, 16777215);
        TradeOffer offer = new TradeOffer(Items.ANDESITE.getDefaultStack(), Items.BROWN_BANNER.getDefaultStack(), Items.CYAN_CANDLE.getDefaultStack(), 10, 10, 1f);
        this.height = 8 * itemSize + (8 + 1) * interline;

        for (int i = 0; i < 8; i++) {
            int newY = y + (itemSize + interline) * i + interline;

            renderOfferLine(offer, context, newY, i, List.of());
        }
    }

    public void renderOffers(VillagerEntity villager, TradeOfferList offerList, DrawContext context) {
        if (offerList.isEmpty()) return;

        //context.drawCenteredTextWithShadow(client.textRenderer, Text.literal(StringUtils.capitalize(villager.getVillagerData().getProfession().toString())), x + width / 2, y + 7, 16777215);

        int offersHeight = offerList.size() * itemSize + (offerList.size() + 1) * interline;
        this.width = lineWidth;
        this.height = offersHeight;

        super.render(context, 0, 0, 0);

        List<Integer> glints = offerList.stream().filter(
                        offer -> offer.getSellItem().hasEnchantments())
                .map(offerList::indexOf).collect(Collectors.toList());

        if (glintIndex >= glints.size()) glintIndex = 0;
        else if (glintIndex <= -1) glintIndex = glints.size() - 1;

        for (int i = 0; i < offerList.size(); i++) {
            TradeOffer offer = offerList.get(i);
            int newY = y + (itemSize + interline) * i + interline;
            renderOfferLine(offer, context, newY, i, glints);
        }
    }

    public void renderOfferLine(TradeOffer offer, DrawContext context, int newY, int index, List<Integer> glints) {
        OffersWidget.drawItemWithCount(client, context, offer.getAdjustedFirstBuyItem(), x + space, newY);
        OffersWidget.drawItemWithCount(client, context, offer.getSecondBuyItem(), x + space + itemSize + space, newY);
        renderArrow(context, offer, newY);
        int sellItem = x + space + itemSize + space + itemSize + space + arrowWidth + space;
        OffersWidget.drawItemWithCount(client, context, offer.getSellItem(), sellItem, newY);

        if (offer.getSellItem().isOf(Registries.ITEM.get(new Identifier("minecraft", "enchanted_book")))) {
            List<Text> list = offer.getSellItem().getTooltip(client.player, client.options.advancedItemTooltips ? TooltipContext.Default.ADVANCED : TooltipContext.Default.BASIC);
            list.remove(0);
            context.drawTooltip(client.textRenderer, list, x + lineWidth, newY + 16);
        } else if (offer.getSellItem().hasEnchantments()) {
            if (glints.get(glintIndex) == index) {
                drawRectWithOutline(context, sellItem - 2, newY - 2, 20, 20, glintBack, glintOut);
                context.drawItemTooltip(client.textRenderer, offer.getSellItem(), x + lineWidth - 4, newY);
            }
        }
    }

    public void renderArrow(DrawContext context, TradeOffer tradeOffer, int y) {
        RenderSystem.enableBlend();
        if (tradeOffer.isDisabled()) {
            context.drawTexture(TEXTURE, x + space + itemSize + space + itemSize + space, y + 3, 0, 25.0F, 171.0F, 10, 9, 512, 256);
        } else {
            context.drawTexture(TEXTURE, x + space + itemSize + space + itemSize + space, y + 3, 0, 15.0F, 171.0F, 10, 9, 512, 256);
        }
    }

    public void renderContainer(DrawContext context) {
        drawRectWithOutline(context, x, y, width, height, backgroundColor, outlineColor);
    }

    public void updatePositionFromStored() {
        this.x = TradePreview.X_COORD.getValue();
        this.y = TradePreview.Y_COORD.getValue();
    }

    public void scroll(double vertical) {
        if (vertical > 0) scrollUp();
        else scrollDown();
    }

    public void scrollDown() {
        glintIndex++;
    }

    public void scrollUp() {
        glintIndex--;
    }

    public void resetGlintIndex() {
        glintIndex = 0;
    }
}
