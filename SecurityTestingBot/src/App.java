import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("ui.fxml"));
        
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root));
        primaryStage.setTitle("Security Testing Tool");
        primaryStage.show();
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