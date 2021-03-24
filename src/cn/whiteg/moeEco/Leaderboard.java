package cn.whiteg.moeEco;

import cn.whiteg.mmocore.MMOCore;

import java.io.*;
import java.util.*;

public class Leaderboard {
    final int size;
    private final List<Item> list = new LinkedList<>();
    private boolean modified = false;

    public Leaderboard(int size) {
        this.size = size;
    }

    public void check(String name,double amount) {
        Iterator<Item> it = list.iterator();
        while (it.hasNext()) {
            Item e = it.next();
            if (e.name.equals(name)){
                if (e.amount == amount){
                    return;
                } else {
                    it.remove();
                    break;
                }
            }
        }

        int i = 0;
        for (Item e : list) {
            if (e.amount < amount){
                if (list.size() < size){
                    list.add(i,new Item(name,amount));
                } else {
                    list.add(i,new Item(name,amount));
                    list.remove(list.size() - 1);
                }
                modified = true;
                return;
            }
            i++;
        }

        if (list.size() < size){
            list.add(new Item(name,amount));
            modified = true;
        }
    }

    public void sort() {
        Collections.sort(list,new Item.solt());
    }


    public void load(File file) {
        if (file.exists()){
            try{
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                String f;
                while ((f = br.readLine()) != null) {
                    String[] arg = f.split(":");
                    if (arg.length < 1) continue;
                    try{
                        list.add(new Item(arg[0],Double.parseDouble(arg[1])));
                    }catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                br.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void save(File file) {
        if (!modified) return;
        try{
            modified = false;
            if (!file.exists()){
                File p = file.getParentFile();
                if (!p.exists()){
                    p.mkdirs();
                }
                file.createNewFile();
            }
            //1、打开流
            Writer w = new FileWriter(file);
            Iterator<Item> i = list.iterator();
            //2、写入内容
            while (i.hasNext()) {
                Item e = i.next();
                w.write(e.name + ':' + e.amount);
                if (i.hasNext()) w.write('\n');
            }
            //3、关闭流
            w.close();
        }catch (IOException e){
            System.out.println("文件写入错误：" + e.getMessage());
        }
    }

    public List<Item> getList() {
        return list;
    }

    public void clearup() {
        if (list.size() > size){
            list.subList(size,list.size()).clear();
        }
        list.removeIf(e -> !MMOCore.hasPlayerData(e.name));
    }

    public static class Item {
        String name;
        double amount;

        public Item(String name,double amount) {
            this.name = name;
            this.amount = amount;
        }

        public String getName() {
            return name;
        }

        public double getAmount() {
            return amount;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name + ':' + amount;
        }

        //套娃排序类
        static class solt implements Comparator<Item> {
            @Override
            public int compare(Item o1,Item o2) {
                //如果o1比o2小则往后移
                if (o1.amount < o2.amount){
                    return 1;
                } else if (o1.amount > o2.amount){
                    return -1;
                }
                return 0;
            }
        }
    }
}
