package evergoodteam.tradepreview.networking;

import evergoodteam.tradepreview.client.TradePreviewClient;
import evergoodteam.tradepreview.utils.EntityUtils;
import evergoodteam.tradepreview.utils.Reference;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOfferList;

import java.util.Optional;

public class TradePreviewNetworking {

    private TradeOfferList offers;
    private Boolean received = false;

    public TradePreviewNetworking() {
    }

    public TradeOfferList getStoredOffers() {
        return offers;
    }

    public Boolean hasReceived() {
        return received;
    }

    public void reset() {
        received = false;
        TradePreviewClient.offersWidget.resetGlintIndex();
    }

    public void registerSyncServerReceiver() {
        ServerPlayNetworking.registerGlobalReceiver(new Identifier(Reference.MOD_ID, "sync"), (server, player, handler, buf, responseSender) -> {
            Optional<VillagerEntity> presence = EntityUtils.getVillager(server.getOverworld(), player);

            presence.ifPresent(villager -> server.execute(() -> {
                PacketByteBuf offerBuf = PacketByteBufs.create();
                villager.getOffers().toPacket(offerBuf);
                responseSender.sendPacket(new Identifier(Reference.MOD_ID, "offers"), offerBuf);
            }));
        });
    }

    public void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(new Identifier(Reference.MOD_ID, "offers"), (client, handler, buf, responseSender) -> {
            offers = TradeOfferList.fromPacket(buf);
            received = true;
        });
    }
}
