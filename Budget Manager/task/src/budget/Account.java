package budget;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;


public class Account {
    private static BigDecimal balance;
    private static HashMap<Purchase, Categories> purchaseList;
    private static final Scanner keyboard = new Scanner(System.in);
    private static Status running = Status.RUNNING;
    public static final String MENU = """
            Choose your action:
            1) Add income
            2) Add purchase
            3) Show list of purchases
            4) Balance
            5) Save
            6) Load
            7) Analyze (Sort)
            0) Exit
                      """;
    public static final String CATEGORIES = """
            Choose the type of purchase
            1) Food
            2) Clothes
            3) Entertainment
            4) Other
            5) Back
                    """;
    public static final String PURCHASE_LIST = """
            Choose the type of purchases
            1) Food
            2) Clothes
            3) Entertainment
            4) Other
            5) All
            6) Back
                    """;
    public static final String SORT = """
            How do you want to sort?
            1) Sort all purchases
            2) Sort by type
            3) Sort certain type
            4) Back
            """;
    public static final String SORT_CERTAIN_TYPE = """
            1) Food
            2) Clothes
            3) Entertainment
            4) Other
            """;

    private static final File file = new File("C://test/purchases.txt");
    //private static final File file = new File("purchases.txt");

    public Account() {
        balance = BigDecimal.valueOf(0);
        purchaseList = new HashMap<>();
    }
    private void shutdown() {
        System.out.println("\nBye!");
        running = Status.SHUTDOWN;
        keyboard.close();
        //System.exit(0);
    }

    public void menu() {
        while (running == Status.RUNNING){
            System.out.print(MENU);
            switch (MenuSelection.values()[keyboard.nextInt()]) {
                case INCOME -> addIncome();
                case PURCHASE -> purchase();
                case LIST_PURCHASES -> showPurchaseList();
                case BALANCE -> showBalance();
                case SAVE -> save();
                case LOAD -> load();
                case ANALYZE -> sort();
                case EXIT -> shutdown();
                default -> {
                    // Do Nothing
                }
            }
        }
    }
    public void addIncome() {
        System.out.println("\nEnter an income:");
        balance = balance.add(keyboard.nextBigDecimal());
        System.out.println("Income was added!\n");
    }

