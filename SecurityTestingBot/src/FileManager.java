import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {

	private ArrayList<String> allFilePath;
	private ArrayList<String> fileContent;
	private int indexFileFocus;
	
	private PrintWriter writer;
	
	public FileManager(){	
		indexFileFocus = 0;
	}
	
	public void setReader(String path){
		allFilePath = this.getFilePath(Paths.get("").toAbsolutePath().toString() 
				+ "//src//PenetrationScript//" + path);
	}
	
	public void setWriter(String path){
		try {
			writer = new PrintWriter(new FileWriter(Paths.get("").toAbsolutePath().toString() 
					+ "//src//OutputLog//" + path));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> getFilePath(String path){
		File folder = new File(path);
		ArrayList<String> filepath = new ArrayList<String>();
		
		if(folder.exists()){
			for(File file : folder.listFiles()){
				if(file.isDirectory()){
					filepath.addAll(getFilePath(file.getPath()));
				}else if(file.isFile()){
					filepath.add(file.getPath());
				}
			}
		}
				
		return filepath;		
	}

	private ArrayList<String> readFile(String path){
		try {
			
			BufferedReader reader = new BufferedReader(new FileReader(path));
			String temp;
			ArrayList<String> script = new ArrayList<String>();
			
			while((temp = reader.readLine()) != null){
				script.add(temp);
			}
			reader.close();
			
			return script;
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public String readLineAllFile(){
		if(allFilePath == null){
			System.out.println("Please set reader ...");
			return null;
		}
		if(fileContent == null){
			if(indexFileFocus >= allFilePath.size()) return null;
			
			fileContent = this.readFile(allFilePath.get(indexFileFocus));
			indexFileFocus++;
			
			if(fileContent == null){
				System.out.println("Error reading file.");
				return readLineAllFile();
			}
		}
		if(fileContent.size() > 0){
			String temp = fileContent.get(0);
			fileContent.remove(0);
			
			return temp;
		}
		
		fileContent = null;
		return readLineAllFile();
	}
	
	public void writeLine(String message){
		if(writer == null){
			System.out.println("Please set writer ...");
			return;
		}
		
		writer.println(message);
	}
	
	public void close(){
		//--------------------- Reader ---------------------
		if(allFilePath != null){
			allFilePath.clear();
			allFilePath = null;
		} 
		fileContent = null;
		
		//--------------------- Writer -----------------------
		if(writer != null){
			writer.close();
			writer = null;
		} 
	}
	
	public int numberOfFile(){
		File folder = new File(Paths.get("").toAbsolutePath().toString() 
				+ "//src//OutputLog");
		
		return folder.listFiles().length;
	}
}
