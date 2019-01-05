package com.memory.container;


import com.memory.db.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/18 0018 14:41
 * @Description:
 */
public class LoginFrame {
    public static void init(){
        Font font =new Font("微软雅黑", Font.PLAIN, 16);//设置字体
        JFrame jFrame = new JFrame("代理加密系统");

        ImageIcon imageIcon = new ImageIcon("title300.png");
        jFrame.setIconImage(imageIcon.getImage());

        jFrame.setResizable(false);
        jFrame.setSize(400, 300);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(null);


        JLabel label_loginname=new JLabel("账 号: ");
        label_loginname.setBounds(20, 60, 60, 40);
        label_loginname.setFont(font);
        panel.add(label_loginname);

        JTextField txt_loginname=new JTextField();
        txt_loginname.setBounds(100, 60, 250, 40);
        txt_loginname.setFont(font);
        panel.add(txt_loginname);


        JLabel label_password=new JLabel("密 码: ");
        label_password.setBounds(20, 130, 60, 40);
        label_password.setFont(font);
        panel.add(label_password);

        JPasswordField txt_pasword=new JPasswordField();
        txt_pasword.setBounds(100, 130, 250, 40);
        txt_pasword.setFont(font);
        panel.add(txt_pasword);


        JButton btn_login=new JButton("登 陆");
        btn_login.setBounds(100, 200, 100, 40);
        btn_login.setFont(font);
        panel.add(btn_login);


        JButton btn_exit=new JButton("退 出");
        btn_exit.setBounds(250, 200, 100, 40);
        btn_exit.setFont(font);
        panel.add(btn_exit);

        btn_login.addActionListener(e -> {
            login(jFrame, txt_loginname, txt_pasword);
        });

        btn_exit.addActionListener(e -> {
            System.exit(0);
        });
        KeyListener keyListener = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()==10){
                    login(jFrame, txt_loginname, txt_pasword);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        };

        txt_loginname.addKeyListener(keyListener);
        txt_pasword.addKeyListener(keyListener);

        jFrame.setContentPane(panel);
        jFrame.setVisible(true);
    }

    private static void login(JFrame jFrame, JTextField txt_loginname, JPasswordField txt_pasword) {
        String loginname = txt_loginname.getText();
        String password = new String(txt_pasword.getPassword());
        if(Utils.loginname.equals(loginname)&&Utils.password.equals(password)){
            jFrame.setVisible(false);// 本窗口隐藏,
            IndexFrame.init();
            jFrame.dispose();//本窗口销毁,释放内存资源
        }else{
            JOptionPane.showMessageDialog(null,
                    "账号或密码错误，请重新输入",
                    "提示框",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
