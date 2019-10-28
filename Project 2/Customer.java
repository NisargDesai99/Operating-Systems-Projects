


public class Customer implements Runnable {

    static final int DEPOSIT = 1;
    static final int WITHDRAW = 2;
    static final int REQUESTLOAN = 3;

    int id;
    int visits = 3;
    int balance = 1000;
    int loansTaken = 0;

    int currentTask;

    int depositAmount = 0;
    int withdrawAmount = 0;
    int loanAmountRequested = 0;

    BankTeller bankTeller;
    LoanOfficer loanOfficer;

    public Customer(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        for (int i = 0; i < this.visits; i++) {
            try {
                Main.maxCustomers.acquire();

                this.currentTask = (int) (Math.random() * ((3 - 1) + 1)) + 1;
                // System.out.println(randomTask);

                if (this.currentTask == DEPOSIT) {
                    this.deposit();
                    this.bankTeller = null;
                } else if (this.currentTask == WITHDRAW) {
                    this.withdraw();
                    this.bankTeller = null;
                } else if (this.currentTask == REQUESTLOAN) {
                    this.requestLoan();
                    this.loanOfficer = null;
                } else {
                    System.out.println("Incorrect task");
                }

                Main.maxCustomers.release();

            } catch (Exception e) {
                System.out.println("Error processing customer side at cust: " + this.id);
                e.printStackTrace();
            }

        }

    }

    public void deposit() {
        this.depositAmount = 100 * (1 + (int) (Math.random() * 5));
        try {
            Main.mutexForBankTellerReqList.acquire();
            Main.bankTellerRequestList.add(this);

            // signal that a bank teller is needed to process this deposit
            Main.bankTellerReqListNotEmpty.release();
            Main.mutexForBankTellerReqList.release();

            // wait until a bank teller is ready to process this deposit
            Main.bankTellerAvailable[this.id].acquire();
            System.out.println("Customer " + this.id + " requests of teller " + this.bankTeller.id + " to make a deposit of $" + this.depositAmount);

            // signal teller to start processing request
            Main.whichBankTellerSemaphores[this.bankTeller.id].release();

            // wait for teller to process the transaction
            Main.depositProcessedByTeller[this.bankTeller.id].acquire();

            // finish the transaction and release bank teller
            System.out.println("Customer " + this.id + " gets deposit receipt from teller " + this.bankTeller.id);
            this.depositAmount = 0;
            Main.customerResponseToDeposit[this.bankTeller.id].release();

        } catch (Exception e) {
            System.out.println("Error processing deposit on customer side at cust: " + this.id);
            e.printStackTrace();
        }

    }

    public void withdraw() {
        this.withdrawAmount = 100 * (1 + (int) (Math.random() * 5));
        try {

            Main.mutexForBankTellerReqList.acquire();
            Main.bankTellerRequestList.add(this);
            Main.bankTellerReqListNotEmpty.release();
            Main.mutexForBankTellerReqList.release();

            // wait until a bank teller is ready to process this withdrawal
            Main.bankTellerAvailable[this.id].acquire();
            System.out.println("Customer " + this.id + " requests of teller " + this.bankTeller.id + " to make a withdrawal of $" + this.withdrawAmount);

            // tell teller to process
            Main.whichBankTellerSemaphores[this.bankTeller.id].release();

            // wait until processed by teller
            Main.withdrawProcessedByTeller[this.bankTeller.id].acquire();

            System.out.println("Customer " + this.id + " gets cash and receipt from teller " + this.bankTeller.id);
            this.withdrawAmount = 0;
            Main.customerResponseToWithdraw[this.bankTeller.id].release();

            // System.out.println("Customer " + this.id + " about to withdraw money; Visits left: " + this.visits);
        } catch (Exception e) {
            System.out.println("Error processing withdraw on customer side at cust: " + this.id);
            e.printStackTrace();
        }
    }

    public void requestLoan() {
        this.loanAmountRequested = 100 * (1 + (int) (Math.random() * 5));
        try {
            Main.mutexForLoanOfficerReqList.acquire();
            Main.loanOfficerRequestList.add(this);
            Main.loanOfficerReqListNotEmpty.release();
            Main.mutexForLoanOfficerReqList.release();

            Main.loanOfficerAvailable[this.id].acquire();
            System.out.println("Customer " + this.id + " requests of Loan Officer to apply for a loan of $" + this.loanAmountRequested);

            Main.loanOfficerRequested.release();

            Main.loanProcessedByLoanOfficer.acquire();

            System.out.println("Customer " + this.id + " gets loan from loan officer");
            this.loanAmountRequested = 0;
            Main.customerResponseToLoanApproval.release();

        } catch (Exception e) {
            System.out.println("Error processing loan request on customer side at cust: " + this.id);
            e.printStackTrace();
        }

    }

}

