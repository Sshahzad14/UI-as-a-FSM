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
      context = WarehouseContext.instance();
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

  public void clerkmenu() {
    (WarehouseContext.instance()).changeState(0);
  }

   public void assignProduct()
   {
        boolean result;
        System.out.println("Assigning Product To Manufacturer");
        System.out.println("=========================");
        String pid = getToken("Enter Product Id (Example P1):");
        String mid = getToken("Enter Manufacturer Id (Example M1):");
        result = warehouse.assignProductToManufacturer(pid, mid);

        if(result == true){
            System.out.println("SUCCESS: Assigned product to manufacturer ");
        }
        else
        {
            System.out.println("FAILED to assign product");
        }
   }

   public void modifyPrice(){
    String p = getToken("Please enter product ID: ");
    Product product = warehouse.searchProduct(p);
    String m = getToken("Please enter manufacturer ID: ");
    Manufacturer manufacturer = warehouse.searchManufacturer(m);
    Manufacturer s;
    String price;
    Double pr;
    if (product != null) {
      Iterator<Manufacturer> suppTraversal = warehouse.getManufacturers(product);
      while (suppTraversal.hasNext() != false) {
        s=suppTraversal.next();
        if(s.getManufacturers()==manufacturer){
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

public void addManufacturer()
    {
        String name = getToken("Enter manufacturer name");
        String address = getToken("Enter address");
        String phone = getToken("Enter phone");
        Manufacturer result;
        result = warehouse.addManufacturer(name, address, phone);

        if(result == null)
        {
            System.out.println("Could not add manufacturer");
        }

        System.out.println(result);
    }

  

  public void logout() {
    (WarehouseContext.instance()).changeState(2); // logout to manager state
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
        case ADD_MANUFACTURER:              addManufacturer();
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
