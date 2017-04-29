import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Grapher extends JPanel {
	private BufferedImage img;
	private Graphics g;
	private ArrayList<GraphableFunction> functs;
	private double xmin, xmax, ymin, ymax;
	private double d, xdiff, ydiff;

	public Grapher(int width, int height)
	{
		this(width, height, -10, 10, -10, 10, 1, 1);
	}

	public Grapher(int width, int height, double xmin, double xmax, double ymin, double ymax, double xstep, double ystep) {
		this.setPreferredSize(new Dimension(width, height));
		img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		g = img.getGraphics();
		functs = new ArrayList<GraphableFunction>();
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		d = (xmax-xmin)/width/10;
		xdiff = xstep;
		ydiff = ystep;
		update();
	}
	
	public void setWindow(double xmin, double xmax, double ymin, double ymax, double xstep, double ystep) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		d = (xmax-xmin)/img.getWidth()/10;
		xdiff = xstep;
		ydiff = ystep;
		update();
	}

	public void addFunct(GraphableFunction f)
	{
		functs.add(f);
	}

	public void clearFuncts() {
		functs.clear();
	}

	public void update() {
		g.clearRect(0, 0, img.getWidth(), img.getHeight());
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, img.getWidth(), img.getHeight());
		for (GraphableFunction f: functs) if (!f.getColor().equals(Color.WHITE)) graph(f);
		drawAxes(xdiff, ydiff);
		repaint();
	}

	public void drawAxes(double xdiff, double ydiff) {
		int x = getPixX(0);
		int y = getPixY(0);
		g.setColor(Color.LIGHT_GRAY);
		g.drawLine(0, y, img.getWidth(), y);
		g.drawLine(x, 0, x, img.getHeight());
		g.setColor(Color.BLACK);
		g.drawLine(x-10, y, x+10, y);
		g.drawLine(x, y-10, x, y+10);
		for (double xval = 0; xval >= xmin; xval-=xdiff) g.drawLine(getPixX(xval), y-5, getPixX(xval), y+5);
		for (double yval = 0; yval >= ymin; yval-=ydiff) g.drawLine(x-5, getPixY(yval), x+5, getPixY(yval));
		for (double xval = 0; xval <= xmax; xval+=xdiff) g.drawLine(getPixX(xval), y-5, getPixX(xval), y+5);
		for (double yval = 0; yval <= ymax; yval+=ydiff) g.drawLine(x-5, getPixY(yval), x+5, getPixY(yval));
	}

	public void graph(GraphableFunction f) {
		g.setColor(f.getColor());
		Map<Double, Double> pts = f.getPoints(xmin, xmax, d);
		Set<Double> xvals = pts.keySet();
		Double x1 = null, y1 = null;
		for (Double x2: xvals) {
			double y2 = pts.get(x2);
			if (x1!=null&&Double.isFinite(y1)&&Double.isFinite(y2)&&(inHeight(getPixY(y1))||inHeight(getPixY(y2))))
				g.drawLine(getPixX(x1), getPixY(y1), getPixX(x2), getPixY(y2));
			x1 = x2;
			y1 = y2;
		}
	}

	public int getPixX(double x) {
		return (int)((x-xmin)*img.getWidth()/(xmax-xmin));
	}

	public int getPixY(double y) {
		return (int)((ymax-y)*img.getHeight()/(ymax-ymin));
	}

	public boolean inWidth(int x) {
		return (x>=0&&x<img.getWidth());
	}

	public boolean inHeight(int y) {
		return (y>=0&&y<img.getHeight());
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
	}
}
