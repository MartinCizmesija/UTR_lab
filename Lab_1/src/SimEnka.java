import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class SimEnka {
    private static TreeSet<String> outputSet= new TreeSet<>();
    private static List<String> lastState= new LinkedList<>();

    public static void main(String args[]) {
        List<String> lines= new LinkedList<>();
/*
        Scanner scanner= new Scanner(System.in);
        do {
            lines.add(scanner.nextLine());
        } while (scanner.hasNext());
*/
        Path path = Paths.get("./test.txt");
        try {
            lines= Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(lines.size()<6) return;
        String entryLines= lines.get(0);
        String beginingState[]= lines.get(4).split(",");

        String behaviour[]= new String[lines.size()-5];
        for (int a=5; a<lines.size(); ++a) {
            behaviour[a-5]= lines.get(a);
        }

        List<String[]> input= new ArrayList<>();
        List<String[]> output= new ArrayList<>();
        for (int a=0; a<behaviour.length; ++a){
            String temp[]= behaviour[a].split("->");
            if (temp.length!= 2) return;
            input.add(temp[0].split(","));
            if (input.get(a).length!= 2) return;
            output.add(temp[1].split(","));
        }

        String entryLine[]= entryLines.split(Pattern.quote("|"));

        //popis ulaznih nizova
        List<String[]> entries = new ArrayList<>();
        for (String anEntryLine : entryLine) {
            entries.add(anEntryLine.split(","));
        }

        //po ulaznom nizu
        for (String[] entry : entries) {
            int commaConuter;
            int branchesCount;
            boolean hashFlag;

            for (String aBeginingState : beginingState) {
                if (!outputSet.contains(aBeginingState)) {
                    lastState.add(aBeginingState);
                    outputSet.add(aBeginingState);
                }
                epsilonHandler(input, output, aBeginingState);
            }

            //po ulaznim elementima jednoga niza
            for (String anEntry : entry) {
                commaConuter = outputSet.size() - 1;
                for (String s : outputSet) {
                    System.out.print(s);
                    if (commaConuter != 0) {
                        System.out.print(",");
                        --commaConuter;
                    }
                }
                branchesCount = outputSet.size();
                outputSet.clear();

                System.out.print("|");
                hashFlag = false;

                int lastStateCounter = lastState.size();
                //po granama rijesenja
                for (int c = 0; c < branchesCount; ++c) {
                    //po svim funkcijama prijelaza automata
                    for (int d = 0; d < input.size(); ++d) {
                        if (input.get(d)[0].equals(lastState.get(lastStateCounter - (c + 1))) &&
                                input.get(d)[1].equals(anEntry)) {

                            //iteriram po mogucim iducim stanjima
                            for (int x = 0; x < output.get(d).length; ++x) {
                                if (!output.get(d)[x].equals("#")) {
                                    if (!outputSet.contains(output.get(d)[x])) {
                                        outputSet.add(output.get(d)[x]);
                                        lastState.add(output.get(d)[x]);
                                    }

                                    epsilonHandler(input, output, output.get(d)[x]);
                                    hashFlag = true;
                                }
                            }
                            break;
                        }
                    }
                }
                if (!hashFlag) {
                    System.out.print("#");
                }

            }
            commaConuter = outputSet.size() - 1;
            for (String s : outputSet) {
                System.out.print(s);
                if (commaConuter != 0) {
                    System.out.print(",");
                    --commaConuter;
                }
            }

            outputSet.clear();
            System.out.println();
        }

    }

    private static boolean epsilonCheck(List<String[]> input, String startState) {
        for (String[] anInput : input) {
            if (anInput[0].equals(startState) && anInput[1].equals("$")) return true;
        }
        return false;
    }

    private static String[] epsilonFinder(List<String[]> input, List<String[]> output, String startState) {
        for (int a=0; a<input.size(); ++a) {
            if (input.get(a)[0].equals(startState) && input.get(a)[1].equals("$")) {
                return output.get(a);
            }
        }
        return new String[0];
    }

    private static void epsilonHandler (List<String[]> input, List<String[]> output, String startState) {
        if (epsilonCheck(input, startState)) {
            String[] temp = epsilonFinder(input, output, startState);
            for (String aTemp : temp) {
                if (!outputSet.contains(aTemp) && !aTemp.equals("#")) {
                    outputSet.add(aTemp);
                    lastState.add(aTemp);
                    epsilonHandler(input, output, aTemp);
                }
            }
        }
    }

}
