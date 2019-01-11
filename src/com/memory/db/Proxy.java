package com.memory.db;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 13:28
 * @Description:
 */
public class Proxy {
    private String id;          //代理
    private String name;        //名称
    private String parent;      //父级ID
    private String parentName;  //父级名称
    private Integer count;      //团队人数
    private Integer level;      //代理等级
    private String type;        //代理体系

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
