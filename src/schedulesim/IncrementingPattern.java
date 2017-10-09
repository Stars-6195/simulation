package schedulesim;

import java.util.ArrayList;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class IncrementingPattern implements MetataskPattern{

  private final int start;
  private final int stop;

  public IncrementingPattern(int start, int stop){
    this.start = start;
    this.stop = stop;
  }

  @Override
  public ArrayList<Task> generateMetatask() {
    ArrayList<Task> tasks = new ArrayList<>();
    for(int i = start; i < stop; i++){
      tasks.add(new Task(i));
    }
    return tasks;
  }

}
