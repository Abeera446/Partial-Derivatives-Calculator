package org.example;

import org.matheclipse.core.eval.EvalUtilities;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IExpr;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartialDerivatives {
    public static void main(String[] args) {

        int choice;

        do {
            System.out.println("\n---Partial Derivatives Program---\n");
            System.out.println("Choose from the following list:\n");
            System.out.println("1. First Partial Derivative \n2. First Partial Derivative (indicate numeric values) \n3. Implicit Differentiation" +
                    "\n4. Second Partial Derivative \n5. Verify Clairaut's Theorem \n6. Higher Order Partial Derivative " +
                    "\n7. Verify Laplace's Equation \n8. Quit");

            Scanner sc = new Scanner(System.in);
            System.out.print("\nEnter your choice: ");
            choice = sc.nextInt();

            switch (choice) {
                case 1: FirstPartialDerivative(); break;
                case 2: FirstPartialDerivative2(); break;
                case 3: ImplicitDiff(); break;
                case 4: SecondPartialDerivative(); break;
                case 5: Clairaut(); break;
                case 6: HigherOrder(); break;
                case 7: Laplace(); break;
            }
        } while(choice!=8);
    }

    public static void FirstPartialDerivative() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your function: ");
        String function = sc.nextLine();

        Set<String> detected = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(function);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi"))
                detected.add(var);
        }

        if (detected.isEmpty()) {
            System.out.println("No variables detected.");
            return;
        }

        System.out.println("Detected variables: " + detected);
        System.out.print("Enter space-separated variables to differentiate with respect to: ");
        String[] chosenVars = sc.nextLine().trim().split("\\s+");

        try {
            for (String var : chosenVars) {
                IExpr derivative = util.evaluate("D(" + function + ", " + var + ")");
                System.out.println("∂f/∂" + var + " = " + derivative);
            }
        }
        catch (Exception e) {
            System.out.println("Error parsing or differentiating function: " + e.getMessage());
        }
    }

    public static void FirstPartialDerivative2() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your function: ");
        String function = sc.nextLine();

        Set<String> detected = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(function);

        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi"))
                detected.add(var);
        }

        if (detected.isEmpty()) {
            System.out.println("No variables detected.");
            return;
        }

        System.out.println("Detected variables: " + detected);

        System.out.print("Enter the dependent variable: ");
        String diffVar = sc.nextLine().trim();

        Map<String, Double> values = new HashMap<>();
        for (String var : detected) {
            System.out.print("Enter value for " + var + ": ");
            values.put(var, sc.nextDouble());
        }

        try {
            IExpr derivative = util.evaluate("D(" + function + ", " + diffVar + ")");
            System.out.println("Symbolic derivative ∂f/∂" + diffVar + " = " + derivative);

            StringBuilder evalExpr = new StringBuilder(derivative.toString());
            for (Map.Entry<String, Double> entry : values.entrySet()) {
                evalExpr = new StringBuilder(
                        evalExpr.toString().replaceAll("\\b" + entry.getKey() + "\\b", "(" + entry.getValue() + ")")
                );
            }

            IExpr numericResult = util.evaluate(evalExpr.toString());
            System.out.println("Value at point = " + numericResult);

        }
        catch (Exception e) {
            System.out.println("Error parsing or differentiating function: " + e.getMessage());
        }
    }

    public static void ImplicitDiff() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter your equation: ");
        String equation = sc.nextLine().trim();

        // Split into left and right sides
        String[] sides = equation.split("=");
        if (sides.length != 2) {
            System.out.println("Error: Equation must contain exactly one '=' sign.");
            return;
        }
        String lhs = sides[0].trim();
        String rhs = sides[1].trim();

        // Detect all variables
        Set<String> variables = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(equation);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi")) {
                variables.add(var);
            }
        }

        if (variables.isEmpty()) {
            System.out.println("No variables detected in the equation.");
            return;
        }

        System.out.println("Detected variables: " + variables);

        // Get dependent variable
        System.out.print("Enter dependent variable: ");
        String dependentVar = sc.nextLine().trim();

        if (!variables.contains(dependentVar)) {
            System.out.println("Error: The dependent variable must be one of the detected variables.");
            return;
        }

        // Get variables to differentiate with respect to
        System.out.print("Enter space-separated variables to differentiate with respect to: ");
        String[] diffVars = sc.nextLine().trim().split("\\s+");

        // Verify all differentiation variables are valid
        for (String var : diffVars) {
            if (!variables.contains(var)) {
                System.out.println("Error: " + var + " is not a valid variable in the equation.");
                return;
            }
            if (var.equals(dependentVar)) {
                System.out.println("Error: Cannot differentiate dependent variable with respect to itself.");
                return;
            }
        }

        try {
            String F = "(" + lhs + ") - (" + rhs + ")";

            System.out.println("\nImplicit differentiation results:");

            for (String var : diffVars) {
                // Compute partial derivatives
                IExpr dF_dDep = util.evaluate("D(" + F + ", " + dependentVar + ")");
                IExpr dF_dVar = util.evaluate("D(" + F + ", " + var + ")");

                // Compute derivative using implicit differentiation formula: dy/dx = - (∂F/∂x)/(∂F/∂y)
                IExpr derivative = util.evaluate("-(" + dF_dVar + ")/(" + dF_dDep + ")");

                System.out.println("∂" + dependentVar + "/∂" + var + " = " + derivative);
            }

        }
        catch (Exception e) {
            System.out.println("Error during differentiation: " + e.getMessage());
        }
    }

    public static void SecondPartialDerivative() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your function: ");
        String function = sc.nextLine();

        // Detect variables
        Set<String> detected = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(function);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi")) {
                detected.add(var);
            }
        }

        // Verify exactly 2 variables
        if (detected.size() != 2) {
            System.out.println("Error: Function must contain exactly 2 variables. Detected: " + detected);
            return;
        }

        List<String> vars = new ArrayList<>(detected);
        String var1 = vars.get(0);
        String var2 = vars.get(1);

        System.out.println("Detected variables: " + var1 + " and " + var2);
        System.out.println("\nComputing all second-order partial derivatives...");

        try {
            // First derivatives
            IExpr fx = util.evaluate("D(" + function + ", " + var1 + ")");
            IExpr fy = util.evaluate("D(" + function + ", " + var2 + ")");

            System.out.println("\nFirst derivatives:");
            System.out.println("∂f/∂" + var1 + " = " + fx);
            System.out.println("∂f/∂" + var2 + " = " + fy);

            // Second derivatives
            IExpr fxx = util.evaluate("D(" + function + ", { " + var1 + ", 2 })");
            IExpr fyy = util.evaluate("D(" + function + ", { " + var2 + ", 2 })");
            IExpr fxy = util.evaluate("D(D(" + function + ", " + var1 + "), " + var2 + ")");
            IExpr fyx = util.evaluate("D(D(" + function + ", " + var2 + "), " + var1 + ")");

            System.out.println("\nSecond derivatives:");
            System.out.println("∂²f/∂" + var1 + "² = " + fxx);
            System.out.println("∂²f/∂" + var2 + "² = " + fyy);
            System.out.println("∂²f/∂" + var1 + "∂" + var2 + " = " + fxy);
            System.out.println("∂²f/∂" + var2 + "∂" + var1 + " = " + fyx);

            // Verify Clairaut's theorem (fxy should equal fyx for continuous functions)
            if (fxy.equals(fyx)) {
                System.out.println("\nNote: Mixed partials are equal (∂²f/∂" + var1 + "∂" + var2 +
                        " = ∂²f/∂" + var2 + "∂" + var1 + "), as expected by Clairaut's theorem.");
            } else {
                System.out.println("\nWarning: Mixed partials are not equal. This may indicate either:");
                System.out.println("1. The function or its derivatives are not continuous at some point");
                System.out.println("2. There might be a computational error");
            }

        } catch (Exception e) {
            System.out.println("Error parsing or differentiating function: " + e.getMessage());
        }
    }

    public static void Clairaut() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("\nEnter your function: ");
        String function = sc.nextLine();

        // Detect variables
        Set<String> variables = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(function);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi")) {
                variables.add(var);
            }
        }

        // Verify exactly 2 variables
        if (variables.size() != 2) {
            System.out.println("Error: Function must contain exactly 2 variables. Detected: " + variables);
            return;
        }

        List<String> vars = new ArrayList<>(variables);
        String x = vars.get(0);
        String y = vars.get(1);

        System.out.println("\nDetected variables: " + x + " and " + y);
        System.out.println("Computing mixed partial derivatives...");

        try {
            // Compute mixed partial derivatives
            IExpr fxy = util.evaluate("D(D(" + function + ", " + x + "), " + y + ")");
            IExpr fyx = util.evaluate("D(D(" + function + ", " + y + "), " + x + ")");

            System.out.println("\n∂²f/∂" + x + "∂" + y + " = " + fxy);
            System.out.println("∂²f/∂" + y + "∂" + x + " = " + fyx);

            // Check if they're equal
            if (fxy.equals(fyx)) {
                System.out.println("\nClairaut's theorem HOLDS for this function");
            }
            else {
                System.out.println("\nClairaut's theorem does NOT HOLD for this function");
                System.out.println("The mixed partial derivatives are not equal.");
                System.out.println("Possible reasons:");
                System.out.println("1. The function or its derivatives are not continuous");
                System.out.println("2. There might be a computational error");
            }

        }
        catch (Exception e) {
            System.out.println("Error computing derivatives: " + e.getMessage());
        }
    }

    public static void HigherOrder() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter your function: ");
        String function = sc.nextLine();

        // Detect variables
        Set<String> detected = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("\\b[a-zA-Z]\\b").matcher(function);
        while (matcher.find()) {
            String var = matcher.group();
            if (!var.equalsIgnoreCase("e") && !var.equalsIgnoreCase("Pi")) {
                detected.add(var);
            }
        }

        if (detected.isEmpty()) {
            System.out.println("No variables detected.");
            return;
        }

        System.out.println("Detected variables: " + detected);

        // Ask how many derivatives
        System.out.print("How many derivatives do you want?: ");
        int numDerivatives = Integer.parseInt(sc.nextLine().trim());

        List<String> derivatives = new ArrayList<>();
        for (int i = 1; i <= numDerivatives; i++) {
            System.out.print("Enter derivative " + i + " (e.g. xx, xyx): ");
            derivatives.add(sc.nextLine().trim());
        }

        System.out.println("\nSolution:");
        try {
            for (String der : derivatives) {
                String expr = function;
                // Apply differentiation step-by-step according to each letter
                for (char varChar : der.toCharArray()) {
                    expr = util.evaluate("D(" + expr + ", " + varChar + ")").toString();
                }
                System.out.println("f" + der + " = " + expr);
            }
        }
        catch (Exception e) {
            System.out.println("Error computing derivatives: " + e.getMessage());
        }
    }

    public static void Laplace() {
        F.initSymbols();
        EvalUtilities util = new EvalUtilities(false, true);

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter the function f(x, y): ");
        String function = sc.nextLine();

        try {
            IExpr fxx = util.evaluate("D(D(" + function + ", x), x)");
            IExpr fyy = util.evaluate("D(D(" + function + ", y), y)");
            IExpr sum = util.evaluate("Simplify(" + fxx + " + " + fyy + ")");

            System.out.println("fxx = " + fxx);
            System.out.println("fyy = " + fyy);
            System.out.println("fxx + fyy = " + sum);

            if (sum.toString().equals("0"))
                System.out.println("This function is a solution of Laplace's equation.");
            else
                System.out.println("This function is NOT a solution of Laplace's equation.");

        }
        catch (Exception e) {
            System.out.println("Error while computing derivatives.");
        }
    }
}