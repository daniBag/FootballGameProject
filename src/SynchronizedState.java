import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronizedState<T> {
    private T state;
    private Lock lock;

    public SynchronizedState(T state){
        this.state = state;
        this.lock = new ReentrantLock();
    }
    public void setState(T state){
        this.lock.lock();
        try {
            this.state = state;
        }finally {
            this.lock.unlock();
        }
    }
    public T getState(){
        this.lock.lock();
        try {
            return this.state;
        }finally {
            this.lock.unlock();
        }
    }
}
