import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
// import java.util.LinkedList;
// import java.util.Queue;
import java.util.LinkedList;
import java.util.Queue;


// TODO: make FCFS, SPN and HRRN work for cases where the CPU has to be idle


public class Scheduler {

	int algorithm;

    public static final int FCFS = 1;
    public static final int RR = 2;
    public static final int SPN = 3;
    public static final int SRT = 4;
    public static final int HRRN = 5;
    public static final int FB = 6;
    public static final int ALL = 7;

	public Scheduler(int algorithm) {
		this.algorithm = algorithm;
	}

	// private void buildOutput(StringBuilder bldr, String job, String prev) {
	// 	String[] jobDetails = job.split("\t");
	// 	String[] prevDetails = ((prev == null) ? null : prev.split("\t"));

	// 	bldr.append(jobDetails[0]).append("\t");

	// 	if (prevDetails != null) {
	// 		for (int i = 0; i < (int)prevDetails[1]; i++) {
	// 			bldr.append(" ");
	// 		}
	// 	}

	// 	for (int i = 0; i < (int)jobDetails[1]; i++) {
	// 		bldr.append("X");
	// 	}
	// }

	private void printArr(String[] jobStr) {
		for (int i = 0; i < jobStr.length; i++) {
			System.out.print(jobStr[i] + " ");
		}
	}

	private void print(ArrayList<Job> jobs) {
		StringBuilder bldr = new StringBuilder();
		for (Job job : jobs) {
			bldr.append(job.toString()).append("\n");
		}
		System.out.println(bldr.toString());
	}

	public void run(ArrayList<String> jobs) {

		// Queue<Job> jobsQueue = new LinkedList<Job>();

		ArrayList<Job> jobsList = new ArrayList<>();

        for (String s : jobs) {
			String[] jobStr = s.split("\t");
			jobsList.add(new Job(jobStr[0], Integer.valueOf(jobStr[1]), Integer.valueOf(jobStr[2])));
		}

		// print(jobsList);

		switch (this.algorithm) {
			case FCFS:
				this.FCFS(jobsList);
				break;
			case RR:
				this.RR(jobsList, 1);
				break;
			case SPN:
				this.SPN(jobsList);
				break;
			case SRT:
				this.SRT(jobsList);
				break;
			case HRRN:
				this.HRRN(jobsList);
				break;
			case FB:
				this.FB(jobsList, 1);
				break;
			case ALL:
				ArrayList<Job>[] jobsForAllSchedulers = new ArrayList[6];
				for (int i = 0; i < 6; i++) {
					jobsForAllSchedulers[i] = jobsList;
				}
				this.ALL(jobsList);
				break;
			default:
				break;
		}

	}

	public void FCFS(ArrayList<Job> jobs) {

		StringBuilder bldr = new StringBuilder();
		int lastIdx = -1;

        bldr.append("FCFS").append("\n");
        int prevDuration = 0;

        // System.out.println("jobs.size(): " + jobs.size() + "  queue.size(): " + queue.size());
		// System.out.println("Starting queue...");
		int counter = 0;
        // while (!jobs.isEmpty()) {
		while (counter < jobs.size()) {
            // System.out.println("jobs.size(): " + jobs.size() + "  queue.size(): " + queue.size());
            // queue.add(jobs.remove(0));

			int arrivalIdx = checkForArrivals(jobs, counter, lastIdx);
			if (arrivalIdx == -1) {
				prevDuration++;
				counter++;
				continue;
			}

			// Job j = jobs.remove(0);
			Job j = jobs.get(counter);
            bldr.append(j.name).append("\t|");

            for (int i = 0; i < prevDuration; i++) {
                bldr.append(" ");
            }

            for (int i = 0; i < j.duration; i++) {
                bldr.append("X");
            }

            bldr.append("\n");
			prevDuration += j.duration;

			counter++;
        }

        System.out.println(bldr.toString());

		// StringBuilder bldr = new StringBuilder();
		// int jobsLength = jobs.length();
		// String prev = "";
		// for (int i = 0; i < jobs.length(); i++) {
			// String job = jobs.remove();
			// buildOutput(bldr, job, ((i == 0) ? null : prev);
			// prev = job;
		// }
	}

