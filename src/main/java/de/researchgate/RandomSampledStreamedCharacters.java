package de.researchgate;


import java.util.Random;

public class RandomSampledStreamedCharacters {

    private char[] randomSample;
    private Random randomGenerator;
    private boolean firstBufferFlag = true;

    public RandomSampledStreamedCharacters(int sampleSize) {
        this.randomSample = new char[sampleSize];
        this.randomGenerator = new Random();
    }

    // for testing purposes.
    public void setRandomGenerator(Random randomGenerator) {
        this.randomGenerator = randomGenerator;
    }

    /* This method generate the actual random sample representative of the character stream.
    *
    *  If the given buffer is the first, it generate number of character corresponds to the required sample size, because it is not guaranteed that the stream contains more.
    *  If not : then for each following buffer we take number of elements which is randomly generated.
    *
    *  The idea is to generate a sample which is really represntative for the whole stream. If the sample is taken for each given buffer only, so the
    *  resulting sample is not representative to the whole stream, but to the last seen buffer only.
    *
    *  The solution relies heavily on Randomness, in order to generate a truly representative sample.
    *
    * */
    public void generateRandomSample(char[] buffer, int readChars) {

        if (buffer == null)
            throw new NullPointerException(" Input Buffer is Null");

        if (readChars < 1)
            throw new IllegalArgumentException("Number of input characters is not sufficient. It is " + readChars);

        if (this.firstBufferFlag) {
            for (int i = 0; i < this.randomSample.length; i++) {
                this.randomSample[i] = buffer[generateRandomSampleIndex(readChars)];
            }

            this.firstBufferFlag = false;
        } else {

            int sampleSize = generateRandomSampleSize();
            for (int j = 0; j < sampleSize; j++) {
                this.randomSample[generateRandomSampleIndex(this.randomSample.length)] = buffer[generateRandomSampleIndex(readChars)];
            }
        }
    }

    // generate random number of elements to be taken as a sample for this buffer read. The generated number in the closed range [ 1 , sampleSize ].
    private int generateRandomSampleSize() {

        return this.randomGenerator.nextInt(this.randomSample.length) + 1;
    }

    // generate a random number represent an index of the array. The generated number in the open range [ 0 , upperBoundRange ).
    private int generateRandomSampleIndex(int upperBoundRange) {

        return this.randomGenerator.nextInt(upperBoundRange);
    }

    public String getRandomSampleRepresentative() {

        return new String(this.randomSample);
    }

}
