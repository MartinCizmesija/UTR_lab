import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MinDka {


    public static void main(String args[]) {
        List<String> lines = new LinkedList<>();
/*
        Scanner scanner= new Scanner(System.in);
        do {
            lines.add(scanner.nextLine());
        } while (scanner.hasNext());
*/
        Path path = Paths.get("./test.txt");
        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lines.size() < 5) return;
        String states[] = lines.get(0).split(",");
        String alfabetSymbols[] = lines.get(1).split(",");
        String allowedStates[] = lines.get(2).split(",");
        String beginingState = lines.get(3);

        if (beginingState.contains(",")) {
            System.out.println("more than one begining state");
            return;
        }

        String behaviour[] = new String[lines.size() - 4];
        for (int a = 4; a < lines.size(); ++a) {
            behaviour[a - 4] = lines.get(a);
        }

        List<String[]> input= new ArrayList<>();
        List<String> output= new ArrayList<>();
        for (int a=0; a<behaviour.length; ++a){
            String temp[]= behaviour[a].split("->");

            if (temp.length!= 2) return;
            input.add(temp[0].split(","));
            if (input.get(a).length!= 2) return;

            output.add(temp[1]);
            if(output.contains(",")) {
                System.out.println("vise od jednog outputa");
                return;
            }
        }

        if(input.size()!= output.size()) return;

        //nedohvatljiva stanja
        List<String> dohvatljivaStanja= new LinkedList<>();
        dohvatljivaStanja.add(beginingState);
        for (int a=0; a< input.size(); ++a) {
            if(dohvatljivaStanja.contains(input.get(a)[0])) {
                if(!dohvatljivaStanja.contains(output.get(a))) {
                    dohvatljivaStanja.add(output.get(a));
                }
            }
        }

        for (int a=0; a< input.size(); ++a) {
            if(dohvatljivaStanja.contains(input.get(a)[0])) {
                if(!dohvatljivaStanja.contains(output.get(a))) {
                    dohvatljivaStanja.add(output.get(a));
                }
            }
        }

        Set<String> outputStates= new TreeSet();
        Set<String> outputAllowedStates= new TreeSet<>();

        for (String state : states) {
            if (dohvatljivaStanja.contains(state)) {
                outputStates.add(state);
            }
        }

        for (String allowedState : allowedStates) {
            if (dohvatljivaStanja.contains(allowedState)) {
                outputAllowedStates.add(allowedState);
            }
        }

        for (int a=0; a<input.size(); ++a) {
            if (!dohvatljivaStanja.contains(input.get(a)[0])) {
                input.remove(a);
                output.remove(a);
                --a;
            }
        }

        //implementacija 3. algoritma
        LinkedList<String> outputStateList= new LinkedList<>(outputStates);
        boolean istovjetnaStanja[][]= new boolean[outputStates.size()-1][outputStates.size()-1];
        int positionCounter=1;
        for(int a=1; a<outputStates.size();++a) {
            for(int b=0; b<outputStates.size()-1; ++b) {
                if(b< positionCounter) {
                    if((outputAllowedStates.contains(outputStateList.get(a)) && !outputAllowedStates.contains(outputStateList.get(b)))
                    || (!outputAllowedStates.contains(outputStateList.get(a)) && outputAllowedStates.contains(outputStateList.get(b)))) {
                        istovjetnaStanja[a-1][b]= true;
                    }
                }
            }
            ++positionCounter;
        }

        LinkedList<ParStanja> paroviStanja= new LinkedList<>();
        ParStanja outputPar;
        String stanje1= "-";
        String stanje2= "-";
        String output1= "-";
        String output2= "-";
        positionCounter= 1;
        boolean promjena= false;
        for (int a=1; a<outputStates.size(); ++a) {
            for (int b=0; b<outputStates.size()-1; ++b) {
                if(!istovjetnaStanja[a-1][b] && b< positionCounter) {
                    for (String alfabetSymbol : alfabetSymbols) {
                        for (int d = 0; d < input.size(); ++d) {
                            if (input.get(d)[0].equals(outputStateList.get(a)) &&
                                    input.get(d)[1].equals(alfabetSymbol)) {
                                output1 = output.get(d);
                                stanje1 = outputStateList.get(a);
                                promjena = true;
                            }
                            if (input.get(d)[0].equals(outputStateList.get(b)) &&
                                    input.get(d)[1].equals(alfabetSymbol)) {
                                output2 = output.get(d);
                                stanje2 = outputStateList.get(b);
                                promjena = true;
                            }
                        }

                        if (promjena) {
                            outputPar = new ParStanja(output1, output2);
                            int indexA = outputStateList.indexOf(output1);
                            int indexB = outputStateList.indexOf(output2);
                            if (indexA == indexB) continue;
                            if (indexA < indexB) {
                                int pom = indexA;
                                indexA = indexB;
                                indexB = pom;
                            }
                            if (istovjetnaStanja[indexA - 1][indexB]) {
                                indexA = outputStateList.indexOf(stanje1);
                                indexB = outputStateList.indexOf(stanje2);
                                if (indexA < indexB) {
                                    int pom = indexA;
                                    indexA = indexB;
                                    indexB = pom;
                                }
                                istovjetnaStanja[indexA - 1][indexB] = true;
                                break;
                            } else if (!paroviStanja.contains(outputPar)) {
                                paroviStanja.add(outputPar);
                                int index = paroviStanja.indexOf(outputPar);
                                paroviStanja.get(index).dodajUListu(new ParStanja(stanje1, stanje2));
                            } else {
                                int index = paroviStanja.indexOf(outputPar);
                                paroviStanja.get(index).dodajUListu(new ParStanja(stanje1, stanje2));
                            }
                            promjena = false;
                        }

                    }
                }
            }
            ++positionCounter;
        }

        boolean imaPromjene= false;
        do {
            for (ParStanja aParoviStanja : paroviStanja) {
                stanje1 = aParoviStanja.getStanje1();
                stanje2 = aParoviStanja.getStanje2();
                int index1 = outputStateList.indexOf(stanje1);
                int index2 = outputStateList.indexOf(stanje2);

                if (index1 < index2) {
                    int pom = index1;
                    index1 = index2;
                    index2 = pom;
                }

                if (istovjetnaStanja[index1 - 1][index2]) {
                    for (int b = 0; b < aParoviStanja.vezanaStanja.size(); ++b) {
                        String pomStanje1 = aParoviStanja.vezanaStanja.get(b).getStanje1();
                        String pomStanje2 = aParoviStanja.vezanaStanja.get(b).getStanje2();
                        int ind1 = outputStateList.indexOf(pomStanje1);
                        int ind2 = outputStateList.indexOf(pomStanje2);
                        if (ind1 < ind2) {
                            int pom = ind1;
                            ind1 = ind2;
                            ind2 = pom;
                        }
                        istovjetnaStanja[ind1 - 1][ind2] = true;
                    }
                    imaPromjene = true;
                }
            }
        } while (imaPromjene);


        List <ParStanja> istaStanja= new LinkedList<>();
        positionCounter= 1;
        for(int a=1; a<outputStates.size();++a) {
            for(int b=0; b<outputStates.size()-1; ++b) {
                if(b< positionCounter && !istovjetnaStanja[a-1][b]) {
                    istaStanja.add(new ParStanja(outputStateList.get(a), outputStateList.get(b)));
                }
            }
            ++positionCounter;
        }


        //uklanjanje duplica
        for (ParStanja anIstaStanja : istaStanja) {
            stanje1= anIstaStanja.getStanje1();
            stanje2= anIstaStanja.getStanje2();

            if (stanje1.compareTo(stanje2) > 0) {
                String pom= stanje1;
                stanje1= stanje2;
                stanje2= pom;
            }
            outputStates.remove(stanje2);
            outputAllowedStates.remove(stanje2);

            int size = input.size();
            int removeCounter = 0;
            for (int b = 0; b < size - removeCounter + 1; ++b) {
                if (output.get(b - removeCounter).equals(stanje2)) {
                    output.remove(b - removeCounter);
                    output.add(b - removeCounter, stanje1);
                }
                if (input.get(b - removeCounter)[0].equals(stanje2)) {
                    input.remove(b - removeCounter);
                    output.remove(b - removeCounter);
                    ++removeCounter;
                }
                if ((b + 1) == size && removeCounter == 0) break;
            }

            if (beginingState.equals(stanje2)) beginingState = stanje1;
        }


        //printanje
        int comaCounter= outputStates.size()-1;
        for (String s: outputStates) {
            System.out.print(s);
            if (comaCounter!= 0) {
                System.out.print(",");
                --comaCounter;
            }
        }
        System.out.println();

        System.out.println(lines.get(1));

        comaCounter= outputAllowedStates.size()-1;
        for (String s: outputAllowedStates) {
            System.out.print(s);
            if (comaCounter!= 0) {
                System.out.print(",");
                --comaCounter;
            }
        }
        System.out.println();

        System.out.println(beginingState);

        for (int a=0; a<input.size(); ++a) {
            System.out.println(input.get(a)[0]+ "," + input.get(a)[1] + "->"+ output.get(a));
        }

    }

    //klasa par stanja
    private static class ParStanja {
        String stanje1;
        String stanje2;
        LinkedList<ParStanja> vezanaStanja= new LinkedList<>();

        ParStanja(String s1, String s2) {
            this.stanje1= s1;
            this.stanje2= s2;
        }

        ParStanja () {}

        String getStanje1() {
            return stanje1;
        }

        String getStanje2() {
            return stanje2;
        }

        LinkedList<ParStanja> getVezanaStanja() {
            return this.vezanaStanja;
        }

        void dodajUListu(ParStanja stanje) {
            this.vezanaStanja.add(stanje);
        }

        boolean isEqual(String s1, String s2) {
            if (this.stanje2.equals(s1) && this.stanje1.equals(s2)) return true;
            return this.stanje2.equals(s2) && this.stanje1.equals(s1);
        }
    }

}