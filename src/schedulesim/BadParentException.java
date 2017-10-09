package schedulesim;

/**
 * This work is licensed under the Creative Commons Attribution 4.0 International License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by/4.0/
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 * 
 * @author paul moggridge (paulmogs398@gmail.com)
 */
public class BadParentException extends Exception{
  
  private SimEntity parent;
  private ConsumingEntity child;
  
  public BadParentException(String message, SimEntity parent, ConsumingEntity child){
    super(message);
    this.parent = parent;
    this.child = child;
  }

  public SimEntity getParent() {
    return parent;
  }

  public ConsumingEntity getChild() {
    return child;
  }
  
  
}
