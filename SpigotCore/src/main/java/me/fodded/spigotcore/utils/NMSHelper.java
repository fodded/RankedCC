// @ Jordan Osterberg

package me.fodded.spigotcore.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import lombok.Getter;
import me.fodded.core.managers.stats.impl.profile.GeneralStatsDataManager;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutRespawn;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Optional;

public class NMSHelper {

    @Getter
    private static final NMSHelper instance = new NMSHelper();

    private NMSHelper() {

    }

    public Object getHandle(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return player.getClass().getMethod("getHandle").invoke(player);
    }

    public GameProfile getGameProfile(Player player) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object handle = getHandle(player);

        return (GameProfile) handle.getClass()
                .getSuperclass()
                .getDeclaredMethod("getProfile")
                .invoke(handle);
    }

    public Property getTexturesProperty(GameProfile profile) {
        Optional<Property> texturesProperty = profile.getProperties().get("textures").stream().findFirst();
        return texturesProperty.orElse(null);
    }

    public void setDisguise(Player player, String fakeName) {
        GameProfile gameProfile = getProfile(player);

        findAndSetDisguiseSkin(player, fakeName);
        setDisguiseNick(player, fakeName, gameProfile);
        updatePlayerVisibility(player);

        GeneralStatsDataManager.getInstance().applyChangeToRedis(
                player.getUniqueId(),
                generalStats -> generalStats.setDisguisedName("")
        );
    }

    public void setDisguise(Player player, String fakeName, String texture, String signature) {
        GameProfile gameProfile = getProfile(player);

        findAndSetDisguiseSkin(player, fakeName);
        setDisguiseNick(player, fakeName, gameProfile);
        updatePlayerVisibility(player);

        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getPlayer().getUniqueId(), generalStats -> generalStats.setDisguisedName(fakeName));
        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getPlayer().getUniqueId(), generalStats -> generalStats.setDisguisedSkinSignature(signature));
        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getPlayer().getUniqueId(), generalStats -> generalStats.setDisguisedSkinTexture(texture));
    }

    private void setDisguiseNick(Player player, String fakeName, GameProfile gameProfile) {
        try {
            Field field = gameProfile.getClass().getDeclaredField("name");
            field.setAccessible(true);
            field.set(gameProfile, fakeName);
            field.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        if(fakeName != null) {
            player.setDisplayName(fakeName);
        }
    }

    private void updatePlayerVisibility(Player player) {
        Location location = player.getLocation();
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                entityPlayer)
        );

        World world = entityPlayer.world;
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(
                entityPlayer.dimension, world.getDifficulty(),
                world.getWorldData().getType(), entityPlayer.playerInteractManager.getGameMode())
        );

        player.teleport(location);
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(
                PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                entityPlayer)
        );

        player.updateInventory();
        Bukkit.getOnlinePlayers().forEach(eachPlayer -> {
            eachPlayer.hidePlayer(player);
            eachPlayer.showPlayer(player);
        });
    }

    public String getUUIDFromName(String name) {
        String uuid = "";
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openStream()));
            uuid = (((JsonObject)new JsonParser().parse(in)).get("id")).toString().replaceAll("\"", "");
            uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            in.close();
        } catch (Exception e) {
            uuid = "";
        }
        return uuid;
    }

    public void findAndSetDisguiseSkin(Player player, String skinFromName) {
        URL mojang;
        InputStreamReader reader;
        try {
            mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + getUUIDFromName(skinFromName) + "?unsigned=false");
            reader = new InputStreamReader(mojang.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        if (textureProperty.get("value").getAsString() == null || textureProperty.get("signature").getAsString() == null) {
            return;
        }

        setSkin(player, textureProperty);
    }

    private void setSkin(Player player, JsonObject textureProperty) {
        GameProfile gameProfile = getProfile(player);
        if (gameProfile == null) return;

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures",
                new Property(
                        "textures",
                        textureProperty.get("value").getAsString(),
                        textureProperty.get("signature").getAsString()
                )
        );
    }

    private GameProfile getProfile(Player player) {
        GameProfile gameProfile;

        try {
            gameProfile = getGameProfile(player);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return gameProfile;
    }
}
