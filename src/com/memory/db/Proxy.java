package com.memory.db;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 13:28
 * @Description:
 */
public class Proxy {
    private String id;
    private String name;
    private String parent;
    private String parentName;
    private Integer count;

    private JSONObject goods;
    private JSONObject goodsSum;

    private JSONObject goodsLs;
    private JSONObject goodsSumLs;

    @Override
    public String toString() {
        if(count>0){
            return name + "（"+count+"）";
        }else{
            return name;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public JSONObject getGoods() {
        return goods;
    }

    public void setGoods(JSONObject goods) {
        this.goods = goods;
    }

    public JSONObject getGoodsSum() {
        return goodsSum;
    }

    public void setGoodsSum(JSONObject goodsSum) {
        this.goodsSum = goodsSum;
    }

    public JSONObject getGoodsLs() {
        return goodsLs;
    }

    public void setGoodsLs(JSONObject goodsLs) {
        this.goodsLs = goodsLs;
    }

    public JSONObject getGoodsSumLs() {
        return goodsSumLs;
    }

    public void setGoodsSumLs(JSONObject goodsSumLs) {
        this.goodsSumLs = goodsSumLs;
    }
}
