package lv.home.test.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.JLineShellComponent;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.logging.Logger;

@Component
public class HalloComand implements CommandMarker {
    protected final Logger LOG = Logger.getLogger(getClass().getName());

    private static final String CMD_GEN = "gen";

    private static int MAX_ADDRESS = 1000;
    public static volatile boolean CHECK_STATUS;
    public static volatile int CURRENT_ADDRESS = 0;
    private Thread progressThread = new Thread(new ProgressCheck());

    @Autowired
    private JLineShellComponent shell;

    public HalloComand(){
        CURRENT_ADDRESS = 0;
        progressThread.start();
    }

    @CliAvailabilityIndicator({CMD_GEN})
    public boolean isSimpleAvailable() {
        return true;
    }

    @CliCommand(value = CMD_GEN, help = "Print a simple hello world message")
    public String simple(
            @CliOption(key = { "ecp" }, mandatory = true, help = "Password for EC priv key file")
            final String ecp,
            @CliOption(key = { "cp" }, mandatory = false, help = "Tmp password for payment core")
            final String cp
    ) {

        return "ecp = [" + ecp + "] cp = [" + cp + "]";
    }

    @CliCommand(value = "l", help = "Print a simple hello world message")
    public void runLongCmd() {
        CHECK_STATUS = true;
        CURRENT_ADDRESS = 0;
        try {
            for (int i = 0; i < MAX_ADDRESS; ++i) {
                Thread.sleep(1);
                ++CURRENT_ADDRESS;
            }
            // iepauzējam, lai proress bārs var pabeigt zīmēt un tad atgriežam consoli spring-shell
            Thread.sleep(1000);
            System.out.print("\n");
        }
        catch (Exception e){
            CHECK_STATUS = false;
            e.printStackTrace();
        }
    }


    private class ProgressCheck implements Runnable{
        public void run(){
            while (true){
                try {
                    Thread.sleep(500);
                    if (CHECK_STATUS) {
                        if (CURRENT_ADDRESS == MAX_ADDRESS){
                            CHECK_STATUS = false;
                        }
                        double fract = CURRENT_ADDRESS / Double.parseDouble(String.valueOf(MAX_ADDRESS));
                        final int width = 70;
                        System.out.print("\r[");
                        int i = 0;
                        for (; i <= (int) (fract * width); i++) {
                            System.out.print("=");
                        }
                        for (; i < width; i++) {
                            System.out.print(" ");
                        }
                        DecimalFormat df = new DecimalFormat("#");
                        System.out.print(" " + CURRENT_ADDRESS + "/" + MAX_ADDRESS + " (" + df.format(fract * 100) + "%) ]");
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

}
