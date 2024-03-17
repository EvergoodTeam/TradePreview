package evergoodteam.tradepreview.client.gui;

import evergoodteam.chassis.util.handlers.Renderer;
import evergoodteam.tradepreview.TradePreview;
import evergoodteam.tradepreview.client.TradePreviewClient;
import evergoodteam.tradepreview.utils.EntityUtils;
import evergoodteam.tradepreview.utils.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;

import java.util.Optional;

@Environment(EnvType.CLIENT)
public class OverlayRenderer implements Renderer {

    private VillagerEntity previousVillager;
    public boolean canRender = false;

    @Override
    public void onRenderGameOverlayPost(DrawContext drawContext) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (TradePreviewClient.moveDown.wasPressed()) {
            TradePreviewClient.offersWidget.scrollDown();
        } else if (TradePreviewClient.moveUp.wasPressed()) {
            TradePreviewClient.offersWidget.scrollUp();
        }

        if (TradePreviewClient.preview.isPressed()) {
            renderInventoryOverlay(drawContext, client);
        }

        while (TradePreviewClient.editPreview.wasPressed()) {
            if (client.currentScreen != null) return;
            client.setScreen(new EditPositionOverlay(TradePreviewClient.offersWidget));
        }
    }


    public void renderInventoryOverlay(DrawContext drawContext, MinecraftClient client) {
        if (client.currentScreen != null) return;

        Optional<VillagerEntity> presence = EntityUtils.getVillager(client);

        if (presence.isEmpty()) {
            TradePreview.NETWORKING.reset();
            canRender = false;
            return;
        }

        presence.ifPresent(villager -> {
            if (previousVillager == null) previousVillager = villager;

            if (villager.getVillagerData().getProfession().equals(VillagerProfession.NONE) || villager != previousVillager) {
                previousVillager = null;
                TradePreview.NETWORKING.reset();
                canRender = false;
                return;
            }

            canRender = true;

            // Not a server, can get the data directly from the entity
            if (!EntityUtils.isServer(client)) {
                TradePreviewClient.offersWidget.renderOffers(villager, villager.getOffers(), drawContext);
            }

            // Is a server, have to send offers through a packet
            else {
                if (!TradePreview.NETWORKING.hasReceived()) {
                    // Max amount of packets that are sent but not used should be 5/6
                    ClientPlayNetworking.send(new Identifier(Reference.MOD_ID, "sync"), PacketByteBufs.create());
                } else if (TradePreview.NETWORKING.getStoredOffers() != null) {
                    TradePreviewClient.offersWidget.renderOffers(villager, TradePreview.NETWORKING.getStoredOffers(), drawContext);
                }
            }
        });
    }
}

