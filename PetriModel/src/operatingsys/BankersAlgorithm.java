package operatingsys;

import java.util.Arrays;
import java.util.Random;

class Customer implements Runnable {

    private int customersNum;
    private int resourceNum;
    private Thread thread;
    private String threadName = "listn t";

    Customer(int customerNum, int j) {
        threadName = Integer.toString(customerNum) + ':' + Integer.toString(j);
    }

    public void run() {
        int customersNum = this.customersNum;
        int resourceNum = this.resourceNum;
        Random rnd = new Random();
        int waitTime = rnd.nextInt(5) + 1;
        try {
            Thread.sleep(waitTime * 1000);
        } catch (InterruptedException ioe) {
        }
        BankersAlgorithm Banker = new BankersAlgorithm();
        Banker.releaseResource(customersNum, resourceNum);
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, threadName);
            thread.start();
        }
    }

}

public class BankersAlgorithm {

    public final static int customerNum = 5, resourcesNum = 3;// customerNum=n |resourcesNum=m.
    public static int[] avail = new int[resourcesNum];//currently available resources.
    public static int[][] max = new int[customerNum][resourcesNum]; //maximum how many resources each customer can request for.
    public static int[][] allocate = new int[customerNum][resourcesNum]; //Number of resources per customer.
    public static int[][] reserved = new int[customerNum][resourcesNum];//how many resources are already allocated to each customer 

    public static void main(String[] args) {
        for (int i = 0; i < resourcesNum; i++) {
            avail[i] = 20;
            for (int j = 0; j < customerNum; j++) {
                Random rnd = new Random();
                int rndNum = rnd.nextInt(7) + 1;
                max[j][i] = rndNum;
                allocate[j][i] = 0;
                reserved[j][i] = max[j][i] - allocate[j][i];
            }

        }
        System.out.println("customerNum : "+customerNum+", resourceNum : "+resourcesNum);
        int[] request = new int[resourcesNum];
        while (true) {
            Random rand = new Random();
            int customerNum = rand.nextInt(5); //randomly get customer
            System.out.println("Randomly slected customer No. "+customerNum);
            for (int i = 0; i < resourcesNum; i++) {
                request[i] = rand.nextInt(4);
            }
            System.out.println(Arrays.toString(request));
            int reqRescNum = requestResource(customerNum, request);
            if (reqRescNum == 0) {
                System.out.println("Allocated customer" + customerNum + " Resources -> : " + Arrays.toString(request));
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
            }
        }
    }

    public static int requestResource(int customerNum, int[] request) {
        if (rule(customerNum, request)) {
            for (int i = 0; i < resourcesNum; i++) {
                avail[i] -= request[i];
                allocate[customerNum][i] += request[i];
                reserved[customerNum][i] -= request[i];
                for (int j = 0; j < request[i]; j++) {
                    Customer customer = new Customer(customerNum, i);
                    customer.start();
                }
            }
            return 0;
        } else {
            return -1;
        }
    }

    public static boolean rule(int customerNum, int[] request) {
        boolean b = true;
        for (int i = 0; i < resourcesNum; i++) 
        {
            if (request[i] > reserved[customerNum][i]) {
                b = false;
                break;
            }
            if (b && request[i] > avail[i]) {
                b = false;
                break;
            }
        }
        return b;
    }

    public static int releaseResource(int custoimerNum, int resourceNum) {
        avail[resourceNum] += 1;
        reserved[custoimerNum][resourceNum] += 1;
        allocate[custoimerNum][resourceNum] -= 1;

        return 0;
    }

}
