package net.posick.concurrency.comcast;

public interface ExecutionScheduler
{
    public ThreadGroup THREAD_GROUP = new ThreadGroup("Concurrency Test Time Threads");
    
    public String THREAD_NAME = "Concurrency Test Thread ";
    
    
    public void submit(Runnable command);
    
    
    public void start();
    
    
    public void shutdown();
}
