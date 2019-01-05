package com.memory.container;

import com.alibaba.fastjson.JSONObject;
import com.memory.db.Proxy;
import com.memory.db.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 15:15
 * @Description:
 */
public class ModelFrame {
    public static JFrame jFrame = null;
    public static void init(String type, Proxy proxy){
        if(jFrame==null){
            Font font =new Font("微软雅黑", Font.PLAIN, 16);//设置字体
            jFrame = new JFrame(type.equals("add")?"增 加":"修改");

            ImageIcon imageIcon = new ImageIcon("title300.png");
            jFrame.setIconImage(imageIcon.getImage());

            jFrame.setResizable(false);
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(null);

            JLabel label_parent=new JLabel("上 级: ");
            label_parent.setBounds(20, 40, 60, 40);
            label_parent.setFont(font);
            panel.add(label_parent);

            JTextField txt_parent=new JTextField();
            txt_parent.setBounds(100, 40, 250, 40);
            txt_parent.setFont(font);
            txt_parent.setEditable(false);
            panel.add(txt_parent);

            JLabel label_name=new JLabel("姓 名: ");
            label_name.setBounds(20, 90, 60, 40);
            label_name.setFont(font);
            panel.add(label_name);

            JTextField txt_name=new JTextField();
            txt_name.setBounds(100, 90, 250, 40);
            txt_name.setFont(font);
            panel.add(txt_name);

            JButton btn_yes=new JButton("确 认");
            btn_yes.setBounds(100, 200, 100, 40);
            btn_yes.setFont(font);
            panel.add(btn_yes);

            JButton btn_no=new JButton("取 消");
            btn_no.setBounds(250, 200, 100, 40);
            btn_no.setFont(font);
            panel.add(btn_no);

            if("add".equals(type)){
                txt_parent.setText(proxy.getName());
                txt_name.setText("");
            }else if("upd".equals(type)){
                txt_parent.setText(proxy.getParentName());
                txt_name.setText(proxy.getName());
            }
            txt_name.setFocusable(true);
            jFrame.setContentPane(panel);
            jFrame.setVisible(true);

            btn_yes.addActionListener(e -> {
                if("add".equals(type)){
                    Utils.createObj(txt_name.getText(), proxy.getId());
                }else if("upd".equals(type)){
                    JSONObject object = Utils.getObj(proxy.getId());
                    object.put("name", txt_name.getText());
                }
                Utils.write2LocalDB();
                IndexFrame.reload();
                jFrame.setVisible(false);// 本窗口隐藏,
                jFrame.dispose();//本窗口销毁,释放内存资源
                jFrame = null;
                /*JOptionPane.showMessageDialog(null,
                        "输入数字",
                        "错 误",
                        JOptionPane.ERROR_MESSAGE);*/
            });

            btn_no.addActionListener(e -> {
                jFrame.setVisible(false);// 本窗口隐藏,
                jFrame.dispose();//本窗口销毁,释放内存资源
                jFrame = null;
            });
            jFrame.addWindowListener(new WindowListener() {
                @Override
                public void windowOpened(WindowEvent e) {

                }

                @Override
                public void windowClosing(WindowEvent e) {

                }

                @Override
                public void windowClosed(WindowEvent e) {
                    jFrame = null;
                }

                @Override
                public void windowIconified(WindowEvent e) {

                }

                @Override
                public void windowDeiconified(WindowEvent e) {

                }

                @Override
                public void windowActivated(WindowEvent e) {

                }

                @Override
                public void windowDeactivated(WindowEvent e) {

                }
            });
        }else{
            jFrame.setFocusable(true);
        }
    }
}
