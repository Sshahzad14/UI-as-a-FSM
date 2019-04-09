import java.util.*;
import java.text.*;
import java.io.*;
public class ClientState extends WarehouseState {
  private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
  private static Warehouse warehouse;
  private WarehouseContext context;
  private boolean running;
  private int exitCode;
  private static ClientState instance;
  private static final int EXIT = 0;
  private static final int VIEW_ACCOUNT = 1;
  private static final int PLACE_ORDER = 2;
  private static final int CHECK_PRICE_OF_PRODUCTS = 3;
  private static final int SALESCLERK_MENU = 4;
  private static final int MANAGER_MENU = 6;
  private static final int HELP = 13;
  private ClientState() {
      super();
      warehouse = Warehouse.instance();
      context = WarehouseContext.instance();
  }
  
  public static ClientState instance() {
    if (instance == null) {
      instance = new ClientState();
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

  public void help() {
    System.out.println("Enter a number as explained below:");
    System.out.println(EXIT + " to Exit\n");
    System.out.println(VIEW_ACCOUNT + " to view your client account");
    System.out.println(PLACE_ORDER + " to place an order");
    System.out.println(CHECK_PRICE_OF_PRODUCTS + " check the price of product(s)");
    System.out.println(SALESCLERK_MENU + " to switch to salesclerk menu");
    System.out.println(MANAGER_MENU + " to switch to manager menu");
    System.out.println(HELP + " for help");
  }


  public void viewAccount() {
	  
	String cid = context.getUser(); 
    
    Client client = warehouse.searchClient(cid);
    
    if(client != null)
    {

		System.out.println("==================================");
    	System.out.println("Viewing Client " + client.toString());
    	System.out.println("==================================");

        
        Iterator allOrders = warehouse.getWaitListedOrdersForClient(cid);
        
        if(allOrders != null)
        {
        	System.out.println("==================================");
            System.out.println("| Waitlisted Orders:");
            System.out.println("==================================");
            
            while(allOrders.hasNext())
            {
                Order order = (Order)(allOrders.next());
                if(order.getClient().getId().equals(cid))
                {
                    System.out.println(order.toString());
                }
            }
        }
        
        Iterator processedOrders = client.getOrders();
        
        if(processedOrders != null)
        {
            System.out.println("==================================");
            System.out.println("| Processed Orders:");
            System.out.println("==================================");
            
            while(processedOrders.hasNext())
            {
                Order order = (Order)(processedOrders.next());
                System.out.println(order.toString());
            }
        }

        
        
    }
  }
  
  public void placeOrder() {
	  String cid = context.getUser(); 
      
      Client client = warehouse.searchClient(cid);
      
      if(client != null)
      {
          String pid = getToken("Enter Product Id or 0 to stop");
          
          while(!(pid.equals("0"))){
              int quantity = getNumber("Enter quantity");
              if(quantity <= 0){
                  System.out.println("Invalid Quantity: Try Again");
                  continue;
              }
              boolean result = warehouse.addAndProcessOrder(cid, pid, quantity);
              pid = getToken("Enter Product Id or 0 to stop");
          }
      }
  }
  
  public void checkProductPrice()
  {
	  String id = getToken("Enter Product Id (Example. P1):");
	  
	  if (Warehouse.instance().searchProduct(id) != null)
	  {
		  System.out.println(Warehouse.instance().searchProduct(id)); 
	  }
	  else 
		  System.out.println("Invalid product id."); 
  }

  public void salesclerkmenu()
  {
       running = false;
       exitCode = 1;       
       
       //System.out.println("Switch to sales clerk menu requested."); 
  }

  public void managermenu()
  {
       running = false;
       exitCode = 2;       
       
       //System.out.println("Switch to manager menu requested."); 
  }
  
  public void setUID_tester(String uID)
  {
	  context.setUser(uID);  
  }

  public void logout()
  {
	   running = false; 
	   exitCode = 0;
	   //System.out.println("Logout requested"); 
  }
 
  public void terminate()
  {
    context.changeState(exitCode);
  }
  
  public void process() {
    int command;
    help();
    while (running) {
      command = getCommand();
      switch (command) {
        case EXIT:              logout();
                                break;
        case VIEW_ACCOUNT:      viewAccount();
                                break;
        case PLACE_ORDER:       placeOrder();
                                break;
        case CHECK_PRICE_OF_PRODUCTS: 
        						checkProductPrice();
                                break;
        case SALESCLERK_MENU:   salesclerkmenu();
                                break;
        case MANAGER_MENU:      managermenu();
                                break;
        case HELP:              help();
                                break;
      }
    }
    terminate();
  }
  
  public void run() {
    running = true;
    process();
    
  }
}
