package schedulesim;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import javax.imageio.ImageIO;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class RenderSchedule extends Render {
  
  public RenderSchedule(Architecture architecture) {
    super(architecture);
  }

  public void writeToFile(BufferedImage schedule) throws IOException{
    //Write image
    File outputfile = new File(super.getArchitecture().getProducer().getName() + "_" + super.getArchitecture().getName() + ".png");
    ImageIO.write(schedule, "png", outputfile);
  }
  
  public BufferedImage renderSchedule() {
    // Get sizes and measures
    int biggestConsumerUPS = super.getBiggestConsumerUPS();
    int scaleStartPosition =  super.getScaleStartPosition(biggestConsumerUPS);
    int width = super.getWidth(scaleStartPosition);
    int height = super.getHeight();

    // Create image to draw on
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    // Prepare to draw on image
    Graphics2D g = image.createGraphics();

    // White background
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);

    // Draw scale across top
    g.setColor(Color.BLACK);
    for(int i = 0; i < width; i=i+10){
      g.fillRect(scaleStartPosition + (i*2) + 1 , super.getMargin(), 1, 1);
    }
    scaleStartPosition = biggestConsumerUPS + super.getMargin() + 1;

    // Find biggest and smallest tasks to scale shading with
    int max = Integer.MIN_VALUE;
    int min = Integer.MAX_VALUE;
    for(Consumer consumer : super.getArchitecture().getConsumers()){
      for(Task task : consumer.getCompletedTasks()){
        // Scanning through all tasks to find biggest and smallest
        if(task.getStartUnits() > max){
          max = task.getStartUnits();
        }
        if(task.getStartUnits() < min){
          min = task.getStartUnits();
        }
      }
    }
    double range = max - min;

    // Draw consumers with their tasks
    int consumerPos = super.getMargin() + 2;

    Collections.sort(super.getArchitecture().getConsumers(), new ConsumingEntityMinFirstComparator());

    for(Consumer consumer : super.getArchitecture().getConsumers()){
      // Draw bar to represent consumer UPS
      g.setColor(Color.BLACK);
      g.drawLine(super.getMargin(), consumerPos, super.getMargin() + consumer.getUnitsPerStep(), consumerPos);
      // Draw tasks
      for(Task task : consumer.getCompletedTasks()){
        // Draw task start dot
        g.setColor(Color.CYAN);
        g.fillRect(((task.getStepProcessingStarted()*2)-1) + scaleStartPosition, consumerPos, 1, 1);
        // Draw task time line, (245 so small jobs are not white on white)
        int shade = 245 - (int)(245 * ((task.getStartUnits() - min) / range));
        g.setColor(new Color(shade,shade,shade));
        g.drawLine((task.getStepProcessingStarted() *2) + scaleStartPosition, consumerPos, (task.getStepFinished()*2) + scaleStartPosition, consumerPos);
      }

      consumerPos += 2;
    }
    
    return image;
  }
}
