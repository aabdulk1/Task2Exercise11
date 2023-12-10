public class TickTock {
    String state; // stores the clock state

    synchronized void tick(boolean running) {
        if (!running) { // stopping the clock
            state = "ticked";
            notify(); // notify the waiting thread
            return;
        }

        System.out.print("tick ");

        // waits for half a second
        try {
            Thread.sleep(500); // half a second
        } catch (InterruptedException e) {
            System.out.println("The thread has been interrupted");
        }

        state = "ticked"; // changes clock state to ticked
        notify(); // allows execution of the tock() method

        try {
            while (!state.equals("tocked"))
                wait(); // waits for tock() to complete
        } catch (InterruptedException e) {
            System.out.println("The thread has been interrupted");
        }
    }

    synchronized void tock(boolean running) {
        if (!running) { // stops the clock
            state = "tocked";
            notify(); // notify the waiting thread
            return;
        }

        System.out.println("tack");

        // waits for half a second
        try {
            Thread.sleep(500); // half a second
        } catch (InterruptedException e) {
            System.out.println("The thread has been interrupted");
        }

        state = "tocked"; // changes clock state to "tocked"
        notify(); // allows execution of the tick() method

        try {
            while (!state.equals("ticked"))
                wait(); // waits for tick() to complete
        } catch (InterruptedException e) {
            System.out.println("The thread has been interrupted");
        }
    }
}

class MyThreadClock implements Runnable {
    Thread thread;
    TickTock ttOb;

    MyThreadClock(String name, TickTock tt) {
        thread = new Thread(this, name);
        ttOb = tt;
    }

    // call method that creates and starts the thread
    public static MyThreadClock createAndStart(String name, TickTock tt) {
        MyThreadClock myThreadClock = new MyThreadClock(name, tt);
        myThreadClock.thread.start(); // starts a new thread
        return myThreadClock;
    }

    public void run() {
        if (thread.getName().compareTo("tick") == 0) {
            for (int i = 0; i < 20; i++)
                ttOb.tick(true);
            ttOb.tick(false);
        } else {
            for (int i = 0; i < 20; i++)
                ttOb.tock(true);
            ttOb.tock(false);
        }
    }
}

class ThreadCom {
    public static void main(String[] args) {
        TickTock tt = new TickTock();
        MyThreadClock myThreadClock1 = MyThreadClock.createAndStart("tick", tt);
        MyThreadClock myThreadClock2 = MyThreadClock.createAndStart("tack", tt);

        try {
            myThreadClock1.thread.join();
            myThreadClock2.thread.join();
        } catch (InterruptedException e) {
            System.out.println("The main thread has been terminated");
        }
    }
}
