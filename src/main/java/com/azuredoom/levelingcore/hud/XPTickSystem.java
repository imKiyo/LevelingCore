package com.azuredoom.levelingcore.hud;

import com.buuz135.mhud.MultipleHUD;
import com.hypixel.hytale.common.plugin.PluginIdentifier;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.plugin.PluginManager;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.logging.Level;

import com.azuredoom.levelingcore.LevelingCore;
import com.azuredoom.levelingcore.api.LevelingCoreApi;

public class XPTickSystem extends EntityTickingSystem<EntityStore> {

    public XPTickSystem() {}

    @Override
    public void tick(
        float var1,
        int index,
        @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
        @NonNullDecl Store<EntityStore> store,
        @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        final Holder<EntityStore> holder = EntityUtils.toHolder(index, archetypeChunk);
        final Player player = holder.getComponent(Player.getComponentType());
        final PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        if (player == null || playerRef == null) {
            return;
        }
        LevelingCoreApi.getLevelServiceIfPresent().ifPresent(levelService1 -> {
            var xpHud = new XPBarHud(playerRef, levelService1);
            if (PluginManager.get().getPlugin(new PluginIdentifier("Buuz135", "MultipleHUD")) != null) {
                MultipleHUD.getInstance().setCustomHud(player, playerRef, "levelingcore_xpbar", xpHud);
            } else {
                player.sendMessage(
                    Message.raw(
                        "LevelingCore Error: MultipleHUD not found, XP HUD will not work correctly with other mods adding custom UI"
                    )
                );
                LevelingCore.LOGGER.at(Level.WARNING)
                    .log("MultipleHUD not found, XP HUD will not work correctly with other mods adding custom UI");
                player.getHudManager().setCustomHud(playerRef, xpHud);
            }
        });
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
