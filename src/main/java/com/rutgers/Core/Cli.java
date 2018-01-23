/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.rutgers.Core;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author eduard
 */
public class Cli {
    private static final Logger log = Logger.getLogger(Cli.class.getName());
    private String[] args = null;
    private Options options = new Options();
    private CommandLineParser parser = null;
    private CommandLine cmd = null;
    public String[] boot = null, addr = null, inter = null, gps = null, area = null;
    public Boolean nat = false, user = false, gui = false;
    public String port = null;
    public String dir = null;
    public int repli = 0;

    public Cli(String[] args) {
        this.args = args;
        options.addOption("h", "help", false, "Show help.");
        options.addOption("n", "nat", false, "Set client Nat .");
        options.addOption("u", "user", false, "Start as application user.");    
        options.addOption("g", "gui", false, "Start GUI.");
        options.addOption("r", "replication factor", false, "Set Replication Factor.");
        Option p = OptionBuilder.hasArgs(1).withArgName("Port").withDescription("Listen on port.").isRequired(true).withLongOpt("port").create("p");
        options.addOption(p);
        Option l = OptionBuilder.hasArgs(1).withArgName("Location").withDescription("Store certificate at.").isRequired(true).withLongOpt("location").create("l");
        options.addOption(l);
        Option b = OptionBuilder.hasArgs(2).withArgName("IP>:<Port").withValueSeparator(':').withLongOpt("bootstrap").withDescription("Set bootstrap ip address and port").create("b");
        options.addOption(b);
        Option gps = OptionBuilder.hasArgs(2).withArgName("Latitude>:<Longitude").withValueSeparator(':').withLongOpt("gps-location").withDescription("Set gps location").isRequired(true).create("gps");
        options.addOption(gps);
        Option area = OptionBuilder.hasArgs(4).withArgName("north>:<south>:<east>:<west").withValueSeparator(':').withLongOpt("area").withDescription("Set working area coordinates").create("a");
        options.addOption(area);
        
    }

    public void parse() {
        parser = new BasicParser();        
        try {
            cmd = parser.parse(options, args);

            if (cmd.hasOption("h"))
                help();

            user = cmd.hasOption("u");
            nat = cmd.hasOption("n");
            boot = cmd.getOptionValues("b");
            gps = cmd.getOptionValues("gps");
            port = cmd.getOptionValue("p");
            dir = cmd.getOptionValue("l");
            gui = cmd.hasOption("g");
            area = cmd.getOptionValues("a");

            if(cmd.getOptionValue("r") == null) {
                repli = 0;
            } else {
                repli = Integer.parseInt(cmd.getOptionValue("r"));
            }

            if(boot == null) 
                boot = new String[]{"",""};

            if(area == null) 
                area = new String[]{"","","",""};

        } catch (ParseException ex) {
            Logger.getLogger(Cli.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}
