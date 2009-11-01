package org.jrender;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.EventListener;
import java.util.EventObject;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.filechooser.FileFilter;

import org.jrender.ProcessFile.Figure;

public class RenderGUI {
	private ProcessFile.Figure figure;
	public final ProcessFile.Params params;
	private BufferedImage buffer;
	private JRenderer renderer;
	
	public void saveParams(OutputStream out) throws IOException {
		ObjectOutputStream encoder = new ObjectOutputStream(out);
		encoder.writeObject(params);
		encoder.flush();
	}
	
	public void saveParams(File file) throws IOException {
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		saveParams(out);
		out.close();
	}
	
	public RenderGUI(Figure figure, ProcessFile.Params params) {
		this.params = params; 
		this.figure = figure;
	}
	
	private void buildBuffer() {
		if (renderer == null) {
			renderFigure();
		} else {
			this.buffer = renderer.createImage(0, 0, params.w, params.h);
			invalidateBuffer();
		}
	}
	
	public void setFigure(ProcessFile.Figure figure) {
		this.figure = figure;
		renderFigure();
	}

	public interface BufferListener extends EventListener {
		void bufferUpdated(EventObject event);
	}
	
	private EventListenerList eventListeners = new EventListenerList();
	
	protected void fireBufferUpdated() {
		EventObject event = null;
		for(BufferListener listener: eventListeners.getListeners(BufferListener.class)) {
			if (event == null) event = new EventObject(this);
			listener.bufferUpdated(event);
		}
	}

	public void addBufferListener(BufferListener bufferListener) {
		this.eventListeners.add(BufferListener.class, bufferListener);
	}

	public void removeBufferListener(BufferListener bufferListener) {
		this.eventListeners.remove(BufferListener.class, bufferListener);
	}

	private void invalidateBuffer() {
		fireBufferUpdated();
	}
	
	private void renderBuffer() {
		if (renderer == null) renderFigure();
		else {
			renderer.renderBuffer(params.w, params.h);
			buildBuffer();
		}
	}
	
	private void renderFigure() {
		double[][] pts = new double[figure.points.getLength()][];
		int n = figure.points.getLength();
		for (int i=0; i<n; i++) {
			pts[i] = new double[]{figure.points.getX(i), figure.points.getY(i), figure.points.getZ(i), 1};
		}
		Array3D pointsTransformed = new Array3D(params.xform.apply(pts));
		
		renderer = new JRenderer(pointsTransformed, figure.meshes);
		
		renderBuffer();
	}
	
	public RenderGUI(int w, int h, Figure figure) {
		this(figure, new ProcessFile.Params(w, h));
	}
	
	public BufferedImage getBuffer() {
		return buffer;
	}
	
	public void setTransform(ProcessFile.Transform xform) {
		params.xform = xform;
		renderFigure();
	}
	
	public void transform(ProcessFile.Transform xform) {
		setTransform(params.xform.add(xform));
	}
	
	private void setBufferSize(Dimension size) {
		params.w = size.width;
		params.h = size.height;
		
		renderBuffer();
	}
	
	public void drawCurrentBufferWithCropFrame(Graphics2D g) {
		if (renderer == null) renderFigure();
		BufferedImage img = getBuffer();
		g.drawImage(img, 0, 0, img.getWidth(), img.getHeight(), null);
		
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g.setPaint(Color.GREEN);
		g.setStroke(new BasicStroke(0.5f));
		
		int left = params.crop.x;
		int top = params.crop.y;
		int width = params.crop.width;
		int height = params.crop.height;
		
		int imgWidth = img.getWidth();
		int imgHeight = img.getHeight();
		
		g.fill(new Rectangle(0, 0, imgWidth, top));
		g.fill(new Rectangle(0, top, left, height));
		g.fill(new Rectangle(left+width, top, imgWidth-left-width, height));
		g.fill(new Rectangle(0, top+height, imgWidth, imgHeight-height-top));

		g.setPaint(Color.RED);
		g.draw(params.crop);
	}
	
