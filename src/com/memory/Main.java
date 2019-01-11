package com.memory;

import com.memory.container.LoginFrame;
import com.memory.db.Utils;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/18 0018 14:17
 * @Description:
 */
public class Main {
    public static void main(String[] args) {
        if(Utils.checkProcess()){
            Utils.read2System();
            LoginFrame.init();
        }else{
            System.exit(0);
        }
    }
}
