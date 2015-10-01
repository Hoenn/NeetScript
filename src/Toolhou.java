import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Stack;

import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Toolhou extends JFrame {

	private static final long serialVersionUID = 1L;
	// constants for menu shortcuts
	private static final int kControlG = 71;
	private static final int kControlO = 79;
	private static final int kControlS = 83;
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
	private JFileChooser fileChooser;
	private JColorChooser colorChooser;
	private JSlider gridSizeSlider;
	private JTextField gridSizeTextField;
	private File currentFile;


	public Toolhou() 
	{
		super("NeetScript");
		addMenu();
		addPanel();
		this.addWindowListener(new WindowHandler());
		this.setSize(640, 480);
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		mainWindow=this;
		
		colorChooser= new JColorChooser();
		colorChooser.setPreviewPanel(new JPanel());
		colorChooser.setColor(panel.gridColor);
		
		fileChooser = new JFileChooser();
		FileFilter filter = new FileNameExtensionFilter("Waypoint file", "way");
		fileChooser.setFileFilter(filter);
		
		gridSizeSlider = new JSlider(1, 150, panel.gridSize);
		gridSizeSlider.setMinorTickSpacing(10);
		gridSizeSlider.setMajorTickSpacing(50);
		gridSizeSlider.setPaintTicks(true);
		gridSizeSlider.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e) {
		        JSlider source = (JSlider)e.getSource();
		        //This Line will keep grid from changing until slider is released
		        //if (!source.getValueIsAdjusting()) {
		            panel.gridSize = (int)source.getValue();
		            gridSizeTextField.setText(panel.gridSize+" px");
		            panel.repaint();      
		    }
		});
		gridSizeTextField = new JTextField();
		gridSizeTextField.setText(panel.gridSize+" px");
		gridSizeTextField.setEditable(false);
		gridSizeTextField.setHorizontalAlignment(JTextField.CENTER);
	}

	public static void main(String args[]) 
	{
		new Toolhou();
	}

	private void addMenu() 
	{
		// Add menu bar to our frame
		MenuBar menuBar = new MenuBar();
		Menu file = new Menu("File");
		Menu edit = new Menu("Edit");
		Menu window = new Menu("Window");
		// now add menu items to these Menu objects
		file.add(new MenuItem("Open", new MenuShortcut(kControlO))).addActionListener(new WindowHandler());
		file.add(new MenuItem("Save", new MenuShortcut(kControlS))).addActionListener(new WindowHandler());
		file.add(new MenuItem("Save as")).addActionListener(new WindowHandler());
		file.add(new MenuItem("About")).addActionListener(new WindowHandler());
		file.add(new MenuItem("Exit")).addActionListener(new WindowHandler());
		
		edit.add(new MenuItem("Undo", new MenuShortcut(kControlZ))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Redo", new MenuShortcut(kControlY))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Toggle Grid", new MenuShortcut(kControlG))).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Grid Color")).addActionListener(new WindowHandler());
		edit.add(new MenuItem("Grid Size")).addActionListener(new WindowHandler());
		for(int i=0; i<resolutions.length; i++)
		{
			window.add(new MenuItem(resolutions[i])).addActionListener(new WindowHandler());
		}
		//The default window size is at position 0 of the list
		window.getItem(0).setEnabled(false);	
		
		// add menus to menubar
		menuBar.add(file);
		menuBar.add(edit);
		menuBar.add(window);
		if (null == this.getMenuBar()) {
			this.setMenuBar(menuBar);
		}
	}

	private void addPanel() 
	{
		panel = new DrawingPanel();
		// get size of SimpleDrawingTool frame
		Dimension d = this.getSize();
		// get insets of frame
		Insets ins = this.getInsets();
		// exclude insets from the size of the panel
		d.height = d.height - ins.top - ins.bottom;
		d.width = d.width - ins.left - ins.right;
		panel.setSize(d);
		panel.setLocation(ins.left, ins.top);
		panel.setBackground(Color.white);
		// add mouse listener. Panel itself will be handling mouse events
		panel.addMouseListener(panel);
		panel.addMouseMotionListener(panel);
		this.add(panel);
	}
	private void undo()
	{
		if(!panel.dragging&&panel.stateStack.size()>0)
		{

			panel.redoStateStack.push(panel.stateStack.pop());
			if(!panel.stateStack.isEmpty())
				panel.list = panel.getShallowList(panel.stateStack.peek());
			else
				panel.list.clear();
			panel.repaint();
		}
	}
	private void redo()
	{
		if(!panel.dragging&&panel.redoStateStack.size()>0)
		{
			panel.stateStack.push(panel.redoStateStack.pop());
			panel.list = panel.getShallowList(panel.stateStack.peek());
			panel.repaint();
		}
	}
	private void toggleGrid()
	{
		panel.grid=!panel.grid;
		panel.repaint();
	}
	private void changeGridColor()
	{
		JDialog d = JColorChooser.createDialog(null,"Grid Color",true,colorChooser,null,null); 
	    d.setVisible(true);
	    
		panel.gridColor = colorChooser.getColor();
		
		panel.repaint();
	}
	private void changeGridSize()
	{
		
		JDialog d = new JDialog(new JFrame(),"Grid Size");
		d.setBounds(0, 0, 300, 100);
		d.add(gridSizeSlider, BorderLayout.NORTH);
		d.add(gridSizeTextField, BorderLayout.SOUTH);
		d.setVisible(true);
	}
	private int handleWindowResize(ActionEvent e)
	{
		String targetSize = e.getActionCommand().toString();
		int resPos=-1;
		int i = 0;
		while(resPos<0)
		{
			if(resolutions[i].equals(targetSize))
				resPos=i;
			else
				i++;
		}
			
		int width = Integer.parseInt(targetSize.substring(targetSize.indexOf("-")+1, targetSize.indexOf("x")).trim());
		int height = Integer.parseInt(targetSize.substring(targetSize.indexOf("x")+1).trim());
		mainWindow.setSize(width, height);
		
		return resPos;
	}	
	private String getPointListFormatted()
	{
		String formatted="";
		for(Point p: panel.list)
		{
			formatted+="["+p.x+","+p.y+"]";
		}
		
		return formatted;
	}
	private class WindowHandler extends WindowAdapter implements ActionListener {
		private final String QUIT_MESSAGE= "You may have unsaved work. "+
				"Save before quit?";
		
		public void windowClosing(WindowEvent e) 
		{
			quitWithPrompt();
		}
		private void quitWithPrompt()
		{
			String buttonLabels[] = {"Save", "Don't Save", "Cancel"};
	        int choice= JOptionPane.showOptionDialog(null, QUIT_MESSAGE, "Exit", JOptionPane.DEFAULT_OPTION,
	        			JOptionPane.WARNING_MESSAGE, null, buttonLabels, buttonLabels[1]);
	        if(choice==0)
	        {
				if(fileChooser.showSaveDialog(Toolhou.this) == JFileChooser.APPROVE_OPTION)
				{
					currentFile = fileChooser.getSelectedFile();
					saveAs(currentFile);
					System.exit(0);
				}	     
	        }
	        else if(choice==1)
	        {
	        	System.exit(0);
	        }
	       
	    }
		private void clearMenuSelection(int menuNum) 
		{
			//Sets all menu items to enabled
			Menu menu = getMenuBar().getMenu(menuNum);
			for (int i = 0; i < menu.getItemCount(); i++)
				menu.getItem(i).setEnabled(true);
		}
		private void openFile(File f)
		{
			panel.stateStack.clear();
			panel.redoStateStack.clear();
			ArrayList<Point> listFromFile = new ArrayList<Point>();
			
			String path = f.getAbsolutePath();
			if(!path.contains(".way"))
				path+=".way";
			
			String content=null;
			try
			{
				content = new String(Files.readAllBytes(Paths.get(path)));
			} catch (IOException e)
			{
				e.printStackTrace();
			}
			while(content.contains("]"))
			{
				int start = content.indexOf("[");
				int comma = content.indexOf(",");
				int end = content.indexOf("]");
				int x = Integer.parseInt(content.substring(start+1, comma));
				int y= Integer.parseInt(content.substring(comma+1, end));
				listFromFile.add(new Point(x, y));
				content = content.substring(end+1);
			}
			panel.list = listFromFile;
			panel.stateStack.push(panel.getShallowList(listFromFile));
			panel.repaint();
			
		}
		private void saveAs(File f)
		{
			String path = f.getAbsolutePath();
			if(!path.contains(".way"))
				path+=".way";
			
			try (Writer writer = new BufferedWriter(new OutputStreamWriter(
									new FileOutputStream(path), "utf-8"))) 
			{
				writer.write(getPointListFormatted());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		public void actionPerformed(ActionEvent e) 
		{
			//Allows access to the name of the Menu form which item was chosen
			Menu menu = (Menu)((MenuItem)e.getSource()).getParent();
			if(e.getActionCommand().equalsIgnoreCase("Open"))
			{
				int returnVal = fileChooser.showOpenDialog(Toolhou.this);
				if(returnVal == JFileChooser.APPROVE_OPTION)
				{
					currentFile = fileChooser.getSelectedFile();
					openFile(currentFile);
				}
			}
			else if(e.getActionCommand().equalsIgnoreCase("Save"))
			{
			
				if(currentFile!=null)
					saveAs(currentFile);
				else
				{
					if(fileChooser.showSaveDialog(Toolhou.this) == JFileChooser.APPROVE_OPTION)
					{
						currentFile = fileChooser.getSelectedFile();
						saveAs(currentFile);
					}
				}			
			}
			else if(e.getActionCommand().equalsIgnoreCase("Save as"))
			{
				if(fileChooser.showSaveDialog(Toolhou.this) == JFileChooser.APPROVE_OPTION)
				{
					currentFile = fileChooser.getSelectedFile();
					saveAs(currentFile);
				}
			}
			else if (e.getActionCommand().equalsIgnoreCase("Exit")) {
				quitWithPrompt();
			} else if (e.getActionCommand().equalsIgnoreCase("Undo")) {
				undo();
			} else if (e.getActionCommand().equalsIgnoreCase("Redo")) {
				redo();
			} else if (e.getActionCommand().equalsIgnoreCase("Toggle Grid")) {
				toggleGrid();
			} else if(e.getActionCommand().equalsIgnoreCase("Grid Color")){
				changeGridColor();
			} else if (e.getActionCommand().equalsIgnoreCase("Grid Size")) {
				changeGridSize();
			}
			else if(menu.getLabel().equals("Window"))
			{
				int resPos= handleWindowResize(e);
				//Clears Window Menu 
				clearMenuSelection(2);
				menu.getItem(resPos).setEnabled(false);
			}
			else if (e.getActionCommand().equalsIgnoreCase("About")) {
				JOptionPane.showMessageDialog(null, "A tool for making touhou scripts", "Info",
						JOptionPane.PLAIN_MESSAGE);
			}
		}
	}

	public class DrawingPanel extends JPanel implements MouseListener, MouseMotionListener
	{		
		private static final long serialVersionUID = 1L;
		
		public ArrayList<Point> list = new ArrayList<Point>();
		private Stack<ArrayList<Point>> stateStack = new Stack<ArrayList<Point>>();
		private Stack<ArrayList<Point>> redoStateStack = new Stack<ArrayList<Point>>();
		
		private boolean dragging = false; 
		private Point dragged = null;
		
		private int pointMarkerSize = 10;
		
		private boolean grid = true;
		private int gridSize = 10;
		private Color gridColor = new Color(238, 238, 238);
	
		public void paint(Graphics g) 
		{
			//Clears screen, fixes tearing
			super.paintComponent(g);
			
			Graphics2D g2 = (Graphics2D)g;
			if(grid)
				drawGrid(g2);
			
			if(dragging)
				g.setColor(Color.red);
			else
				g.setColor(Color.blue);
			
			for(int i =0; i < list.size(); i++)
			{	
				Point currPoint = list.get(i);
				g.drawRect(currPoint.x-pointMarkerSize/2, currPoint.y-pointMarkerSize/2, pointMarkerSize, pointMarkerSize);
				if(i>0)
				{
					Point prevPoint = list.get(i-1);
					g2.draw(new Line2D.Double(prevPoint, currPoint));
				}
			}
			//Connects last point to first point
			if(list.size()>1)
			{
				g.setColor(Color.green);
				g2.draw(new Line2D.Double(list.get(0), list.get(list.size()-1)));
			}
		}
		private void drawGrid(Graphics2D g2)
		{
			g2.setColor(gridColor);
			int w = mainWindow.getWidth();
			int h = mainWindow.getHeight();
			for(int i=0; i<h; i+=gridSize)
			{
				g2.draw(new Line2D.Double(0, i, w, i));
				
			}
			for(int j=0; j<w; j+= gridSize)
			{
				g2.draw(new Line2D.Double(j, 0, j, h));
			}
	
		}
		public void mouseClicked(MouseEvent e) 
		{
			//Left Click
			if(e.getButton() == MouseEvent.BUTTON1 && !dragging)
			{
				list.add(e.getPoint());
				stateStack.push(getShallowList(list));
	
				if(redoStateStack.size()>0)
					redoStateStack.clear();
			}
			
			repaint();
		}
	
		public void mousePressed(MouseEvent e) 
		{
			if(e.getButton() == MouseEvent.BUTTON3)
			{
				if(list.size()>0)
				{
					for(int i = 0 ; i < list.size() ; i++)
					{
						Point p = list.get(i);
						if(Math.abs(e.getPoint().x-p.x)<pointMarkerSize && Math.abs(e.getPoint().y-p.y)<pointMarkerSize)
						{
							dragged=(Point)list.get(i).clone();
							list.set(i, dragged);
							dragging= true;
							i=list.size();
						}
					}
				}
			}
		}
	
		public void mouseReleased(MouseEvent e) 
		{
			if(dragging)
			{
				dragging=false;
				dragged=null;
				stateStack.push(getShallowList(list));
				if(redoStateStack.size()>0)
					redoStateStack.clear();
				
				repaint();
			}
		}
	
		public void mouseDragged(MouseEvent e)
		{
			if(dragging)
			{
				dragged.x=e.getX();
				dragged.y=e.getY();
				repaint();
	
			}
		}
		
		@SuppressWarnings("unchecked")
		public ArrayList<Point> getShallowList(ArrayList<Point> l)
		{
			return (ArrayList<Point>)l.clone();
		}
		//Unimplemented Methods
		public void mouseMoved(MouseEvent e){	
		}
		public void mouseEntered(MouseEvent e) {
		}
		public void mouseExited(MouseEvent e) {
		}
	}	
}
