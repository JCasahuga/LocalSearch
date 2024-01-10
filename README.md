# Projecte IA QT 2022-2023
Projecte de cerca local de [IA](https://www.fib.upc.edu/en/studies/bachelors-degrees/bachelor-degree-informatics-engineering/curriculum/syllabus/IA) sobre l'energia.

Per compilar el programa (des de la carpeta on es troba el Main):

> javac Main.java

Per executar el programa:

> java Main

Comandes del programa:

run						Executa la cerca local   
cmds						Fa print de les comandes i què fan   
print						Imprimeix els paràmetres actuals   
    
algo [I]						Permet decidir quin algoritme s’utilitza   
0 → Hill Climbing   
1 → Simulated Annealing     
heur [I]						Permet decidir heurística    
0 → Benefici    
1 → Distancia Energia      
2 → Distancia Energia Penalització    
genmethod [I]					Permet decidir quina generació inicial s’utilitza     
0 → Proximitat     
1 → Assignació Aleatoria Només Garantitzats     
2 → Assignació Aleatoria Només Garantitzats Proximitat a Meitat de les Centrals     
3 → Assignació Aleatoria     
4 → Assignació Aleatoria Proximitat a Meitat de les Centrals     
5 → Buit     
seed [N]					Assigna el valor de la seed     
ncentrals [A] [B] [C]				Assigna el número de centrals de cada tipus       
nclients [N]					Assigna el número de clients      
gclients [N]					Assigna la probabilitat(%) de garantit      
typeclient [XG] [MG] [G]				Assigna el número de centrals de cada tipus    

