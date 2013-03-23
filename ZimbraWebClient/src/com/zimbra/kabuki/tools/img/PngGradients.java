/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2008, 2009, 2010 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.kabuki.tools.img;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.regex.*;

import javax.imageio.*;
import javax.imageio.stream.*;

import com.zimbra.kabuki.util.Colors;

public class PngGradients {

	//
	// Constants
	//

	static final int DEFAULT_WIDTH = 10;
	static final int DEFAULT_HEIGHT = 32;
	static final Color DEFAULT_COLOR = Color.black;
	static final Orientation DEFAULT_ORIENTATION = Orientation.VERTICAL;

	//
	// Constructors
	//

	private PngGradients() {}

	//
	// MAIN
	//

	public static void main(String[] argv) throws Exception {
		// help
		if (argv.length == 0) {
			System.out.println("Options:");
			System.out.println("  -o | --output {filename}  Output filename.");
			System.out.println();
			System.out.println("  -w | --width {length}   Image width (default: "+DEFAULT_WIDTH+").");
			System.out.println("  -h | --height {length}  Image height (default: "+DEFAULT_HEIGHT+").");
			System.out.println("  -c | --color {color}    Background color (default: "+DEFAULT_COLOR+").");
			System.out.println();
			System.out.println("  -g | --gradient {gradient(s)}  Gradient list.");
			System.out.println("       Pattern is {start_pos}-{end_pos}[,{step}[,{offset}]]");
			System.out.println("       separated by path separator ("+File.pathSeparator+").");
			System.out.println();
			System.out.println("  -O | --orientation (horizontal | vertical)  Orientation.");
			System.exit(1);
		}

		// params
		String filename = null;
		int width = DEFAULT_WIDTH;
		int height = DEFAULT_HEIGHT;
		Color color = DEFAULT_COLOR;
		Gradient[] gradients = null;
		Orientation orientation = DEFAULT_ORIENTATION;

		for (int i = 0; i < argv.length; i++) {
			String arg = argv[i];
			if (arg.equals("-o") || arg.equals("--output")) {
				filename = argv[++i];
				continue;
			}
			if (arg.equals("-w") || arg.equals("--width")) {
				width = Integer.parseInt(argv[++i]);
				continue;
			}
			if (arg.equals("-h") || arg.equals("--height")) {
				height = Integer.parseInt(argv[++i]);
				continue;
			}
			if (arg.equals("-c") || arg.equals("--color")) {
				color = Color.getColor(argv[++i]);
				if (color == null) {
					color = Colors.getColor(argv[i]);
				}
				continue;
			}
			if (arg.equals("-g") || arg.equals("--gradient")) {
				String[] parts = argv[++i].split(File.pathSeparator);
				gradients = new Gradient[parts.length];
				for (int j = 0; j < parts.length; j++) {
					gradients[j] = new Gradient(parts[j]);
				}
				continue;
			}
			if (arg.equals("-O") || arg.equals("--orientation")) {
				String s = argv[++i].toUpperCase();
				if (Orientation.VERTICAL.toString().startsWith(s)) {
					orientation = Orientation.VERTICAL;
				}
				else if (Orientation.HORIZONTAL.toString().startsWith(s)) {
					orientation = Orientation.HORIZONTAL;
				}
				else {
					System.err.println("error: invalid orientation ("+s+")");
					System.exit(1);
				}
				continue;
			}
			System.err.println("error: unknown argument ("+arg+")");
			System.exit(1);
		}

		// check params
		if (filename == null) {
			System.err.println("error: Missing output parameter");
			System.exit(1);
		}

		// create image and fill it with black
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(color);
		graphics.fillRect(0, 0, width, height);

		// apply gradients
		WritableRaster alpha = image.getAlphaRaster();
		int[] values = new int[3];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				values[0] = values[1] = values[2] = 0;
				alpha.setPixel(x, y, values);
			}
		}
		if (gradients != null && orientation.equals(Orientation.VERTICAL)) {
			for (Gradient gradient : gradients) {
				int delta = gradient.p2 - gradient.p1;
				if (delta >= 0) {
					for (int y = gradient.p1; y <= gradient.p2; y++) {
						int value = gradient.offset + (gradient.p2 - y) * gradient.step;
						values[0] = values[1] = values[2] = value;
						for (int x = 0; x < width; x++) {
							alpha.setPixel(x, y, values);
						}
					}
				}
				else {
					for (int y = gradient.p1; y >= gradient.p2; y--) {
						int value = gradient.offset + (y - gradient.p2) * gradient.step;
						values[0] = values[1] = values[2] = value;
						for (int x = 0; x < width; x++) {
							alpha.setPixel(x, y, values);
						}
					}
				}
			}
		}
		else if (gradients != null && orientation.equals(Orientation.HORIZONTAL)) {
			for (Gradient gradient : gradients) {
				int delta = gradient.p2 - gradient.p1;
				if (delta >= 0) {
					for (int x = gradient.p1; x < gradient.p2; x++) {
						int value = gradient.offset + (gradient.p2 - x) * gradient.step;
						values[0] = values[1] = values[2] = value;
						for (int y = 0; y < height; y++) {
							alpha.setPixel(x, y, values);
						}
					}
				}
				else {
					for (int x = gradient.p1; x >= gradient.p2; x--) {
						int value = gradient.offset + (x - gradient.p2) * gradient.step;
						values[0] = values[1] = values[2] = value;
						for (int y = 0; y < height; y++) {
							alpha.setPixel(x, y, values);
						}
					}
				}
			}
		}

		// write output
		ImageOutputStream out = new FileImageOutputStream(new File(filename));
		ImageWriter writer = (ImageWriter)ImageIO.getImageWritersByFormatName("PNG").next();
		writer.setOutput(out);
		writer.write(image);
		out.close();
	}

	//
	// Classes
	//

	static enum Orientation { VERTICAL, HORIZONTAL };

	static class Gradient {
		// Constants
		static final int DEFAULT_STEP = 1;
		static final int DEFAULT_OFFSET = 0;
		static final Pattern RE_GRADIENT = Pattern.compile(
			"(\\d+)"+				// p1
			"-"+
			"(\\d+)"+				// p2
			"(?:"+
				","+
				"(\\d+)"+			// step
				"(?:"+
					","+
					"(\\d+)"+		// offset
				")?"+
			")?"
		);
		// Data
		public int p1;
		public int p2;
		public int step = DEFAULT_STEP;
		public int offset = DEFAULT_OFFSET;
		// Constructors
		public Gradient(String s) {
			Matcher m = RE_GRADIENT.matcher(s);
			if (!m.matches()) throw new IllegalArgumentException();
			this.p1 = Integer.parseInt(m.group(1));
			this.p2 = Integer.parseInt(m.group(2));
			if (m.group(3) != null) {
				this.step = Integer.parseInt(m.group(3));
				if (m.group(4) != null) {
					this.offset = Integer.parseInt(m.group(4));
				}
			}

		}
		// Object methods
		public String toString() {
			StringBuilder str = new StringBuilder();
			str.append("p1=");
			str.append(this.p1);
			str.append(",");
			str.append("p2=");
			str.append(this.p2);
			str.append(",");
			str.append("step=");
			str.append(this.step);
			str.append(",");
			str.append("offset=");
			str.append(this.offset);
			return str.toString();
		}
	}

} // class PngGradients