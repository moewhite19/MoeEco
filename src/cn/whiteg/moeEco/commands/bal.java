package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandInterface;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

import static cn.whiteg.moeEco.MoeEco.plugin;

public class bal extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 0){
            DataCon dc = MMOCore.getPlayerData(sender);
            if (dc != null)
                sender.sendMessage("§b阁下当前拥有: §f" + plugin.getVaultHandler().getFormatBalance(dc));
            else {
                sender.sendMessage("§b找不到玩家");
                return false;
            }
        }
        if (args.length == 1){
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (dc != null){
                sender.sendMessage("§f" + args[0] + "§b当前拥有:§f " + plugin.getVaultHandler().getFormatBalance(dc));
            } else {
                sender.sendMessage("§b找不到玩家");
                return false;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return getMatches(MMOCore.getLatelyPlayerList(),args);
    }

    @Override
    public String getDescription() {
        return "查询玩家钱包";
    }
}
