package com.memory.container;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.memory.db.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 8:38
 * @Description:
 */
public class XSLFrame {
    public static JFrame jFrame = null;
    public static Object[][] tableData;
    public static int selectRow;
    public static int selectCol;
    public static JSONObject updJSONObject = new JSONObject();
    public static void init(){
        if(jFrame==null){
            jFrame = new JFrame();

            ImageIcon imageIcon = new ImageIcon("title300.png");
            jFrame.setIconImage(imageIcon.getImage());

            Font font =new Font("微软雅黑", Font.PLAIN, 16);//设置字体

            JScrollPane jScrollPane = new JScrollPane();
            jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            jFrame.setLayout(new BorderLayout());

            //Object[] columnTitle = {"月份", "产 品" , "发货数量", "发货数量(编辑列)", "标识"};

            JSONArray goodsJSON = Utils.getGoodsJSON();
            Object[] columnTitle = new Object[goodsJSON.size()+3];
            columnTitle[0] = "月份";
            columnTitle[1] = "直接";
            columnTitle[2] = "间接";
            for (int i = 0; i < goodsJSON.size(); i++) {
                columnTitle[i+3] = goodsJSON.getJSONObject(i).getString("name");
            }

            tableData = new Object[Utils.getEndMonth()][];
            int index = 0;
            for (int i = Utils.getEndMonth(); i >= Utils.getBeginMonth(); i--) {
                tableData[index] = new Object[goodsJSON.size()+3];
                tableData[index][0] = i;
                double zsum = 0.0;
                double jsum = 0.0;
                for (int j = 0; j < goodsJSON.size(); j++) {
                    String goodsName = goodsJSON.getJSONObject(j).getString("name");
                    double zxs = goodsJSON.getJSONObject(j).getJSONObject("xs").getDouble("z");
                    double jxs = goodsJSON.getJSONObject(j).getJSONObject("xs").getDouble("j");
                    int count = Utils.getCountByidmonthname(Utils.getProxy().getId(), i, goodsName);
                    tableData[index][j+3] = count==0?"-":count;
                    zsum += count*zxs;
                    jsum += count*jxs;
                }
                if(Utils.getProxy().getLevel()>=3){
                    tableData[index][1] = zsum;
                    tableData[index][2] = jsum;
                }else if(Utils.getProxy().getLevel()==2){
                    tableData[index][1] = zsum;
                    tableData[index][2] = "-";
                }else if(Utils.getProxy().getLevel()==1){
                    tableData[index][1] = "-";
                    tableData[index][2] = "-";
                }else{
                    tableData[index][1] = "-";
                    tableData[index][2] = "-";
                }
                index++;
            }

            JTable jTable = new JTable();
            JTableHeader head = jTable.getTableHeader(); // 创建表格标题对象
            head.setSize(head.getWidth(), 22);// 设置表头大小
            head.setFont(font);// 设置表格字体
            jTable.setFont(font);
            jTable.setRowHeight(22);

            DefaultTableModel newTableModel = new DefaultTableModel(tableData, columnTitle){
                @Override
                public boolean isCellEditable(int row,int column){
                    return false;
                }
            };
            jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            jTable.setModel(newTableModel);
            jTable.getColumnModel().getColumn(0).setPreferredWidth(50);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            for (int i = 3; i < jTable.getColumnCount(); i++) {
                jTable.getColumnModel().getColumn(i).setPreferredWidth(80);
            }
            //jTable.getTableHeader().setReorderingAllowed(false);   //不可整列移动
            jTable.getTableHeader().setResizingAllowed(false);   //不可拉动表格
            DefaultTableCellRenderer tcr = new DefaultTableCellRenderer();// 设置table内容居中
            tcr.setHorizontalAlignment(SwingConstants.CENTER);// 这句和上句作用一样
            jTable.setDefaultRenderer(Object.class, tcr);

            jScrollPane.setViewportView(jTable);

            Panel panel = new Panel(new GridLayout(1, 5, 3,0));
            Font font1 =new Font("微软雅黑", Font.PLAIN, 16);//设置字体

            panel.add(new JLabel());

            JLabel type_label = new JLabel("体系："+Utils.getProxy().getType());
            type_label.setFont(font1);
            panel.add(type_label);

            JLabel name_label = new JLabel("姓名："+Utils.getProxy().getName());
            name_label.setFont(font1);
            panel.add(name_label);

            panel.add(new JLabel());

            jFrame.add(panel, BorderLayout.NORTH);
            jFrame.add(jScrollPane, BorderLayout.CENTER);
            jFrame.setUndecorated(false);
            /*Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            jFrame.setSize(d.width, d.height);*/
            jFrame.setSize(800, 400);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);



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
