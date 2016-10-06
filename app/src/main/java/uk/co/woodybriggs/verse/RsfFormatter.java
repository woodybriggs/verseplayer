package uk.co.woodybriggs.verse;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

class FormatRSF {

    FormatRSF (String FilePath, String Location) {
        HEADER head = new HEADER();
        this.byteIntArray = new byte[4];
        this.byteLongArray = new byte[8];

        try {
            this.inputFile = new RandomAccessFile(FilePath, "rw");

            // Read Header Size
            this.inputFile.read(byteIntArray);
            head.headerSize = this.byteArrayToInt(byteIntArray);

            // Read Last Played
            this.inputFile.read(byteIntArray);
            head.lastPlayed = this.byteArrayToInt(byteIntArray);

            // Read Number of Versions
            this.inputFile.read(byteIntArray);
            head.numberOfVersions = this.byteArrayToInt(byteIntArray);

            // Read Bytes Per Sample
            head.bytesPerSample = new long[head.numberOfVersions];
            for (int i = 0; i < head.numberOfVersions; ++i) {
                this.inputFile.read(byteLongArray);
                head.bytesPerSample[i] = this.byteArrayToLong(byteLongArray);
            }

            this.choice = randomChoice(head.numberOfVersions, head.lastPlayed);

            for (int i = 0; i < this.choice - 1; ++i) {
                this.seekPos = this.seekPos + (int) head.bytesPerSample[i];
            }
            this.seekPos = this.seekPos + head.headerSize;

        } catch (IOException e) {
            e.printStackTrace();
        }

        this.outputPath = new File(Location + "/temp.mp3");

        if (this.outputPath.exists()) {
            this.outputPath.delete();
            try {
                this.outputPath.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        RandomAccessFile outputFile = null;

        byte[] allData = new byte[(int) head.bytesPerSample[this.choice-1]];

        try {
            this.inputFile.seek(4);
            this.inputFile.write(intToByteArray(this.choice));

            this.inputFile.seek(this.seekPos);
            this.inputFile.read(allData);



            outputFile = new RandomAccessFile(this.outputPath, "rw");
            outputFile.write(allData);

            this.inputFile.close();
            outputFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getOutputPath(){
        return this.outputPath.toString();
    }

    private File outputPath;

    private RandomAccessFile inputFile;

    private int seekPos;
    private int choice;

    private byte[] byteIntArray;
    private byte[] byteLongArray;

    private int randomChoice(int numOfVersions, int lastPlayed) {

        int choice = 0;
        Random rand = new Random();

        while (choice == 0 || choice == lastPlayed) {
            choice = rand.nextInt((numOfVersions - 1) + 1) + 1;
        }

        return choice;
    }

    private byte[] intToByteArray(int integer) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(integer);
        byte[] bytes = buf.array();
        return bytes;
    }

    private int  byteArrayToInt (byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getInt();
    }

    public long byteArrayToLong(byte[] b) {
        final ByteBuffer bb = ByteBuffer.wrap(b);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        return bb.getLong();
    }

}
