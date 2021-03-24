package cn.whiteg.moeEco;

import cn.whiteg.mmocore.common.CommandManage;
import cn.whiteg.mmocore.common.PluginBase;
import cn.whiteg.moeEco.listener.PlayerJoin;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.ServicePriority;

import java.io.File;
import java.util.logging.Logger;

import static cn.whiteg.moeEco.Setting.config;
import static cn.whiteg.moeEco.Setting.reload;


public class MoeEco extends PluginBase {
    public static Logger logger;
    public static MoeEco plugin;
    public CommandManage mainCommand;
    public File leaderboard_file;
    private VaultHandler vaultHandler;
    private Leaderboard leaderboard;

    public MoeEco() {
        plugin = this;
    }

    public void onLoad() {
        saveDefaultConfig();
        logger = getLogger();
        reload();
        leaderboard_file = new File(getDataFolder(),"leaderboard.txt");
    }

    public void onEnable() {
        logger.info("开始加载插件");
        if (Setting.DEBUG) logger.info("§a调试模式已开启");
        mainCommand = new EcoCommandManage(this);
        mainCommand.setExecutor();
        setupVault();
        //排行榜
        leaderboard = new Leaderboard(config.getInt("Leaderboard",20));
        leaderboard.load(leaderboard_file);
        if (!leaderboard.getList().isEmpty()){
            leaderboard.clearup();
            leaderboard.sort();
        }
        logger.info("全部加载完成");
        regListener(new PlayerJoin());
    }

    public void onDisable() {
        unregListener();
        //注销注册玩家加入服务器事件
        leaderboard.save(leaderboard_file);
        logger.info("插件已关闭");
    }

    public void onReload() {
        logger.info("--开始重载--");
        reload();
        logger.info("--重载完成--");
    }


    private void setupVault() {
        final Plugin vault = this.getServer().getPluginManager().getPlugin("Vault");
        if (vault == null){
            return;
        }
        setVaultHandler(new VaultHandler(this));
        this.getServer().getServicesManager().register(Economy.class,getVaultHandler(),this,ServicePriority.Highest);
    }

    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    public void log(String str) {
        logger.info(str);
    }

    public VaultHandler getVaultHandler() {
        return vaultHandler;
    }

    public void setVaultHandler(VaultHandler vaultHandler) {
        this.vaultHandler = vaultHandler;
    }
}
