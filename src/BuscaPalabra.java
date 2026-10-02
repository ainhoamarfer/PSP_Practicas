import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Escribe una clase BuscaPalabra (lo usaremos como subproceso) que reciba 2 argumentos:  1) el nombre de un fichero, 2) una palabra.
 */
public class BuscaPalabra {

    private String rutaFichero;
    private String palabra;

    ArrayList<String> lineas = new ArrayList<>();

    public BuscaPalabra(String rutaFichero, String palabra) {
        this.rutaFichero = rutaFichero;
        this.palabra = palabra;
    }

    static void main(String[] args) {
        try (BufferedReader lector = new BufferedReader(new FileReader(rutaFichero))) {

            String linea;

            while ((linea = lector.readLine()) != null) {
                lineas.add(linea);
            }

        } catch (IOException e) {
            System.err.println("No se pudo leer el fichero: " + e.getMessage());
        }

        int contador = 0;

        for (String linea : lineas) {
            if (linea.contains(palabra)) {
                contador++;
            }
        }
    }

    //public class LanzadorSumaConLectura {
    //
    //    public static void main(String[] args) throws IOException, InterruptedException {
    //        String classpath = System.getProperty("java.class.path");
    //        ProcessBuilder constructor = new ProcessBuilder("java", "-cp", classpath, "TareaSuma", "12", "30");
    //        // Esta vez NO usamos inheritIO(): queremos leer la salida nosotros,
    //        // no que se imprima directamente en nuestra consola.
    //        Process proceso = constructor.start();
    //
    //        BufferedReader lector = new BufferedReader(new InputStreamReader(proceso.getInputStream()));
    //        String resultado = lector.readLine();
    //        proceso.waitFor();
    //
    //        int suma = Integer.parseInt(resultado);
    //        System.out.println("El proceso hijo devolvió: " + suma);
    //        System.out.println("El doble de ese resultado es: " + (suma * 2));
    //    }
}
