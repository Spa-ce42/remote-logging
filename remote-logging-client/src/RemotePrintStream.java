import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class RemotePrintStream extends PrintStream {
    public RemotePrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void print(boolean b) {
        super.print(b);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(char c) {
        super.print(c);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(int i) {
        super.print(i);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(long l) {
        super.print(l);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(float f) {
        super.print(f);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(double d) {
        super.print(d);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(char[] s) {
        super.print(s);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(String s) {
        super.print(s);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void print(Object obj) {
        super.print(obj);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println() {
        super.println();

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(boolean x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(char x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(int x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(long x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(float x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(double x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(char[] x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(String x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public void println(Object x) {
        super.println(x);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        super.printf(format, args);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }

        return this;
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        super.printf(l, format, args);

        try {
            super.out.flush();
        } catch(IOException ignored) {

        }

        return this;
    }
}
