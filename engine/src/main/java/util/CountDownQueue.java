package util;

import com.google.common.collect.Iterators;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/16/13
 * Time: 11:31 AM
 */
public class CountDownQueue<E>
        extends AbstractQueue<E>
        implements BlockingQueue<E> {

    private final Lock lock = new ReentrantLock();
    private final Condition timeExceeded = lock.newCondition();

    private long awakeTime = 0;
    private E element = null;

    private volatile long countDownTime = 0;

    public long getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(long time) {
        if (time < 0) {
            throw new IllegalArgumentException("time");
        }
        this.countDownTime = time;
    }

    @Override
    public void put(E e) {
        if (e == null) {
            throw new IllegalArgumentException("e");
        }
        long time = System.currentTimeMillis() + countDownTime;
        lock.lock();
        try {
            awakeTime = Math.max(time, awakeTime);
            element = e;
            timeExceeded.signalAll();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean offer(E e) {
        put(e);
        return true;
    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {
        return offer(e);
    }


    @Override
    public E take() throws InterruptedException {
        return take(Long.MAX_VALUE);
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return take(System.currentTimeMillis()
                + unit.toMillis(timeout));
    }

    private E take(long deadline) throws InterruptedException {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            while (currentTime < deadline &&
                    (element == null || awakeTime > currentTime)) {

                long delta = awakeTime - currentTime;
                timeExceeded.await(delta, TimeUnit.MILLISECONDS);

                currentTime = System.currentTimeMillis();
            }
            if (currentTime >= deadline) {
                return null;
            }

            E ret = element;
            element = null;

            return ret;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public E poll() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (element != null && currentTime >= awakeTime) {
                E ret = element;
                element = null;
                return ret;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int remainingCapacity() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return drainTo(c, Integer.MAX_VALUE);
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        if (c == null)
            throw new NullPointerException();
        if (c == this)
            throw new IllegalArgumentException();
        if (maxElements == 0) {
            return 0;
        }
        E el = poll();
        if (el != null) {
            c.add(el);
            return 1;
        }
        return 0;
    }

    @Override
    public E peek() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (element != null && currentTime >= awakeTime) {
                return element;
            }
            return null;
        } finally {
            lock.unlock();
        }
    }


    @Override
    public Iterator<E> iterator() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (element != null && currentTime >= awakeTime) {
                return Iterators.forArray(element);
            }
            return Iterators.emptyIterator();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public int size() {
        lock.lock();
        try {
            long currentTime = System.currentTimeMillis();
            if (element != null && currentTime >= awakeTime) {
                return 1;
            }
            return 0;
        } finally {
            lock.unlock();
        }
    }
}
