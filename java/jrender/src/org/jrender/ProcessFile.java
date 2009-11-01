package org.jrender;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class ProcessFile {
	public static class Transform implements Serializable {
		private static final long serialVersionUID = 1L;
		public final double matrix[][];

		public Transform(double[][] matrix) {
			this.matrix = matrix;
		}

		public Transform(Transform xform) {
			this(xform.matrix);
		}

		public Transform add(Transform xform) {
			return new Transform(MatrixMath.mul(matrix, xform.matrix));
		}

		public double[][] apply(double[][] points) {
			return MatrixMath.mul(points, matrix); 
		}

		public String toString() {
			return "Transform"+MatrixMath.toString(matrix);
		}

		public static Transform getScale(double[] scale) {
			double matrix[][] = new double[4][4];
			for(int i = 0; i<3; i++) matrix[i][i] = scale[i];
			matrix[3][3] = 1;
			return new Transform(matrix); 
		}

		public static double[] fromUniform(double[] uniform) {
			int n = uniform.length;
			int last = n-1;
			double[] res = new double[last];
			double w = uniform[last];
			for(int i=0;i<last;i++) res[i] = uniform[i]/w;
			return res;
		}

		public static double[] toUniform(double[] row) {
			int n = row.length;
			double[] uniform = new double[n+1];
			System.arraycopy(row, 0, uniform, 0, n);
			uniform[n] = 1;
			return uniform;
		}

		public static Transform getMove(double[] d) {
			Transform t = new Transform(IDENTITY);
			for(int i=0; i<3 && i<d.length ;i++) t.matrix[3][i] = d[i];
			return t;
		}

		public static Transform getRotate(int axis, double alfa) {
			double[][] matrix = new double[4][4];

			double sin = Math.sin(alfa), cos = Math.cos(alfa);

			int sincos = (axis+1)%3;
			int cossin = (axis+2)%3;

			matrix[axis][axis] = 1;
			matrix[sincos][sincos] = cos;
			matrix[cossin][sincos] = sin;

			matrix[sincos][cossin] = -sin;
			matrix[cossin][cossin] = cos;
			matrix[3][3] = 1;

			return new Transform(matrix);
		}

		public static final Transform IDENTITY = getScale(new double[]{1, 1, 1});
	}

	public static class Params implements Serializable {
		private static final long serialVersionUID = 1L;

		public int w;
		public int h;
		public Transform xform;
		public Rectangle crop;

		public Params(int w, int h, Transform xform, Rectangle crop) {
			super();
			this.xform = xform != null ? xform : Transform.IDENTITY;
			this.w = w;
			this.h = h;
			this.crop = crop != null ? crop : new Rectangle(0, 0, w, h);
		}

		public Params(int w, int h) {
			this(w, h, null, null);
		}

		public int getW() {
			return w;
		}

		public int getH() {
			return h;
		}

		public Transform getXform() {
			return xform;
		}

		public Rectangle getCrop() {
			return crop;
		}
	}

	public static class Figure {
		public final Array3D points;
		public final Array3I meshes;
		public Figure(Array3D points, Array3I meshes) {
			super();
			this.points = points;
			this.meshes = meshes;
		}
	}

	public static Figure readXmlFile(File file) throws ScanFigureException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			XPathFactory xf = XPathFactory.newInstance();
			XPath xpath = xf.newXPath();
			String pointsText = xpath.evaluate("/Separator/Group/Group/Separator/Coordinate3/point/text()", doc, XPathConstants.STRING).toString();
			String meshText = xpath.evaluate("/Separator/Group/Group/Separator/IndexedFaceSet/coordIndex/text()", doc, XPathConstants.STRING).toString();

			Array3D points = Array3D.fromString(pointsText);
			Array3I meshes = Array3I.fromString(meshText);
			
			return new Figure(points, meshes);
		} catch (Exception e) {
			throw new ScanFigureException(e);
		}
	}
	
	public static Figure readShapeFile(File file) throws ScanFigureException {
		return MatArrayUtils.loadFigure(file);
	}
	
	public static Figure readWrlFile(File file) throws ScanFigureException {
		throw new UnsupportedOperationException();
	}
	
	public static Figure readFile(File file) throws ScanFigureException {
		if (file.getName().endsWith(".xml")) return readXmlFile(file);
		if (file.getName().endsWith(".wrl")) return readWrlFile(file);
		if (file.getName().endsWith(".shape")) return readShapeFile(file);
		throw new ScanFigureException("Don't know how to read "+file);
	}

	public static BufferedImage processXml(File file, Params params) throws ScanFigureException  {
		return processData(readXmlFile(file), params);
	}

	public static BufferedImage processData(Figure figure, Params params) {
		double[][] pts = new double[figure.points.getLength()][];
		int n = figure.points.getLength();
		for (int i=0; i<n; i++) {
			pts[i] = new double[]{figure.points.getX(i), figure.points.getY(i), figure.points.getZ(i), 1};
		}
		Array3D pointsTransformed = new Array3D(params.xform.apply(pts));

		JRenderer renderer = new JRenderer(pointsTransformed, figure.meshes);

		renderer.ensureNornamls();
		renderer.renderBuffer(params.w, params.h);
		return renderer.createImage(params.crop.x, params.crop.y, params.crop.width, params.crop.height);
	}

	public static void main(String argv[]) {
		System.out.println(Transform.getRotate(0, 0));
		System.out.println(Transform.getRotate(1, 0));
		System.out.println(Transform.getRotate(2, 0));
	}
}
