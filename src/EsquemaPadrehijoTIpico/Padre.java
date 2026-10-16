package EsquemaPadrehijoTIpico;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Padre {

    public static void main(String[] args) throws Exception {

        int numHijos = 3;

        // Por cada hijo guardamos tres cosas: el proceso en sí y los dos "cables"
        // para hablar con él (uno para escribirle y otro para leerle).
        Process[] procesos = new Process[numHijos];
        PrintWriter[] haciaHijo = new PrintWriter[numHijos];      // lo que el padre ESCRIBE (stdin del hijo)
        BufferedReader[] desdeHijo = new BufferedReader[numHijos]; // lo que el padre LEE (stdout del hijo)

        for (int i = 0; i < numHijos; i++) {

            // ----------------------------------------------------------------
            // 1. "CONSTRUCTOR": se crea el ProcessBuilder con la orden que ejecuta el hijo.
            //    Un hijo es otra JVM ejecutando la clase Hijo. Le pasamos el mismo classpath
            //    que usa el padre para que la encuentre, y el id como argumento.
            // ----------------------------------------------------------------
            //esta en el bucle porque le pasa un parametro concreto
            ProcessBuilder pb = new ProcessBuilder(
                    "java",
                    "-cp", System.getProperty("java.class.path"),
                    "Hijo",
                    String.valueOf(i));

            // Los errores del hijo (stderr) van directamente a la consola del padre.
            // Así, si el hijo falla con una excepción, la vemos sin tener que leerla nosotros.
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            // ----------------------------------------------------------------
            // 2. LANZAR EL HIJO: start() es el momento en que nace de verdad.
            //    Se arrancan todos dentro del bucle, así trabajan a la vez.
            // ----------------------------------------------------------------
            procesos[i] = pb.start();

            // ----------------------------------------------------------------
            // 3. CREAR LOS FLUJOS. Los nombres se piensan desde el punto de vista del padre:
            //      getOutputStream() -> lo que el padre ESCRIBE (llega al stdin del hijo)
            //      getInputStream()  -> lo que el padre LEE     (viene del stdout del hijo)
            //    El "true" del PrintWriter vacía el buffer en cada println. Sin él, el mensaje
            //    se queda esperando en el buffer y el hijo no lo recibe nunca.
            // ----------------------------------------------------------------
            haciaHijo[i] = new PrintWriter(procesos[i].getOutputStream(), true);
            desdeHijo[i] = new BufferedReader(new InputStreamReader(procesos[i].getInputStream()));
        }

        // --------------------------------------------------------------------
        // 4. COMUNICACIÓN PADRE -> HIJO: le mandamos a cada uno su encargo.
        //    Una línea de texto por mensaje; el hijo la leerá con readLine().
        // --------------------------------------------------------------------
        for (int i = 0; i < numHijos; i++) {
            haciaHijo[i].println("SUMA " + (i + 1) * 10);
        }

        // --------------------------------------------------------------------
        // 5. AQUÍ VA LO QUE EL PADRE HAGA MIENTRAS LOS HIJOS TRABAJAN (si hace algo).
        //    Puede ser calcular, atender al usuario, preparar el siguiente lote...
        //    Si no tiene nada que hacer, este bloque se deja vacío.
        // --------------------------------------------------------------------
        System.out.println("[padre] los hijos están trabajando, yo aprovecho para lo mío");

        // --------------------------------------------------------------------
        // 6. COMUNICACIÓN HIJO -> PADRE: recogemos la respuesta de cada hijo.
        //    readLine() BLOQUEA al padre hasta que el hijo escriba una línea.
        //    Si el hijo no contesta nunca, el padre se queda aquí para siempre.
        // --------------------------------------------------------------------
        int total = 0;
        for (int i = 0; i < numHijos; i++) {
            String respuesta = desdeHijo[i].readLine();
            System.out.println("[padre] hijo " + i + " contesta: " + respuesta);
            total += Integer.parseInt(respuesta.trim());
        }
        System.out.println("[padre] total = " + total);

        // --------------------------------------------------------------------
        // 7. AVISAR A LOS HIJOS DE QUE TERMINEN. Con cerrar el flujo de escritura
        //    el hijo ve "fin de fichero" en su readLine() (devuelve null) y sabe que no hay más.
        // --------------------------------------------------------------------
        for (int i = 0; i < numHijos; i++) {
            haciaHijo[i].close();
        }

        // --------------------------------------------------------------------
        // 8. ESPERAR A QUE ACABEN TODOS. waitFor() bloquea hasta que ese hijo termina
        //    y devuelve su código de salida (0 = todo bien, otro número = algo fue mal).
        // --------------------------------------------------------------------
        for (int i = 0; i < numHijos; i++) {
            int codigo = procesos[i].waitFor();
            System.out.println("[padre] hijo " + i + " ha terminado con código " + codigo);
        }
        System.out.println("[padre] todos los hijos han terminado");
    }
}
