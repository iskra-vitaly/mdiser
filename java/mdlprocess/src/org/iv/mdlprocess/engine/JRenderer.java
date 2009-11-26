package org.iv.mdlprocess.engine;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

public class JRenderer {
	private Pt3dArray points;
	private TriMeshArray meshes;

	private double[][] normals;
	
	private double[][] zbuffer;
	private int[][] ibuffer;

	private double[] min = new double[3];
	private double[] max = new double[3];
	
	public JRenderer(Pt3dArray points, TriMeshArray meshes) {
		super();
		this.points = points;
		this.meshes = meshes;

		for (int i=0; i<3; i++) {
			min[i] = Double.POSITIVE_INFINITY;
			max[i] = Double.NEGATIVE_INFINITY;
		}
		
		int n = points.getLength();
		for (int i=0; i<n; i++) {
			double pt[] = {points.getX(i), points.getY(i), points.getZ(i)};
			for (int j=0; j<3; j++) {
				if(pt[j] < min[j]) min[j] = pt[j];
				if(pt[j] > max[j]) max[j] = pt[j];
			}
		}
	}
	

	public void ensureNornamls() {
		if (normals != null) return;
		
		
		int n = meshes.getLength();
		normals = new double[n][3];

		
		for (int i = 0; i < n; i++) {
			int i1 = meshes.getA(i);
			int i2 = meshes.getB(i);
			int i3 = meshes.getC(i);
			double x1 = points.getX(i1), y1 = points.getY(i1), z1 = points.getZ(i1);
			double x2 = points.getX(i2), y2 = points.getY(i2), z2 = points.getZ(i2);
			double x3 = points.getX(i3), y3 = points.getY(i3), z3 = points.getZ(i3);
			
			double nx = y1*(z2 - z3) + y2*(z3 - z1) + y3*(z1 - z2);
			double ny = z1*(x2 - x3) + z2*(x3 - x1) + z3*(x1 - x2);
			double nz = x1*(y2 - y3) + x2*(y3 - y1) + x3*(y1 - y2);
			
			
			normals[i][0] = nx; normals[i][1] = ny; normals[i][2] = nz;

			double nlen = Math.sqrt(nx*nx+ny*ny+nz*nz);
			if (nlen == 0) throw new RuntimeException("Normal is null");
			for (int j=0; j<3; j++) {
				normals[i][j]/=nlen;
			}
			// System.out.println(String.format("%d:%3.3g", i, Math.abs(normals[i][2])));
		}
	}
	
