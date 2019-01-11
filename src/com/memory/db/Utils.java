package com.memory.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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

    public static String type = "体系：";
    public static String xm = "姓名：";
    public static String xsl = "销售量";
    public static String hz = "汇 总";

    //src/com/memory/db/
    private static final String file_dir = "proxy";
    private static final String dbpath = "local.db";
    private static final String goodspath = "goods.db";
    private static final String detailpath = "detail.db";
    private static final String usepath = "use.db";
    private static final String fileLockPath = "file.lock";
    public static FileLock lock = null;

    private static int beginMonth = 1;
    private static int endMonth = Integer.parseInt(sf_MM.format(new Date()));
    private static int currentMonth = endMonth;

    private static Proxy proxy = null;
    private static JSONArray proxyJSON = null;
    private static JSONArray goodsJSON = null;
    private static JSONObject detailJSON = null;
    private static JSONObject useJSON = null;

    public static JSONArray getProxyJSON() {
        return proxyJSON;
    }
    public static JSONArray getGoodsJSON() {
        return goodsJSON;
    }
    public static JSONObject getDetailJSON() {
        return detailJSON;
    }
    public static JSONObject getUseJSON() {
        return useJSON;
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

    /***************************************************************************/
    public static double getUseSumByidmonth(String id, int month){
        double sum = 0.0;
        if(useJSON.containsKey(id)){
            if(useJSON.getJSONObject(id).containsKey(""+month)){
                sum = useJSON.getJSONObject(id).getJSONObject(""+month).getDouble("sum");
            }
        }
        return sum;
    }
    public static String  getUseBzByidmonth(String id, int month){
        String bz = "";
        if(useJSON.containsKey(id)){
            if(useJSON.getJSONObject(id).containsKey(""+month)){
                bz = useJSON.getJSONObject(id).getJSONObject(""+month).getString("bz");
            }
        }
        return bz;
    }

    public static JSONObject getXSzjBymonthid(int month, String id){
        JSONObject zjJSON = new JSONObject();
        double z_sum = 0.0;
        double j_sum = 0.0;
        for (int i = 0; i < goodsJSON.size(); i++) {
            JSONObject goodsObjectJSON = goodsJSON.getJSONObject(i);

            double xs_z = goodsObjectJSON.getJSONObject("xs").getDouble("z");
            double xs_j = goodsObjectJSON.getJSONObject("xs").getDouble("j");

            String goodsName = goodsObjectJSON.getString("name");
            int count = getCountByidmonthname(id, month, goodsName);

            z_sum+=xs_z*count;
            j_sum+=xs_j*count;
        }
        zjJSON.put("zsum", z_sum);
        zjJSON.put("jsum", j_sum);
        return zjJSON;
    }

    public static int getCountByidmonthname(String proxyId, int selMonth, String goodsName){
        int count = 0;
        if(detailJSON.containsKey(proxyId) &&
                detailJSON.getJSONObject(proxyId).containsKey(""+selMonth) &&
                detailJSON.getJSONObject(proxyId).getJSONObject(""+selMonth).containsKey(goodsName)){
            count = detailJSON.getJSONObject(proxyId).getJSONObject(""+selMonth).getInteger(goodsName);
        }
        return count;
    }

    public static void setCountByidmonthname(String proxyId, int selMonth, JSONObject updJSON){
        JSONObject months = new JSONObject();
        JSONObject month = new JSONObject();
        if(detailJSON.containsKey(proxyId)){
            months = detailJSON.getJSONObject(proxyId);
            if(months.containsKey(""+selMonth)){
                month = months.getJSONObject(""+selMonth);
            }
        }
        for(String key : updJSON.keySet()){
            month.put(key, updJSON.getInteger(key));
        }
        months.put(""+selMonth, month);
        detailJSON.put(proxyId, months);
        write2DetailDB();
    }

    public static JSONObject createObj(String name, String parent, Integer level){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", Utils.getShortUuid());
        jsonObject.put("name", name);
        jsonObject.put("parent", parent);

        proxyJSON.add(jsonObject);

        return jsonObject;
    }

    public static JSONObject getObj(String id){
        JSONObject jsonObject = null;
        for (int i = 0; i < proxyJSON.size(); i++) {
            if(id.equals(proxyJSON.getJSONObject(i).getString("id"))){
                jsonObject = proxyJSON.getJSONObject(i);
                break;
            }
        }
        return jsonObject;
    }

    public static boolean hasNode(String id){
        boolean flag = false;
        for (int i = 0; i < proxyJSON.size(); i++) {
            if(id.equals(proxyJSON.getJSONObject(i).getString("parent"))){
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
        File proxyFile = new File(file_dir+File.separator+dbpath);
        File goodsFile = new File(file_dir+File.separator+goodspath);
        File detailFile = new File(file_dir+File.separator+detailpath);
        File useFile = new File(file_dir+File.separator+usepath);
        try{
            if(!dir.exists()){
                dir.mkdir();
            }
            if(!proxyFile.exists()){
                proxyFile.createNewFile();
            }
            if(!detailFile.exists()){
                detailFile.createNewFile();
            }
            if(!useFile.exists()){
                useFile.createNewFile();
            }

            FileReader reader = new FileReader(proxyFile);
            BufferedReader br = new BufferedReader(reader);
            //加载proxy
            String line;
            StringBuffer stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                proxyJSON = JSONArray.parseArray(stringBuffer.toString());
            }else{
                proxyJSON = new JSONArray();
            }
            //加载goods
            reader = new FileReader(goodsFile);
            br = new BufferedReader(reader);
            stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                goodsJSON = JSONArray.parseArray(stringBuffer.toString());
            }else{
                goodsJSON = new JSONArray();
            }
            //加载detail
            reader = new FileReader(detailFile);
            br = new BufferedReader(reader);
            stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                detailJSON = JSONObject.parseObject(stringBuffer.toString());
            }else{
                detailJSON = new JSONObject();
            }
            //加载use
            reader = new FileReader(useFile);
            br = new BufferedReader(reader);
            stringBuffer = new StringBuffer("");
            while ((line = br.readLine()) != null) {
                stringBuffer.append(line);
            }
            if(!"".equals(stringBuffer.toString())){
                useJSON = JSONObject.parseObject(stringBuffer.toString());
            }else{
                useJSON = new JSONObject();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write2LocalDB(){
        File proxyFile = new File(file_dir+File.separator+dbpath);
        String proxyContent = proxyJSON.toJSONString();
        try{
            FileWriter fileWriter=new FileWriter(proxyFile.getAbsoluteFile());
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(proxyContent.toCharArray());
            bufferedWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void write2DetailDB(){
        File detailFile = new File(file_dir+File.separator+detailpath);
        String detailContent = detailJSON.toJSONString();
        try{
            FileWriter fileWriter=new FileWriter(detailFile.getAbsoluteFile());
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(detailContent.toCharArray());
            bufferedWriter.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void write2UseDB(){
        File useFile = new File(file_dir+File.separator+usepath);
        String detailContent = useJSON.toJSONString();
        try{
            FileWriter fileWriter=new FileWriter(useFile.getAbsoluteFile());
            BufferedWriter bufferedWriter=new BufferedWriter(fileWriter);
            bufferedWriter.write(detailContent.toCharArray());
            bufferedWriter.close();
        }catch(Exception e){
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
