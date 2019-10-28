


public class LoanOfficer implements Runnable {

    int id;
    Customer custBeingProcessed;

    public LoanOfficer(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        while(true) {
            try {
                Main.loanOfficerReqListNotEmpty.acquire();

                // critical section to provide exclusion for getting something from request list
                Main.mutexForLoanOfficerReqList.acquire();
                this.custBeingProcessed = Main.loanOfficerRequestList.remove();
                Main.mutexForLoanOfficerReqList.release();

                // signal loan officer is available to serve a particular customer
                Main.loanOfficerAvailable[this.custBeingProcessed.id].release();

                if (this.custBeingProcessed.currentTask == Customer.REQUESTLOAN) {

                    // wait until customer signals a request ot process loan
                    Main.loanOfficerRequested.acquire();
                    this.custBeingProcessed.loansTaken += this.custBeingProcessed.loanAmountRequested;

                    Main.loanProcessedByLoanOfficer.release();

                    Main.customerResponseToLoanApproval.acquire();
                }

            } catch (Exception e) {
                System.out.println("Error occured while trying to process loan for customer " + ((this.custBeingProcessed == null) ? "null" : this.custBeingProcessed.id));
            }
        }
    }



}


