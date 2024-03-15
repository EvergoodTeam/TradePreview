package evergoodteam.tradepreview.utils;

import evergoodteam.chassis.client.gui.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.Optional;

public class EntityUtils {

    /**
     * To be used on the client
     */
    @Environment(EnvType.CLIENT)
    public static Optional<VillagerEntity> getVillager(MinecraftClient client) {
        return getVillager(EntityUtils.getWorld(client), EntityUtils.getCamera(client));
    }

    /**
     * To be used on the server
     */
    public static Optional<VillagerEntity> getVillager(World world, Entity entityIn) {
        Optional<VillagerEntity> result = Optional.empty();
        HitResult trace = RenderUtils.getRayTraceFromEntity(world, entityIn);

        if (trace.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) trace).getEntity();

            if (entity instanceof VillagerEntity villager) {
                result = Optional.of(villager);
            }
        }

        return result;
    }

    @Environment(EnvType.CLIENT)
    public static Entity getCamera(MinecraftClient client) {
        World world = getWorld(client);
        Entity cameraEntity = getCameraEntity(client);

        // SP
        if (cameraEntity == client.player && world instanceof ServerWorld) {
            Entity serverPlayer = world.getPlayerByUuid(client.player.getUuid());

            if (serverPlayer != null) {
                cameraEntity = serverPlayer;
            }
        }

        return cameraEntity;
    }

    @Environment(EnvType.CLIENT)
    public static World getWorld(MinecraftClient client) {
        if (!isServer(client)) {
            return client.getServer().getWorld(client.world.getRegistryKey());
        } else return client.world;
    }

    @Environment(EnvType.CLIENT)
    public static Boolean isServer(MinecraftClient client) {
        return client.getServer() == null;
    }

    @Environment(EnvType.CLIENT)
    public static Entity getCameraEntity(MinecraftClient client) {
        Entity entity = client.getCameraEntity();

        if (entity == null) {
            entity = client.player;
        }

        return entity;
    }
}