	public void renderBuffer(int w, int h) {
		ensureNornamls();
		zbuffer = new double[h][w];
		ibuffer = new int[h][w];
		
		for (int i=0; i<h; i++) for (int j=0; j<w; j++) {
			zbuffer[i][j] = Double.NEGATIVE_INFINITY;
			ibuffer[i][j] = -1;
		}
		
		double rw = max[0] - min[0];
		double rh = max[1] - min[1];
		
		int ww = w-1, hh = h-1;
		
		double sw = ww/rw, sh = hh/rh;
		
		double sx = -min[0], sy = -min[1], sz = -min[2];
		
		double s = 0;
		
		if (sh < sw) {
			s = sh;
			sx += (ww-s*rw)/2.0/s;
		} else {
			s = sw;
			sy += (hh-s*rh)/2.0/s;
		}
		
		int npoints = points.getLength();
		final double[] x = new double[npoints];
		final double[] y = new double[npoints];
		final double[] z = new double[npoints];
		
		for (int i = 0; i<npoints; i++) {
			x[i] = (points.getX(i)+sx)*s;
			y[i] = h - 1 - (points.getY(i)+sy)*s;
			z[i] = (points.getZ(i)+sz)*s;
		}
		
		int nmesh = meshes.getLength();
		
		for (int imesh = 0; imesh < nmesh; imesh++) {
			Integer[] pt = {meshes.getA(imesh), meshes.getB(imesh), meshes.getC(imesh)};
			if (isTriInvi(x, y, pt)) continue;

			double minx = Double.POSITIVE_INFINITY, maxx = Double.NEGATIVE_INFINITY;
			double miny = Double.POSITIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;
			
			for (int i = 0; i < pt.length; i++) {
				minx = Math.min(minx, x[pt[i]]);
				maxx = Math.max(maxx, x[pt[i]]);
				miny = Math.min(miny, y[pt[i]]);
				maxy = Math.max(maxy, y[pt[i]]);
			}
			
			int lowx = (int)Math.ceil(minx), lowy=(int)Math.ceil(miny);
			int highx = (int)Math.floor(maxx), highy=(int)Math.floor(maxy);
			
			double[] plainPt = {x[pt[0]], y[pt[0]], z[pt[0]]};
			for (int intx=lowx; intx<=highx; intx++) for(int inty=lowy; inty<=highy; inty++) {
				if (isInTri(intx, inty, x, y, pt)) {
					double ptZ = calcZ(plainPt, this.normals[imesh], intx, inty);
					if (zbuffer[inty][intx] < ptZ) {
						zbuffer[inty][intx] = ptZ;
						ibuffer[inty][intx] = imesh;
					}
				}
			}
			
			/* old buggy behavoiur
			Arrays.sort(pt, new Comparator<Integer>(){
				@Override
				public int compare(Integer o1, Integer o2) {
					return x[o1] > x[o2] ? 1 : (x[o1] < x[o2] ? -1 : 0);
				}
			});

			int i1 = pt[0], i2 = pt[1], i3 = pt[2];
			
			double x1 = x[i1];
			double x2 = x[i2];
			double x3 = x[i3];

			/* 
			 * (x-x1)*y21/x21 + y1 = y
			 */
			/*
			double y21 = y[i2]-y[i1], x21 = x[i2]-x[i1], z21 = z[i2]-z[i1];
			double y31 = y[i3]-y[i1], x31 = x[i3]-x[i1], z31 = z[i3]-z[i1];;
			double px = x[i1];
			while (px<x2) {
				double py1 = (px-x1)*y21/x21 + y[i1];
				double py2 = (px-x1)*y31/x31 + y[i1];
				double pz1 = (px-x1)*z21/x21 + z[i1];
				double pz2 = (px-x1)*z31/x31 + z[i1];
				
				if (py1 < py2) vline(px, py1, py2, pz1, pz2, imesh);
				else vline(px, py2, py1, pz2, pz1, imesh);
				px = px + 1;
			}
		
			double y32 = y[i3]-y[i2], x32 = x[i3]-x[i2], z32 = z[i3]-z[i2];;
			while (px < x3) {
				double py1 = (px-x2)*y32/x32 + y[i2];
				double py2 = (px-x1)*y31/x31 + y[i1];
				double pz1 = (px-x2)*z32/x32 + z[i2];
				double pz2 = (px-x1)*z31/x31 + z[i1];
				
				if (py1 < py2) vline(px, py1, py2, pz1, pz2, imesh);
				else vline(px, py2, py1, pz2, pz1, imesh);
				px = px + 1;
			}*/ 
		}
	}
	
	private static double calcZ(double[] pt, double n[], double x, double y) {
		if (n[2] == 0) return pt[2]; // this plain is orthogonal to view line (z-axis) so its anyway invizible
		double dn = 0;
		for (int i=0; i<3; i++) dn+=pt[i]*n[i];
		return (dn - n[0]*x - n[1]*y) / n[2];
	}
	
	@SuppressWarnings("unused")
	private void vline(double x, double y1, double y2, double z1, double z2, int ni) {
		double z21 = z2 - z1;
		double y21 = y2 -y1;
		int px = (int)Math.round(x);
		for (double py = y1; py<=y2; py+=1) {
			double z = Math.abs(y21)>0.01 ? (py - y1)*z21/y21 + z1 : z1;
			int y = (int)Math.round(py);
			if (zbuffer[y][px] < z) {
				ibuffer[y][px] = ni;
				zbuffer[y][px] = z;
			}
		}
	}
	
	private static int getRGB(float gray, ColorSpace cspace) {
		float[] color = {gray};
		float []frgb = cspace.toRGB(color);
		int rgb = 0xff000000
			|(Math.round(frgb[0]*255)<<16)
			|(Math.round(frgb[1]*255)<<8)
			|(Math.round(frgb[2]*255));
		return rgb;
	}
	
