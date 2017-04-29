import java.text.DecimalFormat;

public class FunctionTest {
	public static void main(String[] args) throws Exception {
		DecimalFormat df = new DecimalFormat("0.0000");
		Function f = new Function("x^(x+1)");
		for (double n = 0; n < 5; n+=1)
			System.out.println(df.format(n)+"  "+df.format(f.get(n)));
		/*
		"x^(2-4)2cos(x/3)sinx/2"
		 */
	}

}
