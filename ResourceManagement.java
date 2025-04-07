import java.io.File;
import java.util.*;

public class ResourceManagement {
    private PriorityQueue<Department> departmentPQ;
    private List<Department> allDepartments;
    private Double remainingBudget;
    private Double budget;
    private List<String> purchasedItemsOutput;

    public static void printName() {
        System.out.println("This solution was completed by:");
        System.out.println("Joshua Guzman");
        System.out.println("Santiago Yuriar");
        System.out.println("Dawson Merriman");
        System.out.println("Matthew McCabe");
        System.out.println("Patrick Tilotta");
    }

    public ResourceManagement(String[] fileNames, Double budget) {
        departmentPQ = new PriorityQueue<>();
        allDepartments = new ArrayList<>();
        purchasedItemsOutput = new ArrayList<>();
        this.budget = budget;
        this.remainingBudget = budget;

        // Create departments from file names
        for (String fileName : fileNames) {
            Department dept = new Department(fileName);
            departmentPQ.add(dept);
            allDepartments.add(dept);
        }

        // Algorithm for distributing budget
        while (remainingBudget > 0 && !departmentPQ.isEmpty()) {
            Department dept = departmentPQ.poll();

            // Remove items that are too expensive
            while (!dept.itemsDesired.isEmpty() && dept.itemsDesired.peek().price > remainingBudget) {
                Item removedItem = dept.itemsDesired.poll();
                dept.itemsRemoved.add(removedItem);
            }

            // Process department
            if (dept.itemsDesired.isEmpty()) {
                // If no items are left, give a scholarship (but only if we still have departments in the PQ)
                if (departmentPQ.isEmpty() || remainingBudget <= 0) {
                    continue; // Skip if we're the last department or out of budget
                }
                
                double scholarship = Math.min(1000.0, remainingBudget);
                if (scholarship > 0) {
                    dept.priority += scholarship;
                    remainingBudget -= scholarship;
                    purchasedItemsOutput.add(String.format("Department of %-30s- %-30s- %30s", 
                                                           dept.name, "Scholarship", 
                                                           String.format("$%.2f", scholarship)));
                    // Add back to queue only if scholarship was given
                    departmentPQ.add(dept);
                }
            } else {
                // Buy the next affordable item
                Item item = dept.itemsDesired.poll();
                dept.itemsReceived.add(item);
                dept.priority += item.price;
                remainingBudget -= item.price;
                purchasedItemsOutput.add(String.format("Department of %-30s- %-30s- %30s",
                                                      dept.name, item.name,
                                                      String.format("$%.2f", item.price)));
                
                // Add the department back to the queue if it has more items or we could give it a scholarship
                if (!dept.itemsDesired.isEmpty() || remainingBudget >= 1000.0) {
                    departmentPQ.add(dept);
                }
            }
        }
        
        // Move any remaining desired items to the removed list
        while (!departmentPQ.isEmpty()) {
            Department dept = departmentPQ.poll();
            while (!dept.itemsDesired.isEmpty()) {
                dept.itemsRemoved.add(dept.itemsDesired.poll());
            }
        }
    }

    public void printSummary() {
        // First print purchased items
        System.out.println("ITEMS PURCHASED\n----------------------------");
        for (String line : purchasedItemsOutput) {
            System.out.println(line);
        }

        System.out.println();
        
        // Then print departmental summaries
        for (Department dept : allDepartments) {
            System.out.println(dept.name);
            System.out.printf("Total Spent             = $%.2f\n", dept.priority);
            System.out.printf("Percent of Budget = %.2f%%\n", (dept.priority / budget) * 100);
            System.out.println("----------------------------");
            
            System.out.println("ITEMS RECEIVED");
            if (dept.itemsReceived.isEmpty()) {
                System.out.println("None");
            } else {
                for (Item item : dept.itemsReceived) {
                    System.out.println(item.name);
                }
            }
            
            System.out.println("ITEMS NOT RECEIVED");
            if (dept.itemsRemoved.isEmpty() && dept.itemsDesired.isEmpty()) {
                System.out.println("None");
            } else {
                for (Item item : dept.itemsRemoved) {
                    System.out.println(item.name);
                }
                for (Item item : dept.itemsDesired) {
                    System.out.println(item.name);
                }
            }
            System.out.println();
        }
        
        // Print remaining budget
        System.out.printf("Remaining Budget: $%.2f\n", remainingBudget);
    }
}

class Department implements Comparable<Department> {
    String name;
    Double priority;
    Queue<Item> itemsDesired;
    Queue<Item> itemsReceived;
    Queue<Item> itemsRemoved;

    public Department(String fileName) {
        itemsDesired = new LinkedList<>();
        itemsReceived = new LinkedList<>();
        itemsRemoved = new LinkedList<>();
        priority = 0.0;

        Scanner input = null;
        try {
            File file = new File(fileName);
            input = new Scanner(file);

            // Read department name
            if (input.hasNextLine()) {
                name = input.nextLine().trim();
            }

            // Read items and prices
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) continue;
                
                String itemName = line;
                
                // Look for price on next line
                if (input.hasNextLine()) {
                    String priceLine = input.nextLine().trim();
                    if (!priceLine.isEmpty()) {
                        try {
                            double itemPrice = Double.parseDouble(priceLine);
                            itemsDesired.add(new Item(itemName, itemPrice));
                        } catch (NumberFormatException e) {
                            // If price can't be parsed, continue to next line
                            System.out.println("Error parsing price for item: " + itemName);
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
        } finally {
            if (input != null) input.close();
        }
    }

    @Override
    public int compareTo(Department other) {
        int priorityComparison = this.priority.compareTo(other.priority);
        return (priorityComparison != 0) ? priorityComparison : this.name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Department)) return false;
        Department that = (Department) obj;
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public String toString() {
        return "NAME: " + name + "\nPRIORITY: " + priority + "\nDESIRED: " + itemsDesired + 
               "\nRECEIVED " + itemsReceived + "\nREMOVED " + itemsRemoved + "\n";
    }
}

class Item {
    String name;
    Double price;

    public Item(String name, Double price) {
        this.name = name;
        this.price = price;
    }

    @Override
    public String toString() {
        return "{ " + name + ", $" + String.format("%.2f", price) + " }";
    }
}
