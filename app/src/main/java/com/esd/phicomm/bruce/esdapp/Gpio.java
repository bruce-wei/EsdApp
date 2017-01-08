package com.esd.phicomm.bruce.esdapp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

/**
 * Created by Bruce on 2017/1/4.
 */

class Gpio {

    private String port;

    public boolean output(int value) {
        String command = String.format("echo %d > /sys/class/gpio_sw/%s/data\n", value, port);
        try {
            Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String readinput(){
        Process p;
        String command = String.format("cat /sys/class/gpio_sw/%s/data\n", port);
        try {
            p = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(p.getOutputStream());
            outputStream.write(command.getBytes());
            outputStream.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                break;
            }
            reader.close();
            return text.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public boolean setcfg(int cfg){
        String command = String.format("echo %d > /sys/class/gpio_sw/%s/cfg\n", cfg, port);
        try {
            Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private String readcfg(){
        Process p;
        String command = String.format("cat /sys/class/gpio_sw/%s/cfg\n", port);
        try {
            p = Runtime.getRuntime().exec(new String[] {"su", "-c", command});
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            StringBuilder text = new StringBuilder();
            String line;
            while((line = reader.readLine()) != null){
                text.append(line);
                text.append("\n");
            }
            return text.toString();
        } catch (IOException e) {
            return "";
        }
    }

    public int input(){
        char ch;
        String cfg;

        cfg = readinput();

        if(cfg.isEmpty())
            return -1;
        else{
            ch = cfg.charAt(0);
            if(Character.isDigit(ch))
                return Character.getNumericValue(ch);
            else
                return -1;
        }
    }

    public int getcfg(){
        char ch;
        String cfg;

        cfg = readcfg();
        if(cfg.isEmpty())
            return -1;
        else{
            ch = cfg.charAt(0);
            if(Character.isDigit(ch))
                return Character.getNumericValue(ch);
            else
                return -1;
        }
    }

    //Constructor
    Gpio(String port){
        this.port = port;
    }

}
