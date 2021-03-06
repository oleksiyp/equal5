package util;

import util.CountDownQueue;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/16/13
 * Time: 12:46 PM
 */
public class CountDownQueueTest {
    private CountDownQueue<String> queue;

    @Before
    public void setUp() throws Exception {
        queue = new CountDownQueue<String>();
    }

    @Test
    public void testGetCountDownTime() throws Exception {
        assertEquals(0, queue.getCountDownTime());
    }

    @Test
    public void testSetCountDownTime() throws Exception {
        queue.setCountDownTime(10);
        assertEquals(10, queue.getCountDownTime());
    }

    @Test
    public void testPut() throws Exception {
        queue.put("abc");
        assertEquals("abc", queue.poll());
        assertNull(queue.poll());
    }

    @Test
    public void testPut2() throws Exception {
        queue.put("def");
        queue.put("abc");
        assertEquals("abc", queue.poll());
        assertNull(queue.poll());
    }

    @Test
    public void testPut3() throws Exception {
        queue.setCountDownTime(100);
        queue.put("def");
        queue.put("abc");
        assertEquals(null, queue.poll());
        Thread.sleep(150);
        assertEquals("abc", queue.poll());
    }

    @Test
    public void testPut4() throws Exception {
        queue.setCountDownTime(100);
        queue.put("def");
        assertEquals(null, queue.poll());
        assertEquals(null, queue.peek());
        Thread.sleep(50);
        queue.put("abc");
        Thread.sleep(150);
        assertEquals("abc", queue.peek());
        assertEquals("abc", queue.poll());
        assertEquals(null, queue.peek());
    }

    @Test
    public void testDrainTo() throws Exception {
        queue.put("abc");
        List<String> lst = new ArrayList<String>();
        assertEquals(1, queue.drainTo(lst));
        assertEquals(Arrays.asList("abc"), lst);

        lst.clear();
        queue.drainTo(lst);
        assertEquals(0, queue.drainTo(lst));
        assertEquals(Arrays.asList(), lst);
    }

    @Test
    public void testDrainTo2() throws Exception {
        queue.put("def");
        queue.put("abc");
        List<String> lst = new ArrayList<String>();
        assertEquals(1, queue.drainTo(lst));
        assertEquals(Arrays.asList("abc"), lst);

        lst.clear();
        queue.drainTo(lst);
        assertEquals(0, queue.drainTo(lst));
        assertEquals(Arrays.asList(), lst);
    }

    @Test
    public void testPeek() throws Exception {
        queue.put("abc");
        assertEquals("abc", queue.peek());
        assertEquals("abc", queue.peek());
        assertEquals("abc", queue.poll());
        assertNull(queue.peek());
        assertNull(queue.peek());
    }

    @Test
    public void testPeek2() throws Exception {
        queue.put("def");
        queue.put("abc");
        assertEquals("abc", queue.peek());
        assertEquals("abc", queue.peek());
        assertEquals("abc", queue.poll());
        assertNull(queue.peek());
        assertNull(queue.peek());
    }

    @Test
    public void testIterator() throws Exception {
        queue.put("abc");
        Iterator<String> it = queue.iterator();
        assertTrue(it.hasNext());
        assertEquals("abc", it.next());
        assertFalse(it.hasNext());
        assertEquals("abc", queue.poll());
        it = queue.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void testIterator2() throws Exception {
        queue.put("def");
        queue.put("abc");
        Iterator<String> it = queue.iterator();
        assertTrue(it.hasNext());
        assertEquals("abc", it.next());
        assertFalse(it.hasNext());
        assertEquals("abc", queue.poll());
        it = queue.iterator();
        assertFalse(it.hasNext());
    }

    @Test
    public void testSize() throws Exception {
        queue.put("abc");
        assertEquals(1, queue.size());
        assertEquals("abc", queue.poll());
        assertEquals(0, queue.size());
    }

    @Test
    public void testSize2() throws Exception {
        queue.put("def");
        queue.put("abc");
        assertEquals(1, queue.size());
        assertEquals("abc", queue.poll());
        assertEquals(0, queue.size());
    }
}
