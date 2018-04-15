# Teorige grafů a her 2018

## Zadání
### 2.6 Nejspolehlivější cesta
kód: **RELCONN**

Máme rozlehlou počítačovou síť, která je realizována rádiovým spojením. Rádiové spojení může být nejrůznějším
způsobem rušeno a tudíž není moc spolehlivé. Síť tvoří uzly a jejich rádiová spojení. Uzly jsou očíslované **0** až **n−1**
a jedno spojení je dáno: číslem **i** vysílajícího uzlu, číslem **j** přijímajícího uzlu a pravděpodobností **0 < pi,j < 1**
přijmutí správného paketu. Spojení mezi dvěma uzly sítě je vždy symetrické. Najděte v zadané síti nejspolehlivější
cestu z vrcholu **s** do vrcholu **t**. Nejspolehlivější cesta je ta s nejmenší pravděpodobností chyby. 
  
Na prvním řádku vstupu je počet uzlů **n** a celkový počet spojení mezi nimi, **m**. Na dalších **m** řádkcích je pro
každé spojení číslo vysílajícího uzlu, číslo přijímajícího uzlu (číslováno od 0) a pravděpodobnost **p** (např. 0.8931).
Na řádku **m + 2** je počet testovacích dotazů **N** a na dalších **N** řádcích jsou data pro jednotlivé dotazy. Jeden
dotaz je dvojice uzlů, mezi kterými chceme najít cestu, první je index počátečního a druhý index koncového
uzlu.  
  
Na výstupu bude **N** řádků, pro každý dotaz jeden. Výsledek jednoho dotazu je posloupnost indexů uzlů podél
nalezené nejspolehlivější cesty včetně počátečního a koncového ze zadání dotazu. V případě, že žádná cesta (byť
s minimální spolehlivostí) pro dotaz neexistuje, vypíše se pouze index počátečního uzlu ze zadání dotazu.  

**Příklad vstupu:**  
  
3 3  
0 1 0.8  
0 2 0.5  
1 2 0.7  
1  
0 2  
  
**Očekávaný výstup:**  
  
0 1 2  
