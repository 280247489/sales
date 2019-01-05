package com.memory.container;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.memory.db.Utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 8:38
 * @Description:
 */
public class HkFrame {
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

            Object[] columnTitle = {"序 号", "产 品" , "发货数量", "发货数量(编辑列)", "标识"};

            JSONArray jsonArrayGoods = Utils.getJsonArrayGoods();
            tableData = new Object[jsonArrayGoods.size()][];
            JSONObject jsonObjectGoods = Utils.getProxy().getGoods();
            for (int i = 0; i < jsonArrayGoods.size(); i++) {
                String goodsName = jsonArrayGoods.getString(i);
                if(jsonObjectGoods.containsKey(goodsName)){
                    tableData[i] = new Object[]{(i+1), goodsName, jsonObjectGoods.getInteger(goodsName), jsonObjectGoods.getInteger(goodsName), Utils.getProxy().getId()};
                }else{
                    tableData[i] = new Object[]{(i+1), goodsName, 0, 0, Utils.getProxy().getId()};
                }
            }

            JTable jTable = new JTable();
            JTableHeader head = jTable.getTableHeader(); // 创建表格标题对象
            head.setSize(head.getWidth(), 22);// 设置表头大小
            head.setFont(font);// 设置表格字体
            jTable.setFont(font);
            jTable.setRowHeight(22);


            jScrollPane.setViewportView(jTable);

            Panel panel = new Panel(new GridLayout(1, 5, 3,0));
            Font font1 =new Font("微软雅黑", Font.PLAIN, 12);//设置字体
            JButton btn_add=new JButton(Utils.getCurrentMonth() + " 月销售数据-保存");
            btn_add.setFont(font1);
            panel.add(new JLabel());
            JLabel name_label = new JLabel("姓名："+Utils.getProxy().getName());
            name_label.setFont(font1);
            panel.add(name_label);
            panel.add(btn_add);
            panel.add(new JLabel());
            panel.add(new JLabel());

            jFrame.add(panel, BorderLayout.NORTH);
            jFrame.add(jScrollPane, BorderLayout.CENTER);
            //jFrame.pack();
            jFrame.setSize(800, 600);
            jFrame.setLocationRelativeTo(null);
            jFrame.setVisible(true);
            DefaultTableModel newTableModel = new DefaultTableModel(tableData, columnTitle){
                @Override
                public boolean isCellEditable(int row,int column){
                    if(column==3){
                        selectRow = row;
                        selectCol = column;
                        return true;
                    }else{
                        return false;
                    }
                }
            };
            jTable.setModel(newTableModel);

            jTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(300);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(300);
            jTable.getColumnModel().getColumn(3).setPreferredWidth(300);

            jTable.getColumnModel().getColumn(4).setMinWidth(0);
            jTable.getColumnModel().getColumn(4).setMaxWidth(0);

            jTable.addPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    int changeRow = selectRow;
                    System.out.println("changeRow: "+ changeRow);
                    int changeCol = selectCol;
                    if(jTable.getRowCount()!=0){
                        int goodsCount = (int) jTable.getValueAt(changeRow, 2);

                        try {
                            int changeGoodsCount = Integer.parseInt(jTable.getValueAt(changeRow, 3).toString());
                            jTable.setValueAt(changeGoodsCount, changeRow, 3);
                            String id = (String) jTable.getValueAt(changeRow, 4);
                            if(goodsCount!=changeGoodsCount){
                                updJSONObject.put("id", id);
                                updJSONObject.put("month", ""+Utils.getCurrentMonth());
                                updJSONObject.put(Utils.getJsonArrayGoods().getString(changeRow), changeGoodsCount);
                            }
                        } catch (Exception e) {
                            jTable.setValueAt(goodsCount, changeRow, 3);
                            JOptionPane.showMessageDialog(null,
                                    "请输入数字",
                                    "错 误",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            });

            btn_add.addActionListener(e -> {
                int flag_int = JOptionPane.showConfirmDialog(null,
                        "确认保存所有编辑的内容么?",
                        "提 交",
                        JOptionPane.YES_NO_OPTION);
                if(flag_int==0){

                    String month = updJSONObject.getString("month");
                    JSONObject jsonObject = Utils.getObj(updJSONObject.getString("id"));
                    JSONObject monthGoods;
                    if(jsonObject.containsKey("monthGoods")){
                        monthGoods = jsonObject.getJSONObject("monthGoods");
                    }else{
                        monthGoods = new JSONObject();
                    }

                    JSONObject monthGoods_month;
                    if(monthGoods.containsKey(month)){
                        monthGoods_month = monthGoods.getJSONObject(month);
                    }else{
                        monthGoods_month = new JSONObject();
                    }

                    JSONArray jsonArrayGoods1 = Utils.getJsonArrayGoods();
                    for (int i = 0; i < jsonArrayGoods1.size(); i++) {
                        String goodsName = jsonArrayGoods1.getString(i);
                        if(updJSONObject.containsKey(goodsName)){
                            monthGoods_month.put(goodsName, updJSONObject.getInteger(goodsName));
                        }
                    }
                    monthGoods.put(month, monthGoods_month);

                    Utils.write2LocalDB();
                    Utils.setProxy(null);
                    IndexFrame.reload();
                    jFrame.setVisible(false);// 本窗口隐藏,
                    jFrame.dispose();//本窗口销毁,释放内存资源
                    jFrame = null;
                }

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

    private static int initTableData(int index, Object[][] tableData, String parentId, JSONArray array) {
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if(parentId.equals(obj.getString("parent"))){
                //初始化数据
                double money = 0.0;
                if(obj.containsKey("monthMoney")){
                    JSONObject monthMoneyObj = obj.getJSONObject("monthMoney");
                    if(monthMoneyObj.containsKey(""+Utils.getCurrentMonth())){
                        money = monthMoneyObj.getDouble(""+Utils.getCurrentMonth());
                    }
                }
                tableData[index] = new Object[]{(index+1), obj.getString("name"), money, money, obj.getString("id")};
                index++;
                if(Utils.hasNode(obj.getString("id"))){
                    //内置操作节点数据
                    index = initTableData(index, tableData, obj.getString("id"), array);
                }
            }
        }
        return index;
    }
}
