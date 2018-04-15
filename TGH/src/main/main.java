package main;   // Odstranit při odevzdávání na TestServer

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.*;

public class main {
    private static ArrayList graf;
    private static ArrayList nextVisit;
    private static ArrayList dotazy;
    private static ArrayList cesta;
    private static String[] line;

    private static int n, m, N;


    public static void main(String[] args) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        
        graf = new ArrayList();
        dotazy = new ArrayList();
        nextVisit = new ArrayList();
        cesta = new ArrayList();

        line = in.readLine().split(" ");
        n = Integer.parseInt(line[0]);
        m = Integer.parseInt(line[1]);
        

        for (int i = 0; i < n; i++){
            graf.add(new Vrchol(i));
        }

        for (int i = 0; i < m; i++){
            line = in.readLine().split(" ");
            int a = Integer.parseInt(line[0]);
            int b = Integer.parseInt(line[1]);
            float p = Float.parseFloat(line[2]);
            ((Vrchol)graf.get(a)).setSoused(new Hrana(((Vrchol)graf.get(b)).getId(), p));
            ((Vrchol)graf.get(b)).setSoused(new Hrana(((Vrchol)graf.get(a)).getId(), p));
        }

        line = in.readLine().split(" ");
        N = Integer.parseInt(line[0]);
        for (int i = 0; i < N; i++){
            line = in.readLine().split(" ");
            dotazy.add(new Dotaz(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
        }

        for (int i = 0; i < dotazy.size(); i++){
            DjikstruvAlgoritmus(((Dotaz)dotazy.get(i)).getI(), ((Dotaz)dotazy.get(i)).getJ());
            vypisCestu();
        }
    }

    private static void DjikstruvAlgoritmus(int i, int j) {
        float pp; // Pravděpodobnost předka
        int ids; // ID Souseda
        Vrchol curr;
        for (int x = 0; x < graf.size(); x++){
            ((Vrchol)graf.get(x)).setDefault();
        }

        nextVisit.clear();
        nextVisit.add(graf.get(i));

        ((Vrchol)graf.get(i)).setHodnota(1);
        ((Vrchol)graf.get(i)).setPredek(((Vrchol)graf.get(i)).getId());

        while (!nextVisit.isEmpty()){
            sortujPodleP(nextVisit);
            curr = (Vrchol)nextVisit.remove(0);
            pp = curr.getHodnota();
            curr.setVisited(true);
            
            for (int x = 0; x < curr.getSoused().size(); x++){
                //ids = ((Hrana)curr.getSoused().get(x)).getVrchol().getId();
                ids = ((Hrana)curr.getSoused().get(x)).getId();
                if (!((Vrchol)graf.get(ids)).isVisited()){
                    if (!((Vrchol)graf.get(ids)).isAdded()){
                        nextVisit.add(graf.get(ids));
                        ((Vrchol)graf.get(ids)).setAdded(true);
                    }
                    if (((Vrchol)graf.get(ids)).getHodnota() < (((Hrana)curr.getSoused().get(x)).getP()*pp)){
                        ((Vrchol)graf.get(ids)).setHodnota(((Hrana)curr.getSoused().get(x)).getP()*pp);
                        ((Vrchol)graf.get(ids)).setPredek(curr.getId());
                    }
                }
            }
        }

        cesta.clear();
        curr = ((Vrchol)graf.get(j));
        if (((Vrchol)graf.get(j)).getPredek() == -1){
            cesta.add(i);
        } else {
            cesta.add(j);
            while(curr.getPredek() != curr.getId()){
                cesta.add(0,curr.getPredek());
                curr = ((Vrchol)graf.get(curr.getPredek()));
            }
        }
        //Collections.reverse(cesta);
    }

    private static void sortujPodleP(ArrayList List) {
        Collections.sort(List, new Comparator() {
            public int compare(Object o1, Object o2) {
                return ((Vrchol)o1).getHodnota() > ((Vrchol)o2).getHodnota() ? -1 : (((Vrchol)o1).getHodnota() < ((Vrchol)o2).getHodnota()) ? 1 : 0;
            }
        });
    }

    private static void vypisCestu() {
        
        for(int i = 0; i < cesta.size(); i++){
            System.out.print(cesta.get(i)+" ");
        }
        System.out.println();
    }
}

class Vrchol {
    private int id;
    private float hodnota;
    private boolean visited;
    private boolean added;
    private int predek;
    private ArrayList soused = new ArrayList();

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

    public void setDefault(){
        this.hodnota = -1;
        this.predek = -1;
        this.visited = false;
        this.added = false;
    }
}

class Hrana {
    private int id;  // kam
    private float p;    // pravdepodobnost

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
    private int i;      // počáteční uzel
    private int j;      // koncový uzel

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