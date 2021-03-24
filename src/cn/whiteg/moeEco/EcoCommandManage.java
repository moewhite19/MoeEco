package cn.whiteg.moeEco;

import cn.whiteg.mmocore.common.CommandManage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class EcoCommandManage extends CommandManage {
    public EcoCommandManage(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @Override
    public boolean onCommand(CommandSender sender,Command cmd,String str,String[] args) {
        if (args.length == 0) args = new String[]{"bal"};
        return super.onCommand(sender,cmd,str,args);
    }
}
