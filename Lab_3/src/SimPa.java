import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class SimPa {
    private static Stack<Character> stog= new Stack<>();

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

        //dohvat podataka
        if(lines.size()<8) return;

        //popis ulaznih nizova
        String entryLine[]= lines.get(0).split(Pattern.quote("|"));
        List<String[]> entries = new ArrayList<>();
        for (String anEntryLine : entryLine) {
            entries.add(anEntryLine.split(","));
        }
        //String states[] = lines.get(1).split(",");
        //String entrySigns[]= lines.get(2).split(",");
        //String stogChars[]= lines.get(3).split(",");
        String endStates[]= lines.get(4).split(",");
        String beginingState= lines.get(5);
        Character beginingStogChar= lines.get(6).charAt(0);

        String behaviour[]= new String[lines.size()-7];
        for (int a=7; a<lines.size(); ++a) {
            behaviour[a-7]= lines.get(a);
        }

        List<Behaviour> listOfBehaviours= new LinkedList<>();
        for (String aBehaviour : behaviour) {
            listOfBehaviours.add(new Behaviour(aBehaviour));
        }


        //pocetak algoritma
        for (String[] entry: entries) {
            stog.clear();
            stog.add(beginingStogChar);
            printaj(beginingState, stog);
            String currentState= epsilonHandler(beginingState, beginingStogChar, listOfBehaviours);
            boolean errorPrinted= false;
            for (String anEntry: entry) {
                boolean found= false;
                for (Behaviour behaviour1: listOfBehaviours) {
                    if(stog.empty()) {
                        errorPrinted= true;
                        break;
                    }
                    if(behaviour1.trenutnoStanje.equals(currentState) &&
                            behaviour1.ulazniZnak.equals(anEntry) &&
                            behaviour1.znakStoga.equals(stog.peek())) {

                        currentState= behaviour1.novoStanje;
                        if(Arrays.toString(behaviour1.nizPonasanjaStoga).
                                replaceAll("\\[", "").replaceAll("]", "")
                                .equals("$")) {
                            stog.pop();
                        } else {
                            for (int a = behaviour1.nizPonasanjaStoga.length; a > 0; --a) {
                                boolean addExtra= false;
                                if(behaviour1.nizPonasanjaStoga.length>1 && a>1) {
                                    if (behaviour1.nizPonasanjaStoga[a-1].equals(behaviour1.nizPonasanjaStoga[a-2])) {
                                        addExtra= true;
                                    }
                                }
                                if (!stog.peek().equals(behaviour1.nizPonasanjaStoga[a - 1]) || addExtra) {
                                    stog.push(behaviour1.nizPonasanjaStoga[a - 1]);
                                }
                            }
                        }
                        printaj(currentState, stog);
                        if(!stog.empty()) {
                            currentState = epsilonHandler(currentState, stog.peek(), listOfBehaviours);
                        }
                        found= true;
                        break;
                    }
                }
                if(!found) {
                    System.out.print("fail|");
                    errorPrinted= true;
                    break;
                }

            }
            if(!errorPrinted) {
                for (String endState : endStates) {
                    if (endState.equals(currentState)) {
                        System.out.println(1);
                        break;
                    }
                    System.out.println(0);
                }
            } else System.out.println(0);

        }



    }

    static class Behaviour {
        String trenutnoStanje;
        String ulazniZnak;
        Character znakStoga;

        String novoStanje;
        Character nizPonasanjaStoga[];

        Behaviour (String behaviour) {
            String subs[]= behaviour.split("->");
            if (subs.length!=2) return;

            String input[]= subs[0].split(",");
            if(input.length!=3) return;
            this.trenutnoStanje= input[0];
            this.ulazniZnak= input[1];
            this.znakStoga= input[2].charAt(0);

            String output[]= subs[1].split(",");
            if(output.length!=2) return;
            this.novoStanje= output[0];

            nizPonasanjaStoga= new Character[output[1].length()];
            for (int a=0; a<output[1].length(); ++a) {
                this.nizPonasanjaStoga[a] = output[1].charAt(a);
            }
        }

    }

    private static String epsilonHandler (String state, Character stogChar,
                                        List<Behaviour> behaviourList) {
        for(Behaviour behaviour: behaviourList) {
            if(behaviour.trenutnoStanje.equals(state) && behaviour.znakStoga.equals(stogChar)
                    && behaviour.ulazniZnak.equals("$")) {
                if (Arrays.toString(behaviour.nizPonasanjaStoga).
                        replaceAll("\\[", "").replaceAll("]", "")
                        .equals("$")) {
                    stog.pop();
                } else {
                    for (int a = behaviour.nizPonasanjaStoga.length; a > 0; --a) {
                        boolean addExtra= false;
                        if(behaviour.nizPonasanjaStoga.length>1 && a>1) {
                            if (behaviour.nizPonasanjaStoga[a-1].equals(behaviour.nizPonasanjaStoga[a-2])) {
                                addExtra= true;
                            }
                        }

                        if (!behaviour.nizPonasanjaStoga[a - 1].equals(stog.peek()) || addExtra) {
                            stog.push(behaviour.nizPonasanjaStoga[a - 1]);
                        }
                    }
                }
                printaj(behaviour.novoStanje, stog);
                if (!stog.empty()) {
                    return epsilonHandler(behaviour.novoStanje, stog.peek(), behaviourList);
                } else return behaviour.novoStanje;
            }
        }
        return state;
    }


    private static void printaj(String state, Stack<Character> stack) {
        System.out.print(state);
        System.out.print("#");
        if(!stack.empty()) {
            Object stackArray[]= stack.toArray();
            for (int a=stackArray.length; a>0; --a) {
                System.out.print(stackArray[a-1]);
            }

        } else System.out.print("$");
        System.out.print("|");
    }


}
