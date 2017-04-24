package net.posick.concurrency.comcast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * The main entry point of the application.
 * 
 * @author Steve Posick <posicks@gmail.com>
 */
public class Main
{
    private static final SimpleDateFormat ISO_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    
    private static final int NUMBER_OF_THREADS = 5;
    
    private Main()
    {
    }


    /**
     * Application entry point.  Executed when program is started.
     * 
     * @param args The command line argument.
     */
    public static void main(String[] args)
    {
        ExecutionScheduler scheduler = new ExecutionSchedulerImpl();
        for (int index = 0; index < NUMBER_OF_THREADS; index++)
        {
            scheduler.submit(() ->
            {
                Date now = new Date();
                System.out.println("Default Time = " + now.toString() + "\t-\t" + "ISO Time = " + ISO_FORMATTER.format(now));
            });
        }
        scheduler.start();
    }
}