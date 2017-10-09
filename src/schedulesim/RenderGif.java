package schedulesim;

import schedulesim.gif.GifSequenceWriter;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class RenderGif extends Render {

  private final ArrayList<BufferedImage> frames;
  
  public RenderGif(Architecture architecture) {
    super(architecture);
    frames = new ArrayList<>();
  }
  
  public void writeToFile() throws IOException{
    String file = super.getArchitecture().getProducer().getName() + "_" + super.getArchitecture().getName() + ".gif";

      // Make frames a uniform size, nessary for
      // gif animation to work properly.
      unifyFrameSize();
      
      // create a new BufferedOutputStream
      ImageOutputStream output = 
        new FileImageOutputStream(new File(file));
      
      // create a gif sequence with the type of the first image, 1 second
      // between frames, which loops continuously
      GifSequenceWriter writer = 
        new GifSequenceWriter(output, frames.get(0).getType(), 1, true);
      
      // write out the first image to our sequence...
      writer.writeToSequence(frames.remove(0));
      while(frames.size()>0) {
        writer.writeToSequence(frames.remove(0));
      }
      
      writer.close();
      output.close();
  }
  
  private void unifyFrameSize(){
    
    // Find biggest width and height
    int targetWidth = findMaxWidth();
    int targetHeight = findMaxHeight();
    
    // Replace frame with a new consistently sized version of itself.
    for(int i = 0; i < frames.size(); i++){
      frames.set(i, extendOFrame(targetWidth, targetHeight, frames.get(i)));
    }    
  }
  
  private int findMaxWidth(){
    // Find frame with biggest width
    int width = 0;
    for(BufferedImage frame : frames){
      if(frame.getWidth() > width){
        width = frame.getWidth();
      }
    }
    return width;
  }
  
  private int findMaxHeight(){
    // Find frame with biggest height
    int height = 0;
    for(BufferedImage frame : frames){
      if(frame.getHeight() > height){
        height = frame.getHeight();
      }
    }
    return height;
  }
  
  private BufferedImage extendOFrame(int width, int height, BufferedImage frame) {

    // Create image to draw on
    BufferedImage newFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
    // Prepare to draw on image
    Graphics2D g = newFrame.createGraphics();

    // White background
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, width, height);
    
    // Draw the smaller image on to image.
    g.drawImage(frame,
      0, 0, frame.getWidth(), frame.getHeight(),
      0, 0, frame.getWidth(), frame.getHeight(), null);
    
    return newFrame;
  }
  
  // Call me after updating the architecture
  public void renderFrame(int step) {
    
    // Get sizes and measures
    int biggestConsumerUPS = super.getBiggestConsumerUPS();
    int scaleStartPosition =  getScaleStartPosition(biggestConsumerUPS);
    int width = getWidth(scaleStartPosition);
    int height = getHeight();

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
    for(Consumer consumer : super.getArchitecture().getConsumers() ){
      for(Task task : consumer.getCompletedTasks()){
        // Scanning through all tasks to find biggest and smallest
        if(task.getStartUnits() > max){
          max = task.getStartUnits();
        }
        if(task.getStartUnits() < min){
          min = task.getStartUnits();
        }
      }
      for(Task task : consumer.getWaitingTasks()){
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
      
      // Draw completed tasks in grey
      for(Task task : consumer.getCompletedTasks()){
        
        // Draw task start dot
        g.setColor(Color.CYAN);
        g.fillRect(((task.getStepProcessingStarted()*2)-1) + scaleStartPosition, consumerPos, 1, 1);
        
        // Draw task time line, (245 so small jobs are not white on white)
        int shade = 245 - (int)(245 * ((task.getStartUnits() - min) / range));
        g.setColor(new Color(shade,shade,shade));
        g.drawLine((task.getStepProcessingStarted() *2) + scaleStartPosition, consumerPos, (task.getStepFinished()*2) + scaleStartPosition, consumerPos);
      }

      // Draw buffered tasks in green
      for(Task task : consumer.getWaitingTasks()){
        
        // Place yellow dot to show when it is estimated this task will start
        g.setColor(Color.BLUE);
        
        // Estimate when the task will start
        int estStartStep = (estimateStartStep(consumer,task,step));
        
        // step + tasks before this task
        g.fillRect(((estStartStep*2)-1) + scaleStartPosition, consumerPos, 1, 1);
        
        // Draw task time line, (245 so small jobs are not white on white)
        int shade = 245 - (int)(245 * ((task.getStartUnits() - min) / range));
        g.setColor(new Color(0,shade,0));
        
        // Runtime of this task on this consumer, from the estimated start time
        int estTaskLength = estimateTaskLength(consumer, task);
        
        g.drawLine((estStartStep * 2) + scaleStartPosition,
          consumerPos,
          (estStartStep * 2) + (estTaskLength * 2) + scaleStartPosition,
          consumerPos);
      }

      consumerPos += 2;
    }
    
    // Draw current step line
    g.setColor(Color.MAGENTA);
    int currentStepLineX = scaleStartPosition + (step*2) - 1;
    g.drawLine(currentStepLineX, super.getMargin(), currentStepLineX, height-super.getMargin());
    
    // Write number showing current step
    String stepString = step + "";
    g.drawChars(stepString.toCharArray(), 0, stepString.length(), (currentStepLineX-15), height);
    
    // Add frame to list
    frames.add(image);
    
  }
  
  private int estimateStartStep(Consumer consumer, Task taskToEstimate, int step){
    // where not this step
    int delay = 0;
    
    int lengthAhead = 0;
    for(Task task : consumer.getWaitingTasks()){
      // Is it our tasks turn
      if(task == taskToEstimate){
        break;
      }
      // Add the delay, as this task will happen before taskToEstimate
      lengthAhead+= task.getRemaingUnits();
      
    }
    
    delay = lengthAhead / consumer.getUnitsPerStep();
    
    //return stepOfLastTaskToFinish + (int)delay;
    return step + delay ;
  }
  
  private int estimateTaskLength(Consumer consumer, Task task){
    return task.getRemaingUnits() / consumer.getUnitsPerStep();
  }
  
}
