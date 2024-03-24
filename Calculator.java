// Calculator.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculator {
    public static void main(String[] args) {
        // Create and configure the JFrame
        JFrame frame = new JFrame("Simple Calculator");
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Create and configure the JPanel for displaying the calculator
        JPanel calculatorPanel = new JPanel();
        calculatorPanel.setLayout(new GridLayout(4, 4, 5, 5));
        frame.add(calculatorPanel, BorderLayout.CENTER);

        // Create and configure the JTextArea for displaying the result
        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        frame.add(resultArea, BorderLayout.NORTH);

        // Create buttons for the calculator
        String[] buttonLabels = {"7", "8", "9", "/", "4", "5", "6", "*", "1", "2", "3", "-", "C", "0", "=", "+"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String buttonText = button.getText();
                    if (buttonText.equals("=")) {
                        // Calculate the result
                        String expression = resultArea.getText();
                        try {
                            double result = evaluateExpression(expression);
                            resultArea.setText(Double.toString(result));
                        } catch (NumberFormatException ex) {
                            resultArea.setText("Error: Invalid expression");
                        }
                    } else if (buttonText.equals("C")) {
                        // Clear the result
                        resultArea.setText("");
                    } else {
                        // Append the button label to the expression
                        resultArea.append(buttonText);
                    }
                }
            });
            calculatorPanel.add(button);
        }

        // Display the JFrame
        frame.setVisible(true);
    }

    // Evaluate the arithmetic expression
    private static double evaluateExpression(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char)ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) { 
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { 
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char)ch);
                }

                return x;
            }
        }.parse();
    }
}
