


public class Job {

    // given from jobs file
    String name;
    int arrivalTime;
    int timeRequired;

    // values that need to be calculated
    int waitTime;
    int executionTime;

    public Job(String name, int arrivalTime, int timeRequired) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.timeRequired = timeRequired;
    }

    public String toString() {
        return this.name + " " + this.arrivalTime + " " + this.timeRequired + " " + this.executionTime;
    }

}

