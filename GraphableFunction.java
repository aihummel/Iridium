import java.awt.Color;
import java.util.*;

public class GraphableFunction extends Function {
	private Color color;

	public GraphableFunction(String f, Color c) throws Exception {
		super(f);
		color = c;
	}

	public Map<Double, Double> getPoints(double xmin, double xmax, double d) {
		Map<Double, Double> m = new TreeMap<Double, Double>();
		for (double x = xmin-d/2; x <= xmax+d; x+=d) m.put(x, get(x));
		return m;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color c) {
		color = c;
	}

	public static boolean isValid(String f) {
		try {
			GraphableFunction test = new GraphableFunction(f, Color.BLACK);
			test.getPoints(-10, 10, 1);
			return true;
		}
		catch(Exception e) {
			return false;
		}
	}
}
