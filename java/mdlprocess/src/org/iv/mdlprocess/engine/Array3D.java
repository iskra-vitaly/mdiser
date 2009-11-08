package org.iv.mdlprocess.engine;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Array3D implements Pt3dArray {

	public final double[][] array;
	
	public Array3D(double[][] array) {
		this.array = array;
	}
	
	@Override
	public int getLength() {
		return array.length;
	}

	@Override
	public double getX(int i) {
		return array[i][0];
	}

	@Override
	public double getY(int i) {
		return array[i][1];
	}

	@Override
	public double getZ(int i) {
		return array[i][2];
	}

	public static Array3D fromString(String str) {
		ArrayList<Double> points = new ArrayList<Double>();
		StringTokenizer tokenizer = new StringTokenizer(str, " ;\n\r\t");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (token.length()>0) points.add(Double.parseDouble(token));
		}
		
		double[][] array = new double[points.size()/3][3];
		int n = array.length*3;
		for (int i=0, j=0; i<n; i+=3, j++) {
			for (int k=0; k<3;k++) array[j][k] = points.get(i+k);
		}
		
		return new Array3D(array);
	}
}
