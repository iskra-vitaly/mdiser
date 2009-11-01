package org.jrender;

public class MatrixMath {
	public static double[] mul(double[] row, double[][] B) {
		if (row.length != B.length) throw new ArrayIndexOutOfBoundsException("row.length != B.length");
		int n = B[0].length;
		double[] res = new double[n];
		int m = row.length;
		for(int i=0; i<n; i++) {
			res[i] = 0;
			for (int j=0; j<m; j++) res[i]+=row[j]*B[j][i];
		} 
		return res;
	}

	public static double[][] mul(double[][] A, double[][] B) {
		int n = A.length;
		double[][] res = new double[n][];
		for (int i=0; i<n; i++) {
			res[i] = mul(A[i], B);
		}
		return res;
	}
	
	public static void main(String args[]) {
		double[][] A = {{1}, {2}, {3}};
		double[][] B = {{1, 2, 3}};
		double[][] res = mul(A, B);
		
		System.out.println("A="+toString(A));
		System.out.println("B="+toString(B));
		System.out.println("res="+toString(res));
	}
	
	
	public static String toString(double[] row) {
		StringBuilder buf = new StringBuilder("{");
		if (row.length >0 ) {
			buf.append(row[0]);
			for (int i=1;i<row.length;i++) buf.append(", ").append(row[i]);
		}
		buf.append('}');
		return buf.toString();
	}

	public static String toString(double[][] M) {
		StringBuilder buf = new StringBuilder("{");
		if (M.length>0) {
			buf.append("\n\t").append(toString(M[0]));
			for (int i=1;i<M.length;i++) buf.append(",\n\t").append(toString(M[i]));
		}
		buf.append("\n}");
		return buf.toString();
	}
}
