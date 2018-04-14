package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
/**
 *
 * @author Minaj
 */
public class Main {
    private static ArrayList<Vrchol> vrcholy;
    private static ArrayList<Vrchol> graf;
    private static ArrayList<Vrchol> nextVisit;
    private static ArrayList<Vrchol> visited;
    private static ArrayList<Dotaz> dotazy;
    private static ArrayList<Integer> cesta;
    
    private static int n, m, N;
    static Scanner scan = new Scanner(System.in);
    
    
    public static void main(String[] args) {
        vrcholy = new ArrayList<Vrchol>();
        dotazy = new ArrayList<Dotaz>();
        nextVisit = new ArrayList<Vrchol>();
        graf = new ArrayList<Vrchol>();
        visited = new ArrayList<Vrchol>();
        cesta = new ArrayList<Integer>();
        
        n = scan.nextInt(); // počet vrcholů - uzel
        m = scan.nextInt(); // počet hran - rádiové spojení
        
        for (int i = 0; i < n; i++){
            vrcholy.add(new Vrchol(i));
        }
        
        for (int i = 0; i < m; i++){
            int a = scan.nextInt();
            int b = scan.nextInt();
            float p = scan.nextFloat();
            vrcholy.get(a).setSoused(new Hrana(vrcholy.get(b), p));
            vrcholy.get(b).setSoused(new Hrana(vrcholy.get(a), p));
        }
        
        N = scan.nextInt();
        for (int i = 0; i < N; i++){
            dotazy.add(new Dotaz(scan.nextInt(), scan.nextInt()));
        }
       
        
        for (Dotaz d : dotazy){
            DjikstruvAlgoritmus(d.getI(), d.getJ());
            vypisCestu();
        }
    }
    
    private static void DjikstruvAlgoritmus(int i, int j) {
        float pp; // Pravděpodobnost předka
        int ids; // ID Souseda
        Vrchol curr;
        graf.clear();
        for (Vrchol v : vrcholy){
            graf.add(v.clone());
        }
        
        nextVisit.clear();
        nextVisit.add(graf.get(i));
        
        graf.get(i).setHodnota(1);
        graf.get(i).setPredek(graf.get(i));
        
        visited.clear();
        
        while (!nextVisit.isEmpty()){
            sortujPodleP(nextVisit);
            curr = nextVisit.remove(0);
            pp = curr.getHodnota();
            visited.add(curr);
            
            for (int x = 0; x < curr.getSoused().size(); x++){
                ids = curr.getSoused().get(x).getVrchol().getId();
                if (!visited.contains(graf.get(ids))){
                    if (!nextVisit.contains(graf.get(ids))){
                        nextVisit.add(graf.get(ids));
                    }
                    if (graf.get(ids).getHodnota() < (curr.getSoused().get(x).getP()*pp)){
                        graf.get(ids).setHodnota(curr.getSoused().get(x).getP()*pp);
                        graf.get(ids).setPredek(curr);
                    }
                }
            }
        }
        
        cesta.clear();
        curr = graf.get(j);
        if (graf.get(j).getPredek() == null){
            cesta.add(i);
        } else {
            cesta.add(j);
            while(curr.getPredek() != curr){
                cesta.add(curr.getPredek().getId());
                curr = curr.getPredek();
            }
        }
        Collections.reverse(cesta);
    }
    
    private static void sortujPodleP(ArrayList<Vrchol> List) {
        Collections.sort(List, new Comparator<Vrchol>() {
            @Override
            public int compare(Vrchol o1, Vrchol o2) {
                return o1.getHodnota() > o2.getHodnota() ? -1 : (o1.getHodnota() < o2.getHodnota()) ? 1 : 0;
            }
            
        });
    }
    
    private static void vypisVrcholy(ArrayList<Vrchol> vrcholy) {
        System.out.println("----------");
        for (Vrchol v : vrcholy){
            System.out.println("ID: " + v.getId() + ", H: " + v.getHodnota());
        }
    }

    private static void vypisCestu() {
        System.out.println();
        for(int c : cesta){
            System.out.print(c+" ");
        }
    }
}

class Vrchol {
    private int id;
    private float hodnota = -1;
    private Vrchol predek = null;
    private ArrayList<Hrana> soused = new ArrayList<Hrana>();
    
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

    public ArrayList<Hrana> getSoused() {
        return soused;
    }
    
    public void setPredek(Vrchol predek) {
        this.predek = predek;
    }
    
    public float getHodnota() {
        return hodnota;
    }
    
    public Vrchol getPredek() {
        return predek;
    }
    
    public Vrchol clone(){
        Vrchol v = new Vrchol(id);
        for(Hrana h : soused){
            v.setSoused(h);
        }
        return v;
    }
    public void setDefault(){
        this.hodnota = -1;
        this.predek = null;
    }
}

class Hrana {
    private Vrchol vrchol;  // kam
    private float p;    // pravdepodobnost
    
    public Hrana(Vrchol vrchol, float p) {
        this.vrchol = vrchol;
        this.p = p;
    }

    public Vrchol getVrchol() {
        return vrchol;
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