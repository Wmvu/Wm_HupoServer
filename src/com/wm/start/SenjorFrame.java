package com.wm.start;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import utils.Encdoutils;

public class SenjorFrame extends JFrame{
	/**
	 * this is second window also send and receive user data of place
	 * 此窗口接收调试连接对象
	 */
private static final long serialVersionUID = -7439086377374228045L;
ServerWindow aoser;
JButton spend;
JTextField cd;
JPanel jp;

JList<String> table ;
JPanel panel;
JScrollPane scrollPane;
private  JPopupMenu jpmenu;
private  JMenuItem menuitem;
public SenjorFrame(int x,int y,ServerWindow aoser) {
		this.aoser = aoser;
		this.setSize(300, 400);
		this.setLocation(x, y);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		//以上初始化窗口基本操作
		this.panel = new JPanel();
		this.scrollPane = new JScrollPane();
		this.scrollPane.setPreferredSize(new Dimension(200,295));
		//初始化面板
		try {
			ini();
		} catch (AWTException e) {
			e.printStackTrace();
		}
		build();//监视器添加
		
		this.setVisible(true);//给用户一次性展现
		
	}
	private void ini() throws AWTException {
		jp = new JPanel(null);
		spend = new JButton("send");
		cd = new JTextField();
		table = new JList<String>(this.aoser.datachat);//绑定列表的数据结构，需要一个数据结构，直接在DataChat里添加就能对列表项进行修改
		jpmenu = new JPopupMenu();//菜单
		menuitem = new JMenuItem("删除");
		new Robot();
		//组件创建
		scrollPane.setViewportView(table);
		spend.setBounds(150, 320, 80, 30);
		cd.setBounds(10, 320, 130, 23);
		panel.setBounds(10, 10, 200, 300);
		jpmenu.add(menuitem);
		//布局调整
		panel.add(scrollPane);
		jp.add(cd);
		jp.add(spend);
		this.getContentPane().add(panel);
		this.getContentPane().add(jp);
		this.setTitle(System.getProperty("user.dir"));//获得当前路径
		table.add(jpmenu);
	}
	private void build() {
		spend.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<Excagent> v = aoser.onlineList;
				aoser.datachat.addElement(Encdoutils.doenc(cd.getText()));
					Excagent satemp = (Excagent) v.get(0);
					satemp.sendMessageData(Encdoutils.doenc(cd.getText()));
				cd.setText("");
			}
		});
		
		table.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
//				System.out.println(table.getSelectedIndex());
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == 3 && table.getSelectedIndex()>0) {
					jpmenu.show(table, e.getX(), e.getY());
				}
			}
		});
		
	menuitem.addActionListener(new ActionListener() {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			int selectedlist[] = table.getSelectedIndices();
			for (int i = selectedlist.length-1; i >= 0; i--) {
//				System.out.println(aoser.datachat.getElementAt(i));
				aoser.datachat.removeElementAt(selectedlist[i]);
				
			}
			
		}
	});	
	}
}
