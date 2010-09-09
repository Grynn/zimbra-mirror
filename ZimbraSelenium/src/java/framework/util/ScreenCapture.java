package framework.util;
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class ScreenCapture {


	public static void capture(String fileName)  {
     // capture the whole screen
	  try{
        BufferedImage screencapture = new Robot().createScreenCapture(
          new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()) );

      //  Save as JPEG
       File file = new File(fileName);
       ImageIO.write(screencapture, "jpg", file);

	  }
	  catch ( AWTException ae ) {
		 ae.printStackTrace();
      // log
	  }
	  
	  catch ( IOException  io) {
		  io.printStackTrace();
	  // log
	  }
	  
	  
	}	  


	
	public static void main(String args[]) throws
    AWTException, IOException {
	  //capture the current screen
	  capture("sample.jpg");
    
    }

}
