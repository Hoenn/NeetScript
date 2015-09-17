import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Panel;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Stack;

import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Toolhou extends Frame {

	// constants for menu shortcuts
	private static final int kControlA = 65;
	private static final int kControlC = 67;
	private static final int kControlD = 68;
	private static final int kControlP = 80;
	private static final int kControlQ = 81;
	private static final int kControlR = 82;
	private static final int kControlT = 84;
	private static final int kControlX = 88;
	private static final int kControlY = 89;
	private static final int kControlZ = 90;
	
	private static String[] resolutions = {
		"4:3 - 640x480",
		"4:3 - 1024x768",
		"4:3 - 1280x960",
		"3:2 - 480x320",
		"3:2 - 960x640",
		"16:10 - 800x480",
		"16:10 - 1280x800",
		"17:10 - 1024x600",
		"16:9 - 640x360",
		"16:9 - 854x480",
		"16:9 - 1136x640",
		"16:9 - 1920x1080"
	};
	
	private DrawingPanel panel;
	private Toolhou mainWindow;
	public Toolhou() {
		super("Touhou Scripting Tool");
		addMenu();
		addPanel();
		this.addWindowListener(new WindowHandler());
		this.setSize(640, 480);
		this.setVisible(true);
		this.setResizable(false);
		mainWindow=this;
	}

	public static void main(String args[]) {
		new Toolhou();
	}

	private void addMenu() {
		// Add menu bar to our frame
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu window = new Menu("Window");
		Menu about = new Menu("About");
		// now add menu items to these Menu objects
		file.add(new MenuItem("Exit", new MenuShortcut(kControlQ))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Undo", new MenuShortcut(kControlZ))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Redo", new MenuShortcut(kControlY))).addActionListener(new WindowHandler());
		for(int i=0; i<resolutions.length; i++)
		{
			window.add(new MenuItem(resolutions[i])).addActionListener(new WindowHandler());
		}
		//The default window size is at position 0 of the list
		window.getItem(0).setEnabled(false);	
		about.add(new MenuItem("About")).addActionListener(new WindowHandler());
		
		// add menus to menubar
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(window);
		menuBar.add(about);
		if (null == this.getMenuBar()) {
			this.setMenuBar(menuBar);
		}
	}


	private void addPanel() {
		panel = new DrawingPanel();
		// get size of SimpleDrawingTool frame
		Dimension d = this.getSize();
		// get insets of frame
		Insets ins = this.insets();
		// exclude insets from the size of the panel
		d.height = d.height - ins.top - ins.bottom;
		d.width = d.width - ins.left - ins.right;
		panel.setSize(d);
		panel.setLocation(ins.left, ins.top);
		panel.setBackground(Color.white);
		// add mouse listener. Panel itself will be handling mouse events
		panel.addMouseListener(panel);
		this.add(panel);
	}
	private void undo()
	{
		if(panel.list.size()>0)
		{
			panel.redoStack.push(panel.pointStack.pop());
			panel.list.remove(panel.list.size()-1);
			panel.repaint();
		}
	}
	private void redo()
	{
		if(panel.redoStack.size()>0)
		{
			panel.pointStack.push(panel.redoStack.pop());
			panel.list.add(panel.pointStack.peek());
			panel.repaint();
		}
	}
	private int handleResize(ActionEvent e)
	{
		String targetSize = e.getActionCommand().toString();
		int resPos=-1;
		for(int i=0; i<resolutions.length; i++)
		{
			if(resolutions[i].equals(targetSize))
			{
				resPos = i;
				i=resolutions.length;
			}
		}
		int width = Integer.parseInt(targetSize.substring(targetSize.indexOf("-")+1, targetSize.indexOf("x")).trim());
		int height = Integer.parseInt(targetSize.substring(targetSize.indexOf("x")+1).trim());
		mainWindow.resize(width, height);
		
		return resPos;
	}	
	private class WindowHandler extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		private void clearMenuSelection(int menuNum) {
			Menu menu = getMenuBar().getMenu(menuNum);
			for (int i = 0; i < menu.getItemCount(); i++)
				menu.getItem(i).setEnabled(true);
		}

		public void actionPerformed(ActionEvent e) {
			//Allows access to the name of the Menu form which item was chosen
			Menu menu = (Menu)((MenuItem)e.getSource()).getParent();
		
			if (e.getActionCommand().equalsIgnoreCase("exit")) {
				System.exit(0);
			} else if (e.getActionCommand().equalsIgnoreCase("Undo")) {
				undo();
			} else if (e.getActionCommand().equalsIgnoreCase("Redo")) {
				redo();
			} 
			else if(menu.getLabel().equals("Window"))
			{
				int resPos= handleResize(e);
				
				clearMenuSelection(2);
				menu.getItem(resPos).setEnabled(false);
			}
			else if (e.getActionCommand().equalsIgnoreCase("About")) {
				JOptionPane.showMessageDialog(null, "A tool for making touhou scripts", "Info",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

public class DrawingPanel extends Panel implements MouseListener {

		private Stack<Point> pointStack = new Stack<Point>();
		private Stack<Point> redoStack = new Stack<Point>();
		public ArrayList<Point> list = new ArrayList<Point>();

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g.setColor(Color.blue);
			
			for(int i =0; i < list.size(); i++)
			{	
				Point currPoint = list.get(i);
				g.drawRect(currPoint.x-5, currPoint.y-5, 10, 10);
				if(i>0)
				{
					Point prevPoint = list.get(i-1);
					g2.draw(new Line2D.Double(prevPoint, currPoint));
				}
			}
		}

		public void mouseClicked(MouseEvent e) {
			list.add(e.getPoint());
			pointStack.add(e.getPoint());
			if(redoStack.size()>0)
				redoStack.clear();

			repaint();
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {

		}

		public void mouseReleased(MouseEvent e) {
		}
	}
}
