package cn.whiteg.moeEco.listener;

import cn.whiteg.moeEco.MoeEco;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoin implements Listener {
    @EventHandler(ignoreCancelled = true)
    public void playerJoin(PlayerJoinEvent event){
        MoeEco.plugin.getVaultHandler().hasAccount(event.getPlayer().getName());
    }
    public void unreg(){
        PlayerJoinEvent.getHandlerList().unregister(this);
    }
}
