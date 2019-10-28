import java.lang.System;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;


public class Main {

    // Semaphore semaphore = new Semaphore(5);

    static final int NUM_LOAN_OFFICERS = 1;
    static final int NUM_BANK_TELLERS = 2;
    static final int NUM_CUST = 5;

    // lists of customers requesting either bank teller or loan officers
    static LinkedList<Customer> bankTellerRequestList = new LinkedList<Customer>();
    static LinkedList<Customer> loanOfficerRequestList = new LinkedList<Customer>();

    static Semaphore bankTellerReqListNotEmpty = new Semaphore(0, true);
    static Semaphore loanOfficerReqListNotEmpty = new Semaphore(0, true);

    static Semaphore[] whichBankTellerSemaphores = new Semaphore[NUM_BANK_TELLERS];
    static Semaphore loanOfficerRequested = new Semaphore(0);

    static Semaphore[] depositProcessedByTeller = new Semaphore[NUM_BANK_TELLERS];
    static Semaphore[] customerResponseToDeposit = new Semaphore[NUM_BANK_TELLERS];

    static Semaphore[] withdrawProcessedByTeller = new Semaphore[NUM_BANK_TELLERS];
    static Semaphore[] customerResponseToWithdraw = new Semaphore[NUM_BANK_TELLERS];

    static Semaphore loanProcessedByLoanOfficer = new Semaphore(0);
    static Semaphore customerResponseToLoanApproval = new Semaphore(0);

    static Semaphore[] bankTellerAvailable = new Semaphore[NUM_CUST];
    static Semaphore[] loanOfficerAvailable = new Semaphore[NUM_CUST];

    // set to 1 because someone has to be able to acquire this to add something to the request list
    static Semaphore mutexForBankTellerReqList = new Semaphore(1, true);
    static Semaphore mutexForLoanOfficerReqList = new Semaphore(1, true);

    static Semaphore maxCustomers = new Semaphore(5, true);

    public static void main(String[] args) {
        System.out.println("Hello, World!");

        for (int i = 0; i < NUM_CUST; i++) {
            bankTellerAvailable[i] = new Semaphore(0);
            loanOfficerAvailable[i] = new Semaphore(0);
        }

        for (int i = 0; i < NUM_BANK_TELLERS; i++) {
            whichBankTellerSemaphores[i] = new Semaphore(0);

            depositProcessedByTeller[i] = new Semaphore(0);
            withdrawProcessedByTeller[i] = new Semaphore(0);

            customerResponseToDeposit[i] = new Semaphore(0);
            customerResponseToWithdraw[i] = new Semaphore(0);
        }

        // create list of bank teller threads
        BankTeller[] tellersList = new BankTeller[NUM_BANK_TELLERS];
        Thread[] bankTellerThreadList = new Thread[NUM_BANK_TELLERS];
        for (int i = 0; i < NUM_BANK_TELLERS; i++) {
            tellersList[i] = new BankTeller(i);
            bankTellerThreadList[i] = new Thread(tellersList[i]);
            bankTellerThreadList[i].start();
            System.out.println("Teller " + i + " created");
        }

        LoanOfficer loanOfficer = new LoanOfficer(0);
        Thread loanOfficerThread = new Thread(loanOfficer);
        loanOfficerThread.start();
        System.out.println("Loan Officer 0 created");

        // create list of customer thread
        Customer[] customersList = new Customer[NUM_CUST];
        Thread[] customerThreadList = new Thread[NUM_CUST];
        for (int i = 0; i < NUM_CUST; i++) {
            customersList[i] = new Customer(i);
            customerThreadList[i] = new Thread(customersList[i]);
            customerThreadList[i].start();
            System.out.println("Customer " + i + " created");
        }

        for (int i = 0; i < NUM_CUST; i++) {
            try {
                customerThreadList[i].join();
                System.out.println("Customer " + i + " joined");
            } catch (Exception e) {
                System.out.println("Error joining customer " + i);
            }
        }
        System.out.println("Done joining all threads");

        System.out.println("\n\t Bank Simulation Summary\n");
        System.out.println("\t\tEnding Balance \tLoan Amount\n");
        int totalBalance = 0;
        int totalLoan = 0;
        for (int i = 0; i < 5; i++) {
            System.out.println("Customer " + i + "\t" + customersList[i].balance+ "\t\t" + customersList[i].loansTaken);
            totalBalance = totalBalance + customersList[i].balance;
            totalLoan = totalLoan + customersList[i].loansTaken;
        }
        System.out.println("\nTotals\t\t" + totalBalance + "\t\t" + totalLoan);

        System.exit(10);
    }



}


