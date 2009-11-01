package org.jrender;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class Array3I implements TriMeshArray {
	public int[][] array;
	
	public Array3I(int[][] array) {
		this.array = array;
	}

	@Override
	public int getA(int i) {
		return array[i][0];
	}

	@Override
	public int getB(int i) {
		return array[i][1];
	}

	@Override
	public int getC(int i) {
		return array[i][2];
	}

	@Override
	public int getLength() {
		return array.length;
	}

	public static Array3I fromString(String str) {
		ArrayList<Integer> points = new ArrayList<Integer>();
		StringTokenizer tokenizer = new StringTokenizer(str, " ;\n\r");
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken().trim();
			if (token.length()>0) points.add(Integer.parseInt(token));
		}
		
		int[][] array = new int[points.size()/3][3];
		int n = array.length*3;
		for (int i=0, j=0; i<n; i+=3, j++) {
			for (int k=0; k<3;k++) array[j][k] = points.get(i+k);
		}
		
		return new Array3I(array);
	}
}
