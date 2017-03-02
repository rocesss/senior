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
		
		while(!q.isEmpty()){
			if(!model.getTestRunning()){
				switch(q.poll()){
				case "Joomla-sqli":
					model.testSQLiJoomla(fullUrl, numlogfile);
					break;
				case "Joomla-xss":
					model.testXSSJoomla(fullUrl, numlogfile);
					break;
				case "Wordpress-sqli":
					model.testSQLiWordpress(fullUrl, numlogfile);
					break;
				case "Wordpress-xss":
					model.testXSSWordpress(fullUrl, numlogfile);
					 break;
				case "Drupal-sqli":
					model.testSQLiDrupal(fullUrl, numlogfile);
					break;
				case "Drupal-xss":
					model.testXSSDrupal(fullUrl, numlogfile);
					break;
				default: 
				}
			}
		}
		
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
