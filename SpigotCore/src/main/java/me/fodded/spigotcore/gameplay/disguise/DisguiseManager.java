package me.fodded.spigotcore.gameplay.disguise;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
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
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class DisguiseManager {
    private static DisguiseManager instance;

    private final String defaultSignature = "m4AHOr3btZjX3Rlxkwb5GMf69ZUo60XgFtwpADk92DgX1zz+ZOns+KejAKNpfVZOxRAVpSWwU8+ZNgiEvOdgyTFEW4yVXthQSdBYsKGtpifxOTb8YEXznmq+yVfA1iWZx2P72TbTmbZgG/TyOViMvyqUQsVmaZDCSW/M+ImDTmzrB3KrRW25XY9vaWshNvsaVH8SfrIOm3twtiLc7jRf+sipyxWcbFsw/Kh+6GyCKgID4tgTsydu5nhthm9A5Sa1ZI8LeySSFLzU5VirZeT3LvybHkikART/28sDaTs66N2cjFDNcdtjpWb4y0G9aLdwcWdx8zoYlVXcSWGW5aAFIDLKngtadHxRWnhryydz6YrlrBMflj4s6Qf9meIPI18J6eGWnBC8fhSwsfsJCEq6SKtkeQIHZ9g0sFfqt2YLG3CM6ZOHz2pWedCFUlokqr824XRB/h9FCJIRPIR6kpOK8barZTWwbL9/1lcjwspQ+7+rVHrZD+sgFavQvKyucQqE+IXL7Md5qyC5CYb2WMkXAhjzHp5EUyRq5FiaO6iok93gi6reh5N3ojuvWb1o1cOAwSf4IEaAbc7ej5aCDW5hteZDuVgLvBjPlbSfW9OmA8lbvxxgXR2fUwyfycUVFZUZbtgWzRIjKMOyfgRq5YFY9hhAb3BEAMHeEPqXoSPF5/A=";
    private final String defaultTexture = "ewogICJ0aW1lc3RhbXAiIDogMTU4Nzc0NTY0NTA2NCwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDNiMDZjMzg1MDRmZmMwMjI5Yjk0OTIxNDdjNjlmY2Y1OWZkMmVkNzg4NWY3ODUwMjE1MmY3N2I0ZDUwZGUxIgogICAgfQogIH0KfQ==";

    private DisguiseManager() {
        instance = this;
    }

    public void setDisguise(Player player, String fakeName) {
        GameProfile gameProfile = getProfile(player);

        CompletableFuture<Void> setDisguiseTask = CompletableFuture.runAsync(() -> {
            findAndSetDisguiseSkin(player, fakeName);
            setDisguiseNick(player, fakeName, gameProfile);
        });

        try {
            setDisguiseTask.get();
            updatePlayerVisibility(player);
            GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), generalStats -> generalStats.setDisguisedName(fakeName));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void setDisguise(Player player, String fakeName, String texture, String signature) {
        GameProfile gameProfile = getProfile(player);

        CompletableFuture<Void> setDisguiseTask = CompletableFuture.runAsync(() -> {
            setDisguiseSkin(player, texture, signature);
            setDisguiseNick(player, fakeName, gameProfile);
        });

        try {
            setDisguiseTask.get();
            updatePlayerVisibility(player);
            GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getUniqueId(), generalStats -> generalStats.setDisguisedName(fakeName));
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
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

    private void findAndSetDisguiseSkin(Player player, String skinFromName) {
        String uuid = getUUIDFromName(skinFromName);
        if(uuid.isEmpty()) {
            setDisguiseSkin(player, defaultTexture, defaultSignature);
            return;
        }

        URL mojang;
        InputStreamReader reader;
        try {
            mojang = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
            reader = new InputStreamReader(mojang.openStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JsonObject textureProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        if (textureProperty.get("value").getAsString() == null || textureProperty.get("signature").getAsString() == null) {
            return;
        }

        setDisguiseSkin(player, textureProperty);
    }

    private void setDisguiseSkin(Player player, JsonObject textureProperty) {
        GameProfile gameProfile = getProfile(player);
        if (gameProfile == null) return;

        String skinTexture = textureProperty.get("value").getAsString();
        String skinSignature = textureProperty.get("signature").getAsString();

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures",
                new Property(
                        "textures",
                        skinTexture,
                        skinSignature
                )
        );

        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getPlayer().getUniqueId(), generalStats -> generalStats.setDisguisedSkinTexture(skinTexture));
        GeneralStatsDataManager.getInstance().applyChangeToRedis(player.getPlayer().getUniqueId(), generalStats -> generalStats.setDisguisedSkinSignature(skinSignature));
    }

    private void setDisguiseSkin(Player player, String texture, String signature) {
        GameProfile gameProfile = getProfile(player);
        if (gameProfile == null) return;

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures",
                new Property(
                        "textures",
                        texture,
                        signature
                )
        );
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

    private String getUUIDFromName(String name) {
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

    public String getNameFromUUID(UUID uniqueId) {
        String name = "";
        String request = "https://api.mojang.com/user/profile/" + uniqueId.toString().replace("-", "");
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(new URL(request).openStream()));
            name = (((JsonObject)new JsonParser().parse(in)).get("name")).toString().replaceAll("\"", "");
            name = name.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
            in.close();
        } catch (Exception e) {
            name = "";
        }
        return name;
    }

    private Object getHandle(Player player) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        return player.getClass().getMethod("getHandle").invoke(player);
    }

    private GameProfile getGameProfile(Player player) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        Object handle = getHandle(player);

        return (GameProfile) handle.getClass()
                .getSuperclass()
                .getDeclaredMethod("getProfile")
                .invoke(handle);
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

    public static DisguiseManager getInstance() {
        if(instance == null) {
            return new DisguiseManager();
        }
        return instance;
    }
}
