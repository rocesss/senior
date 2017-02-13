import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FileManager {

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

	public ArrayList<String> getSelectedFilePath(String type){
		ArrayList<String> filepath = new ArrayList<String>();
		
		switch(type){
		case "sqli": 
			filepath  = getFilePath(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript//SQLInjection");
			break;
		case "xss":
			filepath  = getFilePath(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript//XSS");
			break;
		case "all":
			filepath  = getFilePath(Paths.get("").toAbsolutePath().toString() 
					+ "//src//PenetrationScript");
			break;
		}
		
		return filepath;				
	}
}
