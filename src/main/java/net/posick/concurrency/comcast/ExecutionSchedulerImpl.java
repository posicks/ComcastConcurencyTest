package net.posick.concurrency.comcast;

import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

/**
 * @author Steve Posick <posicks@gmail.com>
 */
public class ExecutionSchedulerImpl implements ExecutionScheduler
{
    protected static final Logger LOG = Logger.getLogger(ExecutionSchedulerImpl.class.getName());
    
    private CopyOnWriteArrayList<Thread> threads = new CopyOnWriteArrayList<>();
    
    protected Random randomGenerator = new Random();
    
    protected volatile boolean exit = false;
    
    private volatile int index = 0;
    
    private class ScheduleRunner implements Runnable
    {
        private Runnable command;


        /**
         * 
         */
        public ScheduleRunner(Runnable command)
        {
            this.command = command;
        }
        
        
        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run()
        {
            while (!exit)
            {
                int millis = randomGenerator.nextInt(4000);
                try
                {
                    synchronized (this)
                    {
                        try
                        {
                            this.wait();
                        } catch (InterruptedException e)
                        {
                            // ignore
                        }
                    }
                    Thread.sleep(1000 + millis);
                    System.out.println(Thread.currentThread().getName() + Thread.currentThread().getId());
                    command.run();
                    passTheBaton();
                } catch (Exception e)
                {
                    LOG.severe("Unexpected Exception during execution: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                }
            }
        }    
    }

    
    /**
     * Initializes instance 
     */
    public ExecutionSchedulerImpl()
    {
    }
    
    
    /* (non-Javadoc)
     * @see net.posick.concurrency.comcast.ExecutionScheduler#submit(java.lang.Runnable)
     */
    @Override
    public void submit(Runnable command)
    {
        Thread t = new Thread(THREAD_GROUP, new ScheduleRunner(command), THREAD_NAME);
        threads.add(t);
        t.start();
    }
    
    
    protected void passTheBaton()
    {
        int batton = index++;
        Thread t = threads.get(batton);
        index = index % threads.size();
        synchronized (t)
        {
            t.interrupt();
        }
    }


    /* (non-Javadoc)
     * @see net.posick.concurrency.comcast.ExecutionScheduler#start()
     */
    @Override
    public void start()
    {
        passTheBaton();
    }
    
    
    /* (non-Javadoc)
     * @see net.posick.concurrency.comcast.ExecutionScheduler#shutdown()
     */
    @Override
    public void shutdown()
    {
        exit = true;
        for (Thread t : threads)
        {
            t.notify();
        }
    }
}