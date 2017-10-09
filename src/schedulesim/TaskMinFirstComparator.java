package schedulesim;

import java.util.Comparator;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class TaskMinFirstComparator implements Comparator<Task>{

  @Override
  public int compare(Task o1, Task o2) {
    return Integer.compare(o1.getRemaingUnits(), o2.getRemaingUnits());
  }
}
