package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.moeEco.VaultHandler;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

import static cn.whiteg.moeEco.MoeEco.plugin;

public class set extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            DataCon dc = MMOCore.getPlayerData(sender);
            if (dc != null){
                double amount;
                try{
                    amount = Double.parseDouble(args[0]);
                }catch (NumberFormatException e){
                    sender.sendMessage("参数有误");
                    return true;
                }
                amount = new BigDecimal(amount).setScale(2,BigDecimal.ROUND_DOWN).doubleValue();
                dc.set("Money",amount);
                plugin.getLeaderboard().check(dc.getName(),amount);
                sender.sendMessage("§b将" + sender.getName() + "§b的" + plugin.getVaultHandler().currencyNamePlural() + "设置为§f" + plugin.getVaultHandler().getDecimalFormat().format(amount));
            } else {
                sender.sendMessage("§b找不到玩家");
            }
        } else if (args.length == 2){
            if (plugin.getVaultHandler().hasAccount(args[0])){
                BigDecimal amount;
                try{
                    amount = VaultHandler.toBigDecimal(args[1]);
                }catch (NumberFormatException e){
                    sender.sendMessage("参数有误");
                    return true;
                }
                DataCon dc = MMOCore.getPlayerData(args[0]);
                dc.set("Money",amount);
                plugin.getLeaderboard().check(dc.getName(),amount.doubleValue());
                var value = amount.doubleValue();
                sender.sendMessage("§b将" + args[0] + "§b的" + plugin.getVaultHandler().currencyNamePlural() + "设置为§f" + plugin.getVaultHandler().getDecimalFormat().format(value));
                Player p = Bukkit.getPlayerExact(args[0]);
                if (p != null && p.isOnline()){
                    p.sendMessage(" * §f" + sender.getName() + "§b将阁下的" + plugin.getVaultHandler().currencyNamePlural() + "设置为§f" + plugin.getVaultHandler().getDecimalFormat().format(value));
                }
            } else {
                sender.sendMessage("§b找不到玩家");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        return getMatches(MMOCore.getLatelyPlayerList(),args);
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
