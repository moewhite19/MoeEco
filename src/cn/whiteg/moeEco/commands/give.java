package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.whiteg.moeEco.MoeEco.plugin;

public class give extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            double a;
            try{
                a = Double.parseDouble(args[0]);
            }catch (NumberFormatException e){
                sender.sendMessage("参数有误");
                return true;
            }
            DataCon dc = MMOCore.getPlayerData(sender);
            if (giveMony(sender,a,dc)) return true;
        } else if (args.length == 2){
            double amount;
            try{
                amount = Double.parseDouble(args[1]);
            }catch (NumberFormatException e){
                sender.sendMessage("参数有误");
                return true;
            }
            DataCon dc = MMOCore.getPlayerData(args[0]);
            if (giveMony(sender,amount,dc)) return true;
        } else {
            sender.sendMessage("§a/eco give [玩家ID] [数量] §b送出" + plugin.getVaultHandler().currencyNamePlural());
        }
        return true;
    }

    private boolean giveMony(CommandSender sender,double a,DataCon dc) {
        EconomyResponse st;
        if (dc == null){
            sender.sendMessage("§b找不到玩家");
            return false;
        }
        if (a > 0){
            st = plugin.getVaultHandler().depositPlayer(dc,a);
            sender.sendMessage("§b给予" + dc.getName() + "§f" + plugin.getVaultHandler().getDecimalFormat().format(st.amount));
        } else {
            st = plugin.getVaultHandler().withdrawPlayer(dc,Math.abs(a));
            if (st.type != EconomyResponse.ResponseType.SUCCESS){
                sender.sendMessage("扣除失败,原因:" + st.errorMessage);
                return false;
            }
            sender.sendMessage("§b扣除" + dc.getName() + "§f" + plugin.getVaultHandler().getDecimalFormat().format(st.amount));
        }
        Player p = Bukkit.getPlayerExact(dc.getName());
        if (p != null && p.isOnline()){
            p.sendMessage(" * §b 收到来自§f" + sender.getName() + "§b的§f" + ((a < 0) ? "-" : "") + plugin.getVaultHandler().getDecimalFormat().format(st.amount));
        }
        return true;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        return getMatches(MMOCore.getLatelyPlayerList(),args);
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("moeeco.give");
    }
}
