package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.io.*;
import java.util.PriorityQueue;
import java.util.Queue;

public class main {
    // Vytvoření grafu
    private static Graf g;

    public static void main(String[] args) throws IOException {
        // Input BufferReader
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // Proměnné používané v hlavní metodě
        String[] line;  // Pole stringu pro načtení vstupních hodnot
        int n, m, N;    // Vstupní hodnoty - počet vrcholů (n), počet hran (m) a počet dotazů (N)
        double p;       // Proměnná pro načtení hodnoty (pravděpodobnosti) ze vstupu
        int a, b;       // Proměnné pro načtení vrcholů v hraně ze vstupu
                
        // Načti počet vrcholů (n) a hran (m)
        line = in.readLine().split(" ");
        n = Integer.parseInt(line[0]);
        m = Integer.parseInt(line[1]);

        // Vytvoř a vlož vrcholy do grafu
        g = new Graf(n);
        g.vytvorVrcholy();
  
        // Načti hrany - z (va), do (vb), pravděpodobnost (p) - a vlož je daným vrcholům do pole "soused"
        for (int i = 0; i < m; i++){
            line = in.readLine().split(" ");
            a = Integer.parseInt(line[0]);
            b = Integer.parseInt(line[1]);
            p = Double.parseDouble(line[2]);
            
            g.getVrchol(a).setSoused(new Hrana(b, p));
            g.getVrchol(b).setSoused(new Hrana(a, p));
        }

        // Načti počet dotazů
        line = in.readLine().split(" ");
        N = Integer.parseInt(line[0]);
        
        // Načti konkrétní dotazy - z, do
        for (int i = 0; i < N; i++){
            line = in.readLine().split(" ");
            // Pro každý dotaz spusť Dijkstrův algoritmus (upravený) a vypiš cestu ze startovního do cílového vrcholu    
            DijkstruvAlgoritmus(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
        }
    }

    private static void DijkstruvAlgoritmus(int i, int j) {
            
        // Proměnné používané v průběhu algoritmu
        double currHodnota;                  // Momentální hodnota sousedního vrcholu skrz prohledávaný vrchol
        Vrchol curr;                         // Nejdříve startovní vrchol, poté vrchol s největší pravděpodobností => první v pořádníku
        Vrchol soused;                       // Sousední vrchol
        ArrayList cesta = new ArrayList();   // List pro finální vypsání hledané cesty
        
        // Nejdříve startovní vrchol, poté vrchol s největší pravděpodobností => první v pořádníku
        curr = g.getVrchol(i);
        
        // Nastav všem vrcholům v grafu defaultní nastavení (hodnota = -1, předek = -1, v pořadníku = false)
        g.setDefault();

        // Vlož startovní vrchol do pořadníku
        g.vlozDoPoradniku(curr);
        
        // Nastav startovnímu vrcholu hodnotu 1 a předka jako sebe samého
        curr.setHodnota(1);    
        curr.setPredek(curr.getId());
        
        // Hledej nejspolehlivější cestu v grafu, dokud není pořadník prázdný
        while (!g.Poradnik().isEmpty()){
            curr = g.Poradnik().poll();
            
            // Vyhledej všechny sousedy vrcholu
            for (Hrana h : curr.getSoused()){
                soused = g.getVrchol(h.getId());
                currHodnota = h.getP()*curr.getHodnota();    // Momentální hodnota sousedního vrcholu skrz prohledávaný vrchol
                
                // Pokud je cesta k sousednímu vrcholu spolehlivější skrz momentálně navštívený vrchol (curr), nastav mu novou hodnotu, předka a vlož do pořadníku
                if (soused.getHodnota() < currHodnota){
                    soused.setHodnota(currHodnota);
                    soused.setPredek(curr.getId());
                    g.vlozDoPoradniku(soused);
                }
            }
 
    }

        // Vymaž cestu z minula a vlož cílový vrchol a zapisuj předky, dokud nedojdeš ke startovacímu vrcholu
        cesta.clear();
        curr = g.getVrchol(j);
        
        // Pokud nemá cílový vrchol předka, nevede k němu žádná cesta => zapiš pouze startovní vrchol
        if (curr.getPredek() == -1){
            cesta.add(i);
        } 
        // Cílový vrchol má předka, cesta byla nalezena
        else {    
            cesta.add(j);
            // Zapisuj předky, dokud nenarazíš na startovní vrchol (ten, který má sám sebe jako předka)
            while(curr.getPredek() != curr.getId()){
                cesta.add(0, curr.getPredek());
                curr = g.getVrchol(curr.getPredek());
            }
        }
        
        // Vypiš cestu :)
        for(int x = 0; x < cesta.size(); x++){
            System.out.print(cesta.get(x)+" ");
        }
        System.out.println();        
    }
}

class Graf {
    int n;
    private Vrchol[] vrcholy;    
    private Comparator c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                double o1h = ((Vrchol)o1).getHodnota();
                double o2h = ((Vrchol)o2).getHodnota();
                return o1h > o2h ? -1 : (o1h < o2h) ? 1 : 0;
            }
        };
    private Queue<Vrchol> poradnik = new PriorityQueue<Vrchol>(c);

    public Graf(int n) {
        this.vrcholy = new Vrchol[n];
        this.n = n;
    }

    public Vrchol getVrchol(int id) {
        return vrcholy[id];
    }

    public void vytvorVrcholy() {
        for(int i = 0; i < n; i++){
            vrcholy[i] = new Vrchol(i);
        }
    }

    public Queue<Vrchol> Poradnik() {
        return poradnik;
    }

    public void vlozDoPoradniku(Vrchol vrchol) {
        poradnik.add(vrchol);
    }
    public void setDefault(){
        for(int i = 0; i < n; i++){
            vrcholy[i].setDefault();
        }
    }
}

class Vrchol implements Comparable{
    private int id; // ID vrcholu
    private double hodnota;  // Momentální pravděpodobnost vrcholu
    private int predek; // ID vrcholu skrz který vede nejspolehlivější cesta
    private ArrayList<Hrana> soused = new ArrayList<Hrana>(); // Seznam sousedů (hran) - id vrcholu, pravděpodobnost

    public Vrchol(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setHodnota(double hodnota) {
        this.hodnota = hodnota;
    }

    public void setSoused(Hrana soused) {
        this.soused.add(soused);
    }

    public ArrayList<Hrana> getSoused() {
        return soused;
    }

    public void setPredek(int predek) {
        this.predek = predek;
    }

    public double getHodnota() {
        return hodnota;
    }

    public int getPredek() {
        return predek;
    }
    
    // Nastavení defaultních hodnot vrcholu - použito při novém hledání nejspolehlivější cesty
    public void setDefault(){
        this.hodnota = -1;
        this.predek = -1;
    }
   
    @Override
    public int compareTo(Object o) {
        return (int)(this.hodnota - ((Vrchol)o).hodnota);
    }

}

class Hrana {
    private int id;  // ID vrcholu, kam hrana vede
    private double p;    // Pravděpodobnost/hodnota hrany

    public Hrana(int id, double p) {
        this.id = id;
        this.p = p;
    }

    public int getId() {
        return id;
    }

    public double getP() {
        return p;
    }
}