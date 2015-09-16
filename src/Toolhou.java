import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
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

	private DrawingPanel panel;

	public Toolhou() {
		super("Touhou Scripting Tool");
		addMenu();
		addPanel();
		this.addWindowListener(new WindowHandler());
		// set frame size
		this.setSize(400, 400);
		// make this frame visible
		this.setVisible(true);
	}

	public static void main(String args[]) {
		Toolhou toolhou = new Toolhou();
	}
	
	private void addMenu() {
		// Add menu bar to our frame
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu about = new Menu("About");
		// now add menu items to these Menu objects
		file.add(new MenuItem("Exit", new MenuShortcut(kControlQ))).addActionListener(new WindowHandler());

		edit.add(new MenuItem("Undo", new MenuShortcut(kControlZ))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Redo", new MenuShortcut(kControlY))).addActionListener(new WindowHandler());


		about.add(new MenuItem("About", new MenuShortcut(kControlA))).addActionListener(new WindowHandler());
		// add menus to menubar
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(about);
		// menuBar.setVisible(true);
		if (null == this.getMenuBar()) {
			this.setMenuBar(menuBar);
		}
	}// addMenu()

	/**
	 * This method adds a panel to SimpleDrawingTool for drawing shapes.
	 */
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
	}// end of addPanel();
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
	private class WindowHandler extends WindowAdapter implements ActionListener {
		public void windowClosing(WindowEvent e) {
			System.exit(0);
		}

		private void clearMenuSelection() {
			Menu menu = getMenuBar().getMenu(1);
			for (int i = 0; i < menu.getItemCount(); i++)
				menu.getItem(i).setEnabled(true);
		}

		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equalsIgnoreCase("exit")) {
				System.exit(0);
			} else if (e.getActionCommand().equalsIgnoreCase("Undo")) {
				//clearMenuSelection();
				//getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlR)).setEnabled(false);
				undo();

			} else if (e.getActionCommand().equalsIgnoreCase("Redo")) {
				//clearMenuSelection();
				//getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlC)).setEnabled(false);
				redo();

			} else if (e.getActionCommand().equalsIgnoreCase("About")) {
				JOptionPane.showMessageDialog(null, "A tool for making touhou scripts", "About",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	class DrawingPanel extends Panel implements MouseListener {

		private Stack<Point> pointStack = new Stack<Point>();
		private Stack<Point> redoStack = new Stack<Point>();
		public ArrayList<Point> list = new ArrayList<Point>();

		// override panel paint method to draw shapes
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D)g;
			g.setColor(Color.green);
			// shape.draw(list, g);
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

		public void drawShape() {
			// this.shape = shape;
		}

		// define mouse handler
		public void mouseClicked(MouseEvent e) {
			// //if user wants to draw triangle, call repaint after 3 clicks
			// if(shape instanceof TriangleShape)
			// {
			// list.add(e.getPoint());
			// if(list.size() > 2)
			// {
			// repaint();
			// }
			// }
			// else if(shape instanceof PolygonShape)
			// {
			// list.add(e.getPoint());
			// }
			list.add(e.getPoint());
			pointStack.add(e.getPoint());
			if(redoStack.size()>0)
				redoStack.clear();
			repaint();
		}// mouseClicked

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
		}// mousePressed

		public void mouseReleased(MouseEvent e) {
			// ePoint = e.getPoint();
			// if(ePoint.getX() < sPoint.getX())
			// {
			// Point temp = ePoint;
			// ePoint = sPoint;
			// sPoint = temp;
			// }
			// if(ePoint.getY() < sPoint.getY())
			// {
			// int temp = (int)ePoint.getY();
			// ePoint.y = (int)sPoint.getY();
			// sPoint.y = temp;
			// }
			// if(shape instanceof RectangleShape || shape instanceof OvalShape)
			// {
			// list.clear();
			// list.add(sPoint);
			// list.add(ePoint);
			// repaint();
		}
	}// mouseReleased
}// DrawingPanel
