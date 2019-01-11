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
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 8:38
 * @Description:
 */
public class HzFrame {
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
            //表格头-前3
            Object[] columnTitle = new Object[Utils.getEndMonth()+3];
            columnTitle[0] = "序号";
            columnTitle[1] = "体系";
            columnTitle[2] = "姓名";
            //表格内容-前3
            initTableData_3_method(0, Utils.getProxy().getType(), Utils.getProxy().getId());

            for (int i = Utils.getBeginMonth(); i <= Utils.getEndMonth(); i++) {
                columnTitle[2+i] = i+"月份";
                initTableData_last_method(i);
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
            jTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            jTable.getColumnModel().getColumn(2).setPreferredWidth(150);
            for (int i = 3; i < jTable.getColumnCount(); i++) {
                jTable.getColumnModel().getColumn(i).setPreferredWidth(400);
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

            JLabel name_label = new JLabel("汇总节点："+Utils.getProxy().getName());
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
    public static void initTableData_3_method(int index, String type, String parentId){
        if(!"".equals(Utils.getProxy().getId())){
            tableData = new Object[Utils.getProxy().getCount()+1][];
            tableData[index] = new Object[Utils.getEndMonth()+5];
            tableData[index][0] = index+1;
            tableData[index][1] = Utils.getProxy().getType();
            tableData[index][2] = Utils.getProxy().getName();
            tableData[index][Utils.getEndMonth()+5-2] = Utils.getProxy().getId();
            tableData[index][Utils.getEndMonth()+5-1] = 0;
            index+=1;
        }else{
            tableData = new Object[Utils.getProxy().getCount()][];
        }
        initTableData_3(index, type, parentId);
    }
    private static int initTableData_3(int index, String type, String parentId) {
        for (int i = 0; i < Utils.getProxyJSON().size(); i++) {
            JSONObject obj = Utils.getProxyJSON().getJSONObject(i);
            if(parentId.equals(obj.getString("parent"))){
                tableData[index] = new Object[Utils.getEndMonth()+5];
                tableData[index][0] = index+1;
                tableData[index][1] = type;
                tableData[index][2] = obj.getString("name");
                tableData[index][Utils.getEndMonth()+5-2] = obj.getString("id");
                tableData[index][Utils.getEndMonth()+5-1] = 0;
                index++;
                if(Utils.hasNode(obj.getString("id"))){
                    //内置操作节点数据
                    index = initTableData_3(index, "".equals(type)?obj.getString("name"):type, obj.getString("id"));
                }
            }
        }
        return index;
    }
    public static void initTableData_last_method(int month){
        for (int i = 0; i < tableData.length; i++) {
            String id = tableData[i][Utils.getEndMonth()+5-2].toString();
            double tempsum = Double.parseDouble(tableData[i][Utils.getEndMonth()+5-1].toString());
            double usesum = Utils.getUseSumByidmonth(id, month);
            String usebz = Utils.getUseBzByidmonth(id, month);

            double zsum = getZSUM(month, id);
            double jsum = getJSUM(month);

            boolean fflag = false;
            fflag = zsum+jsum==0;

            tempsum = tempsum+zsum+jsum-usesum;
            tableData[i][Utils.getEndMonth()+5-1] = tempsum;
            tableData[i][2+month] =((fflag?"-":tempsum))+((usesum>0)?" ----- ["+usesum+"元（"+usebz+"）]":"");
        }
    }
    static List<String> proxyList;
    private static double getZSUM(int month, String parentId){
        proxyList = new ArrayList<String>();
        double zsum = 0.0;
        for (int i = 0; i < Utils.getProxyJSON().size(); i++) {
            JSONObject obj = Utils.getProxyJSON().getJSONObject(i);
            if(parentId.equals(obj.getString("parent"))){
                proxyList.add(obj.getString("id"));
                zsum+= getZSUMone(month, obj.getString("id"));
            }
        }
        return zsum;
    }
    private static double getJSUM(int month){
        double jsum = 0.0;
        for (int j = 0; j < proxyList.size(); j++) {
            String parentId = proxyList.get(j);
            for (int i = 0; i < Utils.getProxyJSON().size(); i++) {
                JSONObject obj = Utils.getProxyJSON().getJSONObject(i);
                if(parentId.equals(obj.getString("parent"))){
                    proxyList.add(obj.getString("id"));
                    jsum+= getJSUMone(month, obj.getString("id"));
                }
            }
        }
        return jsum;
    }
    public static double getZSUMone(int month, String id){
        double zsum = 0.0;
        for (int j = 0; j < Utils.getGoodsJSON().size(); j++) {
            String goodsName = Utils.getGoodsJSON().getJSONObject(j).getString("name");
            double zxs = Utils.getGoodsJSON().getJSONObject(j).getJSONObject("xs").getDouble("z");
            int count = Utils.getCountByidmonthname(id, month, goodsName);
            zsum += count*zxs;
        }
        return zsum;
    }
    public static double getJSUMone(int month, String id){
        double jsum = 0.0;
        for (int j = 0; j < Utils.getGoodsJSON().size(); j++) {
            String goodsName = Utils.getGoodsJSON().getJSONObject(j).getString("name");
            double jxs = Utils.getGoodsJSON().getJSONObject(j).getJSONObject("xs").getDouble("j");
            int count = Utils.getCountByidmonthname(id, month, goodsName);
            jsum += count*jxs;
        }
        return jsum;
    }
}
