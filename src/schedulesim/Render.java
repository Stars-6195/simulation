package schedulesim;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public abstract class Render {
  
  private static final int MARGIN = 4;
  private Architecture architecture;
  
  public Render(Architecture architecture){
    this.architecture = architecture;
  }
  
  public void setArchitecture(Architecture architecture){
    this.architecture = architecture;
  }
  
  public Architecture getArchitecture(){
    return architecture;
  }
  
  public int getMargin(){
    return MARGIN;
  }

  public int getBiggestConsumerUPS(){
    int biggestConsumerUPS = -1;
    for (Consumer consumer : architecture.getConsumers()) {
      if (biggestConsumerUPS < consumer.getUnitsPerStep()) {
        biggestConsumerUPS = consumer.getUnitsPerStep();
      }
    }
    return biggestConsumerUPS;
  }
  
  public int getStepOfLastTaskToFinish(Consumer consumer){
    // last finished job
    int stepOfLastTaskToFinish = 0;
    for(Task task : consumer.getCompletedTasks()){
      if(task.getStepFinished() > stepOfLastTaskToFinish){
        stepOfLastTaskToFinish = task.getStepFinished();
      }
    }
    

    return stepOfLastTaskToFinish;
  }
  
  public int getWidth(int scaleStartPosition){
    
    int biggestTaskWidth = 0;
    for(Consumer consumer : architecture.getConsumers()){
      int stepOfLastTaskToFinish = getStepOfLastTaskToFinish(consumer);
      
      int delay = 0;
      for(Task task : consumer.getWaitingTasks()){
        delay += (task.getStartUnits() / consumer.getUnitsPerStep());
      }
      
      if(biggestTaskWidth < (stepOfLastTaskToFinish + delay)){
        biggestTaskWidth = (stepOfLastTaskToFinish + delay);
      }
    }
    
    return (biggestTaskWidth*2) + (scaleStartPosition + (MARGIN * 2));
  }

  public int getHeight(){
    return (architecture.getConsumers().size() * 2) + (MARGIN * 2);
  }

  public int getScaleStartPosition(int biggestConsumerUPS){
    return biggestConsumerUPS + MARGIN + 1;
  }
}
