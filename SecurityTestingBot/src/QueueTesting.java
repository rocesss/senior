import java.util.LinkedList;
import java.util.Queue;

public class QueueTesting implements Runnable{
	
	private Thread t;
	
	private Queue<String> q;
	private Model model;
	private String fullUrl;
	
	public QueueTesting(String web, ProgressTask task){
		q = new LinkedList<String>();
		model = new Model();
		task.setModel(model);
		fullUrl = web;
	}
	
	@Override
	public void run() {
		
		this.addQueue("stop");
		
		int numlogfile = FileManager.numberOfLogFile();
		model.setCancelled(false);
		String cms = "";

		while(!q.isEmpty() && !model.isCancelled()){
			if(!model.isTestRunning()){
				switch(q.poll()){
				case "Joomla-sqli":
					cms = "Joomla";
					model.testSQLiJoomla(fullUrl, numlogfile);
					break;
				case "Joomla-xss":
					cms = "Joomla";
					model.testXSSJoomla(fullUrl, numlogfile);
					break;
				case "Wordpress-sqli":
					cms = "Wordpress";
					model.testSQLiWordpress(fullUrl, numlogfile);
					break;
				case "Wordpress-xss":
					cms = "Wordpress";
					model.testXSSWordpress(fullUrl, numlogfile);
					break;
				case "Drupal-sqli":
					cms = "Drupal";
					model.testSQLiDrupal(fullUrl, numlogfile);
					break;
				case "Drupal-xss":
					cms = "Drupal";
					model.testXSSDrupal(fullUrl, numlogfile);
					break;
				default: 
				}
			}
		}
		
		if(!model.isCancelled()){
			model.sendResultToWeb(fullUrl, cms + "-log" + (numlogfile + 1) + ".txt");
		}
		model.closeDriver();		
	}
	
	public void cancel(){
		model.setCancelled(true);
	}
	
	public void addQueue(String item){
		q.add(item);
	}
	
	public void start(){
		t = new Thread(this);
		t.setName("SScanner Queue Thread");
		t.start();
	}

}
