package display;

public class Terminal {

    public class Cursor {

        public int x, y;

        public byte style;
        public byte[] foreground_colour, background_colour;


        public Cursor() {
            x = 0; y = 0;

            style = 0;

            foreground_colour = new byte[] {(byte)255, (byte)255, (byte)255};
            background_colour = new byte[] {        0,         0,         0};
        }


        public void move(int x, int y) {
            this.x = x; this.y = y;
            System.out.print("\033["+y+";"+x+"H");
        }
        public void shift(int dx, int dy) {
            move(x+dx, y+dy);
        }


        public void applyStyle() {
            System.out.print("\033[m");
            if(((style >> 0) & 1) == 1) System.out.print("\033[?25h");
            else                        System.out.print("\033[?25l");
            if(((style >> 1) & 1) == 1) System.out.print("\033[1m");
            else                        System.out.print("\033[2m");
            if(((style >> 2) & 1) == 1) System.out.print("\033[4m" );
            else                        System.out.print("\033[24m");

            setColours();
        }
        public void setVisible(boolean state) {
            if(state) style |=  1;
            else      style &= ~1;
            applyStyle();
        }
        public void setBold(boolean state) {
            if(state) style |=  2;
            else      style &= ~2;
            applyStyle();
        }
        public void setUnderline(boolean state) {
            if(state) style |=  4;
            else      style &= ~4;
            applyStyle();
        }


        public void setColours() {
            setForegroundColour(foreground_colour);
            setBackgroundColour(background_colour);
        }

        public void setForegroundColour(byte[] colour) { setForegroundColour(colour[0], colour[1], colour[2]); }
        public void setForegroundColour(int r, int g, int b) {
            foreground_colour = new byte[] {(byte)r, (byte)g, (byte)b};
            System.out.print("\033[38;2;"+(r&0xFF)+";"+(g&0xFF)+";"+(b&0xFF)+"m");
        }
        public void setBackgroundColour(byte[] colour) { setBackgroundColour(colour[0], colour[1], colour[2]); }
        public void setBackgroundColour(int r, int g, int b) {
            background_colour = new byte[] {(byte)r, (byte)g, (byte)b};
            System.out.print("\033[48;2;"+(r&0xFF)+";"+(g&0xFF)+";"+(b&0xFF)+"m");
        }

        public void swapColours() {
            byte[] background_temp = background_colour.clone();
            setBackgroundColour(foreground_colour);
            setForegroundColour(background_temp  );
        }

        public void setForegroundDefault() { System.out.print("\033[39m"); }
        public void setBackgroundDefault() { System.out.print("\033[49m"); }


        public void resetStyle() {
            style = 0;
            System.out.print("\033[m");
        }
        public void dispose() {
            setVisible(true);
            resetStyle();
        }
    }
    public Cursor cursor;


    public Terminal() {
        cursor = new Cursor();

        clearScreen();
    }

    public void clearScreen() {
        cursor.move(0, 0);
        System.out.print("\033[J");
    }
    public void clearScreen(int r, int g, int b) {
        cursor.setBackgroundColour(r, g, b);
        clearScreen();
    }

    public void print(String string) {
        int x = 0, y = 0;
        for(char c : string.toCharArray()) {
            System.out.print(c);
            if(c == '\n') { y++; x = 0; }
            else            x++;
        }
        cursor.shift(x, y);
    }
    public void printFixed(String string) {
        cursor.shift(0, 0);
        for(String line : string.split("\n")) {
            System.out.print(line);
            cursor.shift(0, 1);
        } cursor.shift(0, -1);
    }

    public void dispose() {
        System.out.print("\033c");

        cursor = null;
    }
}