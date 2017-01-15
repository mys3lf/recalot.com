// Copyright (C) 2014-2015 Guibing Guo
//
// This file is part of LibRec.
//
// LibRec is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// LibRec is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with LibRec. If not, see <http://www.gnu.org/licenses/>.
//

package librec.util;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.imageio.ImageIO;

/**
 * 
 * * @author Guo Guibing  * @author Matthäus Schmedding (clean up and adjusting to recalot.com)
 *
 */
public class Systems {
	private static String desktopPath = null;

	public final static String FILE_SEPARATOR = System.getProperty("file.separator");
	public final static String USER_NAME = System.getProperty("user.name");
	public final static String USER_DIRECTORY = System.getProperty("user.home");
	public final static String WORKING_DIRECTORY = System.getProperty("user.dir");
	public final static String OPERATING_SYSTEM = System.getProperty("os.name");

	public enum OS {
		Windows, Linux, Mac
	}

	private static OS os = null;

    public static OS getOs() {
		if (os == null) {
			for (OS m : OS.values()) {
				if (OPERATING_SYSTEM.toLowerCase().contains(m.name().toLowerCase())) {
					os = m;
					break;
				}
			}
		}
		return os;
	}

}