	private int checkForArrivals(ArrayList<Job> jobsList, int currentTime, int lastIdx) {

		int counter = 0;

		for (Job job : jobsList) {

			if (currentTime < job.arrivalTime) {
				return -1;
			}

			// if (alreadyArrived.containsKey(job)) {
			// 	counter++;
			// 	continue;
			// }

			// TODO: I think there may be an issue with the counter == lastIdx here
			if (currentTime >= job.arrivalTime) {
				if (counter <= lastIdx) {
					counter++;
					continue;
				}
				return counter;
			}
		}

		return counter;
	}

	public void printQueue(Queue<Job> queue) {
		StringBuilder bldr = new StringBuilder();

		for (Job job : queue) {
			bldr.append(job.toString()).append(", ");
		}

		System.out.println(bldr.toString());
	}

	public void RR(ArrayList<Job> jobs, int timeQuantum) {
		Queue<Job> readyQueue = new LinkedList<>();

		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];
		HashMap<Job, Integer> jobToBldrIdx = new HashMap<>();
		bldr.append("RR\n");

		int lastIdx = -1;
		Job previousJob = null;
		int currentTime = 0;

		// jobToBldrIdx.put(jobs.get(0), 0);
		// readyQueue.add(jobs.get(0));

		int jobCounter = -1;
		while (jobCounter < jobs.size() - 1 || !readyQueue.isEmpty()) {


			// add arriving jobs to readyQueue
			int nextJobIdx = -1;
			Job nextJob = null;
			if (jobCounter < jobs.size() - 1) {
				System.out.println("checking for arrival");
				nextJobIdx = checkForArrivals(jobs, currentTime, lastIdx);
			}

			if (nextJobIdx != -1) {
				nextJob = jobs.get(nextJobIdx);
				System.out.println("arrival: " + nextJob.toString());
			}

			if (nextJob != null && !jobToBldrIdx.containsKey(nextJob)) {
				jobToBldrIdx.put(nextJob, nextJobIdx);
				readyQueue.add(nextJob);
				jobCounter++;
			}

			if (!readyQueue.isEmpty()) {

				printQueue(readyQueue);
				Job runningJob = readyQueue.remove();

				System.out.println("running " + runningJob.toString() + "; currentTime: " + currentTime);

				runningJob.executionTime += timeQuantum;
				runningJob.duration -= timeQuantum;

				// build output
				int runningBldrIdx = jobToBldrIdx.get(runningJob);
				if (builders[runningBldrIdx] == null) {
					builders[runningBldrIdx] = new StringBuilder();
					builders[runningBldrIdx].append(runningJob.name).append("\t|");
				}

				int spaces = Math.abs(currentTime - (builders[runningBldrIdx].length() - 3));
				if (previousJob != null && runningJob.name == previousJob.name) {
					spaces = 0;
				}

				for (int i = 0; i < spaces; i++) {
					builders[runningBldrIdx].append("-");
				}

				builders[runningBldrIdx].append("X");

				lastIdx = nextJobIdx;

				if (jobCounter < jobs.size() - 1) {
					System.out.println("checking for arrival");
					nextJobIdx = checkForArrivals(jobs, currentTime, lastIdx);
				}

				if (nextJobIdx != -1) {
					nextJob = jobs.get(nextJobIdx);
					System.out.println("arrival: " + nextJob.toString());
				}

				if (nextJob != null && !jobToBldrIdx.containsKey(nextJob)) {
					jobToBldrIdx.put(nextJob, nextJobIdx);
					readyQueue.add(nextJob);
					jobCounter++;
				}

				// add job to ready queue again if the job is not done yet
				if (runningJob.duration > 0) {
					readyQueue.add(runningJob);
				}

				lastIdx = nextJobIdx;
				previousJob = runningJob;
			}

			// increment timer
			currentTime++;
		}

		for (int i = 0; i < builders.length; i++) {
			if (builders[i] == null) {
				builders[i] = new StringBuilder();
				builders[i].append("empty");
			}
			bldr.append(builders[i].toString()).append("\n");
		}

