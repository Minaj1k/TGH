package main;   // Odstranit při odevzdávání na TestServer

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class main {
    private static Vrchol[] graf;
    private static Dotaz[] dotazy;
    private static String[] line;
    private static Comparator c;

    private static int n, m, N;


    public static void main(String[] args) throws IOException {
        // Input Buffer
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        // Proměnné vytvořené v rámci optimalizace
        float p;
        Dotaz dotaz;
        Vrchol va;
        Vrchol vb;
        
        // Vytvoření nového comparatoru pro budoucí seřazení pole
        c = new Comparator() {
            public int compare(Object o1, Object o2) {
                Float o1h = ((Vrchol)o1).getHodnota();
                Float o2h = ((Vrchol)o2).getHodnota();
                return o1h > o2h ? -1 : (o1h < o2h) ? 1 : 0;
            }
        };
        
        // Načti počet vrcholů (n) a hran (m)
        line = in.readLine().split(" ");
        n = Integer.parseInt(line[0]);
        m = Integer.parseInt(line[1]);
        
        // Vytvoř a vlož vrcholy do grafu
        graf = new Vrchol[n];
        for (int i = 0; i < n; i++){
            graf[i] = new Vrchol(i);
        }

        // Načti hrany - z (a), do (b), pravděpodobnost (p) - a vlož je daným vrcholům do pole "soused"
        for (int i = 0; i < m; i++){
            line = in.readLine().split(" ");
            va = graf[Integer.parseInt(line[0])];
            vb = graf[Integer.parseInt(line[1])];
            p = Float.parseFloat(line[2]);
            
            va.setSoused(new Hrana(vb.getId(), p));
            vb.setSoused(new Hrana(va.getId(), p));
        }

        // Načti počet dotazů
        line = in.readLine().split(" ");
        N = Integer.parseInt(line[0]);
        
        // Načti konkrétní dotazy - z, do
        dotazy = new Dotaz[N];
        for (int i = 0; i < N; i++){
            line = in.readLine().split(" ");
            dotazy[i] = new Dotaz(Integer.parseInt(line[0]), Integer.parseInt(line[1]));
        }
        
        // Pro každý dotaz spusť Dijkstrův algoritmus (upravený) a vypiš cestu ze startovního do cílového vrcholu
        for (int i = 0; i < N; i++){
            dotaz = dotazy[i];
            DijkstruvAlgoritmus(dotaz.getI(), dotaz.getJ());
        }
    }

    private static void DijkstruvAlgoritmus(int i, int j) {
        float pp;       // Pravděpodobnost předka
        Vrchol curr = graf[i];    // Nejdříve startovní vrchol, poté vrchol s největší pravděpodobností => první v pořádníku
        Vrchol soused;  // Sousední vrchol
        Hrana hrana;    // Hrana k sousednímu vrcholu
        ArrayList cesta = new ArrayList();  // List pro finální vypsání hledané cesty
        ArrayList nextVisit = new ArrayList();  // Pořadník
        
        // Nastav všem vrcholům v grafu defaultní nastavení (hodnota = -1, předek = -1, navštíven = false, v pořadníku = false)
        for (int x = 0; x < n; x++){  
            graf[x].setDefault();
        }

        // Vlož startovní vrchol do pořadníku
        nextVisit.add(curr);

        // Nastav startovnímu vrcholu hodnotu 1 a předka jako sebe samého
        curr.setHodnota(1);    
        curr.setPredek(curr.getId());

        // Hledej nejspolehlivější cestu v grafu, dokud není pořadník prázdný
        while (!nextVisit.isEmpty()){
            Collections.sort(nextVisit, c); // Seřaď pořadník podle hodnoty vrcholu
            curr = (Vrchol)nextVisit.remove(0); // Vyjmi vrchol s nejlepší pravděpodobností/hodnotou (první v pořadníku)
            pp = curr.getHodnota(); // Ulož pravděpodobnost/hodnotu vrcholu
            curr.setVisited(true);  // Nastav vrcholu status "navštívený"
            
            // Vyhledej všechny sousedy vrcholu
            for (int x = 0; x < curr.getSoused().size(); x++){
                hrana = (Hrana)curr.getSoused().get(x); // Hrana k sousednímu vrcholu
                soused = graf[(int)hrana.getId()];  // Sousední vrchol
                
                // Pokračuj pokud sousední vrchol NEBYL navštíven
                if (!soused.isVisited()){
                    // Pokud NENÍ sousední vrchol již v pořadníku, přidej ho tam a nastav mu status "přidán do pořadníku"
                    if (!soused.isAdded()){
                        nextVisit.add(soused);
                        soused.setAdded(true);
                    }
                    // Pokud je cesta k sousednímu vrcholu spolehlivější skrz momentálně navštívený vrchol (curr), nastav mu novou hodnotu a předka
                    if (soused.getHodnota() < hrana.getP()*pp){
                        soused.setHodnota(hrana.getP()*pp);
                        soused.setPredek(curr.getId());
                    }
                }
            }
        }

        // Vlož cílový vrchol a zapisuj předky, dokud nedojdeš ke startovacímu vrcholu
        curr = graf[j];
        if (curr.getPredek() == -1){    // Pokud nemá cílový vrchol předka, nevede k němu žádná cesta => zapiš pouze startovní vrchol
            cesta.add(i);
        } else {    // Cílový vrchol má předka, cesta byla nalezena
            cesta.add(j);
            while(curr.getPredek() != curr.getId()){ // Zapisuj předky, dokud nenarazíš na startovní vrchol (ten, který má sám sebe jako předka)
                cesta.add(0, curr.getPredek());
                curr = graf[curr.getPredek()];
            }
        }
        
        // Vypiš cestu :)
        for(int x = 0; x < cesta.size(); x++){
            System.out.print(cesta.get(x)+" ");
        }
        System.out.println();
    }
}

class Vrchol {
    private int id; // ID vrcholu
    private float hodnota;  // Momentální pravděpodobnost vrcholu
    private boolean visited;    // Navštíven?
    private boolean added;  // Přidán do pořadníku?
    private int predek; // ID vrcholu skrz který vede nejkratší cesta
    private ArrayList soused = new ArrayList(); // Seznam sousedů (hran) - id vrcholu, pravděpodobnost

    public Vrchol(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setHodnota(float hodnota) {
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

    public float getHodnota() {
        return hodnota;
    }

    public int getPredek() {
        return predek;
    }

    public boolean isVisited() {
        return visited;
    }

    public boolean isAdded() {
        return added;
    }

    public void setAdded(boolean added) {
        this.added = added;
    }
    

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    // Nastavení defaultních hodnot vrcholu pro nové hledání nejspolehlivější cesty
    public void setDefault(){
        this.hodnota = -1;
        this.predek = -1;
        this.visited = false;
        this.added = false;
    }
}

class Hrana {
    private int id;  // ID vrcholu, kam hrana vede
    private float p;    // Pravděpodobnost spolehlivosti

    public Hrana(int id, float p) {
        this.id = id;
        this.p = p;
    }

    public int getId() {
        return id;
    }

    public float getP() {
        return p;
    }
}

class Dotaz {
    private int i;      // Startovní vrchol
    private int j;      // Cílový vrchol

    public Dotaz(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}