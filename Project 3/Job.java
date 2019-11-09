


public class Job {

    // given from jobs file
    String name;
    int arrivalTime;
    int duration;

    // values that need to be calculated
    // int startTime;
    int waitTime;
    int executionTime;

    public Job(String name, int arrivalTime, int duration) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.duration = duration;
    }

    public String toString() {
        return this.name + " " + this.arrivalTime + " " + this.duration;
    }

}

