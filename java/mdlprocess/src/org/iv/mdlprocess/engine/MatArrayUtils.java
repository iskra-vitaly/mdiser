package org.iv.mdlprocess.engine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public final class MatArrayUtils {
	public static <T> void asMat(T[][] array, Writer out) throws IOException {
		for (T[] row:array) {
			boolean nonfirst = false;
			for (T v:row) {
				if (nonfirst) out.write(' ');
				out.write(v.toString());
				nonfirst = true;
			}
			out.write(";\n");
		}
	}
	
	public static <T> void asMat(T[][] array, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out); 
		asMat(array, writer);
		writer.flush();
	}
	
	public static <T> String asMat(T[][] array) {
		StringWriter buf = new StringWriter();
		try {
			asMat(array, buf);
			buf.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buf.toString();
	}
	
	public static void asMat(int[][] array, Writer out) throws IOException {
		for (int[] row:array) {
			boolean nonfirst = false;
			for (int v:row) {
				if (nonfirst) out.write(' ');
				out.write(Integer.toString(v));
				nonfirst = true;
			}
			out.write(";\n");
		}
	}
	
	public static void asMat(int[][] array, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out); 
		asMat(array, writer);
		writer.flush();
	}
	
	public static String asMat(int[][] array) {
		StringWriter buf = new StringWriter();
		try {
			asMat(array, buf);
			buf.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buf.toString();
	}
	
	public static void asMat(double[][] array, Writer out) throws IOException {
		for (double[] row:array) {
			boolean nonfirst = false;
			for (double v:row) {
				if (nonfirst) out.write(' ');
				out.write(Double.toString(v));
				nonfirst = true;
			}
			out.write(";\n");
		}
	}
	
	public static void asMat(double[][] array, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out); 
		asMat(array, writer);
		writer.flush();
	}
	
	public static <T> String asMat(double[][] array) {
		StringWriter buf = new StringWriter();
		try {
			asMat(array, buf);
			buf.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return buf.toString();
	}
	
	public static void saveFigure(ProcessFile.Figure figure, Writer out) throws IOException {
		out.write("<figure>\n<points>\n");
		asMat(figure.points.array, out);
		out.write("</points>\n<meshes>\n");
		asMat(figure.meshes.array, out);
		out.write("</meshes>\n</figure>");
	}
	
	public static void saveFigure(ProcessFile.Figure figure, File file) throws IOException {
		Writer out = new BufferedWriter(new FileWriter(file));
		saveFigure(figure, out);
		out.close();
	}
	
	public static ProcessFile.Figure loadFigure(File file) throws ScanFigureException{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(file);

			XPathFactory xf = XPathFactory.newInstance();
			XPath xpath = xf.newXPath();
			String pointsText = xpath.evaluate("/figure/points/text()", doc, XPathConstants.STRING).toString();
			String meshText = xpath.evaluate("/figure/meshes/text()", doc, XPathConstants.STRING).toString();

			Array3D points = Array3D.fromString(pointsText);
			Array3I meshes = Array3I.fromString(meshText);
			
			return new ProcessFile.Figure(points, meshes);
		} catch (Exception e) {
			throw new ScanFigureException(e);
		}
		
	}
	
	public static void main(String argv[]) throws Exception {
		Integer[][] a = {{1, 2}, {3, 4}};
		System.out.println(asMat(a));
	}
}
