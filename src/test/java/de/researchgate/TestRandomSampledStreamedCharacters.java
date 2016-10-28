package de.researchgate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Random;


public class TestRandomSampledStreamedCharacters {

    private static final int SAMPLE_SIZE = 5;
    private static final int INITIAL_RANDOM_SEED = 10;
    private static final int BUFFER_SIZE = 6;
    private static final String CHARACTER_STREAM_MOCK = "THEQUICKBROWNFOXJUMPSOVERTHELAZYDOG\n";
    private static final int INPUT_TEST_SIZE_AS_A_WHOLE = CHARACTER_STREAM_MOCK.length();
    private RandomSampledStreamedCharacters randomSampledStreamedCharacters;
    private RandomSampledStreamedCharacters randomSampledStreamedCharactersMock;
    private Random random;
    private Random randomMock;
    private char[] wholeInputTestBuffer;
    private char[] buffer;
    private File inputTestFile = null;


    @Before
    public void setUp() {

        this.randomSampledStreamedCharacters = new RandomSampledStreamedCharacters(SAMPLE_SIZE);
        this.randomSampledStreamedCharactersMock = new RandomSampledStreamedCharacters(SAMPLE_SIZE);
        this.random = new Random(INITIAL_RANDOM_SEED);
        this.randomMock = new Random(INITIAL_RANDOM_SEED);
        this.randomSampledStreamedCharacters.setRandomGenerator(random);
        this.randomSampledStreamedCharactersMock.setRandomGenerator(randomMock);
        this.buffer = new char[BUFFER_SIZE];
        this.wholeInputTestBuffer = new char[INPUT_TEST_SIZE_AS_A_WHOLE];

        ClassLoader classLoader = getClass().getClassLoader();
        this.inputTestFile = new File(classLoader.getResource("SampleTest.txt").getFile());
    }


    @Test(expected = NullPointerException.class)
    public void testGenerateRandomSampleWithNullBuffer() {

        this.randomSampledStreamedCharactersMock.generateRandomSample(null, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateRandomSampleWithIllegalCharacterReadArgument() {

        this.randomSampledStreamedCharactersMock.generateRandomSample(buffer, -1);
    }


    @Test
    public void testGenerateRandomSampleWithOneBuffer() throws IOException {

        try (InputStream inputStream = new FileInputStream(this.inputTestFile);
             Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {

            int readChars;
            while ((readChars = inputStreamReader.read(this.wholeInputTestBuffer)) != -1) {

                this.randomSampledStreamedCharacters.generateRandomSample(this.wholeInputTestBuffer, readChars);
            }
        }
        String actual = this.randomSampledStreamedCharacters.getRandomSampleRepresentative();

        this.randomSampledStreamedCharactersMock.generateRandomSample(CHARACTER_STREAM_MOCK.toCharArray(), INPUT_TEST_SIZE_AS_A_WHOLE);
        String expected = this.randomSampledStreamedCharactersMock.getRandomSampleRepresentative();

        Assert.assertEquals("Generate Random Sample is not working correctly with ONE input chunk", expected, actual);
    }


    @Test
    public void testGenerateRandomSampleWithBuffers() throws IOException {

        try (InputStream inputStream = new FileInputStream(inputTestFile);
             Reader inputStreamReader = new InputStreamReader(inputStream, "UTF-8")) {

            int readChars;
            while ((readChars = inputStreamReader.read(this.buffer)) != -1) {

                this.randomSampledStreamedCharacters.generateRandomSample(this.buffer, readChars);
            }
        }

        String actual = this.randomSampledStreamedCharacters.getRandomSampleRepresentative();

        int index = 0;

        for (int i = 0; i < (CHARACTER_STREAM_MOCK.length() / BUFFER_SIZE); i++) {
            this.randomSampledStreamedCharactersMock.generateRandomSample(CHARACTER_STREAM_MOCK.substring(index, index + BUFFER_SIZE).toCharArray(), BUFFER_SIZE);
            index = index + BUFFER_SIZE;
        }
        String expected = this.randomSampledStreamedCharactersMock.getRandomSampleRepresentative();

        Assert.assertEquals("Generate Random Sample is not working correctly with multiple input chunks", expected, actual);
    }

}

