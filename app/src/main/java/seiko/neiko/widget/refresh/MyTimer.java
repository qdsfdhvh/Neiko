package seiko.neiko.widget.refresh;

import android.os.Handler;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer
{
    private Handler handler;
    private Timer timer;
    private MyTask mTask;

    public MyTimer(Handler handler)
    {
        this.handler = handler;
        timer = new Timer();
    }

    public void schedule(long period)
    {
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
        mTask = new MyTask(handler);
        timer.schedule(mTask, 0, period);
    }

    public void cancel()
    {
        if (mTask != null)
        {
            mTask.cancel();
            mTask = null;
        }
    }

    class MyTask extends TimerTask
    {
        private Handler handler;

        public MyTask(Handler handler)
        {
            this.handler = handler;
        }

        @Override
        public void run()
        {
            handler.obtainMessage().sendToTarget();
        }

    }
}