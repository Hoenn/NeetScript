import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
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
import java.util.ArrayList;

import javax.swing.JOptionPane;

public class Toolhou extends Frame {

	// constants for menu shortcuts
	private static final int kControlA = 65;
	private static final int kControlD = 68;
	private static final int kControlC = 67;
	private static final int kControlR = 82;
	private static final int kControlP = 80;
	private static final int kControlT = 84;
	private static final int kControlX = 88;

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
		Menu shape = new Menu("Shapes");
		Menu about = new Menu("About");
		// now add menu items to these Menu objects
		file.add(new MenuItem("Exit", new MenuShortcut(kControlX))).addActionListener(new WindowHandler());

		shape.add(new MenuItem("Rectangle", new MenuShortcut(kControlR))).addActionListener(new WindowHandler());
		shape.add(new MenuItem("Circle", new MenuShortcut(kControlC))).addActionListener(new WindowHandler());
		shape.add(new MenuItem("Triangle", new MenuShortcut(kControlT))).addActionListener(new WindowHandler());
		shape.add(new MenuItem("Polygon", new MenuShortcut(kControlP))).addActionListener(new WindowHandler());
		shape.add(new MenuItem("Draw Polygon", new MenuShortcut(kControlD))).addActionListener(new WindowHandler());

		about.add(new MenuItem("About", new MenuShortcut(kControlA))).addActionListener(new WindowHandler());
		// add menus to menubar
		menuBar.add(file);
		menuBar.add(shape);
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
			} else if (e.getActionCommand().equalsIgnoreCase("Rectangle")) {
				clearMenuSelection();
				getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlR)).setEnabled(false);

			} else if (e.getActionCommand().equalsIgnoreCase("Circle")) {
				clearMenuSelection();
				getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlC)).setEnabled(false);

			} else if (e.getActionCommand().equalsIgnoreCase("Triangle")) {
				clearMenuSelection();
				getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlT)).setEnabled(false);

			} else if (e.getActionCommand().equalsIgnoreCase("Polygon")) {
				clearMenuSelection();
				getMenuBar().getShortcutMenuItem(new MenuShortcut(kControlP)).setEnabled(false);

			} else if (e.getActionCommand().equalsIgnoreCase("About")) {
				JOptionPane.showMessageDialog(null, "A tool for making touhou scripts", "About",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	class DrawingPanel extends Panel implements MouseListener {

		private Point sPoint = null;
		private Point ePoint = null;
		private ArrayList<Point> list = new ArrayList<Point>();

		// override panel paint method to draw shapes
		public void paint(Graphics g) {
			g.setColor(Color.green);
			// shape.draw(list, g);
			for(Point p: list)
			{
				g.drawRect(p.x-5, p.y-5, 10, 10);
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
			repaint();
		}// mouseClicked

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}

		public void mousePressed(MouseEvent e) {
			sPoint = e.getPoint();
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
