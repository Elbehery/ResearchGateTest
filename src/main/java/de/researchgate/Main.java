package de.researchgate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;


public class Main {

    public static final String RANDOM_GENERATOR_URL = "https://www.random.org/strings/?num=10&len=8&digits=on&upperalpha=on&loweralpha=on&unique=on&format=html&rnd=new";
    public static final int SAMPLE_SIZE = 5;
    public static final int BUFFER_SIZE = 4 * 1024;
    public static final int GENERATED_CHARACTERS_LENGTH = 2000;

    // This method checks if the piped input is available, if it is not, the HTTP input is used. InputStream.available() should be enough, but the rest for the sake of completeness.
    public static boolean isPipedInputAvailable(InputStream inputStream) {

        PushbackInputStream pushbackInputStream = null;
        try {
            if (inputStream.available() == 0)
                return false;

            pushbackInputStream = new PushbackInputStream(inputStream);
            int data;
            data = pushbackInputStream.read();
            if (data == -1)
                return false;

            pushbackInputStream.unread(data);
        } catch (IOException e) {
            System.err.println("Input Stream is not available : " + e.getMessage());
            return false;
        }

        return true;
    }

    // This method is invoked in case the piped input stream does not exist, it invokes the given URL. If the URL is not correct, the program terminates after throwing the exception.
    public static InputStream getHTTPInputStreamFromGivenURLString(String url) {

        InputStream inputStream = null;
        try {
            URL urlObject = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
            inputStream = connection.getInputStream();
        } catch (MalformedURLException e) {
            System.err.println("The Input URL is Malformed: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Invoking this url caused: " + e.getMessage());
        }

        return inputStream;
    }

    // This method generate Random Character String and return it as InputStream to be consumed.
    public static InputStream generateRandomCharacterStream(int numberCharsToGenerate) {

        Random random = new Random();
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < numberCharsToGenerate; i++) {
            builder.append((char) (random.nextInt((122 - 97) + 1) + 97));
        }

        return new ByteArrayInputStream(builder.toString().getBytes());
    }

    public static String getApplicationMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        return String.valueOf(memoryUsed / (1024 * 1024));
    }


    public static void main(String[] args) throws IOException {

        InputStream inputStream = null;

        if (isPipedInputAvailable(System.in)) {
            inputStream = System.in;
            System.out.println(" Using Piped Input");
        } else {
            // This URL is for test purpose. Replace it with any valid URL. The same for SAMPLE_SIZE.
            inputStream = getHTTPInputStreamFromGivenURLString(RANDOM_GENERATOR_URL);
            if (inputStream != null)
                System.out.println("Using HTTP Source");
        }

        // Use the Generated Random Character Stream as last option.
        if (inputStream == null) {
            inputStream = generateRandomCharacterStream(GENERATED_CHARACTERS_LENGTH);
            System.out.println("Using Generated Random Characters Sequence");
        }

        RandomSampledStreamedCharacters randomSampledStreamedCharacters = new RandomSampledStreamedCharacters(SAMPLE_SIZE);
        char[] buffer = new char[BUFFER_SIZE];

        try (Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {

            int readChars;
            while ((readChars = inputStreamReader.read(buffer)) != -1) {
                randomSampledStreamedCharacters.generateRandomSample(buffer, readChars);
            }
        }

        System.out.println("Random Sample :" + randomSampledStreamedCharacters.getRandomSampleRepresentative());
        System.out.println("Maximum Allocated Memory : " + getApplicationMemoryUsage() + " MB ");

    }
}