		System.out.println(bldr.toString());
	}

	public void SPN(ArrayList<Job> jobs) {

		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];
		HashSet<Job> alreadyPicked = new HashSet<>();

		int currentTime = 0;

		bldr.append("SPN\n");

		int index = 0;
        while (index < jobs.size()) {

			int pickedJob = pickJob(jobs, alreadyPicked, currentTime, Scheduler.SPN);
			Job currentJob = jobs.get(pickedJob);
			alreadyPicked.add(currentJob);

			for (int i = 0; i < currentTime; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append(" ");
			}

			for (int i = 0; i < currentJob.duration; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append("X");
			}

			builders[pickedJob].append("\n");
			currentTime += currentJob.duration;

			index++;
		}

		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i]);
		}

		System.out.println(bldr);
	}

	public void SRT(ArrayList<Job> jobs) {

	}

	public void HRRN(ArrayList<Job> jobs) {
		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];
		HashSet<Job> alreadyPicked = new HashSet<>();

		int currentTime = 0;

		bldr.append("HRRN\n");

		// get wait times
		// for (int i = 1; i < jobs.size(); i++) {
		// 	jobs.get(i).waitTime += Math.abs(jobs.get(i-1).duration - jobs.get(i).arrivalTime);
		// }

		int index = 0;
		while (index < jobs.size()) {

			int pickedJob = pickJob(jobs, alreadyPicked, currentTime, Scheduler.HRRN);
			Job currentJob = jobs.get(pickedJob);
			alreadyPicked.add(currentJob);

			for (int i = 0; i < currentTime; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append(" ");
			}

			for (int i = 0; i < currentJob.duration; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append("X");
			}

			builders[pickedJob].append("\n");
			currentTime += currentJob.duration;

			index++;
		}

		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i]);
		}

		System.out.println(bldr);
	}

	public void FB(ArrayList<Job> jobs, int timeQuantum) {

	}

	public void ALL(ArrayList<Job> jobs) {

		this.FCFS(jobs);
		System.out.print("jobs: ");
		print(jobs);
		this.RR(jobs, 1);
		System.out.print("jobs: ");
		print(jobs);
		this.SPN(jobs);
		System.out.print("jobs: ");
		print(jobs);
		this.SRT(jobs);
		System.out.print("jobs: ");
		print(jobs);
		this.HRRN(jobs);
		System.out.print("jobs: ");
		print(jobs);
		this.FB(jobs, 1);
		System.out.print("jobs: ");
		print(jobs);

	}

    public int pickJob(ArrayList<Job> jobs, HashSet<Job> alreadyPicked, int currentTime, int schedulerAlgorithm) {

		int minIdx = Integer.MAX_VALUE;
		int maxIdx = Integer.MIN_VALUE;

		int counter = 0;

		int currMin = Integer.MAX_VALUE;
		int currMax = Integer.MIN_VALUE;

		double responseRatio = -1;
		double currMaxResponseRatio = Double.MIN_VALUE;

		// System.out.println("size of jobs list: " + jobs.size());

		switch (schedulerAlgorithm) {
			case Scheduler.SPN:

				for (Job job : jobs) {

					if (job.arrivalTime > currentTime) {
						continue;
					}

					if (currMin > job.duration && !alreadyPicked.contains(job)) {
						currMin = job.duration;
						minIdx = counter;
					}

					counter++;
				}

				return minIdx;
			case Scheduler.HRRN:
				// System.out.println("-----");
				// System.out.println("currentTime: " + currentTime);

				for (Job job : jobs) {
					if (job.arrivalTime > currentTime) {
						continue;
					}

					responseRatio = ((Math.abs(job.arrivalTime - currentTime) + job.duration) / (double)job.duration);
					// if (!alreadyPicked.contains(job)) {
					// 	System.out.println(job.name + " " + responseRatio);
					// }

					if (currMaxResponseRatio < responseRatio && !alreadyPicked.contains(job)) {
						currMaxResponseRatio = responseRatio;
						maxIdx = counter;
					}

					counter++;
				}

				// System.out.println("Picked Job for HRRN: " + jobs.get(maxIdx).name);
				// System.out.println("-----");
				return maxIdx;
			default:
				return -1;
		}

    }

}



