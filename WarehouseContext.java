import java.util.*;
import java.text.*;
import java.io.*;
public class WarehouseContext {
  
  private int currentState;
  private static Warehouse warehouse;
  private static WarehouseContext context;
  private int currentUser;
  private String userID;
  private BufferedReader reader = new BufferedReader(new 
                                      InputStreamReader(System.in));
  public static final int IsClient = 0;
  public static final int IsSalesClerk = 1;
  public static final int IsManager = 2;
  /*private LibState[] states;
  private int[][] nextState;*/

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

  private void retrieve() {
    try {
      Warehouse tempWarehouse = Warehouse.retrieve();
      if (tempWarehouse != null) {
        System.out.println(" The warehouse has been successfully retrieved from the file WarehouseData \n" );
        warehouse = tempWarehouse;
      } else {
        System.out.println("File doesnt exist; creating new warehouse" );
        warehouse = Warehouse.instance();
      }
    } catch(Exception cnfe) {
      cnfe.printStackTrace();
    }
  }

  //public void setLogin(int code)
  //{currentUser = code;}
  
  public void changeState(int exitCode) {
    if (exitCode == 0 )
        System.out.println(" Return to Login state" );
    if (exitCode == 1 )
        System.out.println(" Switch to menu for SalesClerk" );
    if (exitCode == 2 )
        System.out.println(" Switch to menu for Manager" );
  }
    
  public void setUser(String uID) {
   userID = uID; //System.out.println(userID);
  }

  //public int getLogin()
  //{ return currentUser;}

  public String getUser()
  { return userID;}

  private WarehouseContext() { //constructor
    System.out.println("In WarehouseContext constructor");
    if (yesOrNo("Look for saved data and  use it?")) {
      retrieve();
    } else {
      warehouse = Warehouse.instance();
    }
     
  }

   
  public static WarehouseContext instance() {
    if (context == null) {
       System.out.println("calling constructor");
      context = new WarehouseContext();
    }
    return context;
  }

  /*public void process(){
    states[currentState].run();
  }*/
  
  /*public static void main (String[] args){
    WarehouseContext.instance().process(); 
  }*/


}
