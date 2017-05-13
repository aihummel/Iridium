import java.util.*;

public class Function {
	private String funct;
	private List<String> tokens;
	private Function sub;
	private String sf;
	private Operation o;
	private static Set<String> operations = new HashSet<String>();
	private static Set<String> subfuncts = new HashSet<String>();

	// x^(2-4)2cos(x/3)sinx/2

	public Function(String f) throws Exception {
		funct = f.trim();
		tokens = new ArrayList<String>();
		String temp = "";
		for (char ch: funct.toCharArray()) if (ch!=' ') temp += ch;
		funct = temp;
		while (funct.charAt(0)=='('&&findCloseParen(0)==funct.length()-1)
			funct = funct.substring(1, funct.length()-1);
		if (funct.charAt(0)=='-') funct = "0"+funct;
		for (String s: new String[] {"+", "-", "X", "*", "/", "^", "log"}) operations.add(s);
		for (String s: new String[] {"sin", "cos", "tan", "sec", "csc", "cot", "asin", "acos",
				"atan", "abs"})
			subfuncts.add(s);

		Operation lowest = null;
		int lowestIndex = -1;
		boolean beforeFunct = true, lastFunct = true;
		for (int i = 0; i < funct.length()&&i>=0; i++) {
			if (nextSubfunct(i)==i) {
				for (String subfunct: subfuncts)
					if (i+subfunct.length()<=funct.length()&&funct.substring(i, i+subfunct.length()).equals(subfunct)) {
						int end = funct.length();
						if (funct.charAt(i+subfunct.length())=='(') end = findCloseParen(i+subfunct.length())+1;
						else {
							int next = nextOperation(i);
							while (next<funct.length()&&next!=-1&&
									(funct.charAt(next)=='^'||funct.charAt(next)=='l')) {
								next = nextOperation(next+1);
							}
							if (next>i&&next<funct.length()) end = next;
						}
						sf = funct.substring(i+subfunct.length(), end);
						tokens.add(funct.substring(i, end));
						i = end-1;
					}
			}
			else if (funct.charAt(i)=='x') tokens.add("x");
			else if (i+2<=funct.length()&&funct.substring(i, i+2).equals("pi")) {
				tokens.add(Math.PI+"");
				i++;
			}
			else if (funct.charAt(i)=='e') tokens.add(Math.E+"");
			else if (funct.charAt(i)=='(') {
				int close = findCloseParen(i);
				tokens.add(funct.substring(i, close+1));
				i = close;
			}
			else if (nextOperation(i)==i) {
				for (String op: operations)
					if (i+op.length()<=funct.length()&&funct.substring(i, i+op.length()).equals(op)) {
						tokens.add(op);
						i+=op.length()-1;
						lastFunct = false;
						Operation test;
						if (op.equals("+")) test = new Add();
						else if (op.equals("-")) test = new Subtract();
						else if (op.equals("*")) test = new Multiply();
						else if (op.equals("X")) test = new AltMultiply();
						else if (op.equals("/")) test = new Divide();
						else if (op.equals("^")) test = new Exp();
						else test = new Log();
						if (lowest==null||test.compareTo(lowest)<=0) {
							lowest = test;
							lowestIndex = tokens.size()-1;
						}
					}
			}
			else {
				int end = lastNum(i);
				tokens.add(funct.substring(i, end));
				i = end-1;
			}
			if (tokens.size()>=2&&beforeFunct&&lastFunct) {
				tokens.add(tokens.size()-1, "X");
				Operation test = new AltMultiply();
				if (lowest==null||test.compareTo(lowest)<=0) {
					lowest = test;
					lowestIndex = tokens.size()-2;
				}
			}
			beforeFunct = lastFunct;
			lastFunct = true;
		}

		if (lowest==null&&sf!=null) sub = new Function(sf);
		else if (lowest!=null) {
			o = lowest;
			String a = "", b = "";
			for (int i = 0; i < lowestIndex; i++) a += tokens.get(i);
			for (int i = lowestIndex+1; i < tokens.size(); i++) b += tokens.get(i);
			o.setA(new Function(a));
			o.setB(new Function(b));	
		}

	}

	private int findCloseParen(int i) throws Exception {
		int nextOpen = i+1+funct.substring(i+1).indexOf('(');
		int nextClose = i+1+funct.substring(i+1).indexOf(')');
		if (nextOpen!=i&&nextOpen < nextClose) return findCloseParen(findCloseParen(nextOpen));
		if (nextClose!=i) return nextClose;
		throw new Exception();
	}

