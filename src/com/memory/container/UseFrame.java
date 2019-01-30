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
public class UseFrame {
    public static JFrame jFrame = null;
    public static void init(int month, String id){
        if(jFrame==null){
            Font font =new Font("微软雅黑", Font.PLAIN, 16);//设置字体
            jFrame = new JFrame("转货款设置");

            ImageIcon imageIcon = new ImageIcon("title300.png");
            jFrame.setIconImage(imageIcon.getImage());

            jFrame.setResizable(false);
            jFrame.setSize(400, 300);
            jFrame.setLocationRelativeTo(null);
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel();
            panel.setLayout(null);

            JLabel label_parent=new JLabel("款 项: ");
            label_parent.setBounds(20, 40, 60, 40);
            label_parent.setFont(font);
            panel.add(label_parent);

            JTextField txt_parent=new JTextField();
            txt_parent.setBounds(100, 40, 250, 40);
            txt_parent.setFont(font);
            panel.add(txt_parent);

            JLabel label_name=new JLabel("备 注: ");
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

            double usesum = Utils.getUseSumByidmonth(id, month);
            String usebz = Utils.getUseBzByidmonth(id, month);

            System.out.println("usesum:"+ usesum);
            txt_parent.setText((usesum==0?"":""+usesum));
            txt_name.setText(usebz);

            txt_name.setFocusable(true);
            jFrame.setContentPane(panel);
            jFrame.setVisible(true);

            btn_yes.addActionListener(e -> {
                try {
                    double useSum = Double.parseDouble(txt_parent.getText());
                    if(useSum>=0){
                        String useBz = txt_name.getText();

                        JSONObject jsonObject = Utils.getUseJSON().getJSONObject(id);
                        if(jsonObject==null){
                            jsonObject = new JSONObject();
                        }
                        JSONObject object = new JSONObject();
                        object.put("sum", useSum);
                        if(useSum==0){
                            useBz = "";
                        }
                        object.put("bz", useBz);

                        jsonObject.put(""+month, object);

                        Utils.getUseJSON().put(id, jsonObject);

                        Utils.write2UseDB();
                        HzFrame.reload();
                        jFrame.setVisible(false);// 本窗口隐藏,
                        jFrame.dispose();//本窗口销毁,释放内存资源
                        jFrame = null;
                    }else{
                        JOptionPane.showMessageDialog(null,
                                "金额不能为负数",
                                "错 误",
                                JOptionPane.ERROR_MESSAGE);
                    }

                } catch (NumberFormatException e1) {
                    JOptionPane.showMessageDialog(null,
                            "金额请输入数字",
                            "错 误",
                            JOptionPane.ERROR_MESSAGE);
                }
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
