import java.util.LinkedList;
import java.util.Queue;

public class QueueTesting implements Runnable{
	
	private Thread t;
	
	private Queue<String> q;
	private Model model;
	private String fullUrl;
	
	public QueueTesting(String web){
		q = new LinkedList<String>();
		model = new Model();
		fullUrl = web;
	}
	
	@Override
	public void run() {
		
		this.addQueue("stop");
		
		int numlogfile = FileManager.numberOfLogFile();
		String cms = "";
		
		while(!q.isEmpty()){
			if(!model.getTestRunning()){
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
		
		model.sendResultToWeb(fullUrl, cms + "-log" + (numlogfile + 1) + ".txt");
		model.closeDriver();
		
	}
	
	public void addQueue(String item){
		q.add(item);
	}
	
	public void start(){
		t = new Thread(this);
		t.start();
	}

}