	private int nextOperation(int index) throws Exception {
		for (int i = index; i < funct.length(); i++) {
			if (funct.charAt(i)=='(') i = findCloseParen(i)+1;
			if (i+1 < funct.length()&&operations.contains(funct.substring(i, i+1))) return i;
			if (i+3 < funct.length()&&funct.substring(i, i+3).equals("log")) return i;
		}
		return -1;
	}

	private int nextSubfunct(int index) throws Exception {
		for (int i = index; i < funct.length(); i++) {
			if (funct.charAt(i)=='(') i = findCloseParen(i)+1;
			if (i+3 <= funct.length()&&subfuncts.contains(funct.substring(i, i+3))) return i;
			if (i+4 <= funct.length()&&subfuncts.contains(funct.substring(i, i+4))) return i;
		}
		return -1;
	}

	private int lastNum(int index) {
		for (int i = index; i < funct.length(); i++)
			if ("0123456789.".indexOf(funct.charAt(i))==-1) return i;
		return funct.length();
	}

	public double get(double x) {
		if (o!=null) return o.get(x);
		else if (sub!=null) {
			String s = funct.substring(0, funct.indexOf(sf));
			if (s.equals("sin")) return Math.sin(sub.get(x));
			else if (s.equals("cos")) return Math.cos(sub.get(x));
			else if (s.equals("tan")) return Math.tan(sub.get(x));
			else if (s.equals("sec")) return 1/Math.cos(sub.get(x));
			else if (s.equals("csc")) return 1/Math.sin(sub.get(x));
			else if (s.equals("cot")) return 1/Math.tan(sub.get(x));
			else if (s.equals("asin")) return Math.asin(sub.get(x));
			else if (s.equals("acos")) return Math.acos(sub.get(x));
			else if (s.equals("atan")) return Math.atan(sub.get(x));
			else if (s.equals("abs")) return Math.abs(sub.get(x));
		}
		if (funct.equals("x")) return x;
		return Double.parseDouble(tokens.get(0));
	}

	public String getFunct()
	{
		return funct;
	}

	public String toString() {
		return funct;
	}
}

abstract class Operation implements Comparable<Operation> {
	private Function a;
	private Function b;

	public Operation() {
		a = null;
		b = null;
	}

	public double get(double x) {
		return doOperation(a.get(x), b.get(x));
	}

	public abstract double doOperation(double a, double b);

	public Function getA() {
		return a;
	}

	public void setA(Function a) {
		this.a = a;
	}

	public Function getB() {
		return b;
	}

	public void setB(Function b) {
		this.b = b;
	}
}

class Add extends Operation {
	public double doOperation(double a, double b) {
		return a + b;
	}

	public int compareTo(Operation other) {
		if (other instanceof Add||other instanceof Subtract) return 0;
		else return -1;
	}
}

class Subtract extends Operation {
	public double doOperation(double a, double b) {
		return a - b;
	}

	public int compareTo(Operation other) {
		if (other instanceof Add||other instanceof Subtract) return 0;
		else return -1;
	}
}

class Multiply extends Operation {
	public double doOperation(double a, double b) {
		return a * b;
	}

	public int compareTo(Operation other) {
		if (other instanceof Add||other instanceof Subtract) return 1;
		else if (other instanceof Multiply||other instanceof Divide) return 0;
		else return -1;
	}
}

class AltMultiply extends Operation {
	public double doOperation(double a, double b) {
		return a * b;
	}

	public int compareTo(Operation other) {
		if (other instanceof Exp) return -1;
		else if (other instanceof AltMultiply) return 0;
		else return 1;
	}
}

class Divide extends Operation {
	public double doOperation(double a, double b) {
		return a / b;
	}

	public int compareTo(Operation other) {
		if (other instanceof Add||other instanceof Subtract) return 1;
		else if (other instanceof Multiply||other instanceof Divide) return 0;
		else return -1;
	}
}

class Exp extends Operation {
	public double doOperation(double a, double b) {
		return Math.pow(a, b);
	}

	public int compareTo(Operation other) {
		if (other instanceof Exp) return 0;
		else return 1;
	}
}

class Log extends Operation {
	public double doOperation(double a, double b) {
		return Math.log(b) / Math.log(a);
	}

	public int compareTo(Operation other) {
		if (other instanceof Exp) return -1;
		else if (other instanceof Log) return 0;
		else return 1;
	}
}