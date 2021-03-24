package cn.whiteg.moeEco.commands;

import cn.whiteg.mmocore.MMOCore;
import cn.whiteg.mmocore.common.HasCommandInterface;
import cn.whiteg.moeEco.MoeEco;
import cn.whiteg.moeEco.Setting;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class impo extends HasCommandInterface {

    @Override
    public boolean executo(CommandSender sender,Command cmd,String label,String[] args) {
        if (args.length == 1){
            if(args[0] .equals("ess")){
                File dir = new File(MMOCore.plugin.getDataFolder(),"Player");
                for (File file : Objects.requireNonNull(dir.listFiles())) {
                    MoeEco.logger.info("导入" + file.getName());
                    File ef = new File("plugins" + File.separator + "Essentials" + File.separator + "userdata" + File.separator + file.getName());
                    YamlConfiguration con = YamlConfiguration.loadConfiguration(file);
                    YamlConfiguration ec = YamlConfiguration.loadConfiguration(ef);
                    String money = ec.getString("money");
                    if (money == null){
                        MoeEco.logger.info("跳过" + file.getName());
                        continue;
                    }
                    con.set(Setting.moneyKey,Double.parseDouble(money));
                    try{
                        con.save(file);
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> complete(CommandSender sender,Command cmd,String label,String[] args) {
        List arr = new ArrayList();
        arr.add("ess");
        return arr;
    }

    @Override
    public boolean canUseCommand(CommandSender sender) {
        return sender.hasPermission("whiteg.test");
    }
}
