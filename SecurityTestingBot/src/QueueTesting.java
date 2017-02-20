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
		
		while(!q.isEmpty()){
			if(!model.getTestRunning()){
				switch(q.poll()){
				case "Joomla-sqli":
					model.testSQLiJoomla(fullUrl);
					break;
				case "Joomla-xss":
					model.testXSSJoomla(fullUrl);
					break;
				case "Wordpress-sqli":
					model.testSQLiWordpress(fullUrl);
					break;
				case "Wordpress-xss":
					model.testXSSWordpress(fullUrl);
					 break;
				case "Drupal-sqli":
					model.testSQLiDrupal(fullUrl);
					break;
				case "Drupal-xss":
					model.testXSSDrupal(fullUrl);
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
