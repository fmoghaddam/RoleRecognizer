package main;

import java.io.IOException;
import java.io.Reader;

public class MyReader extends Reader {

    final private Reader internalReader;
    private int pos;
    private int line;

    public MyReader(Reader internalReader) {
        this.internalReader = internalReader;
    }

    public int getPos() {
        return pos;
    }

    public int getLine() {
        return line;
    }


    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {

        int chars_read = internalReader.read(cbuf, off, 1);
        pos += chars_read;
        if(cbuf[off] =='\n' && chars_read > 0) {
            line++;
        }
        return chars_read;
    }

    @Override
    public void close() throws IOException {
        internalReader.close();
    }
}