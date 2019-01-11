package com.memory.container;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.memory.db.Proxy;
import com.memory.db.Utils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Enumeration;

/**
 * @Auther: cui.Memory
 * @Date: 2018/12/19 0019 8:38
 * @Description:
 */
public class IndexFrame {
    public static DefaultMutableTreeNode root = null;
    public static DefaultMutableTreeNode gongsi = null;
    public static DefaultTreeModel dt = null;
    public static JTree tree = null;
    public static JLabel label_type;
    public static JLabel label_name;
    public static JButton label_xsl;
    public static JButton label_hz;
    public static JLabel label_1;
    public static JLabel label_2;
    public static JButton btn_xs;

    public static void init(){
        JFrame jFrame = new JFrame();

        ImageIcon imageIcon = new ImageIcon("title300.png");
        jFrame.setIconImage(imageIcon.getImage());

        Font font =new Font("微软雅黑", Font.PLAIN, 16);//设置字体
        Font font1 =new Font("微软雅黑", Font.PLAIN, 12);//设置字体
        /**============================================================================*/
        Panel panel = new Panel(new GridLayout(1,3));
        /**============================================================================*/
        Panel panel_name = new Panel(new GridLayout(1, 2, 3,0));
        label_type=new JLabel(Utils.type);
        label_type.setFont(font);

        label_name=new JLabel(Utils.xm);
        label_name.setFont(font);

        panel_name.add(label_type);
        panel_name.add(label_name);
        /**============================================================================*/
        label_xsl=new JButton(Utils.xsl);
        label_xsl.setFont(font1);
        label_xsl.setEnabled(false);

        label_hz=new JButton(Utils.hz);
        label_hz.setFont(font1);
        label_hz.setEnabled(false);

        label_1=new JLabel();
        label_1.setFont(font1);

        label_2=new JLabel();
        label_2.setFont(font1);

        Panel panel_sel = new Panel(new GridLayout(1, 4, 3,0));

        panel_sel.add(label_hz);
        panel_sel.add(label_1);
        panel_sel.add(label_xsl);
        panel_sel.add(label_2);

        /**============================================================================*/
        Panel panel_btn = new Panel(new GridLayout(1, 3, 3,0));

        JComboBox comboBox=new JComboBox();
        for (int i = Utils.getBeginMonth(); i <= Utils.getEndMonth(); i++) {
            comboBox.addItem(i);
        }
        comboBox.setSelectedItem(Utils.getCurrentMonth());
        panel_btn.add(comboBox);

        btn_xs=new JButton("售");
        btn_xs.setFont(font1);
        btn_xs.setEnabled(false);
        panel_btn.add(btn_xs);

        JButton btn_add=new JButton("增");
        btn_add.setFont(font1);
        panel_btn.add(btn_add);

        JButton btn_upd=new JButton("修");
        btn_upd.setFont(font1);
        panel_btn.add(btn_upd);

        JButton btn_del=new JButton("删");
        btn_del.setFont(font1);
        panel_btn.add(btn_del);

        /**============================================================================*/
        Panel panel_row = new Panel(new GridLayout(1, 3, 3,0));
        panel_row.add(panel_name);
        panel_row.add(panel_sel);
        panel_row.add(panel_btn);
        /**============================================================================*/
        panel.add(panel_row);

        //核心
        gongsi = initTree(null, "", Utils.getProxyJSON());

        root = new DefaultMutableTreeNode();
        root.add(gongsi);
        dt = new DefaultTreeModel(root);
        tree = new JTree(dt);
        tree.setFont(font);

        expandAll(tree, new TreePath(root), true);

        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        JScrollPane jScrollPane = new JScrollPane(tree, v, h);

        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setLayout(new BorderLayout());

        jFrame.add(panel, BorderLayout.NORTH);
        jFrame.add(jScrollPane, BorderLayout.CENTER);

        jFrame.setSize(1000, 800);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        tree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree
                    .getLastSelectedPathComponent();
            if(node != null){
                try {
                    Proxy proxy = (Proxy)node.getUserObject();
                    setTitle(proxy.getId(), proxy.getType(), proxy.getName());
                    Utils.setProxy(proxy);
                } catch (Exception e1) {
                }
            }
        });
        comboBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED){
                    Utils.setCurrentMonth((int)comboBox.getSelectedItem());
                    reload();
                }
            }
        });
        label_hz.addActionListener(e -> {
            if(Utils.getProxy()!=null){
                //弹层，添加
                HzFrame.init();
            }
        });
        label_xsl.addActionListener(e -> {
            if(Utils.getProxy()!=null){
                //弹层，添加
                XSLFrame.init();
            }
        });
        btn_xs.addActionListener(e -> {
            if(Utils.getProxy()!=null){
                //弹层，添加
                XsFrame.init();
            }
        });
        btn_add.addActionListener(e -> {
            if(Utils.getProxy()!=null){
                //弹层，添加
                 ModelFrame.init("add", Utils.getProxy());
            }
        });
        btn_upd.addActionListener(e -> {
            if(Utils.getProxy()!=null&&!"".equals(Utils.getProxy().getId())){
                //弹层，修改
                ModelFrame.init("upd", Utils.getProxy());
            }
        });
        btn_del.addActionListener(e -> {
            if(Utils.getProxy()!=null&&!"".equals(Utils.getProxy().getId())&&ModelFrame.jFrame==null){
                int flag = JOptionPane.showConfirmDialog(null,
                        "确认删除<"+Utils.getProxy().getName()+"> ?",
                        "删 除",
                        JOptionPane.YES_NO_OPTION);
                if(flag==0){
                    if(Utils.getProxy().getCount()==0){
                        JSONObject object = Utils.getObj(Utils.getProxy().getId());
                        Utils.getProxyJSON().remove(object);
                        Utils.write2LocalDB();
                        reload();
                    }else{
                        //Toolkit.getDefaultToolkit().beep();
                        JOptionPane.showMessageDialog(null,
                                "请先删除子节点",
                                "错 误",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
    }

    /**
     * 渲染节点核心方法-标记子节点数量
     * @param parentNode
     * @param parent
     * @param array
     * @return
     */
    private static DefaultMutableTreeNode initTree(DefaultMutableTreeNode parentNode, String parent, JSONArray array){
        Proxy top_proxy ;
        if(parentNode == null){
            parentNode = new DefaultMutableTreeNode();
            top_proxy = new Proxy();
            top_proxy.setId("");
            top_proxy.setName("公司管理");
            top_proxy.setParent("");
            top_proxy.setParentName("");
            top_proxy.setCount(0);

            top_proxy.setLevel(0);
            top_proxy.setType("");

            parentNode.setUserObject(top_proxy);
        }else{
            top_proxy = (Proxy)parentNode.getUserObject();
        }
        int parent_count = 0;
        for (int i = 0; i < array.size(); i++) {
            JSONObject obj = array.getJSONObject(i);
            if(parent.equals(obj.getString("parent"))){
                parent_count++;

                DefaultMutableTreeNode node = new DefaultMutableTreeNode();
                //初始化数据
                Proxy proxy = new Proxy();
                proxy.setId(obj.getString("id"));
                proxy.setName(obj.getString("name"));
                proxy.setParent(obj.getString("parent"));
                proxy.setParentName(top_proxy.getName());
                proxy.setCount(0);

                proxy.setLevel(top_proxy.getLevel()+1);
                if("".equals(proxy.getParent())){
                    proxy.setType(proxy.getName());
                }else{
                    proxy.setType(top_proxy.getType());
                }

                node.setUserObject(proxy);
                parentNode.add(node);

                if(Utils.hasNode(obj.getString("id"))){
                    //内置操作节点数据
                    initTree(node, obj.getString("id"), array);
                }
                //统计for循环内操作后的节点数据汇总，提供父节点使用
                parent_count += proxy.getCount();

            }
        }
        //展示父节点
        Proxy proxy = (Proxy) parentNode.getUserObject();
        proxy.setCount(proxy.getCount()+parent_count);
        return parentNode;
    }

    /**
     * 展开，收起节点
     * @param tree
     * @param parent
     * @param expand
     */
    private static void expandAll(JTree tree, TreePath parent, boolean expand) {
        TreeNode node = (TreeNode) parent.getLastPathComponent();
        if (node.getChildCount() >= 0) {
            for (Enumeration e = node.children(); e.hasMoreElements();) {
                TreeNode n = (TreeNode) e.nextElement();
                TreePath path = parent.pathByAddingChild(n);
                expandAll(tree, path, expand);
            }
        }
        if (expand) {
            tree.expandPath(parent);
        } else {
            tree.collapsePath(parent);
        }
    }
    public static void reload(){
        //核心
        root.removeAllChildren();
        gongsi = initTree(null, "", Utils.getProxyJSON());
        root.add(gongsi);
        dt.reload();
        expandAll(tree, new TreePath(root), true);
        Utils.setProxy(null);
        setTitle("", "","");
    }
    public static void setTitle(String id, String type, String name){
        if(!"".equals(name)){
            label_type.setText(Utils.type + "   " + type);
            label_name.setText(Utils.xm + "   " + name);
            label_hz.setEnabled(true);
            if("".equals(id)){
                btn_xs.setEnabled(false);
                label_xsl.setEnabled(false);
            }else{
                btn_xs.setEnabled(true);
                label_xsl.setEnabled(true);
            }
        }else{
            label_type.setText(Utils.type);
            label_name.setText(Utils.xm);
            btn_xs.setEnabled(false);
            label_hz.setEnabled(false);
            label_xsl.setEnabled(false);
        }
    }
}
