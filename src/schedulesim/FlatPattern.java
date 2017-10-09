package schedulesim;

import java.util.ArrayList;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class FlatPattern implements MetataskPattern {

  private final int taskSize;
  private final int taskCount;

  public FlatPattern(int taskCount, int taskSize){
    this.taskCount = taskCount;
    this.taskSize = taskSize;
  }

  @Override
  public ArrayList<Task> generateMetatask() {
    ArrayList<Task> tasks = new ArrayList<>();
    for(int i = 0; i < taskCount; i++){
      tasks.add(new Task(taskSize));
    }
    return tasks;
  }

}
