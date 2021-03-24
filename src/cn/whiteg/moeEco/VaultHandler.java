package cn.whiteg.moeEco;

import cn.whiteg.mmocore.DataCon;
import cn.whiteg.mmocore.MMOCore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.OfflinePlayer;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class VaultHandler implements Economy {
    final BigDecimal minAmount = new BigDecimal("0.01");
    final String defMoney = String.valueOf(Setting.defMoney);
    final private MoeEco plugin;

    public VaultHandler(final MoeEco plugin) {
        this.plugin = plugin;
        plugin.log("Vault support enabled.");
    }

    //去除多余小数
    public static BigDecimal toBigDecimal(double d) {
        final BigDecimal bd = new BigDecimal(d);
        return bd.setScale(Setting.decimalScale,Setting.roundingMode);
    }

    //去除多余小数
    public static BigDecimal toBigDecimal(String str) {
        final BigDecimal bd = new BigDecimal(str);
        return bd.setScale(Setting.decimalScale,Setting.roundingMode);
    }

    public DecimalFormat getDecimalFormat() {
        return Setting.decimalFormat;
    }

    @Override
    public boolean isEnabled() {
        return plugin.isEnabled();
    }

    @Override
    public String getName() {
        return plugin.getName();
    }

    @Override
    public String format(final double amount) {
        return String.valueOf(amount);
    }

    @Override
    public String currencyNameSingular() {
        return Setting.currencyNameSingular;
    }

    @Override
    public String currencyNamePlural() {
        return Setting.currencyNamePlural;
    }

    public BigDecimal getBigBalance(DataCon dc) {
        if (hasAccount(dc)){
            try{
                return toBigDecimal(dc.getConfig().getString(Setting.moneyKey,defMoney));
            }catch (NumberFormatException ignored){
                MoeEco.logger.warning("无法序列化" + dc.getName() + "账户余额：" + dc.get(Setting.moneyKey));
            }
        }
        return BigDecimal.ZERO;
    }

    public double getBalance(DataCon dc) {
        return getBigBalance(dc).doubleValue();
    }

    @Override
    public double getBalance(final String playerName) {
        final DataCon dc = MMOCore.getPlayerData(playerName);
        if (!hasAccount(dc)) return 0;
        return getBalance(dc);
    }


    @Override
    public double getBalance(final OfflinePlayer orfflinePlayer) {
        return this.getBalance(orfflinePlayer.getName());
    }

    @Override
    public double getBalance(final String playerName,final String worldName) {
        return this.getBalance(playerName);
    }

    @Override
    public double getBalance(final OfflinePlayer offlinePlayer,final String worldName) {
        return this.getBalance(offlinePlayer);
    }

    public String getFormatBalance(final String playerName) {
        return getDecimalFormat().format(getBalance(playerName));
    }

    public String getFormatBalance(DataCon dc) {
        return getDecimalFormat().format(getBalance(dc));
    }

    public String getFormatBalance(final OfflinePlayer orfflinePlayer) {
        return getDecimalFormat().format(getBalance(orfflinePlayer));
    }

    @Override
    public EconomyResponse withdrawPlayer(final String playerName,final double amount) {
        return withdrawPlayer(MMOCore.getPlayerData(playerName),amount);
    }


    public EconomyResponse withdrawPlayer(final DataCon dc,double amount) {
        if (!hasAccount(dc)) return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.FAILURE,"找不到玩家");
        var money = getBigBalance(dc);
        if (amount < 0) return new EconomyResponse(0,money.doubleValue(),EconomyResponse.ResponseType.FAILURE,"不可以为负数");
        var bigAmount = toBigDecimal(amount);
        //如果数字小于最小值替换成最小值r
        if (bigAmount.compareTo(minAmount) < 0){
            bigAmount = minAmount;
        }

        if (money.compareTo(bigAmount) >= 0){
            var newMoney = money.subtract(bigAmount);
            var now = newMoney.doubleValue();
            dc.set(Setting.moneyKey,newMoney.toString());
            plugin.getLeaderboard().check(dc.getName(),now);
            amount = money.subtract(newMoney).doubleValue();
            if (Setting.DEBUG){
                MoeEco.logger.info("从" + dc.getName() + "账户扣除" + bigAmount + "最终扣除" + amount);
            }
            return new EconomyResponse(amount,now,EconomyResponse.ResponseType.SUCCESS,"完成");
        }
        return new EconomyResponse(0.0,dc.getDouble(Setting.moneyKey),EconomyResponse.ResponseType.FAILURE,"余额不足");
    }

    @Override
    public EconomyResponse withdrawPlayer(final OfflinePlayer offlinePlayer,final double amount) {
        return this.withdrawPlayer(MMOCore.getPlayerData(offlinePlayer.getUniqueId()),amount);
    }

    @Override
    public EconomyResponse depositPlayer(final String playerName,final double amount) {
        return depositPlayer(MMOCore.getPlayerData(playerName),amount);
    }

    public EconomyResponse depositPlayer(final DataCon dc,double amount) {
        if (!hasAccount(dc)) return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.FAILURE,"找不到玩家");
        var money = getBigBalance(dc);
        if (amount < 0.0)
            return new EconomyResponse(0.0,money.doubleValue(),EconomyResponse.ResponseType.FAILURE,"不可以为负数");
        var bigAmount = toBigDecimal(amount);
        var newMoney = money.add(bigAmount);
        var now = newMoney.doubleValue();
        dc.set(Setting.moneyKey,newMoney.toString());
        amount = newMoney.subtract(money).doubleValue();
        plugin.getLeaderboard().check(dc.getName(),now);
        if (Setting.DEBUG){
            MoeEco.logger.info("给" + dc.getName() + "账户添加" + bigAmount + "最终添加" + amount);
        }
        return new EconomyResponse(amount,now,EconomyResponse.ResponseType.SUCCESS,"完成");
    }

    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer offlinePlayer,final double amount) {
        return this.depositPlayer(offlinePlayer.getName(),offlinePlayer.getUniqueId().toString(),amount);
    }

    @Override
    public boolean has(final String playerName,final double amount) {
        return this.getBalance(playerName) >= amount;
    }

    @Override
    public boolean has(final OfflinePlayer offlinePlayer,final double amount) {
        return this.getBalance(offlinePlayer) >= amount;
    }

    @Override
    public EconomyResponse createBank(final String name,final String player) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse createBank(final String name,final OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse deleteBank(final String name) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse bankHas(final String name,final double amount) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse bankWithdraw(final String name,final double amount) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse bankDeposit(final String name,final double amount) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse isBankOwner(final String name,final String playerName) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse isBankOwner(final String name,final OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse isBankMember(final String name,final String playerName) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse isBankMember(final String name,final OfflinePlayer offlinePlayer) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public EconomyResponse bankBalance(final String name) {
        return new EconomyResponse(0.0,0.0,EconomyResponse.ResponseType.NOT_IMPLEMENTED,"MoeEco没有适配");
    }

    @Override
    public List<String> getBanks() {
        return new ArrayList<String>();
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public boolean hasAccount(final String playerName) {
        final DataCon dc = MMOCore.getPlayerData(playerName);
        return hasAccount(dc);
    }

    @Override
    public boolean hasAccount(final OfflinePlayer offlinePlayer) {
        final DataCon dc = MMOCore.getPlayerData(offlinePlayer);
        return hasAccount(dc);
        //return this.plugin.getAPI().accountExists(offlinePlayer.getName(),offlinePlayer.getUniqueId().toString());
    }

    @Override
    public boolean hasAccount(final String playerName,final String worldName) {
        return this.hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(final OfflinePlayer offlinePlayer,final String worldName) {
        return this.hasAccount(offlinePlayer);
    }

    public boolean hasAccount(DataCon dc) {
        return dc != null && dc.isLoaded();
    }

    @Override
    public boolean createPlayerAccount(final String playerName) {
        if (this.hasAccount(playerName)){
            return false;
        }
        DataCon dc = MMOCore.getPlayerData(playerName);
        dc.set(Setting.moneyKey,Setting.defMoney);
        return true;
    }

    @Override
    public boolean createPlayerAccount(final OfflinePlayer offlinePlayer) {
        return createPlayerAccount(offlinePlayer.getName());
    }

    @Override
    public int fractionalDigits() {
        return -1;
    }


    @Override
    public boolean has(final String playerName,final String worldName,final double amount) {
        return this.has(playerName,amount);
    }

    @Override
    public boolean has(final OfflinePlayer offlinePlayer,final String worldName,final double amount) {
        return this.has(offlinePlayer,amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(final String playerName,final String worldName,final double amount) {
        return this.withdrawPlayer(playerName,amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(final OfflinePlayer offlinePlayer,final String worldName,final double amount) {
        return this.withdrawPlayer(offlinePlayer,amount);
    }

    @Override
    public EconomyResponse depositPlayer(final String playerName,final String worldName,final double amount) {
        return this.depositPlayer(playerName,amount);
    }

    @Override
    public EconomyResponse depositPlayer(final OfflinePlayer offlinePlayer,final String worldName,final double amount) {
        return this.depositPlayer(offlinePlayer,amount);
    }

    @Override
    public boolean createPlayerAccount(final String playerName,final String worldName) {
        return this.createPlayerAccount(playerName);
    }

    @Override
    public boolean createPlayerAccount(final OfflinePlayer offlinePlayer,final String worldName) {
        return this.createPlayerAccount(offlinePlayer);
    }

}
