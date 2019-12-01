import java.util.ArrayList;
import java.util.Arrays;
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
	// 			bldr.append("-");
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

	private void print(ArrayList<Job> jobs, int start, int end) {
		StringBuilder bldr = new StringBuilder();
		bldr.append("[ ");

		int counter = 0;
		for (Job job : jobs) {
			if ((counter >= start)) {
				bldr.append(job.toString()).append(", ");
			}
			counter++;
		}

		if (bldr.length() > 2) {
			bldr.deleteCharAt(bldr.length() - 2);
		}

		bldr.append("]");

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
                bldr.append("-");
            }

            for (int i = 0; i < j.timeRequired; i++) {
                bldr.append(j.name);
            }

            bldr.append("\n");
			prevDuration += j.timeRequired;

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

		// System.out.println("lastIdx = " + lastIdx);
		for (Job job : jobsList) {
			if (currentTime < job.arrivalTime) {
				// System.out.println("returning counter = -1");
				return -1;
			}

			if (currentTime >= job.arrivalTime) {
				if (counter <= lastIdx) {
					counter++;
					continue;
				}
				// System.out.println("returning counter = " + counter);
				return counter;
			}
		}

		// System.out.println("out of loop and returning counter = " + counter);
		return -1;
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

		jobToBldrIdx.put(jobs.get(0), 0);
		readyQueue.add(jobs.get(0));
		lastIdx = 0;
		int jobCounter = 1;

		while (jobCounter < jobs.size() || !readyQueue.isEmpty()) {

			// System.out.println("----------");
			// add arriving jobs to readyQueue
			int nextJobIdx = -1;
			Job nextJob = null;
			Job runningJob = null;
			// if (jobCounter < jobs.size()) {
			// 	// System.out.println("checking for arrival");
			// 	nextJobIdx = checkForArrivals(jobs, currentTime, lastIdx);
			// }

			// if (nextJobIdx != -1) {
			// 	nextJob = jobs.get(nextJobIdx);
			// 	lastIdx = nextJobIdx;
			// 	System.out.println("arrival: " + nextJob.toString());
			// }

			// if (nextJob != null && !jobToBldrIdx.containsKey(nextJob)) {
			// 	jobToBldrIdx.put(nextJob, nextJobIdx);
			// 	readyQueue.add(nextJob);
			// 	jobCounter++;
			// }

			if (!readyQueue.isEmpty()) {

				// printQueue(readyQueue);
				runningJob = readyQueue.remove();

				// System.out.println("running " + runningJob.toString() + "; currentTime: " + currentTime);

				runningJob.executionTime += timeQuantum;
				// runningJob.timeRequired -= timeQuantum;

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

				builders[runningBldrIdx].append(runningJob.name);
			}

			// increment timer
			currentTime++;
			if (jobCounter < jobs.size()) {
				// System.out.println("checking for arrival");
				nextJobIdx = checkForArrivals(jobs, currentTime, lastIdx);
			}

			if (nextJobIdx != -1) {
				nextJob = jobs.get(nextJobIdx);
				lastIdx = nextJobIdx;
				// System.out.println("arrival: " + nextJob.toString());
			}

			if (nextJob != null && !jobToBldrIdx.containsKey(nextJob)) {
				// System.out.println("adding arrival to readyQueue");
				jobToBldrIdx.put(nextJob, nextJobIdx);
				readyQueue.add(nextJob);
				jobCounter++;
			}

			// add job to ready queue again if the job is not done yet
			if (runningJob != null) {
				previousJob = runningJob;
				if (runningJob.timeRequired - runningJob.executionTime > 0) {
					// System.out.println("runningJob.timeRequired > 0");
					readyQueue.add(runningJob);
				}
			}

			// printQueue(readyQueue);
			// System.out.println("readyQueue.isEmpty() = " + readyQueue.isEmpty());
			// System.out.print("jobs array: ");
			// print(jobs, jobCounter, jobs.size());
			// System.out.println("jobCounter = " + jobCounter + "  jobs.size() = " + jobs.size());
			// System.out.println("----------");
			// try {
			// 	Thread.sleep(100);
			// } catch (Exception e) {
			// 	e.printStackTrace();
			// }
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
				builders[pickedJob].append("-");
			}

			for (int i = 0; i < currentJob.timeRequired; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append(currentJob.name);
			}

			builders[pickedJob].append("\n");
			currentTime += currentJob.timeRequired;

			index++;
		}

		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i]);
		}

		System.out.println(bldr);
	}

	private int pickJobForSRT(ArrayList<Job> jobs) {

		int minIdx = Integer.MAX_VALUE;
		int minDifference = Integer.MAX_VALUE;

		int counter = 0;
		int currDifference = -1;

		for (Job job : jobs) {
			currDifference = job.timeRequired - job.executionTime;
			// System.out.print("job = " + job.toString());
			// System.out.print(" | currDifference = " + currDifference);
			if (currDifference < minDifference) {
				minDifference = currDifference;
				minIdx = counter;
				// System.out.print(" | minIdx = " + minIdx + " | minDifference = " + minDifference);
			}
			// System.out.println();
			counter++;
		}


		// System.out.println("returning minIdx = " + minIdx);
		return minIdx;
	}

	public void SRT(ArrayList<Job> jobs) {

		int currentTime = 0;
		int jobsCounter = 0;

		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];
		HashMap<Job, Integer> jobsToIdxMap = new HashMap<>();
		ArrayList<Job> arrivedAndReadyJobs = new ArrayList<>();
		// Queue<Job> readyQueue = new LinkedList<>();

		int lastIdx = -1;
		Job lastJob = null;
		int nextJobIdx = -1;
		Job nextJob = null;

		while (jobsCounter < jobs.size() || !arrivedAndReadyJobs.isEmpty()) {
			// nextJobIdx = pickJob(jobs, null, currentTime, Scheduler.SRT);

			// System.out.println("----------");
			nextJobIdx = checkForArrivals(jobs, currentTime, lastIdx);

			if (nextJobIdx != -1) {
				// System.out.print("nextJobIdx = " + nextJobIdx + " | currentTime = " + currentTime);
				nextJob = jobs.get(nextJobIdx);
				lastIdx = nextJobIdx;
				// System.out.println(" | nextJob = " + nextJob.toString());
			}

			if (nextJob != null && !jobsToIdxMap.containsKey(nextJob)) {
				arrivedAndReadyJobs.add(nextJob);
				jobsToIdxMap.put(nextJob, jobsCounter);
				jobsCounter++;
			}

			// print(arrivedAndReadyJobs, 0, arrivedAndReadyJobs.size());
			Job currentJob = arrivedAndReadyJobs.remove(pickJobForSRT(arrivedAndReadyJobs));
			currentJob.executionTime++;

			int runningBldrIdx = jobsToIdxMap.get(currentJob);
				if (builders[runningBldrIdx] == null) {
					builders[runningBldrIdx] = new StringBuilder();
					builders[runningBldrIdx].append(currentJob.name).append("\t|");
				}

				int spaces = Math.abs(currentTime - (builders[runningBldrIdx].length() - 3));
				if (lastJob != null && currentJob.name == currentJob.name) {
					spaces = 0;
				}

				for (int i = 0; i < spaces; i++) {
					builders[runningBldrIdx].append("-");
				}

				builders[runningBldrIdx].append(currentJob.name);

			if (currentJob.timeRequired - currentJob.executionTime > 0) {
				arrivedAndReadyJobs.add(currentJob);
			}

			// System.out.println("jobsCounter = " + jobsCounter);
			// System.out.println("----------");
			currentTime++;
		}

		bldr.append("SRT").append("\n");
		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i].toString()).append("\n");
		}

		System.out.println(bldr.toString());

		// while (jobsCounter < jobs.size() || !readyQueue.isEmpty()) {
		// 	int minIdx = pickJob(jobs, null, currentTime, Scheduler.SRT);
		// 	Job job = jobs.get(minIdx);

		// 	System.out.println(job.toString());

		// 	if (!jobsToIdxMap.containsKey(job)) {
		// 		jobsToIdxMap.put(job, jobsCounter);
		// 		jobsCounter++;
		// 	}

		// 	System.out.println("jobsCounter = " + jobsCounter);

		// 	// run job
		// 	job.executionTime += 1;
		// 	// job.timeRequired -= 1;

		// 	try {
		// 		Thread.sleep(150);
		// 	} catch (Exception e) {
		// 		e.printStackTrace();
		// 	}

		// 	currentTime++;
		// }

	}

	public void HRRN(ArrayList<Job> jobs) {
		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];
		HashSet<Job> alreadyPicked = new HashSet<>();

		int currentTime = 0;

		bldr.append("HRRN\n");

		// get wait times
		// for (int i = 1; i < jobs.size(); i++) {
		// 	jobs.get(i).waitTime += Math.abs(jobs.get(i-1).timeRequired - jobs.get(i).arrivalTime);
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
				builders[pickedJob].append("-");
			}

			for (int i = 0; i < currentJob.timeRequired; i++) {
				if (builders[pickedJob] == null) {
					builders[pickedJob] = new StringBuilder();
					builders[pickedJob].append(currentJob.name).append("\t|");
				}
				builders[pickedJob].append(currentJob.name);
			}

			builders[pickedJob].append("\n");
			currentTime += currentJob.timeRequired;

			index++;
		}

		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i]);
		}

		System.out.println(bldr);
	}

	public void FB(ArrayList<Job> jobs, int timeQuantum) {
		Queue<Job> level1Queue = new LinkedList<>();
		Queue<Job> level2Queue = new LinkedList<>();
		Queue<Job> level3Queue = new LinkedList<>();

		StringBuilder bldr = new StringBuilder();
		StringBuilder[] builders = new StringBuilder[jobs.size()];

		HashMap<Job, Integer> jobsToIdxMap = new HashMap<>();

		final int LEVEL_1 = 1;
		final int LEVEL_2 = 2;
		final int LEVEL_3 = 3;

		int runningQueue = -1;

		int jobsCounter = 0;
		int currentTime = 0;
		int lastIdx = -1;

		jobsToIdxMap.put(jobs.get(0), 0);
		level1Queue.add(jobs.get(0));
		lastIdx = 0;
		jobsCounter++;

		Job previousJob = null;

		while (jobsCounter < jobs.size() || !level1Queue.isEmpty() || !level2Queue.isEmpty() || !level3Queue.isEmpty()) {

			// check for new jobs and add to level 1 queue and set queueToRun to RUN_LEVEL_1
			int arrivalJobIdx = -1;
			Job arrivalJob = null;

			// System.out.println("----------");
			// System.out.println("currentTime: " + currentTime);
			// System.out.print("level 1: ");
			// printQueue(level1Queue);
			// System.out.print("level 2: ");
			// printQueue(level2Queue);
			// System.out.print("level 3: ");
			// printQueue(level3Queue);

			Queue<Job> workingQueue = new LinkedList<>();
			// decide which queue to run from
			if (!level1Queue.isEmpty()) {
				// System.out.print("1 ");
				// printQueue(level1Queue);
				// Job job = runFBFor(timeQuantum, level1Queue, builders, jobsToIdxMap);
				// System.out.print("2 ");
				// printQueue(level1Queue);
				// System.out.println("ran level 1: " + job.toString());

				runningQueue = LEVEL_1;
				workingQueue = level1Queue;

			} else if (!level2Queue.isEmpty()) {
				// Job job = runFBFor(timeQuantum, level2Queue);
				// System.out.println("ran level 2: " + job.toString());
				// if (job.timeRequired > 0) {
				// 	level3Queue.add(job);
				// }

				runningQueue = LEVEL_2;
				workingQueue = level2Queue;

			} else if (!level3Queue.isEmpty()) {
				// Job job = runFBFor(timeQuantum, level3Queue);
				// System.out.println("ran level 3: " + job.toString());
				// if (job.timeRequired > 0) {
				// 	level3Queue.add(job);
				// }

				runningQueue = LEVEL_3;
				workingQueue = level3Queue;
			}

			Job job = workingQueue.remove();

			job.executionTime += timeQuantum;
			// job.timeRequired -= timeQuantum;

			// System.out.print("hashmap: ");
			// printHashMap(jobsToIdxMap);


			int runningBldrIdx = jobsToIdxMap.get(job);
			if (builders[runningBldrIdx] == null) {
				// System.out.println("runningBldrIdx: " + runningBldrIdx);
				builders[runningBldrIdx] = new StringBuilder();
				builders[runningBldrIdx].append(job.name).append("\t|");
			}

			int spaces = Math.abs(currentTime - (builders[runningBldrIdx].length() - 3));
			if (previousJob != null && job.name == previousJob.name) {
				spaces = 0;
			}

			for (int i = 0; i < spaces; i++) {
				builders[runningBldrIdx].append("-");
			}

			for (int i = 0; i < timeQuantum; i++) {
				// System.out.println(job.toString());
				builders[jobsToIdxMap.get(job)].append(job.name);
			}


			currentTime++;
			if (jobsCounter < jobs.size()) {
				// System.out.println("checking for arrivals");
				arrivalJobIdx = checkForArrivals(jobs, currentTime, lastIdx);
				if (arrivalJobIdx != -1) {
					arrivalJob = jobs.get(arrivalJobIdx);
					lastIdx = arrivalJobIdx;
					// System.out.println("arrival job: " + arrivalJob.toString());
					if (arrivalJob != null) {
						level1Queue.add(arrivalJob);
						jobsToIdxMap.put(arrivalJob, jobsCounter);
						jobsCounter++;
					}
				}
			}

			if (job != null && (job.timeRequired - job.executionTime > 0)) {
				previousJob = job;
				if (runningQueue == LEVEL_1) {
					if (!job.equals(arrivalJob)) {
						level2Queue.add(job);
					} else {
						level1Queue.add(job);
					}
					// if (arrivalJob == null || arrivaJob.equals(job)) {
					// 	level1Queue.add(job);
					// } else if (!arrivalJob.equals(job)) {
					// 	level2Queue.add(job);
					// }
					// level2Queue.add(job);
				} else if (runningQueue == LEVEL_2) {
					if (!job.equals(arrivalJob)) {
						level3Queue.add(job);
					} else {
						level2Queue.add(job);
					}
				} else if (runningQueue == LEVEL_3) {
					level3Queue.add(job);
				}
			}

			// if (arrivalJobIdx != -1) {
			// 	lastIdx = arrivalJobIdx;
			// }

			// // add job to ready queue again if the job is not done yet
			// if (job != null) {
			// 	previousJob = job;
			// 	if (job.timeRequired > 0) {
			// 		// System.out.println("runningJob.timeRequired > 0");
			// 		level2Queue.add(job);
			// 	}
			// }

			// System.out.println("----------");
		}

		bldr.append("FB").append("\n");
		for (int i = 0; i < builders.length; i++) {
			bldr.append(builders[i].toString()).append("\n");
		}

		System.out.println(bldr.toString());
	}

	private void printHashMap(HashMap<Job, Integer> map) {
		StringBuilder bldr = new StringBuilder();

		bldr.append("{ ");

		for (Job j : map.keySet()) {
			bldr.append(j.name).append(" : ").append(map.get(j)).append(", ");
		}

		bldr.deleteCharAt(bldr.length() - 2);
		bldr.append("}");
		System.out.println(bldr.toString());
	}

	private Job runFBFor(int timeQuantum, Queue<Job> queue, StringBuilder[] builders, HashMap<Job, Integer> indexHashMap) {

		Job job = queue.remove();

		// System.out.println("running runFBFor()");
		for (int i = 0; i < timeQuantum; i++) {
			System.out.println(job.toString());
			builders[indexHashMap.get(job)].append(job.name);
		}

		job.executionTime += timeQuantum;
		// job.timeRequired -= timeQuantum;

		return job;
	}

	private ArrayList<Job> clone(ArrayList<Job> arrToClone) {
		ArrayList<Job> clone = new ArrayList<>();

		for (Job job : arrToClone) {
			clone.add(new Job(job.name, job.arrivalTime, job.timeRequired));
		}

		return clone;
	}

	public void ALL(ArrayList<Job> jobs) {

		int numSchedulers = 6;
		ArrayList<Job>[] jobCopies = new ArrayList[numSchedulers];

		for (int i = 0; i < numSchedulers; i++) {
			jobCopies[i] = this.clone(jobs);
		}

		this.FCFS(jobCopies[0]);
		// System.out.print("jobs: ");
		// print(jobs);
		this.RR(jobCopies[1], 1);
		// System.out.print("jobs: ");
		// print(jobs);
		this.SPN(jobCopies[2]);
		// System.out.print("jobs: ");
		// print(jobs);
		this.SRT(jobCopies[3]);
		// System.out.print("jobs: ");
		// print(jobs);
		this.HRRN(jobCopies[4]);
		// System.out.print("jobs: ");
		// print(jobs);
		this.FB(jobCopies[5], 1);
		// System.out.print("jobs: ");
		// print(jobs);

	}

    public int pickJob(ArrayList<Job> jobs, HashSet<Job> alreadyPicked, int currentTime, int schedulerAlgorithm) {

		int minIdx = Integer.MAX_VALUE;
		int maxIdx = Integer.MIN_VALUE;

		int counter = 0;

		int currMin = Integer.MAX_VALUE;
		int currMax = Integer.MIN_VALUE;

		double responseRatio = -1;
		double currMaxResponseRatio = Double.MIN_VALUE;

		int difference = 0;
		int minDifference = Integer.MAX_VALUE;
		// System.out.println("size of jobs list: " + jobs.size());

		switch (schedulerAlgorithm) {
			case Scheduler.SPN:

				for (Job job : jobs) {

					// System.out.println("-----");

					// System.out.println(job.toString());

					if (job.arrivalTime > currentTime) {
						// System.out.println("returning minIdx = " + minIdx + " from if statement");
						return counter - 1;
					}

					if (currMin > job.timeRequired && !alreadyPicked.contains(job)) {
						// System.out.print("in currMin > job.timeRequired if statement");
						currMin = job.timeRequired;
						minIdx = counter;
						// System.out.println(" | minIdx = " + minIdx);
					}

					// System.out.println("-----");
					counter++;
				}

				// System.out.println("returning minIdx = " + minIdx);
				return minIdx;
			case Scheduler.HRRN:
				// System.out.println("-----");
				// System.out.println("currentTime: " + currentTime);

				for (Job job : jobs) {
					if (job.arrivalTime > currentTime) {
						continue;
					}

					responseRatio = ((Math.abs(job.arrivalTime - currentTime) + job.timeRequired) / (double)job.timeRequired);
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
			case Scheduler.SRT:
				minIdx = Integer.MAX_VALUE;
				currMin = Integer.MAX_VALUE;

				System.out.println("----------");
				counter = 0;
				for (Job job : jobs) {
					if (job.arrivalTime > currentTime) {
						// return minIdx;
						continue;
					}

					difference = Math.abs(job.arrivalTime - job.timeRequired);

					System.out.println("job: " + job.toString() + "  difference = " + difference + "  currentTime = " + currentTime);

					if (minDifference > difference) {
						minDifference = difference;
						minIdx = counter;
					}

					counter++;
				}

				System.out.println("----------");
				return minIdx;
			default:
				return -1;
		}

    }

}


