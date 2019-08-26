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
* This class is responsible for all the 
* parameters that need to be passed to run R-Pulsar.
* It relays on the Apache Commons CLI library for parsing command 
* line options passed to programs. 
*
* @author  Eduard Giber Renart
* @version 1.0
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

    /**
     * This is the main method that is always called when the CLI class is instantiated
     * It defines all the parameters that need to be specified in order to run R-Pulsar.
     * @return Nothing.
     */
    public Cli(String[] args) {
        this.args = args;
        /*Prints the help*/ 
        options.addOption("h", "help", false, "Show help.");
        /* This is to tell R-Pulsar if the node will be behind a NAT
         * The flag need to be set if the node will be behind a NAT */ 
        options.addOption("n", "nat", false, "Set client Nat .");
        options.addOption("u", "user", false, "Start as application user.");   
        /* Start the GUI */ 
        options.addOption("g", "gui", false, "Start GUI.");
        /* Set the replication factor of the stored data
         * Sets how many nodes will store the same data to prevent data being lost in case of a failure. */ 
        options.addOption("r", "replication factor", false, "Set Replication Factor.");
        /* Port that R-Pulsar will be listening to */ 
        Option p = OptionBuilder.hasArgs(1).withArgName("Port").withDescription("Listen on port.").isRequired(true).withLongOpt("port").create("p");
        options.addOption(p);
        /* This flag is optional and is for encripting the traffic between nodes */ 
        Option l = OptionBuilder.hasArgs(1).withArgName("Location").withDescription("Store certificate at.").isRequired(true).withLongOpt("location").create("l");
        options.addOption(l);
        /* This is a flag creating an R-Pulsar node that that will be an RP Slave */ 
        Option b = OptionBuilder.hasArgs(2).withArgName("IP>:<Port").withValueSeparator(':').withLongOpt("bootstrap").withDescription("Set bootstrap ip address and port").create("b");
        options.addOption(b);
        /* Set the location of the RP */ 
        Option gps = OptionBuilder.hasArgs(2).withArgName("Latitude>:<Longitude").withValueSeparator(':').withLongOpt("gps-location").withDescription("Set gps location").isRequired(true).create("gps");
        options.addOption(gps);
        /* Set the geographical area of where all RP will be located so the quadtree can automatically split. */ 
        Option area = OptionBuilder.hasArgs(4).withArgName("north>:<south>:<east>:<west").withValueSeparator(':').withLongOpt("area").withDescription("Set working area coordinates").create("a");
        options.addOption(area);
        
    }

    /**
     * This is method is called to parse all the parameters that the users inputs.
     * @return Nothing.
     */
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
    
    /**
     * Prints in the terminal the parameters that are required to pass so R-Pulsar can start.
     * @return Nothing.
     */
    private void help() {
        // This prints out some help
        HelpFormatter formater = new HelpFormatter();

        formater.printHelp("Main", options);
        System.exit(0);
    }
}
