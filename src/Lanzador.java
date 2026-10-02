import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Lanzador {
    /**
     * Escribe una clase BuscaPalabra (lo usaremos como subproceso) que reciba 2 argumentos:  1) el nombre de un fichero, 2) una palabra.
     *
     * Escribe también un lanzador (será nuestro proceso padre) que pregunte dos cosas por teclado:  1) el nombre del fichero, y 2)
     *  una lista de palabras separadas por comas.
     *
     * El lanzador creará tantos subprocesos BuscaPalabra como palabras a buscar., y le pasará a cada uno el fichero en donde buscar,
     *  y SU palabra a buscar en él.
     *
     * Versión LITE - Cada subproceso simplemente devuelve al acabar el número de apariciones de la palabra que le han encargado buscar;
     *  incluso sin necesidad de usar Streams. (puedes usar InheritIO + System.out.println, o el código de salida del proceso:  System.exit(totalApariciones); ).
     *
     * Versión PRO - Cada proceso envía la LÍNEA (no frase) donde estaba la palabra (aquí necesitaréis Streams).
     *  El padre muestra esas líneas a medida que las recibe, y al terminar el subproceso, muestra también el total de apariciones — que el hijo devuelve como código de salida, no por el stream.
     *
     * NOTA: Te aconsejo que partas del ejercicio que hicimos el otro día, por ahorrar tiempo.
     * No sabes cuánto tiempo tardará cada proceso, y no es óptimo esperar a que acabe uno para lanzar el siguiente.
     */

    public static void main(String[] args) {



        System.out.println("Dime el nombre del fichero y una lista de palabras separadas por comas");
        Scanner sc = new Scanner(System.in);
        String nombreFichero = sc.nextLine();
        String palabras = sc.nextLine();
        String[] listaPalabras = palabras.split(",");

        BuscaPalabra buscaPalabra = new BuscaPalabra(nombreFichero, listaPalabras[0]);

        String classpath = System.getProperty("java.class.path");
        ProcessBuilder constructor = new ProcessBuilder("java", "-cp", classpath, "BuscaPalabra");

        Process proceso = constructor.start();

        int contador = 0;
        for (String palabra : listaPalabras) {
          int aparece = buscaPalabra.leerCuantasVecesAparece(nombreFichero, palabra);
          if(aparece > 0){
              contador++;
          }
        }



        Process proceso = constructor.start();
        int codigoSalida = proceso.waitFor();
        BufferedReader lectorPorLineas = new BufferedReader(new InputStreamReader(proceso.getInputStream()));


        System.out.println("El proceso hijo terminó con código " + codigoSalida);
    }
}
