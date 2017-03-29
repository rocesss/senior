import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

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
	private Button btn;
	
	@FXML
	private void checkBeforeTesting(){
		if(url.getText().length() > 0 && framework.getValue() != null && 
				(sqli.isSelected() || xss.isSelected())){
			btn.setDisable(false);
		}else{
			btn.setDisable(true);
		}
	}
    
    @FXML
    private void penetrationTest(){
    	String web = url.getText();
    	String selectedFramework = framework.getValue();
    	
//    	if(web.indexOf("http://") < 0 && web.indexOf("https://") < 0){
//    		web = "http://" + web;
//    	}
    	
    	QueueTesting qt = new QueueTesting(web);
    	if(sqli.isSelected()) qt.addQueue(selectedFramework + "-sqli");
		if(xss.isSelected()) qt.addQueue(selectedFramework + "-xss");
		
		qt.start();
    }
    
}