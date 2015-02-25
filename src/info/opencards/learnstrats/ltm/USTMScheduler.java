package info.opencards.learnstrats.ltm;

import info.opencards.core.Item;

import java.util.*;


/**
 * An unltra-short-term-memory scheduler which allows to put in <code>Item</code>s and provides theses files after a
 * defined amount of time in a FIFO manner.
 *
 * @author Holger Brandl
 */
class USTMScheduler {


    private Timer timer;

    /**
     * The itmes which are already timeouted.
     */
    private final List<Item> rescheduledItems = new ArrayList<Item>();

    /**
     * The map which contains all *running* delay-timers.
     */
    private final Map<Item, ItemReschedulerTask> itemTimers = new HashMap<Item, ItemReschedulerTask>();

    /**
     * The delay to be used to delay a not-learnt item.
     */
    private long delay;


    public USTMScheduler(long delayMs) {
        assert delayMs > 1000; // less make absolutely no sense and the asserts should block values in seconds

        this.delay = delayMs;
        timer = new Timer();
    }


    public synchronized void reschedule(Item item) {
        if (itemTimers.containsKey(item)) {
            itemTimers.get(item).cancel();
        }

        ItemReschedulerTask reschedulerTask = new ItemReschedulerTask(item);
        timer.schedule(reschedulerTask, delay);
        itemTimers.put(item, reschedulerTask);
    }


    public boolean hasScheduledItems() {
        return !rescheduledItems.isEmpty();
    }


    public Item getScheduledItem() {
        return hasScheduledItems() ? rescheduledItems.remove(0) : null;
    }


    public void stop() {
        timer.cancel();
    }


    public Collection<Item> getAllCurrentItems() {
        return new ArrayList<Item>(itemTimers.keySet());
    }


    public synchronized Item cancelNextTask() {
        Item item = Collections.min(itemTimers.keySet(), new Comparator<Item>() {
            public int compare(Item o1, Item o2) {
                return (int) (itemTimers.get(o1).scheduledExecutionTime() - itemTimers.get(o2).scheduledExecutionTime());
            }
        });

        // cancel the task if still possible
        itemTimers.remove(item).cancel();

        return item;
    }


    class ItemReschedulerTask extends TimerTask {


        private final Item item;


        public ItemReschedulerTask(Item item) {
            this.item = item;
        }


        public void run() {
            rescheduledItems.add(item);
            itemTimers.remove(item);
        }
    }
}
