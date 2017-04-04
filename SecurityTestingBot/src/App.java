import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
	
	EventHandler<WindowEvent> eventHandler = new EventHandler<WindowEvent>() {
		
		@Override
		public void handle(WindowEvent event) {
			ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		    int noThreads = currentGroup.activeCount();
		    Thread[] listThreads = new Thread[noThreads];
		    currentGroup.enumerate(listThreads);
		    
		    boolean isRunning = false; // อาจจะต้องเอาไปวางไว้ข้างนอก
		    
		    for (int i = 0; i < noThreads; i++){
		    	if(listThreads[i].getName().equalsIgnoreCase("SScanner Queue Thread") ||
		    			listThreads[i].getName().indexOf("session") >= 0){
		    		isRunning = true;
		    	}
		    }
			
		    if(isRunning){
		    	
		    }else{
		    	Platform.exit();
		    	System.exit(0);
		    }
		    
		}
	};
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
        
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("SScanner");
        primaryStage.show();
        primaryStage.setOnCloseRequest(eventHandler);
    }
    
    private static void setSystemProperty(){
    	System.setProperty("glass.accessible.force", "false");
    	System.setProperty("phantomjs.binary.path", "C:\\My Program\\SeleniumHQ\\SeleniumDriver\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe");
    }

    public static void main(String[] args) {
    	setSystemProperty();
        launch(args);
    }
}