	public BufferedImage createImage(int srcX, int srcY, int w, int h) {
		ensureNornamls();
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_USHORT_GRAY);
		ColorSpace cspace = img.getColorModel().getColorSpace();
		
		for (int y = 0; y<h; y++) for (int x = 0; x<w; x++) {
			int ni = ibuffer[srcY+y][srcX + x];
			
			if (ni >=0 ) img.setRGB(x, y, getRGB((float)Math.abs(normals[ni][2]), cspace));
			else img.setRGB(x, y, 0xff000000);
		}
		
		return img;
	}
	
	public float[][][] getNormals(int srcX, int srcY, int w, int h) {
		ensureNornamls();
		float[][][] res = new float[3][h][w];
		for (int y = 0; y<h; y++) for (int x = 0; x<w; x++) {
			int ni = ibuffer[srcY+y][srcX + x];
			for (int i=0; i<3; i++) res[i][y][x] = (float)normals[ni][i]; 
		}
		return res;
	}
	
	public BufferedImage createDumbImage(int w, int h) {
		ensureNornamls();
		double rw = max[0] - min[0];
		double rh = max[1] - min[1];
		
		int ww = w-1, hh = h-1;
		
		double sw = ww/rw, sh = hh/rh;
		
		double sx = -min[0], sy = -min[1], sz = -min[2];
		
		double s = 0;
		
		if (sh < sw) {
			s = sh;
			sx += (ww-s*rw)/2.0/s;
		} else {
			s = sw;
			sy += (hh-s*rh)/2.0/s;
		}
		
		int npoints = points.getLength();
		final double[] x = new double[npoints];
		final double[] y = new double[npoints];
		final double[] z = new double[npoints];
		
		for (int i = 0; i<npoints; i++) {
			x[i] = (points.getX(i)+sx)*s;
			y[i] = (points.getY(i)+sy)*s;
			z[i] = (points.getZ(i)+sz)*s;
		}
		
		int nmesh = meshes.getLength();
		
		BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = img.createGraphics();
		
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(new Color(0xff00ff00));
		
		for (int imesh = 0; imesh < nmesh; imesh++) {
			Integer[] pt = {meshes.getA(imesh), meshes.getB(imesh), meshes.getC(imesh)};
			
			GeneralPath triangle = new GeneralPath(GeneralPath.WIND_EVEN_ODD, pt.length);
			triangle.moveTo(x[pt[0]], y[pt[0]]);
			for (int i=1; i<pt.length; i++) {
				triangle.lineTo(x[pt[i]], y[pt[i]]);
			}
			triangle.closePath();
			float grayColor = (float)Math.abs(normals[imesh][2]); 
			g2d.setPaint(new Color(grayColor, grayColor, grayColor));
			g2d.fill(triangle);
		}
		
		return img;
	}
	
	private static boolean isInTri(double x, double y, double[] xx, double[] yy, Integer[] triangle) {
		int[] pt = new int[4];
		for (int i=0; i<3; i++) pt[i] = triangle[i];
		pt[3] = triangle[0];
		
		int minuses = 0, pluses = 0, zeros = 0; 

		for(int i=0, j=1; i<3; i++, j++) {
			double d = dFromLine(x, y, xx[pt[i]], yy[pt[i]], xx[pt[j]], yy[pt[j]]);
			if (d > 0) pluses++;
			else if (d < 0) minuses++;
			else zeros++;
		}
		
		return pluses == 3 || minuses == 3 || (zeros == 1 && (pluses == 2 || minuses == 2));
	}
	
	private static boolean isTriInvi(double[] x, double[] y, Integer[] triangle) {
		int[] pt = new int[6];
		for (int i=0; i<6; i++) pt[i] = triangle[i%3];
		
		for (int a=0, b=1, c=2; a<3; a++, b++, c++) {
			if (dFromLine(x[pt[a]], y[pt[a]], x[pt[b]], y[pt[b]], x[pt[c]], y[pt[c]]) == 0) return true;
		}
		return false;
	}
	
	private static double dFromLine(double x, double y, double x1, double y1, double x2, double y2) {
		return (x-x1)*(y2-y1)-(y-y1)*(x2-x1);
	}
}
