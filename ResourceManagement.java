import java.io.File;
import java.util.*;

public class resourceManagement {
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

    public resourceManagement(String[] fileNames, Double budget) {
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
                if (departmentPQ.isEmpty() || remainingBudget <= 0) {
                    continue;
                }
                double scholarship = Math.min(1000.0, remainingBudget);
                if (scholarship > 0) {
                    dept.priority += scholarship;
                    Item scholarshipItem = new Item("Scholarship", scholarship);
                    dept.itemsReceived.add(scholarshipItem);
                    remainingBudget -= scholarship;
                    purchasedItemsOutput.add(String.format("Department of %-40s- %-30s- %30s",
                            dept.name, "Scholarship", String.format("$%.2f", scholarship)));
                    departmentPQ.add(dept);
                }
            } else {
                // Buy next item
                Item item = dept.itemsDesired.poll();
                dept.itemsReceived.add(item);
                dept.priority += item.price;
                remainingBudget -= item.price;
                purchasedItemsOutput.add(String.format("Department of %-40s- %-30s- %30s",
                        dept.name, item.name, String.format("$%.2f", item.price)));
                if (!dept.itemsDesired.isEmpty() || remainingBudget >= 1000.0) {
                    departmentPQ.add(dept);
                }
            }
        }

        // Move remaining items to removed
        while (!departmentPQ.isEmpty()) {
            Department dept = departmentPQ.poll();
            while (!dept.itemsDesired.isEmpty()) {
                dept.itemsRemoved.add(dept.itemsDesired.poll());
            }
        }
    }

    public void printSummary() {
        System.out.println("ITEMS PURCHASED\n----------------------------");
        for (String line : purchasedItemsOutput) {
            System.out.println(line);
        }

        System.out.println();

        for (Department dept : allDepartments) {
            System.out.println(dept.name);
            System.out.printf("Total Spent       = $%.2f\n", dept.priority);
            System.out.printf("Percent of Budget = %.2f%%\n", (dept.priority / budget) * 100);
            System.out.println("----------------------------");

            System.out.println("ITEMS RECEIVED");
            for (Item item : dept.itemsReceived) {
                System.out.printf("%-30s- %30s\n", item.name, String.format("$%.2f", item.price));
            }
            if (dept.itemsReceived.isEmpty()) System.out.println();

            System.out.println("ITEMS NOT RECEIVED");
            for (Item item : dept.itemsRemoved) {
                System.out.printf("%-30s- %30s\n", item.name, String.format("$%.2f", item.price));
            }
            for (Item item : dept.itemsDesired) {
                System.out.printf("%-30s- %30s\n", item.name, String.format("$%.2f", item.price));
            }
            if (dept.itemsRemoved.isEmpty() && dept.itemsDesired.isEmpty()) System.out.println();

            System.out.println();
        }

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
            if (input.hasNextLine()) {
                name = input.nextLine().trim();
            }
            while (input.hasNextLine()) {
                String line = input.nextLine().trim();
                if (line.isEmpty()) continue;
                String itemName = line;
                if (input.hasNextLine()) {
                    String priceLine = input.nextLine().trim();
                    if (!priceLine.isEmpty()) {
                        try {
                            double itemPrice = Double.parseDouble(priceLine);
                            itemsDesired.add(new Item(itemName, itemPrice));
                        } catch (NumberFormatException e) {
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
        return String.format("{ %s, $%.2f }", name, price);
    }
}
