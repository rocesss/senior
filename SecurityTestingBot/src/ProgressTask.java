import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import javafx.concurrent.Task;

public class ProgressTask extends Task<Integer>{
	
	Queue<String> q = new LinkedList<String>();
	Model model;
	
	public ProgressTask(){
		updateProgress(0, 1);
	}
	
	public void setModel(Model model){
		this.model = model;
	}
	
	@Override
	protected Integer call() throws Exception {
		int maxProgress = this.numTask();
		int progress = 0;
		
		while(!isCancelled() && progress < maxProgress){
			if(model != null){
				progress = model.getCompletedTaskNum();
				updateProgress(progress, maxProgress);
				updateMessage(new DecimalFormat("##.#").format(((progress * 100.0)/maxProgress)) + "%");
			}
		}
		
		return maxProgress;
	}
	
	public void addTask(String task){
		q.add(task);
	}
	
	private int numTask(){
		ArrayList<String> task = new ArrayList<String>();
		FileManager filemanager = new FileManager();
		
		while(!q.isEmpty()){
			switch(q.poll()){
			case "Joomla-sqli":
				task.add("Joomla//SQLInjection");
				break;
			case "Joomla-xss":
				task.add("Joomla//XSS");
				break;
			case "Wordpress-sqli":
				task.add("Wordpress//SQLInjection");
				break;
			case "Wordpress-xss":
				task.add("Wordpress//XSS");
				break;
			case "Drupal-sqli":
				task.add("Drupal//SQLInjection");
				break;
			case "Drupal-xss":
				task.add("Drupal//XSS");
				break;
			default: 
			}
		}
		
		return filemanager.lineNumber(task);
	}

}
