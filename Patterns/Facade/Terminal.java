package Patterns.Facade;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.util.Objects;


public final class Terminal {
    private final BufferedReader input;
    private final PrintStream output;

    public Terminal(Reader input, PrintStream output) {
        this.input = input instanceof BufferedReader buffered ? buffered : new BufferedReader(input);
        this.output = Objects.requireNonNull(output, "output");
    }

    public PrintStream output() { return output; }

    public int number(String prompt, int min, int max) throws IOException {
        while (true) {
            try {
                int value = Integer.parseInt(line(prompt).strip());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            output.println("Enter a number from " + min + " to " + max + ".");
        }
    }

    public String line(String prompt) throws IOException {
        output.print(prompt);
        output.flush();
        String value = input.readLine();
        if (value == null) throw new EndOfInputException();
        return value;
    }
}
