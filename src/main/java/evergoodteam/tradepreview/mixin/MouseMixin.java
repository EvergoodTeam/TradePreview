package evergoodteam.tradepreview.mixin;

import evergoodteam.tradepreview.client.TradePreviewClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"), cancellable = true)
    private void injectOnScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        if (vertical != 0 && TradePreviewClient.preview.isPressed() && TradePreviewClient.RENDERER.canRender) {
            TradePreviewClient.offersWidget.scroll(vertical);
            ci.cancel();
        }
    }
}
