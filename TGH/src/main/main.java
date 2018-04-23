package main;   // Odstranit při odevzdávání na TestServer

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class main {
    private static String[] line;
    private static Graf g;

    private static int n, m, N;
    
    private static double pp;       // Pravděpodobnost předka
    private static Vrchol curr;     // Nejdříve startovní vrchol, poté vrchol s největší pravděpodobností => první v pořádníku
    private static Vrchol soused;   // Sousední vrchol
    private static Hrana hrana;     // Hrana k sousednímu vrcholu
    private static ArrayList cesta = new ArrayList();  // List pro finální vypsání hledané cesty

    public static void main(String[] args) throws IOException {
        // Input Buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // Proměnné vytvořené v rámci optimalizace
        double p;
        int a, b;
                
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
            
            g.sortPoradnik();
            curr = (Vrchol)g.Poradnik().remove(0);
            pp = curr.getHodnota(); // Ulož pravděpodobnost/hodnotu vrcholu
            
            // Vyhledej všechny sousedy vrcholu
            for (int x = 0; x < curr.getSoused().size(); x++){
                hrana = (Hrana)curr.getSoused().get(x); // Hrana k sousednímu vrcholu
                soused = g.getVrchol((int)hrana.getId());
                
                // Pokud NENÍ sousední vrchol již v pořadníku, přidej ho tam a nastav mu status "přidán do pořadníku"
                if (!soused.isAdded()){
                    g.vlozDoPoradniku(soused);
                    soused.setAdded(true);
                }
                // Pokud je cesta k sousednímu vrcholu spolehlivější skrz momentálně navštívený vrchol (curr), nastav mu novou hodnotu a předka
                if (soused.getHodnota() < hrana.getP()*pp){
                    soused.setHodnota(hrana.getP()*pp);
                    soused.setPredek(curr.getId());
                }
            }
            
        }

        // Vlož cílový vrchol a zapisuj předky, dokud nedojdeš ke startovacímu vrcholu
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
    private ArrayList poradnik = new ArrayList();
    private Comparator c = new Comparator() {
            public int compare(Object o1, Object o2) {
                double o1h = ((Vrchol)o1).getHodnota();
                double o2h = ((Vrchol)o2).getHodnota();
                return o1h > o2h ? -1 : (o1h < o2h) ? 1 : 0;
            }
        };

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

    public ArrayList Poradnik() {
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

    void sortPoradnik() {
        Collections.sort(poradnik, c);
    }
    
}

class Vrchol {
    private int id; // ID vrcholu
    private double hodnota;  // Momentální pravděpodobnost vrcholu
    private boolean added;  // Přidán do pořadníku?
    private int predek; // ID vrcholu skrz který vede nejspolehlivější cesta
    private ArrayList soused = new ArrayList(); // Seznam sousedů (hran) - id vrcholu, pravděpodobnost

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

    public ArrayList getSoused() {
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
    
    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }

    // Nastavení defaultních hodnot vrcholu - použito při novém hledání nejspolehlivější cesty
    public void setDefault(){
        this.hodnota = -1;
        this.predek = -1;
        this.added = false;
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