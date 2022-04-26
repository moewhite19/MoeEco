package cn.whiteg.moeEco;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Set;

import static cn.whiteg.moeEco.MoeEco.logger;
import static cn.whiteg.moeEco.MoeEco.plugin;

public class Setting {
    public static final String moneyKey = "Money";
    private static final int CONFIGVER = 2;
    public static boolean DEBUG;
    public static FileConfiguration config;
    public static String currencyNamePlural; //复数货币名称
    public static String currencyNameSingular;//单数货币名称
    public static double defMoney; //初始资金
    public static short decimalScale; //小数点位数
    public static DecimalFormat decimalFormat; //格式
    public static RoundingMode roundingMode; //舍入模式

    @SuppressWarnings("all")
    public static void reload() {
        File file = new File(plugin.getDataFolder(),"config.yml");
        config = YamlConfiguration.loadConfiguration(file);
        //自动更新配置文件
        if (config.getInt("ver") < CONFIGVER){
            plugin.saveResource("config.yml",true);
            config.set("ver",CONFIGVER);
            final FileConfiguration newcon = YamlConfiguration.loadConfiguration(file);
            Set<String> keys = newcon.getKeys(true);
            for (String k : keys) {
                if (config.isSet(k)) continue;
                config.set(k,newcon.get(k));
                logger.info("新增配置节点: " + k);
            }
            try{
                config.save(file);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        DEBUG = config.getBoolean("debug");
        defMoney = config.getDouble("defMoney",149D);
        currencyNamePlural = config.getString("currencyNamePlural","$");
        currencyNameSingular = config.getString("currencyNameSingular","Money");
        //不要有疑问， 如果两个字符串内容一致的话共用一个字符串对象；
        if (currencyNamePlural.equals(currencyNameSingular)){
            currencyNamePlural = currencyNameSingular;
        }
        decimalScale = (short) config.getInt("DecimalScale",2);
        roundingMode = RoundingMode.valueOf(config.getString("RoundingMode" , "FLOOR"));
        decimalFormat = new DecimalFormat(config.getString("DecimalFormat",""));
    }

}
