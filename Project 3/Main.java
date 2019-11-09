import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {

		if (args.length < 2) {
			System.out.println("To run this program: \"java Main.java <input_filename> <scheduler_algo>");
			System.exit(10);
		}

		String filename = args[0];
		String schedulerName = args[1];

		if (filename.isEmpty() || filename == null) {
			System.out.println("Please enter a filename");
			System.exit(11);
		}

		if (schedulerName.isEmpty() || schedulerName == null) {
			System.out.println("Please enter a scheduler algorithm name");
			System.exit(12);
		}

		FileIO jobsFileIO = new FileIO(filename);
		ArrayList<String> jobsList = jobsFileIO.readFile();

		if (jobsList == null) {
			//System.out.println("Error reading file");
			System.exit(50);
		}

		int schedulerAlgo = Scheduler.FCFS;

		switch (schedulerName) {
			case "FCFS":
				schedulerAlgo = Scheduler.FCFS;
				break;
			case "RR":
				schedulerAlgo = Scheduler.RR;
				break;
			case "SPN":
				schedulerAlgo = Scheduler.SPN;
				break;
			case "SRT":
				schedulerAlgo = Scheduler.SRT;
				break;
			case "HRRN":
				schedulerAlgo = Scheduler.HRRN;
				break;
			case "FB":
				schedulerAlgo = Scheduler.FB;
				break;
			case "ALL":
				schedulerAlgo = Scheduler.ALL;
				break;
			default:
				break;
		}

		Scheduler scheduler = new Scheduler(schedulerAlgo);
		scheduler.run(jobsList);

	}

}






