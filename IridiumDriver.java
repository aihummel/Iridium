import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class IridiumDriver {
	public static void main(String[] args) {
		JFrame frame = new JFrame("Iridium");
		int width = 400, height = 400;
		//frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(new IridiumPanel(frame, width, height));
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setLocation(frame.getX(), frame.getY()-100);
		frame.setResizable(false);
		frame.setVisible(true);
	}
}

@SuppressWarnings("serial")
class IridiumPanel extends JPanel {
	private JFrame frame;
	private WindowSizePanel wsp;
	private SubPanel subPanel;
	private Grapher grapher;
	private int l;
	private Color bg = new Color(68, 66, 79);

	public IridiumPanel(JFrame frame, int width, int height) {
		this.frame = frame;
		l = width/40;
		setLayout(new BorderLayout());
		wsp = new WindowSizePanel();
		add(wsp, BorderLayout.NORTH);
		grapher = new Grapher(width, height);
		grapher.setBackground(bg);
		add(grapher, BorderLayout.CENTER);
		subPanel = new SubPanel();
		add(subPanel, BorderLayout.EAST);
	}

	private class SubPanel extends JPanel {
		private ArrayList<FunctionPanel> functs;
		private JPanel buttonPanel;
		private JButton newFunct;
		private JButton delFunct;
		private GridBagConstraints c = new GridBagConstraints();

		public SubPanel() {
			setBackground(bg);
			setLayout(new GridBagLayout());
			c.gridx = 0;
			functs = new ArrayList<FunctionPanel>();
			functs.add(new FunctionPanel());
			add(functs.get(0), c);
			buttonPanel = new JPanel();
			buttonPanel.setBackground(bg);
			newFunct = new JButton("+");
			newFunct.addActionListener(new AddFunctListener());
			buttonPanel.add(newFunct);
			delFunct = new JButton("-");
			delFunct.addActionListener(new DelFunctListener());
			buttonPanel.add(delFunct);
			add(buttonPanel, c);
		}

		public void update() {
			removeAll();
			int w = frame.getWidth();
			c.gridx = 0;
			for (int i = 0; i < functs.size(); i++) {
				add(functs.get(i), c);
				if (frame.getHeight()!=frame.getPreferredSize().getHeight()) {
					c.gridx+=2;
					remove(functs.get(i));
					add(functs.get(i), c);
				}
			}
			add(buttonPanel, c);
			if (frame.getHeight()!=frame.getPreferredSize().getHeight()) {
				c.gridx++;
				remove(buttonPanel);
				add(buttonPanel, c);
			}
			frame.pack();
			if (frame.getWidth()!=w) {
				frame.setVisible(false);
				frame.setLocationRelativeTo(null);
				frame.setLocation(frame.getX(), frame.getY()-100);
				frame.setVisible(true);
			}
		}

		private class AddFunctListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				functs.add(new FunctionPanel());
				update();
			}
		}

		private class DelFunctListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				functs.remove(functs.size()-1);
				update();
				subPanel.repaint();
				try {
					regraph();
				} catch(Exception ex) {}
			}
		}

		private class FunctionPanel extends JPanel {
			JLabel functionLabel;
			JTextField f;
			JComboBox<String> c;
			private Color[] colors = new Color[] {Color.BLUE, Color.RED, new Color(0, 175, 0),
					new Color(255, 0, 255), new Color(255, 127, 0), new Color(127, 0, 255),
					Color.BLACK, Color.WHITE};
			private String[] colornames = new String[] {"Blue", "Red", "Green", "Pink", "Orange",
					"Purple", "Black", "HIDE"};

			public FunctionPanel() {
				setBackground(bg);
				functionLabel = new JLabel("y =");
				functionLabel.setForeground(Color.WHITE);
				add(functionLabel);
				f = new JTextField(null, null, l);
				f.addActionListener(new GraphFunctListener());
				add(f);
				c = new JComboBox<String>(colornames);
				c.setSelectedIndex((int)(Math.random()*colornames.length-1));
				c.addActionListener(new GraphFunctListener());
				add(c);
			}

			public Color getColor() {
				return colors[c.getSelectedIndex()];
			}
		}

		private class GraphFunctListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				try {
					regraph();
				} catch(Exception ex) {}
			}
		}

		private void regraph() throws Exception {
			grapher.clearFuncts();
			for (FunctionPanel p: functs) {
				String f = p.f.getText();
				if (GraphableFunction.isValid(f))
					grapher.addFunct(new GraphableFunction(f, p.getColor()));
			}
			grapher.update();
			grapher.repaint();
		}
	}

	private class WindowSizePanel extends JPanel {
		private JTextField xmin, xmax, xstep, ymin, ymax, ystep;
		private JComboBox<String> presets;
		private String[][] sizes = new String[][] {{"-10", "10", "1", "-10", "10", "1"},
			{"-2pi", "2pi", "pi/2", "-4", "4", "1"}};
		private String[] sizenames = new String[] {"default", "trig"};

		public WindowSizePanel() {
			setBackground(bg);
			addLabel("x:");
			xmin = new JTextField(null, "-10", 4);
			xmin.addActionListener(new WindowSizeListener());
			add(xmin);
			addLabel("to");
			xmax = new JTextField(null, "10", 4);
			xmax.addActionListener(new WindowSizeListener());
			add(xmax);
			addLabel("step:");
			xstep = new JTextField(null, "1", 3);
			xstep.addActionListener(new WindowSizeListener());
			add(xstep);
			addLabel("  y:");
			ymin = new JTextField(null, "-10", 4);
			ymin.addActionListener(new WindowSizeListener());
			add(ymin);
			addLabel("to");
			ymax = new JTextField(null, "10", 4);
			ymax.addActionListener(new WindowSizeListener());
			add(ymax);
			addLabel("step:");
			ystep = new JTextField(null, "1", 3);
			ystep.addActionListener(new WindowSizeListener());
			add(ystep);
			presets = new JComboBox<String>(sizenames);
			presets.setSelectedIndex(0);
			presets.addActionListener(new WindowSizeListener());
			presets.addActionListener(new PresetListener());
			add(presets);
		}

		private void addLabel(String m) {
			JLabel x = new JLabel(m);
			x.setForeground(Color.WHITE);
			add(x);
		}

		private class WindowSizeListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				String xmint = xmin.getText();
				String xmaxt = xmax.getText();
				String ymint = ymin.getText();
				String ymaxt = ymax.getText();
				String xstept = xstep.getText();
				String ystept = ystep.getText();
				try {
				grapher.setWindow(new Function(xmint).get(0), new Function(xmaxt).get(0),
						new Function(ymint).get(0), new Function(ymaxt).get(0),
						new Function(xstept).get(0), new Function(ystept).get(0));
				} catch(Exception ex) {}
			}
		}
		
		private class PresetListener implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				xmin.setText(sizes[presets.getSelectedIndex()][0]);
				xmax.setText(sizes[presets.getSelectedIndex()][1]);
				xstep.setText(sizes[presets.getSelectedIndex()][2]);
				ymin.setText(sizes[presets.getSelectedIndex()][3]);
				ymax.setText(sizes[presets.getSelectedIndex()][4]);
				ystep.setText(sizes[presets.getSelectedIndex()][5]);
			}
		}
	}
}