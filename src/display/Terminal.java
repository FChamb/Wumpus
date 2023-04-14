package display;

/**
 * Manages the terminal display.
 */
public class Terminal {

    /**
     * Manages the {@linkplain Terminal terminal} cursor.
     */
    public class Cursor {

        /**
         * The x position.
         */
        public int x;
        /**
         * The y position.
         */
        public int y;

        /**
         * The styling of text.
         */
        public byte style;
        /**
         * The foreground colour.
         */
        public byte[] foreground_colour;
        /**
         * The background colour.
         */
        public byte[] background_colour;


        public Cursor() {
            x = 0; y = 0;

            style = 0;

            foreground_colour = new byte[] {(byte)255, (byte)255, (byte)255};
            background_colour = new byte[] {        0,         0,         0};
        }


        /**
         * Move the cursor to a new position.
         * 
         * @param x the new x position
         * @param y the new y position
         */
        public void move(int x, int y) {
            this.x = x; this.y = y;
            System.out.print("\033["+y+";"+x+"H");
        }
        /**
         * Shift the cursor relative to its position.
         * 
         * @param dx the change in the x position
         * @param dy the change in the y position
         */
        public void shift(int dx, int dy) {
            move(x+dx, y+dy);
        }


        /**
         * Update the text styling.
         */
        public void applyStyle() {
            System.out.print("\033[m");
            if(((style >> 0) & 1) == 1) System.out.print("\033[?25h");
            else                        System.out.print("\033[?25l");
            if(((style >> 1) & 1) == 1) System.out.print("\033[1m");
            else                        System.out.print("\033[2m");
            if(((style >> 2) & 1) == 1) System.out.print("\033[4m" );
            else                        System.out.print("\033[24m");
            if(((style >> 3) & 1) == 1) System.out.print("\033[5m" );
            else                        System.out.print("\033[25m");
            if(((style >> 4) & 1) == 1) System.out.print("\033[3m" );
            else                        System.out.print("\033[23m");

            setColours();
        }
        /**
         * Make the cursor visible.
         * 
         * @param state the new state
         */
        public void setVisible(boolean state) {
            if(state) style |=  1;
            else      style &= ~1;
            applyStyle();
        }
        /**
         * Make the text bold.
         * 
         * @param state the new state
         */
        public void setBold(boolean state) {
            if(state) style |=  2;
            else      style &= ~2;
            applyStyle();
        }
        /**
         * Make the text underline.
         * 
         * @param state the new state
         */
        public void setUnderline(boolean state) {
            if(state) style |=  4;
            else      style &= ~4;
            applyStyle();
        }
        /**
         * Make the text blink.
         * 
         * @param state the new state
         */
        public void setBlinking(boolean state) {
            if(state) style |=  8;
            else      style &= ~8;
            applyStyle();
        }
        /**
         * Make the text italic.
         * 
         * @param state the new state
         */
        public void setItalics(boolean state) {
            if(state) style |=  16;
            else      style &= ~16;
            applyStyle();
        }


        /**
         * Apply the text colouring.
         */
        public void setColours() {
            setForegroundColour(foreground_colour);
            setBackgroundColour(background_colour);
        }

        /**
         * Set the foreground colour.
         * 
         * @param colour the colour array
         */
        public void setForegroundColour(byte[] colour) { setForegroundColour(colour[0], colour[1], colour[2]); }
        /**
         * Set the foreground colour.
         * 
         * @param r the red value
         * @param g the green value
         * @param b the blue value
         */
        public void setForegroundColour(int r, int g, int b) {
            foreground_colour = new byte[] {(byte)r, (byte)g, (byte)b};
            System.out.print("\033[38;2;"+(r&0xFF)+";"+(g&0xFF)+";"+(b&0xFF)+"m");
        }
        /**
         * Set the background colour.
         * 
         * @param colour the colour array
         */
        public void setBackgroundColour(byte[] colour) { setBackgroundColour(colour[0], colour[1], colour[2]); }
        /**
         * Set the background colour.
         * 
         * @param r the red value
         * @param g the green value
         * @param b the blue value
         */
        public void setBackgroundColour(int r, int g, int b) {
            background_colour = new byte[] {(byte)r, (byte)g, (byte)b};
            System.out.print("\033[48;2;"+(r&0xFF)+";"+(g&0xFF)+";"+(b&0xFF)+"m");
        }

        /**
         * Swaps the foreground and background colours.
         */
        public void swapColours() {
            byte[] background_temp = background_colour.clone();
            setBackgroundColour(foreground_colour);
            setForegroundColour(background_temp  );
        }

        /**
         * Sets the foreground colour to the terminal's default.
         */
        public void setForegroundDefault() { System.out.print("\033[39m"); }
        /**
         * Sets the background colour to the terminal's default.
         */
        public void setBackgroundDefault() { System.out.print("\033[49m"); }


        /**
         * Resets the styling.
         */
        public void resetStyle() {
            style = 0;
            System.out.print("\033[m");
        }
        /**
         * Dispose the cursor.
         */
        public void dispose() {
            setVisible(true);
            resetStyle();
        }
    }
    /**
     * The terminal's {@linkplain Cursor cursor}.
     */
    public Cursor cursor;


    public Terminal() {
        cursor = new Cursor();

        clearScreen();
    }

    /**
     * Clears the screen.
     */
    public void clearScreen() {
        cursor.move(0, 0);
        System.out.print("\033[J");
    }
    /**
     * Clears the screen with a specific background colour.
     * 
     * @param r the red value
     * @param g the green value
     * @param b the blue value
     */
    public void clearScreen(int r, int g, int b) {
        cursor.setBackgroundColour(r, g, b);
        clearScreen();
    }

    /**
     * Print a string to the terminal.
     * 
     * @param string the string to print
     */
    public void print(String string) {
        int x = 0, y = 0;
        for(char c : string.toCharArray()) {
            System.out.print(c);
            if(c == '\n') { y++; x = 0; }
            else            x++;
        }
        cursor.shift(x, y);
    }
    /**
     * Prints text with new-line characters with a fixed x position.
     * 
     * @param string the string to print
     */
    public void printFixed(String string) {
        cursor.shift(0, 0);
        for(String line : string.split("\n")) {
            System.out.print(line);
            cursor.shift(0, 1);
        } cursor.shift(0, -1);
    }

    /**
     * Dispose the terminal.
     */
    public void dispose() {
        System.out.print("\033c");

        cursor = null;
    }
}