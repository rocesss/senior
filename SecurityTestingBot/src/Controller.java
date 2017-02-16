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
    	
    	if(web.indexOf("http://") < 0 && web.indexOf("https://") < 0){
    		web = "http://" + web;
    	}
    	
    	switch(selectedFramework){
    	case "Joomla" : 
    		if(sqli.isSelected()) model.testSQLiJoomla(web);
    		if(xss.isSelected()) model.testXSSJoomla(web);
    		break;
    	case "Wordpress" :
    		if(sqli.isSelected()) model.testSQLiWordpress(web);
    		if(xss.isSelected()) model.testXSSWordpress(web);
    		break;
    	case "Drupal" :
    		if(sqli.isSelected()) model.testSQLiDrupal(web);
    		if(xss.isSelected()) model.testXSSDrupal(web);
    		break;
    	}

//    	model.runSecurityTesting(web, selectedFramework, selectedAttack);
    }
    
}