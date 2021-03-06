import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Controller implements Initializable{
	
	@FXML
	private TextField url;
	@FXML
	private ComboBox<String> framework;
	@FXML
	private CheckBox sqli;
	@FXML
	private CheckBox xss;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label percent;
	@FXML
	private Button cancelBtn;
	@FXML
	private Button runBtn;
	@FXML
	private Button yesBtnDialog;
	@FXML
	private Button noBtnDialog;
	
	private QueueTesting qt;
	private ProgressTask task;
	
	private Stage appStage;
	private Stage dialogStage;
	
	private EventHandler<WindowEvent> exitHandler = new EventHandler<WindowEvent>() {
		
		@Override
		public void handle(WindowEvent event){
			ThreadGroup currentGroup = Thread.currentThread().getThreadGroup();
		    int noThreads = currentGroup.activeCount();
		    Thread[] listThreads = new Thread[noThreads];
		    currentGroup.enumerate(listThreads);
		    
		    boolean isRunning = false;
		    
		    for (int i = 0; i < noThreads; i++){
		    	if(listThreads[i].getName().equalsIgnoreCase("SScanner Queue Thread") ||
		    			listThreads[i].getName().indexOf("session") >= 0){
		    		isRunning = true;
		    	}
		    }

		    if(isRunning){
		    	event.consume();
		    	dialogStage.show();  	
		    }
		    
		}
	};
	
	private final String buttonStyle = "-fx-focus-color: transparent;"
    									+ "-fx-faint-focus-color: transparent;"
							    		+ "-fx-border-color: lightgrey;"
							    		+ "-fx-background-color: white;";
    
    private final String buttonHoverStyle = "-fx-background-color: lightgrey;";
	
	private EventHandler<Event> buttonHandler = new EventHandler<Event>() {
		
		@Override
		public void handle(Event event) {
			if(event.getEventType() == MouseEvent.MOUSE_ENTERED){
				((Button) event.getSource()).setStyle(buttonHoverStyle);
			}else if(event.getEventType() == MouseEvent.MOUSE_EXITED){
				((Button) event.getSource()).setStyle(buttonStyle);
			}else if(event.getEventType() == MouseEvent.MOUSE_CLICKED){
				String btnName = ((Button)event.getSource()).getText();
				
				if(btnName.equals("Yes")){
					task.cancel();
					qt.cancel();
			    	Platform.exit();
				}else if(btnName.equals("No")){
					((Node)(event.getSource())).getScene().getWindow().hide();
				}
			}		
		}
	};
	
	private EventHandler<WorkerStateEvent> progressBarHandler = new EventHandler<WorkerStateEvent>() {
		
		@Override
		public void handle(WorkerStateEvent event) {
			if(event.getEventType() == WorkerStateEvent.WORKER_STATE_CANCELLED){
				progressBar.lookup(".bar").setStyle("-fx-background-color: red;");
			}else if(event.getEventType() == WorkerStateEvent.WORKER_STATE_RUNNING){
				progressBar.lookup(".bar").setStyle("-fx-background-color: #3aaf1f;");
			}else if(event.getEventType() == WorkerStateEvent.WORKER_STATE_SUCCEEDED){
				cancelBtn.setDisable(true);
				url.setDisable(false);
		    	framework.setDisable(false);
		    	sqli.setDisable(false);
		    	xss.setDisable(false);
		    	runBtn.setDisable(false);
			}
		}
	};
	
	public void setStage(Stage stage){
		this.appStage = stage;
		this.appStage.setOnCloseRequest(exitHandler);
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		dialogStage = new Stage();
	    dialogStage.initModality(Modality.APPLICATION_MODAL);
	    dialogStage.getIcons().add(new Image("/images/sscan-icon.png"));
	    dialogStage.setResizable(false);
	    dialogStage.setTitle("Exit");
	    
	    Label message1 = new Label();
	    message1.setText("This will close SScanner without completing your program.");
	    message1.setFont(new Font(Font.getDefault().getFamily(), 13));
	    
	    Label message2 = new Label();
	    message2.setText("Are you sure you want to exit?");
	    message2.setFont(new Font(Font.getDefault().getFamily(), 13));
	    
	    Button yesBtnDialog = new Button();
	    yesBtnDialog.setText("Yes");
	    yesBtnDialog.setPrefWidth(50);
	    yesBtnDialog.setStyle(buttonStyle);
	    yesBtnDialog.setOnMouseClicked(buttonHandler);
	    yesBtnDialog.setOnMouseEntered(buttonHandler);
	    yesBtnDialog.setOnMouseExited(buttonHandler);
	    
	    Button noBtnDialog = new Button();
	    noBtnDialog.setText("No");
	    noBtnDialog.setPrefWidth(50);
	    noBtnDialog.setStyle(buttonStyle);
	    noBtnDialog.setOnMouseClicked(buttonHandler);
	    noBtnDialog.setOnMouseEntered(buttonHandler);
	    noBtnDialog.setOnMouseExited(buttonHandler);
	    
	    HBox buttonContainer = new HBox(yesBtnDialog, noBtnDialog);
	    buttonContainer.setSpacing(10);
	    buttonContainer.setAlignment(Pos.TOP_RIGHT);
	    buttonContainer.setPadding(new Insets(10));
	    
	    VBox vbox = new VBox(message1, message2, buttonContainer);
	    vbox.setPrefSize(350, 100);
	    vbox.setSpacing(10);
	    vbox.setPadding(new Insets(10));
	    vbox.setStyle("-fx-background-color: #ffffff");

	    dialogStage.setScene(new Scene(vbox));
	}
	
	@FXML
	private void checkBeforeTesting(){
		if(framework.getValue() != null && framework.getValue().equals("Drupal")){
			sqli.setSelected(false);
			sqli.setDisable(true);
		}else{
			sqli.setDisable(false);
		}
		
		if(url.getText().length() > 0 && framework.getValue() != null && 
				(sqli.isSelected() || xss.isSelected())){
			runBtn.setDisable(false);		
		}else{
			runBtn.setDisable(true);
		}
	}
	
	@FXML
	private void cancelTest(){
		cancelBtn.setDisable(true);
		url.setDisable(false);
    	framework.setDisable(false);
    	sqli.setDisable(false);
    	xss.setDisable(false);
    	runBtn.setDisable(false);
		
    	task.cancel();
    	qt.cancel();
	}
    
    @FXML
    private void penetrationTest(){
    	cancelBtn.setDisable(false);
    	url.setDisable(true);
    	framework.setDisable(true);
    	sqli.setDisable(true);
    	xss.setDisable(true);
    	runBtn.setDisable(true);
    	
    	String web = url.getText();
    	String selectedFramework = framework.getValue();
    	
    	task = new ProgressTask();
    	qt = new QueueTesting(web, task);	
    	
    	if(sqli.isSelected()){
    		qt.addQueue(selectedFramework + "-sqli");
    		task.addTask(selectedFramework + "-sqli");
    	} 
		if(xss.isSelected()){
			qt.addQueue(selectedFramework + "-xss");
			task.addTask(selectedFramework + "-xss");
		} 
		
		qt.start();	
		
		progressBar.progressProperty().bind(task.progressProperty());
		percent.textProperty().bind(task.messageProperty());
		task.setOnCancelled(progressBarHandler);
		task.setOnRunning(progressBarHandler);
		task.setOnSucceeded(progressBarHandler);
		
		new Thread(task).start();
    }	
    
}