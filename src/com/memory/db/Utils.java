package com.memory.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/18 0018 15:07
 * @Description:
 */
public class Utils {
    public static final String loginname = "";
    public static final String password = "";

    /*public static final String loginname = "houai@yjw";
    public static final String password = "147258";*/

    public static SimpleDateFormat sf_yyyyMMdd = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat sf_MM = new SimpleDateFormat("MM");

    public static String xm = "姓名：";
    public static String dygr = "当月个人销售查询";
    public static String dytd = "当月团队销售查询";
    public static String lsgr = "累计个人销售查询";
    public static String lstd = "累计团队销售查询";

    //src/com/memory/db/
    private static final String file_dir = "proxy";
    private static final String dbpath = "local.db";
    private static final String goodspath = "goods.db";
    private static final String fileLockPath = "file.lock";
    public static FileLock lock = null;

    private static int beginMonth = 1;
    private static int endMonth = Integer.parseInt(sf_MM.format(new Date()));
    private static int currentMonth = endMonth;
    private static Proxy proxy = null;

    private static JSONArray jsonArray = null;
    private static JSONArray jsonArrayGoods = null;

    public static JSONArray getJsonArray() {
        return jsonArray;
    }
    public static Proxy getProxy() {
        return proxy;
    }
    public static void setProxy(Proxy proxy) {
        Utils.proxy = proxy;
    }

    public static int getBeginMonth() {
        return beginMonth;
    }
    public static int getEndMonth() {
        return endMonth;
    }
    public static int getCurrentMonth() {
        return currentMonth;
    }
    public static void setCurrentMonth(int currentMonth) {
        Utils.currentMonth = currentMonth;
    }
    public static String getFile_dir() {
        return file_dir;
    }
    public static String getGoodspath() {
        return goodspath;
    }
    public static JSONArray getJsonArrayGoods() {
        return jsonArrayGoods;
    }

    /***************************************************************************/

    public static JSONObject createObj(String name, String parent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", Utils.getShortUuid());
        jsonObject.put("name", name);
        jsonObject.put("parent", parent);

        JSONObject monthGoodsObj = new JSONObject();
        jsonObject.put("monthGoods", monthGoodsObj);

        jsonArray.add(jsonObject);

        return jsonObject;
    }

    public static JSONObject getObj(String id){
        JSONObject jsonObject = null;
        for (int i = 0; i < jsonArray.size(); i++) {
            if(id.equals(jsonArray.getJSONObject(i).getString("id"))){
                jsonObject = jsonArray.getJSONObject(i);
                break;
            }
        }
        return jsonObject;
    }

    public static boolean hasNode(String id){
        boolean flag = false;
        for (int i = 0; i < jsonArray.size(); i++) {
            if(id.equals(jsonArray.getJSONObject(i).getString("parent"))){
                flag = true;
                break;
            }
        }
        return flag;
    }

    public static boolean checkProcess() {
        File dir = new File(file_dir);
        boolean flag = false;
        try {
            if(!dir.exists()){
                dir.mkdir();
            }
            RandomAccessFile raf = new RandomAccessFile(file_dir+File.separator+fileLockPath, "rw");
            FileChannel fc = raf.getChannel();
            lock  = fc.tryLock();
            if (lock != null && lock.isValid()) {
                flag = true;
            }
        } catch (IOException e) {
            flag = false;
        }
        return flag;
    }
    /***************************************************************************/

    public static void read2System() {
        File dir = new File(file_dir);
        File file = new File(file_dir+File.separator+dbpath);
        try{
            if(!dir.exists()){
                dir.mkdir();
            }
            if(!file.exists()){
                file.createNewFile();
            }
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String line;
            StringBuffer stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                //String jie = Base64Utils.getInstance().decode(stringBuffer.toString());
                jsonArray = JSONArray.parseArray(stringBuffer.toString());
            }else{
                jsonArray = new JSONArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write2LocalDB(){
        File file = new File(file_dir+File.separator+dbpath);
        String content = jsonArray.toJSONString();
        try{
            FileWriter fileWriter=new FileWriter(file.getAbsoluteFile());
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(content.toCharArray());
            bufferedWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void readGoods() {
        File dir = new File(file_dir);
        File file = new File(file_dir+File.separator+goodspath);
        System.out.println(file.exists());
        try{
            if(!dir.exists()){
                dir.mkdir();
            }
            if(!file.exists()){
                file.createNewFile();
            }
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);

            String line;
            StringBuffer stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                jsonArrayGoods = JSONArray.parseArray(stringBuffer.toString());
            }else{
                jsonArrayGoods = new JSONArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /***************************************************************************/

    private static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z" };
    public static String getShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }
    public static Double toDouble(String money){
        return new BigDecimal(money).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
