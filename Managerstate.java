// Ivy - Manager

import java.util.*;

import javax.swing.LookAndFeel;

import java.text.*;
import java.io.*;
public class Managerstate extends WarehouseState {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private WarehouseContext context;
  private static Managerstate instance;
  private static final int EXIT = 0;
  private static final int MODIFY_PRICE = 1;
  private static final int ASSIGN_PRODUCT = 2; 
  private static final int ADD_MANUFACTURER = 3;
  private static final int LOAD_DATA = 4;
  private static final int CLERKMENU = 5;
  private static final int HELP = 6;
  private Managerstate() {
      super();
      warehouse = Warehouse.instance();
      //context = WarehouseContext.instance();
  }

  public static Managerstate instance() {
    if (instance == null) {
      instance = new Managerstate();
    }
    return instance;
  }

  public String getToken(String prompt) {
    do {
      try {
        System.out.println(prompt);
        String line = reader.readLine();
        StringTokenizer tokenizer = new StringTokenizer(line,"\n\r\f");
        if (tokenizer.hasMoreTokens()) {
          return tokenizer.nextToken();
        }
      } catch (IOException ioe) {
        System.exit(0);
      }
    } while (true);
  }
  private boolean yesOrNo(String prompt) {
    String more = getToken(prompt + " (Y|y)[es] or anything else for no");
    if (more.charAt(0) != 'y' && more.charAt(0) != 'Y') {
      return false;
    }
    return true;
  }
  public int getNumber(String prompt) {
    do {
      try {
        String item = getToken(prompt);
        Integer num = Integer.valueOf(item);
        return num.intValue();
      } catch (NumberFormatException nfe) {
        System.out.println("Please input a number ");
      }
    } while (true);
  }
  public Calendar getDate(String prompt) {
    do {
      try {
        Calendar date = new GregorianCalendar();
        String item = getToken(prompt);
        DateFormat df = SimpleDateFormat.getDateInstance(DateFormat.SHORT);
        date.setTime(df.parse(item));
        return date;
      } catch (Exception fe) {
        System.out.println("Please input a date as mm/dd/yy");
      }
    } while (true);
  }
  public int getCommand() {
    do {
      try {
        int value = Integer.parseInt(getToken("Enter command:" + HELP + " for help"));
        if (value >= EXIT && value <= HELP) {
          return value;
        }
      } catch (NumberFormatException nfe) {
        System.out.println("Enter a number");
      }
    } while (true);
  }


  //The manager operations:  Modify the sale price of an item, add a maufacturer, 
  //connect a product to a manufacturer.  All manager operations need a password for con rmation.


  //modifyprice()
  //addManufacturer()
  //Assign product for manufacturer


  public void help() {
    System.out.println("Enter a number between 0 and 6 as explained below:");
    System.out.println(EXIT + " to Exit\n");
    System.out.println(MODIFY_PRICE + " to modify price");
    System.out.println(ASSIGN_PRODUCT+ " to assign product to manufacturer");
    System.out.println(ADD_MANUFACTURER+ " to add manufacturer");
    System.out.println(LOAD_DATA + " to load warehouse data");
    System.out.println(CLERKMENU + " to  switch to the clerk menu");
    System.out.println(HELP + " for help");
  }

  public void loadData() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The Warehouse has been successfully retrieved from the file WarehouseData \n");
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesnt exist; creating new warehouse");
        warehouse = Warehouse.instance();
      }
    } catch (Exception cnfe) {
      cnfe.printStackTrace();
    }
  }

  public void clerkmenu() {
    (WarehouseContext.instance()).changeState(0);
  }

  public void assignProduct() {

    // get input from user. check if product exists
    String pID = getToken("Enter product ID: ");
    Product product;
    if ((product = warehouse.searchProduct(pID)) == null) {
      System.out.println("Product does not exist.");
      return;
    }

    // get input from user. check if manufacturer exists
    String mID = getToken("Enter manufacturer ID: ");
    Manufacturer m;
    if ((m = warehouse.searchManufacturer(mID)) == null) {
      System.out.println("No such manufacturer.");
      return;
    }

    // get price and quantity, turn to doubles
    double p, q;
    while (true) {
      String price = getToken("Enter product unit price: ");
      String quantity = getToken("enter product quantity to assign");
      try {
        p = Double.parseDouble(price);
        q = Double.parseDouble(quantity);
        break;
      } catch (NumberFormatException ignore) {
        System.out.println("Invalid input");
      }
    }

    // pass inputs to warehouse
    product = warehouse.assignProdToManufacturer(pID, mID, p, q);
    if (product != null) {
      System.out
          .println("Product: (" + product.getProductName() + ") was assigned too " + m.getManufacturerName() + "\n");
    } else {
      System.out.println("error in assigning product");
    }
  }

  public void modifyPrice(){
    String p = getToken("Please enter product ID: ");
    Product product = warehouse.searchProduct(p);
    String m = getToken("Please enter manufacturer ID: ");
    Manufacturer manufacturer = warehouse.searchManufacturer(m);
    Supplier s;
    String price;
    Double pr;
    if (product != null) {
      Iterator<Supplier> suppTraversal = warehouse.getManufacturerForProduct(product);
      while (suppTraversal.hasNext() != false) {
        s=suppTraversal.next();
        if(s.getManufacturer()==manufacturer){
          price = getToken("Enter new price: ");
          pr=Double.parseDouble(price);
          s.setNewPrice(pr);
          System.out.println("Price updated successfully");
          break;
        }
        System.out.println("Not found");
      }
      
  }
  else
  System.out.println("Not found");
}

  public void addnewManufacturer() {
    Manufacturer result;
    do {
      String mName = getToken("Enter name");
      String mAddress = getToken("Enter address");
      String mPhonenumber = getToken("Enter phone number");
      result = warehouse.addManufacturer(mName, mAddress, mPhonenumber);
      if (result != null) {
        System.out.println(result);
      } else {
        System.out.println("Manufacturer could not be added");
      }
      if (!yesOrNo("Add more Manufacturers?")) {
        break;
      }
    } while (true);
  }

  

  public void logout() {
    (WarehouseContext.instance()).changeState(3); // exit with a code 0
  }



  public void process() {
    int command;
    help();
    while ((command = getCommand()) != EXIT) {
      switch (command) {
        case MODIFY_PRICE:                  modifyPrice();
                                            break;
        case ASSIGN_PRODUCT:                assignProduct();
                                            break;
        case ADD_MANUFACTURER:              addnewManufacturer();
                                            break;
        case LOAD_DATA:                     loadData();
                                            break;
        case CLERKMENU:                     clerkmenu();
                                            break;
        case HELP:                          help();
                                            break;
      }
    }
    logout();
  }


  public void run() {
    process();
  }
}
