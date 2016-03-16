package lv.home.test;

import org.springframework.shell.Bootstrap;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Bootstrap.main(args);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }


}