	public Component createDisplay() {
		final JComponent component = new JComponent() {
			private static final long serialVersionUID = 2404868403671767330L;

			@Override
			public void paint(Graphics g) {
				drawCurrentBufferWithCropFrame((Graphics2D)g);
			}
		};
		Dimension size = new Dimension(params.w, params.h);
		component.setPreferredSize(size);
		component.setSize(size);
		
		component.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension newSize = component.getSize();
				if(params.w != newSize.width || params.h != newSize.height) setBufferSize(newSize);
			}
		});

		addBufferListener(new BufferListener() {
			@Override
			public void bufferUpdated(EventObject event) {
				component.repaint();
			}
		});
		
		return component;
	}
	
	public void setCropFactor(float left, float top, float right, float bottom) {
		int l = (int)(params.w*left);
		int r = (int)(params.w*right);
		int t = (int)(params.h*top);
		int b = (int)(params.h*bottom);
		
		int w = Math.max(r-l, 0);
		int h = Math.max(b-t, 0);
		
		params.crop = new Rectangle(l, t, w, h);
		
		invalidateBuffer();
	}
	
	public static JFrame createToolFrame(final RenderGUI gui) {
		final JFrame toolFrame = new JFrame();
		Container pane = toolFrame.getContentPane();
		pane.setLayout(new GridLayout(12, 1));
		
		for(int i=0; i<3; i++) {
			final int axis = i;
			final JSlider slider = new JSlider(0, 100, 50);
			slider.setMajorTickSpacing(10);
			slider.setMinorTickSpacing(1);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					if (!slider.getValueIsAdjusting()) {
						int v = slider.getValue();
						float normV = (float)(v-slider.getMinimum()) / (slider.getMaximum() - slider.getMinimum()) - 0.5f;
						double alfa = 2*Math.PI*normV;
						gui.setTransform(ProcessFile.Transform.getRotate(axis, alfa));
					}
				}
			});
			pane.add(slider);
		}
		
		pane.add(new JLabel("Crop"));
		
		final JSlider left = new JSlider(0, 1000, 0);
		final JSlider right = new JSlider(0, 1000, 1000);
		final JSlider top = new JSlider(0, 1000, 0);
		final JSlider bottom = new JSlider(0, 1000, 1000);
		final JSlider[] sliders = {left, right, top, bottom};
		
		ChangeListener cropListaner = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()) {
					gui.setCropFactor(getValue(left), getValue(top), getValue(right), getValue(bottom));
				}
			}
			
			private float getValue(JSlider slider) {
				return (float)(slider.getValue() - slider.getMinimum())/(slider.getMaximum() - slider.getMinimum());
			}
		};
		
		for (JSlider slider: sliders) {
			slider.setMajorTickSpacing(100);
			slider.setMinorTickSpacing(10);
			slider.setPaintTicks(true);
			slider.setPaintLabels(false);
			slider.addChangeListener(cropListaner);
			pane.add(slider);
		}
		
		JPanel btPanel = new JPanel(new FlowLayout());
		
		final JFileChooser fileDlg = new JFileChooser();
		fileDlg.setAcceptAllFileFilterUsed(false);
		
		fileDlg.setMultiSelectionEnabled(false);
		
		final FileFilter jpegFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "jpeg";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".jpg");
			}
		};  
		final FileFilter shapeFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "wrl, xml";
			}
			
			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".xml") || f.getName().endsWith(".wrl") || f.getName().endsWith(".shape");
			}
		};  
		
		JButton saveButton = new JButton("Save Params");
		saveButton.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				fileDlg.removeChoosableFileFilter(shapeFilter);
				fileDlg.addChoosableFileFilter(jpegFilter);
				if (fileDlg.showSaveDialog(toolFrame) == JFileChooser.APPROVE_OPTION) {
					try {
						gui.saveParams(fileDlg.getSelectedFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton loadFigButton = new JButton("Load Shape");
		loadFigButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileDlg.removeChoosableFileFilter(jpegFilter);
				fileDlg.addChoosableFileFilter(shapeFilter);
				if (fileDlg.showOpenDialog(toolFrame) == JFileChooser.APPROVE_OPTION) {
					try {
						gui.setFigure(ProcessFile.readFile(fileDlg.getSelectedFile()));
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		JButton saveImg = new JButton("Save Image");
		saveImg.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				fileDlg.addChoosableFileFilter(jpegFilter);
				File oldFile = fileDlg.getSelectedFile();
				if (oldFile != null) {
					String fileName = oldFile.getAbsolutePath();
					fileName = fileName.replaceFirst("\\.xml$", ".jpg");
					fileName = fileName.replaceFirst("\\.wrl$", ".jpg");
					fileDlg.setSelectedFile(new File(fileName));
				}
				fileDlg.removeChoosableFileFilter(shapeFilter);
				if (fileDlg.showSaveDialog(toolFrame) == JFileChooser.APPROVE_OPTION) {
					try {
						ImageIO.write(gui.getBuffer(), "jpeg", fileDlg.getSelectedFile());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		btPanel.add(saveButton);
		btPanel.add(loadFigButton);
		btPanel.add(saveImg);
		
		pane.add(btPanel);
		
		JButton gcButton = new JButton("GC");
		gcButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				System.gc();
			}
		});
		pane.add(gcButton);
		
		toolFrame.pack();
		return toolFrame;
	}
	
	public static void main(String argv[]) throws Exception {
		Figure figure = ProcessFile.readXmlFile(new File("/media/e/VDiser/Zhang.review/work/cara1_abajo.xml"));
		
		final RenderGUI gui = new RenderGUI(800, 600, figure);
//		gui.renderFigure();
	//	ImageIO.write(gui.buffer, "jpeg", new File("/media/tmp/aaa.jpg"));
		
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container pane = frame.getContentPane();
		pane.setLayout(new BorderLayout());
		pane.add(gui.createDisplay());
		frame.pack();
		frame.setVisible(true);
		
		frame = createToolFrame(gui); 
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
