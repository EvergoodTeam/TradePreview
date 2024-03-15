package evergoodteam.tradepreview.compat;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import evergoodteam.chassis.client.gui.screen.ConfigScreen;
import evergoodteam.tradepreview.utils.Reference;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class TradePreviewModMenu implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> new ConfigScreen(parent, Reference.CONFIGS);
    }
}