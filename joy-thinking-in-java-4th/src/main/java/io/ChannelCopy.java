package io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class ChannelCopy {
    private static final int BSIZE = 1024;

    public static void main(String[] args) throws IOException {
        String ss = "D:\\idea_workspace\\smc-multi-project\\joy-thinking-in-java-4th\\src\\main\\java\\io\\BufferedInputFile.java";

        FileChannel in = new FileInputStream(ss).getChannel();
        FileChannel out = new FileOutputStream("ttt.txt").getChannel();

        ByteBuffer buffer = ByteBuffer.allocate(BSIZE);
        while (in.read(buffer) != -1) {
            buffer.flip();
            out.write(buffer);
            buffer.clear();
        }
    }
}
