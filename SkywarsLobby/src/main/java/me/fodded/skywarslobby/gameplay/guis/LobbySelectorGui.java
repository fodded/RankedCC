package me.fodded.skywarslobby.gameplay.guis;

import me.fodded.skywarslobby.managers.SkywarsLobbyPlayer;
import me.fodded.spigotcore.SpigotCore;
import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.servers.SpigotServerManager;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;

public class LobbySelectorGui extends AbstractGui {

    public LobbySelectorGui(Player player) {
        super();

        int index = 1;
        for(Map.Entry entry : SpigotServerManager.getInstance().getPlayersServerMap("Skywars-Lobby").entrySet()) {
            int lobbyIndex = (int) entry.getKey();
            int playersOnline = (Integer) entry.getValue();

            String serverName = "Skywars-Lobby-" + lobbyIndex;
            boolean isThisServer = serverName.equalsIgnoreCase(SpigotCore.getInstance().getServerName());

            ItemStack itemStack = ItemUtils.getItemStack(
                    (isThisServer ? Material.RECORD_11 : Material.GOLD_RECORD),
                    (isThisServer ? "&c" : "&f") + "Skywars Lobby &6#" + lobbyIndex,
                    Arrays.asList(
                            "&fPlayers: " + playersOnline + "/" + Bukkit.getMaxPlayers(),
                            isThisServer ? "&cAlready connected" : "&6&lClick to connect!"),
                    isThisServer,
                    index
            );

            setItem(itemStack, index++-1);
        }

        player.openInventory(getInventory());
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        if(event.getCurrentItem() == null) {
            return;
        }

        if(event.getCurrentItem().getItemMeta().getDisplayName() == null) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String serverName = "Skywars-Lobby-" + itemName.substring("&fSkywars Lobby &6#".length());

        event.setCancelled(true);
        if(serverName.equalsIgnoreCase(SpigotCore.getInstance().getServerName())) {
            return;
        }

        SkywarsLobbyPlayer lobbyPlayer = SkywarsLobbyPlayer.getLobbyPlayer(player.getUniqueId());
        lobbyPlayer.sendPlayerToLobby(serverName);
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Lobby Selector";
    }
}
