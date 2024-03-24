It's a complex minecraft minigame network containing multiple modules.

The project uses:

**Redis** to cache user's data to save time on data write/read and to send messages between spigot servers, for example when we need to send player to another game; 
**MongoDB** to save user's data once they left network
**Spigot (+ 1.8.8 NMS) and Bungeecord APIs**

Lombok + some extra libraries such as commons-io and caffeine
