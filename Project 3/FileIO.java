import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class FileIO {

	File file;

	FileReader fileReader;
	BufferedReader bufferedReader;
	
	FileWriter fileWriter;
	BufferedWriter bufferedWriter;
	
	public FileIO() {

	}

	public FileIO(String filename) {
		this.file = new File(filename);
	}

	public FileIO(File file) {
		this.file = file;
	}

	public ArrayList<String> readFile() {

		String line;
		ArrayList<String> list = new ArrayList<>();
		
		try {
			if (this.openFileForReading()) {
				while ((line = bufferedReader.readLine()) != null) {
					list.add(line);
				}
				closeFileForReading();
			} else {
				return null;
			}
		} catch (Exception ex) {
			System.out.println("An error occurred while reading the file.");
		}

		return list;
	}

	private boolean openFileForReading() {
		try {
			this.fileReader = new FileReader(this.file);
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (Exception ex) {
			if (ex instanceof FileNotFoundException) {
				System.out.println("The requested file could not be found. Please enter a new filename or make sure it exists.");
			} else {
				System.out.println("Error opening the given file for reading.");
			}
		}

		return (this.fileReader != null && this.bufferedReader != null);
	}

	private boolean closeFileForReading() {
		try {
			this.bufferedReader.close();
			this.fileReader.close();
			
			this.bufferedReader = null;
			this.fileReader = null;
		} catch (Exception ex) {
			System.out.println("Error closing the file for reading.");
		}

		return (this.fileReader == null && this.bufferedReader == null);
	}


}





