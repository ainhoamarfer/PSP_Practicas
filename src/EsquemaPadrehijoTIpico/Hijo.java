package EsquemaPadrehijoTIpico;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//si quieres que escriban en el mismo fichero hay que usar file lock
public class Hijo {

    public static void main(String[] args) throws Exception {

        // ------------------------------------------------------------------
        // 1. SE INICIA. Lo que el padre pasó en la línea de órdenes llega en args.
        // ------------------------------------------------------------------
        int id = Integer.parseInt(args[0]);

        // ------------------------------------------------------------------
        // 2. FLUJOS. En el hijo es más fácil: son los de siempre.
        //    stdin  = lo que el padre escribe   -> lo leemos con un BufferedReader
        //    stdout = lo que el padre lee       -> escribimos con System.out
        //    stderr = errores, que el padre ya redirigió a su consola
        // ------------------------------------------------------------------
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in));

        // ------------------------------------------------------------------
        // 3. COMUNICACIÓN DESDE EL PADRE: leemos lo que nos manda.
        //    Se queda esperando aquí hasta que llegue una línea.
        //    Devuelve null si el padre cierra su flujo de escritura.
        // ------------------------------------------------------------------
        String orden = entrada.readLine();            // por ejemplo "SUMA 10"
        String[] partes = orden.split(" ");
        int limite = Integer.parseInt(partes[1]);

        // ------------------------------------------------------------------
        // 4. EL TRABAJO DEL HIJO: sumamos 1 + 2 + ... + limite
        // ------------------------------------------------------------------
        int suma = 0;
        for (int i = 1; i <= limite; i++) {
            suma += i;
        }

        // ------------------------------------------------------------------
        // 5. COMUNICACIÓN HACIA EL PADRE: escribimos el resultado por la salida estándar.
        //    El println termina la línea, que es lo que el readLine() del padre necesita.
        //    IMPORTANTE: por stdout solo mandamos lo que el padre espera leer.
        //    Los mensajes de depuración, mejor por System.err.
        // ------------------------------------------------------------------
        System.out.println(suma);
        System.out.flush();
        System.err.println("   [hijo " + id + "] he calculado la suma hasta " + limite);

        // ------------------------------------------------------------------
        // 6. AQUÍ VA LO QUE EL HIJO HAGA MIENTRAS ESPERA (si hace algo):
        //    seguir recibiendo órdenes en un bucle, preparar cosas...
        //    Para varias órdenes se usaría un bucle:
        //        while ((orden = entrada.readLine()) != null) { ... }
        // ------------------------------------------------------------------

        // ------------------------------------------------------------------
        // 7. FINALIZACIÓN. Esperamos a que el padre cierre su flujo (readLine devuelve null)
        //    y salimos con código 0, que significa "todo ha ido bien".
        //    Con System.exit(1) o cualquier otro número avisaríamos de un error.
        // ------------------------------------------------------------------
        entrada.readLine();       // devuelve null cuando el padre cierra: ya no hay más órdenes
        System.exit(0);
    }
}
