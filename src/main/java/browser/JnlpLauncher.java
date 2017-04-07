package browser;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

public class JnlpLauncher extends Application {
    @Override
    public void start(final Stage stage) {
        stage.setWidth(600);
        stage.setHeight(300);
        Scene scene = new Scene(new Group());


        final WebView browser = new WebView();
        final WebEngine webEngine = browser.getEngine();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        webEngine.getLoadWorker().stateProperty()
                .addListener(new ChangeListener<State>() {
                    @Override
                    public void changed(ObservableValue ov, State oldState, State newState) {

                        if (newState == Worker.State.SUCCEEDED) {
                            stage.setTitle(webEngine.getLocation());
                        }

                    }
                });
        webEngine.load("http://thomasseidl.de/test/test.html");

        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                File file = new File(System.getProperty("user.home") + "/Downloads/");
                String[] downloadableExtensions = {".jnlp"};
                for(String downloadAble : downloadableExtensions) {
                    if (newValue.endsWith(downloadAble)) {
                        try {
                            if(!file.exists()) {
                                file.mkdir();
                            }
                            File download = new File(file + "/test.jnlp"); // TODO: get file name
//                            if(download.exists()) {
//                                //TODO: overwrite
//                                FileUtils.copyURLToFile(new URL(webEngine.getLocation()), download);
//                                return;
//                            }
                            FileUtils.copyURLToFile(new URL(webEngine.getLocation()), download);
                            String[] cmd = { "javaws", System.getProperty("user.home") + "/Downloads/test.jnlp" };
                            Process p = Runtime.getRuntime().exec(cmd);
                            p.waitFor();
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        scene.setRoot(scrollPane);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}