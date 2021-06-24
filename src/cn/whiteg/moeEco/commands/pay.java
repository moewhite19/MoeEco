package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.CommandInterface;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static cn.whiteg.moeEco.MoeEco.plugin;

public class pay extends CommandInterface {

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 2){
            if (sender.getName().equals(args[0])){
                sender.sendMessage("§b阁下想和自己交易吗w?");
                return false;
            }
            DataCon send = MMOCore.getPlayerData(sender);
            if (send == null){
                sender.sendMessage("阁下当前没有账户");
                return false;
            }
            DataCon receive = MMOCore.getPlayerData(args[0]);
            if (receive == null){
                sender.sendMessage("§b找不到玩家");
                return false;
            }
            double amount;
            try{

                amount = Double.parseDouble(args[1]);
            }catch (NumberFormatException e){
                sender.sendMessage("参数有误");
                return false;
            }
            var vault = plugin.getVaultHandler();
            var response = vault.withdrawPlayer(send,amount);
            if (response.type != EconomyResponse.ResponseType.SUCCESS){
                sender.sendMessage("§b阁下账户只有§f" + vault.format(response.balance));
                return false;
            }
            amount = response.amount;
            if (amount <= 0){
                sender.sendMessage("§b数量必须大于0");
                return false;
            }
            response = vault.depositPlayer(receive,amount);
            if (response.type != EconomyResponse.ResponseType.SUCCESS){
                sender.sendMessage("出现未知错误" + response.type + " : " + response.errorMessage);
                return false;
            }
            amount = response.amount;
            sender.sendMessage("§b给" + receive.getName() + "§b发送§f" + amount);
            Player p = Bukkit.getPlayerExact(receive.getName());
            if (p != null && p.isOnline()){
                p.sendMessage(" * §b 收到来自§f" + sender.getName() + "§b的§f" + amount);
            }
        } else {
            sender.sendMessage("§a/pay [玩家ID] [数额] §b送出" + plugin.getVaultHandler().currencyNamePlural());
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            return getMatches(args,MMOCore.getLatelyPlayerList());
        }
        return null;
    }
}
