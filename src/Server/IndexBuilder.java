package Server;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class IndexBuilder {

    private File[] files;
    private int threadsNumber;
    private ConcurrentHashMap<String, Collection<String>> index = null;

    public IndexBuilder(String dirPath,  int threadsNumber) {
        this.files = new File(System.getProperty("user.dir") + dirPath).listFiles();
        this.threadsNumber = threadsNumber;
        System.out.println("Index builder constructor");
    }

    public Index buildIndex() {
        System.out.println("IndexBuilder: buildIndex()");
        index = new ConcurrentHashMap<>(32768, 0.75f, threadsNumber);
        System.out.println("CHM created");
        Thread[] threads = new Thread[threadsNumber];

        for (int i = 0; i < threadsNumber; ++i) {
            System.out.println("Init thread-" + i);
            threads[i] = new IndexBuilderThread(
                    files.length / threadsNumber * i,
                    i == (threadsNumber - 1) ? files.length : files.length / threadsNumber * (i + 1)
            );
            System.out.println("call start for thread-" +i);
            threads[i].start();
        }
        try {
            for (int i = 0; i < threadsNumber; i++) {
                threads[i].join();
                System.out.println(index.getOrDefault("car", null));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Index(index);
    }

    public Collection<String> findInvertedIndex(String word) {
        if (index == null) {
            return null;
        }
        Collection<String> files = index.getOrDefault(word, null);
        return files;
    }

    private class IndexBuilderThread extends Thread {
        private final int start;
        private final int end;

        IndexBuilderThread(int start, int end) {
            System.out.println("IndexBuilderThread constructor");
            this.start = start;
            this.end = end;
        }

        public void run() {
            System.out.println("IndexBuilderThread method run()" + start + " to " + end);
            for (int i = start; i < end; ++i) {
                try {
                    Path file = files[i].toPath();
                    String fileContent = Files.readString(file);
                    String fileName = file.getFileName().toString();
                    String[] words = fileContent.replaceAll("<br />", " ")
                            .trim()
                            .toLowerCase()
                            .split(" ");

                    for (int j = 0; j < words.length; j++) {
                        String word = words[j];
                        index.computeIfAbsent(word, (key) -> new ConcurrentLinkedQueue<>()).add(fileName);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("IndexBuilderThread after for");
        }
    }
}
