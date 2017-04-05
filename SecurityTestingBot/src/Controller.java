import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
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
	private Rectangle progress;
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
	
	private Stage appStage;
	private Stage dialogStage;
	
	private EventHandler<WindowEvent> eventHandler = new EventHandler<WindowEvent>() {
		
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
	
	public void setStage(Stage stage){
		this.appStage = stage;
		this.appStage.setOnCloseRequest(eventHandler);
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
	    
	    String buttonStyle = "-fx-focus-color: transparent;"
	    		+ "-fx-faint-focus-color: transparent;"
	    		+ "-fx-border-color: lightgrey;"
	    		+ "-fx-background-color: white;";
	    
	    String buttonHoverStyle = "-fx-background-color: lightgrey;";
	    
	    Button yesBtnDialog = new Button();
	    yesBtnDialog.setText("Yes");
	    yesBtnDialog.setPrefWidth(50);
	    yesBtnDialog.setStyle(buttonStyle);
	    yesBtnDialog.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				qt.cancel();
		    	
		    	Platform.exit();	
			}
		});
	    yesBtnDialog.setOnMouseEntered(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				yesBtnDialog.setStyle(buttonHoverStyle);
			}
		});
	    yesBtnDialog.setOnMouseExited(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				yesBtnDialog.setStyle(buttonStyle);
			}
		});
	    
	    Button noBtnDialog = new Button();
	    noBtnDialog.setText("No");
	    noBtnDialog.setPrefWidth(50);
	    noBtnDialog.setStyle(buttonStyle);
	    noBtnDialog.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				((Node)(event.getSource())).getScene().getWindow().hide();	
			}
		});
	    noBtnDialog.setOnMouseEntered(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				noBtnDialog.setStyle(buttonHoverStyle);
			}
		});
	    noBtnDialog.setOnMouseExited(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				noBtnDialog.setStyle(buttonStyle);
			}
		});
	    
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
		progress.setWidth(0);
		percent.setText("0%");
		
		cancelBtn.setDisable(true);
		url.setDisable(false);
    	framework.setDisable(false);
    	sqli.setDisable(false);
    	xss.setDisable(false);
    	runBtn.setDisable(false);
		
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
    	
    	qt = new QueueTesting(web);
    	if(sqli.isSelected()) qt.addQueue(selectedFramework + "-sqli");
		if(xss.isSelected()) qt.addQueue(selectedFramework + "-xss");
		
		qt.start();
    }	
    
}