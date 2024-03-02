package me.fodded.mainlobby.gameplay.guis;

import me.fodded.mainlobby.managers.MainLobbyPlayer;
import me.fodded.spigotcore.gameplay.gui.AbstractGui;
import me.fodded.spigotcore.servers.SpigotServerManager;
import me.fodded.spigotcore.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class MinigamesGui extends AbstractGui {

    public MinigamesGui(Player player) {
        int skywarsPlayersOnline = SpigotServerManager.getInstance().getAmountOfPlayers("Skywars");
        setItem(ItemUtils.getItemStack(
                Material.GOLD_SWORD, "&e&lSkywars",
                Arrays.asList("&fOnline players: " + skywarsPlayersOnline)
        ), 0);
        player.openInventory(getInventory());
    }

    @Override
    public int getSize() {
        return 9;
    }

    @Override
    public String getTitle() {
        return "Minigames Menus";
    }

    @Override
    public void onClick(InventoryClickEvent event, Player player) {
        switch (event.getSlot()) {
            case 0:
                sendToSkywarsLobby(player);
                break;
        }

        event.setCancelled(true);
    }

    private void sendToSkywarsLobby(Player player) {
        MainLobbyPlayer mainLobbyPlayer = MainLobbyPlayer.getLobbyPlayer(player.getUniqueId());
        mainLobbyPlayer.sendPlayerToLobby("Skywars-Lobby");
    }
}
