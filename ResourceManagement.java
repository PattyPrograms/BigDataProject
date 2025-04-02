import java.io.File;
import java.util.*;
public class ResourceManagement
{
  private PriorityQueue<Department> departmentPQ; /* priority queue of departments */
  private Double remainingBudget;                 /* the budget left after purchases are made (should be 0 after the constructor runs) */
  private Double budget;                          /* the total budget allocated */
  
  /* TO BE COMPLETED BY YOU
   * Fill in your name in the function below
   */  
  public static void printName( )
  {
    /* TODO : Fill in your name */
    System.out.println("This solution was completed by:");
    System.out.println("Santiago Yuriar");
    System.out.println("Dawson Merriman");
    System.out.println("Matthew McCabe");
    System.out.println("Patrick Tilotta");
  }

  /* Constructor for a ResourceManagement object
   * TODO
   * Simulates the algorithm from the pdf to determine what items are purchased
   * for the given budget and department item lists.
   */
  public ResourceManagement( String fileNames[], Double budget )
  {
    /* Create a department for each file listed in fileNames */
      for (int a = 0; a < fileNames.length; a++) {
          Department dept = new Department("department" + a);
          departmentList.add(dept);

          try (Scanner scanner = new Scanner(new File(fileNames[a]))) {
              if (scanner.hasNextLine()) {
                  dept.name = scanner.nextLine(); // Reads the first line
              }
          } catch (IOException e) {
              e.printStackTrace();
          }
      }

      // Print department names For Test
      for (Department dept : departmentList) {
          System.out.println(dept.name);
      }
    
    /* Simulate the algorithm for picking the items to purchase */
    /* Be sure to print the items out as you purchase them */
    /* Here's the part of the code I used for printing prices as items */
    //String price = String.format("$%.2f", /*Item's price*/ );
    //System.out.printf("Department of %-30s- %-30s- %30s\n", /*Department's name*/, /*Item's name*/, price );
    
    
  } 

  /* printSummary
   * TODO
   * Print a summary of what each department received and did not receive.
   * Be sure to also print remaining items in each itemsDesired Queue.
   */      
 public void printSummary(Item item){
    
    /* Here's the part of the code I used for printing prices */
    //String price = String.format("$%.2f", /*Item's price*/ );
    //System.out.printf("%-30s - %30s\n", /*Item's name*/, price );
    String price = String.format("%.2f", item(Price));
    System.out.printf("%-30s - %30s\n", item.name, item.price );
  }   
}

/* Department
 *
 * Stores the information associated with a Department at the university
 */
class Department implements Comparable<Department> {
    String name;
    Double priority;
    Queue<Item> itemsDesired;
    Queue<Item> itemsReceived;
    Queue<Item> itemsRemoved;

    // Constructor
    public Department(String fileName) {
        itemsDesired = new LinkedList<>();
        itemsReceived = new LinkedList<>();
        itemsRemoved = new LinkedList<>();
        priority = 0.0; // Initially, the department's priority (money spent) is 0.

        Scanner input = null;
        try {
            File file = new File(fileName);
            input = new Scanner(file);

            if (input.hasNextLine()) {
                name = input.nextLine().trim();  // Read full department name
            }

            while (input.hasNext()) {
                String itemName = input.next();
                if (input.hasNextDouble()) {
                    double itemPrice = input.nextDouble();
                    itemsDesired.add(new Item(itemName, itemPrice));
                } else {
                    itemName += " " + input.nextLine();
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
        } finally {
            if (input != null) {
                input.close();  // Ensure the file is closed properly
            }
        }
    }

    @Override
    public int compareTo(Department dept) {
        int priorityComparison = this.priority.compareTo(dept.priority);
        return (priorityComparison != 0) ? priorityComparison : this.name.compareTo(dept.name);
    }

  public boolean equals( Department dept ){
    return this.name.compareTo( dept.name ) == 0;
  }

  @Override 
  @SuppressWarnings("unchecked") //Suppresses warning for cast
  public boolean equals(Object aThat) {
    if (this == aThat) //Shortcut the future comparisons if the locations in memory are the same
      return true;
    if (!(aThat instanceof Department))
      return false;
    Department that = (Department)aThat;
    return this.equals( that ); //Use above equals method
  }
  
  @Override
  public int hashCode() {
    return name.hashCode(); /* use the hashCode for data stored in this name */
  }

  /* Debugging tool
   * Converts this Department to a string
   */	
  @Override
  public String toString() {
    return "NAME: " + name + "\nPRIORITY: " + priority + "\nDESIRED: " + itemsDesired + "\nRECEIVED " + itemsReceived + "\nREMOVED " + itemsRemoved + "\n";
  }
}

/* Item
 *
 * Stores the information associated with an Item which is desired by a Department
 */
class Item
{
  String name;    /* name of this item */
  Double price;   /* price of this item */

  /*
   * Constructor to build a Item
   */
  public Item( String name, Double price ){
    this.name = name;
    this.price = price;
  }

  /* Debugging tool
   * Converts this Item to a string
   */		
  @Override
  public String toString() {
    return "{ " + name + ", " + price + " }";
  }
}
