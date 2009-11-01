package org.jrender;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;


public class Test {

	
	
	/**
	 * @param args
	 * @throws ParserConfigurationException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws XPathExpressionException 
	 */
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		File file = new File("/media/e/VDiser/Zhang.review/work/cara1_abajo.xml");
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		XPathFactory xf = XPathFactory.newInstance();
		XPath xpath = xf.newXPath();
		String pointsText = xpath.evaluate("/Separator/Group/Group/Separator/Coordinate3/point/text()", doc, XPathConstants.STRING).toString();
		String meshText = xpath.evaluate("/Separator/Group/Group/Separator/IndexedFaceSet/coordIndex/text()", doc, XPathConstants.STRING).toString();
		
		Array3D points = Array3D.fromString(pointsText);
		Array3I meshes = Array3I.fromString(meshText);
		
		JRenderer renderer = new JRenderer(points, meshes);
		
		renderer.ensureNornamls();
		renderer.renderBuffer(750, 750);
		BufferedImage img = renderer.createImage(0, 0, 750, 750);
		
//		BufferedImage img = renderer.createDumbImage(1000, 750);

		ImageIO.write(img, "jpeg", new File("/media/e/out.jpg"));
		ScTest sc = new ScTest();
		sc.test("dsf");
	}

	
}
