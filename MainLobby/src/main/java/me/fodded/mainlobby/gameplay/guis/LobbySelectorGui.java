package me.fodded.mainlobby.gameplay.guis;

import me.fodded.mainlobby.managers.MainLobbyPlayer;
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
        for(Map.Entry entry : SpigotServerManager.getInstance().getPlayersServerMap("Main-Lobby").entrySet()) {
            int lobbyIndex = (int) entry.getKey();
            int playersOnline = (Integer) entry.getValue();

            String serverName = "Main-Lobby-" + lobbyIndex;
            boolean isThisServer = serverName.equalsIgnoreCase(SpigotCore.getInstance().getServerName());

            ItemStack itemStack = ItemUtils.getItemStack(
                    (isThisServer ? Material.RECORD_11 : Material.GOLD_RECORD),
                    (isThisServer ? "&c" : "&f") + "Main Lobby &6#" + lobbyIndex,
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
        String serverName = "Main-Lobby-" + itemName.substring("&fMain Lobby &6#".length());

        event.setCancelled(true);
        if(serverName.equalsIgnoreCase(SpigotCore.getInstance().getServerName())) {
            return;
        }

        MainLobbyPlayer lobbyPlayer = MainLobbyPlayer.getLobbyPlayer(player.getUniqueId());
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