    public void purchase() {
        System.out.println("");
        System.out.printf(CATEGORIES);
        switch (Categories.values()[keyboard.nextInt()]) {
            case FOOD:
                purchaseList.put(getItem(), Categories.FOOD);
                purchase();
                break;
            case CLOTHES:
                purchaseList.put(getItem(), Categories.CLOTHES);
                purchase();
                break;
            case ENTERTAINMENT:
                purchaseList.put(getItem(), Categories.ENTERTAINMENT);
                purchase();
                break;
            case OTHER:
                purchaseList.put(getItem(), Categories.OTHER);
                purchase();
                break;
            case ALL:
                System.out.println("");
                break;
            default:
        }
    }
    private Purchase getItem() {
        System.out.println("\nEnter purchase name: ");
        keyboard.nextLine();
        String name = keyboard.nextLine();
        System.out.println("Enter its price: ");
        BigDecimal price = keyboard.nextBigDecimal();
        balance = balance.subtract(price);
        System.out.println("Purchase was added!");
        return new Purchase(name, price);
    }
    public void showPurchaseList() {
        if(!purchaseList.isEmpty()) {
            System.out.println("");
            System.out.print(PURCHASE_LIST);
            switch (Categories.values()[keyboard.nextInt()]) {
                case FOOD: ListCategoryPurchases(Categories.FOOD); showPurchaseList(); break;
                case CLOTHES: ListCategoryPurchases(Categories.CLOTHES); showPurchaseList(); break;
                case ENTERTAINMENT: ListCategoryPurchases(Categories.ENTERTAINMENT); showPurchaseList(); break;
                case OTHER: ListCategoryPurchases(Categories.OTHER); showPurchaseList(); break;
                case ALL: ListCategoryPurchases(Categories.ALL); showPurchaseList(); break;
                case NOTHING : System.out.println(""); break;
                default: break;
            }
        } else
            System.out.println("\nThe purchase list is empty!\n");
    }
    private void ListCategoryPurchases(Categories category) {
        System.out.println("");
        String name  = category.name();
        name = name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
        System.out.println(name + ":");
        double total = 0;

        if(category.equals(Categories.ALL)) {
            for(var entry : purchaseList.entrySet()) {
                System.out.println(entry.getKey().getName() + " $" + entry.getKey().getCost());
                total += Double.parseDouble(String.valueOf(entry.getKey().getCost()));
            }
        } else {
            for (var entry : purchaseList.entrySet()) {
                if (entry.getValue().equals(category)) {
                    Purchase p = entry.getKey();
                    System.out.println(p.getName() + " $" + p.getCost());
                    total += Double.valueOf(String.valueOf(p.getCost()));
                }
            }
        }
        System.out.println("Total sum: $" + new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
        System.out.println("");
    }

    public void showBalance() {
        System.out.printf("\nBalance: $%f\n\n", balance);
    }
    public void save() {
        try(PrintWriter printWriter = new PrintWriter(file)) {
            for(var entry : purchaseList.entrySet()) {
                printWriter.println(entry.getValue() + ":" + entry.getKey().getName() + ":" + entry.getKey().getCost());
            }
            printWriter.println("Balance " + ":"  + balance );
            System.out.println("\nPurchases were saved!\n");

        } catch (FileNotFoundException e) {
            System.out.printf("An exception occurred %s", e.getMessage());
        }
    }
    public void load() {
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                if (!line.startsWith("Balance")) {
                    String[] parts = line.split(":");
                    String category = parts[0];
                    String name = parts[1];
                    BigDecimal price = new BigDecimal(parts[2]);
                    purchaseList.put(new Purchase(name, price), Categories.valueOf(category));
                } else {
                    double money = Double.parseDouble(line.split(":")[1]);
                    balance = BigDecimal.valueOf(money);
                }

            }
            System.out.println("\nPurchases were loaded!\n");
        } catch (FileNotFoundException ex) {
            System.out.println("No file found: " + file);
        }

    }

    public void sort(){

            System.out.println("");
            System.out.print(SORT);
            Scanner scanner = new Scanner(System.in);
            Integer sortMenu = scanner.nextInt();
            switch (sortMenu) {
                case 1: sortAll(); sort(); break;
                case 2: sortType(); sort(); break;
                case 3: sortCertainTypes(); sort(); break;
                case 4: System.out.println(""); break;
                default: break;
            }

    }

    private void sortAll() {
        if(!purchaseList.isEmpty()) {
            System.out.println("");
            System.out.println("All:");
            double total = 0;
            List<Map.Entry<Purchase, Categories>> entryList = new ArrayList<>(purchaseList.entrySet());

            Collections.sort(
                    entryList, Comparator.comparing(costPurchaseEntry -> costPurchaseEntry.getKey().getName())
            );
            Collections.sort(
                    entryList, Comparator.comparing(costPurchaseEntry -> costPurchaseEntry.getKey().getCost())
            );
            Collections.reverse(entryList);
            for (Map.Entry<Purchase, Categories> purchase : entryList) {
                System.out.println(purchase.getKey().getName() + " $" + purchase.getKey().getCost());
                total += Double.parseDouble(String.valueOf(purchase.getKey().getCost()));
            }
            System.out.println("Total sum: $" + new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
        } else
            System.out.println("\nThe purchase list is empty!");
    }
    private void sortType() {
        if(!purchaseList.isEmpty()) {
            double foodSum = 0;
            double clothesSum = 0;
            double entertainmentSum = 0;
            double otherSum = 0;


            for (var entry : purchaseList.entrySet()) {
                if (entry.getValue().equals(Categories.FOOD))
                    foodSum += Double.parseDouble(String.valueOf(entry.getKey().getCost()));
                else if (entry.getValue().equals(Categories.CLOTHES))
                    clothesSum += Double.parseDouble(String.valueOf(entry.getKey().getCost()));
                else if (entry.getValue().equals(Categories.ENTERTAINMENT))
                    entertainmentSum += Double.parseDouble(String.valueOf(entry.getKey().getCost()));
                else if (entry.getValue().equals(Categories.OTHER))
                    otherSum += Double.parseDouble(String.valueOf(entry.getKey().getCost()));

            }
            double total = foodSum + clothesSum + entertainmentSum + otherSum;
            HashMap<Categories, Double> sums = new HashMap<>();
            sums.put(Categories.FOOD, foodSum);
            sums.put(Categories.CLOTHES, clothesSum);
            sums.put(Categories.ENTERTAINMENT, entertainmentSum);
            sums.put(Categories.OTHER, otherSum);

            List<Map.Entry<Categories, Double>> entryList = new ArrayList<>(sums.entrySet());

            Collections.sort(
                    entryList, Map.Entry.comparingByValue()
            );
            Collections.reverse(entryList);
            System.out.println("\nTypes: ");
            for (Map.Entry<Categories, Double> type : entryList) {
                String name = type.getKey().name();
                name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
                System.out.println(name + " - $" + new BigDecimal(type.getValue()).setScale(2, RoundingMode.HALF_EVEN));
            }

            System.out.println("Total sum: $" + new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
        } else {
            System.out.println("\nTypes:\n" +
                    "Food - $0\n" +
                    "Entertainment - $0\n" +
                    "Clothes - $0\n" +
                    "Other - $0\n" +
                    "Total sum: $0");
        }

    }
    private void sortCertainTypes() {
            System.out.println("");
            //System.out.println(SORT_CERTAIN_TYPE);
            System.out.println("1) Food\n" +
                    "2) Clothes\n" +
                    "3) Entertainment\n" +
                    "4) Other");
            switch (Categories.values()[keyboard.nextInt()]) {
                case FOOD:
                    sortCertainType(Categories.FOOD);
                    break;
                case CLOTHES:
                    sortCertainType(Categories.CLOTHES);
                    break;
                case ENTERTAINMENT:
                    sortCertainType(Categories.ENTERTAINMENT);
                    break;
                case OTHER:
                    sortCertainType(Categories.OTHER);
                    break;
                default:
                    break;
            }
    }
    private void sortCertainType(Categories c) {
        if(!purchaseList.isEmpty()) {
            System.out.println("");
            String name = c.name();
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
            System.out.println(name + ":");
            double total = 0;
            HashMap<Purchase, Categories> purchaseType = new HashMap<>();
            for (var entry : purchaseList.entrySet()) {
                if (entry.getValue().equals(c)) {
                    purchaseType.put(entry.getKey(), entry.getValue());
                }
            }
            List<Map.Entry<Purchase, Categories>> entryList = new ArrayList<>(purchaseType.entrySet());
            Collections.sort(
                    entryList, Comparator.comparing(costPurchaseEntry -> costPurchaseEntry.getKey().getCost())
            );
            Collections.reverse(entryList);
            for (Map.Entry<Purchase, Categories> purchase : entryList) {
                System.out.println(purchase.getKey().getName() + " $" + purchase.getKey().getCost());
                total += Double.parseDouble(String.valueOf(purchase.getKey().getCost()));
            }
            System.out.println("Total sum: $" + new BigDecimal(total).setScale(2, RoundingMode.HALF_EVEN));
        } else
            System.out.println("\nThe purchase list is empty!");

    }
}

