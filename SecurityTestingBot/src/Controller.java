import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.shape.Rectangle;

public class Controller {
	
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
	
	private QueueTesting qt;
	
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