package ccl.rt.v6.fx;

import io.github.coalangsoft.lib.data.Func;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class FXLauncher {

    public static void launch(Func<Stage, Object> f){
        new JFXPanel();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                Stage s = new Stage();
                f.call(s);
            }
        });
    }

    public static void launch(Func<Stage, Object> f, Object noeffect){
        launch(f);
    }

}
