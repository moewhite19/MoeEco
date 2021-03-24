package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.moeEco.Leaderboard;
import cn.whiteg.moeEco.MoeEco;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

public class leaderboard extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        int head;
        if (args.length > 0){
            if (sender.hasPermission("whiteg.test")){
                if (args[0].equals("sort")){
                    sender.sendMessage(" §b开始整理排行榜");
                    MoeEco.plugin.getLeaderboard().sort();
                    return true;
                } else if (args[0].equals("clearup")){
                    sender.sendMessage(" §b开始清理无效项目");
                    MoeEco.plugin.getLeaderboard().clearup();
                    return true;
                }
            }
            try{
                head = Integer.parseInt(args[0]) - 1;
            }catch (NumberFormatException e){
                sender.sendMessage(" §b参数有误");
                return false;
            }
        } else {
            head = 0;
        }
        if (head < 0){
            sender.sendMessage(" §b参数有误");
        }
        List<Leaderboard.Item> list = MoeEco.plugin.getLeaderboard().getList();
        if (head > list.size()){
            sender.sendMessage(" §b再怎么看也没有啦");
        } else {
            int end = head + 10;
            StringBuilder sb = new StringBuilder();
            for (; head < list.size() && head < end; head++) {
                Leaderboard.Item e = list.get(head);
                sb.append(head + 1).append('.').append(e.getName()).append(" - ").append(MoeEco.plugin.getVaultHandler().getDecimalFormat().format(e.getAmount()));
                sender.sendMessage(sb.toString());
                sb.setLength(0);
            }
        }
        return true;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            if (sender.hasPermission("whiteg.test")){
                return Arrays.asList("sort","clearup");
            } else return Arrays.asList("1","10");
        }
        return null;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("moeeco.leaderboard");
    }

}
