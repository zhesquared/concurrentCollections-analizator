import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Main {

    public static BlockingQueue<String> firstQueue = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> secondQueue = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> thirdQueue = new ArrayBlockingQueue<>(100);
    public static final int AMOUNT_OF_TEXTS = 10_000;
    public static final int TEXT_SIZE = 100_000;
    public static void main(String[] args) throws InterruptedException {
        final ExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final List<Callable<Boolean>> threads = new ArrayList<>();

        threads.add(() -> {
            for (int i = 0; i < AMOUNT_OF_TEXTS; i++) {
                String text = generateText("abc", TEXT_SIZE);
                try {
                    firstQueue.put(text);
                    secondQueue.put(text);
                    thirdQueue.put(text);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
            return true;
        });

        threads.add(() -> {
           char symbol = 'a';
            System.out.println("Максимальное количество символов 'a': " + symbolRepeats(firstQueue, symbol));
            return true;
        });

        threads.add(() -> {
            char symbol = 'b';
            System.out.println("Максимальное количество символов 'b': " + symbolRepeats(secondQueue, symbol));
            return true;
        });

        threads.add(() -> {
            char symbol = 'c';
            System.out.println("Максимальное количество символов 'c': " + symbolRepeats(thirdQueue, symbol));
            return true;
        });

        threadPool.invokeAll(threads);
        threadPool.shutdown();
    }

    public static int symbolRepeats(BlockingQueue<String> queue, char symbol) {
        int max = 0;
        for (int i = 0; i < AMOUNT_OF_TEXTS; i++) {
            try {
                max = (int) queue.take().chars().filter(ch -> ch == symbol).count();
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        }
        return max;
    }

    public static String generateText(String letters, int length) {
        Random random = new Random();
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < length; i++) {
            text.append(letters.charAt(random.nextInt(letters.length())));
        }
        return text.toString();
    }
}
