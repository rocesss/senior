import java.nio.file.Paths;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
	
    @Override
    public void start(Stage primaryStage) throws Exception {
        
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("ui.fxml"));
    	Parent root = (Parent)loader.load();
    	Controller controller = (Controller) loader.getController();
        
    	primaryStage.getIcons().add(new Image("/images/sscan-icon.png"));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("SScanner");
        primaryStage.show();

        controller.setStage(primaryStage);
    }
    
    private static void setSystemProperty(){
    	System.setProperty("glass.accessible.force", "false");
    	System.setProperty("phantomjs.binary.path", Paths.get("").toAbsolutePath().toString() + "\\phantomjs.exe");
    }

    public static void main(String[] args) {
    	setSystemProperty();
        launch(args);
    }
}