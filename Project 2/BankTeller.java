


public class BankTeller implements Runnable {

    int id;
    Customer custBeingProcessed;

    public BankTeller(int id) {
        this.id = id;
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

        while (true) {
            try {
                Main.bankTellerReqListNotEmpty.acquire();

                // critical section - get next customer
                Main.mutexForBankTellerReqList.acquire();
                this.custBeingProcessed = Main.bankTellerRequestList.remove();
                Main.mutexForBankTellerReqList.release();

                // set bank teller to customer
                this.custBeingProcessed.bankTeller = this;

                System.out.println("Teller " + this.id + " begins serving customer " + this.custBeingProcessed.id);
                Main.bankTellerAvailable[custBeingProcessed.id].release();

                if (custBeingProcessed.currentTask == Customer.DEPOSIT) {

                    Main.whichBankTellerSemaphores[this.id].acquire();
                    this.custBeingProcessed.balance += custBeingProcessed.depositAmount;

                    // System.out.println("deposit added to balance");
                    // signal to customer that deposit is done
                    Main.depositProcessedByTeller[this.id].release();

                    // wait for customer to release
                    Main.customerResponseToDeposit[this.id].acquire();

                } else if (custBeingProcessed.currentTask == Customer.WITHDRAW) {
                    Main.whichBankTellerSemaphores[this.id].acquire();
                    this.custBeingProcessed.balance -= custBeingProcessed.withdrawAmount;

                    // System.out.println("withdrawal subtracted from balance");
                    // signal to customer that withdraw has been processed
                    Main.withdrawProcessedByTeller[this.id].release();

                    // wait for customer to release
                    Main.customerResponseToWithdraw[this.id].acquire();

                } else {
                    System.out.println("Error occurred while determining what teller " + this.id + " should do");
                }

            } catch (Exception e) {
                System.out.println("Error running BankTeller code");
            }
        }
    }

}


