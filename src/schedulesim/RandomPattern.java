package schedulesim;

import java.util.ArrayList;
import java.util.Random;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class RandomPattern implements MetataskPattern {

  private final int start;
  private final int end;
  private final int count;

  public RandomPattern(int start, int end, int count){
    this.start = start;
    this.end = end;
    this.count = count;
  }

  @Override
  public ArrayList<Task> generateMetatask() {
    ArrayList<Task> tasks = new ArrayList<>();
    Random random = new Random();
    for(int i = 0; i < count; i++){
      tasks.add(new Task(random.nextInt(end-start)+start));
    }
    return tasks;
  }


}
