import java.util.HashSet;

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
	
	private Model model = new Model();
	
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
    	HashSet<String> selectedAttack = new HashSet<String>();
    	
    	if(sqli.isSelected()){
    		selectedAttack.add("sqli");
    	}
    	if(xss.isSelected()){
    		selectedAttack.add("xss");
    	}
    	
    	if(web.indexOf("http://") < 0 && web.indexOf("https://") < 0){
    		web = "http://" + web;
    	}
    	System.out.println(selectedFramework);
    	model.runSecurityTesting(web, selectedFramework, selectedAttack);
    }
    
}