# Comunicación entre procesos en Java (PSP)

Vamos a hacerlo paso a paso, pensando que acabas de empezar PSP
(Programación de Servicios y Procesos) y que todavía no habéis dado
hilos. No vamos a utilizar `Thread`, `Runnable`, `ExecutorService` ni
nada relacionado con hilos.

Mi recomendación es que empieces por la **versión LITE**, entiendas cómo
se comunican dos procesos y después amplíes el programa a la versión
PRO. Las tres versiones comparten prácticamente la misma base.

## 1. Entender qué te pide el ejercicio

Imagina que tienes un fichero `texto.txt` con este contenido:

``` text
Hola mundo.
Hola de nuevo.
El mundo es grande.
Hola a todos.
```

Y el usuario introduce estas palabras:

``` text
Hola, mundo, grande
```

El programa debe crear un proceso independiente por cada palabra:

-   **Proceso padre (`Lanzador`):** lee el fichero y las palabras por
    teclado.
-   **Proceso hijo (`BuscaPalabra`):** recibe el nombre del fichero y
    una única palabra que debe buscar.

En este ejemplo, el padre crea tres procesos. Cada uno ejecuta la clase
`BuscaPalabra`, pero con argumentos diferentes.

### Conceptos importantes

-   **Proceso padre:** el programa principal, que pregunta los datos y
    lanza los hijos.
-   **Proceso hijo:** otra ejecución independiente de Java que busca una
    palabra.
-   **Argumentos:** datos que el padre le entrega al hijo cuando lo
    inicia.
-   **Streams:** canales que permiten enviar información entre procesos.
-   **Código de salida:** número con el que termina un proceso; se puede
    consultar desde el padre mediante `waitFor()`.

Aunque ambos procesos utilicen clases Java, no comparten automáticamente
sus variables. El padre le pasa los datos al hijo mediante argumentos, y
pueden intercambiar información por los canales de entrada y salida.

------------------------------------------------------------------------

## 2. Crear la clase `BuscaPalabra`

Esta es la clase que ejecutará cada proceso hijo. Su trabajo será:

1.  Recibir el nombre del fichero y la palabra como argumentos.
2.  Abrir el fichero.
3.  Leer su contenido línea por línea.
4.  Contar las apariciones de la palabra.
5.  Terminar devolviendo el total.

Crea un archivo llamado `BuscaPalabra.java`.

``` java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuscaPalabra {

    public static void main(String[] args) {

        // Comprobamos que recibimos los dos argumentos
        if (args.length != 2) {
            System.out.println("Uso: BuscaPalabra fichero palabra");
            System.exit(1);
        }

        String nombreFichero = args[0];
        String palabraBuscada = args[1];

        int total = 0;

        try (BufferedReader lector =
                     new BufferedReader(new FileReader(nombreFichero))) {

            String linea;

            // Leemos el fichero línea por línea
            while ((linea = lector.readLine()) != null) {

                String[] palabras = linea.split("\\s+");

                // Comprobamos cada palabra de la línea
                for (String palabra : palabras) {

                    // Quitamos algunos signos de puntuación
                    String limpia = palabra.replaceAll(
                            "^[.,;:!?¡¿()\\[\\]\"']+|[.,;:!?¡¿()\\[\\]\"']+$",
                            ""
                    );

                    if (limpia.equalsIgnoreCase(palabraBuscada)) {
                        total++;
                    }
                }
            }

            System.out.println("Total de apariciones de "
                    + palabraBuscada + ": " + total);

            // Devolvemos el total como código de salida
            System.exit(total);

        } catch (IOException e) {
            System.err.println("Error al leer el fichero: "
                    + e.getMessage());
            System.exit(1);
        }
    }
}
```

### Entender el código

#### A. Los argumentos

``` java
String nombreFichero = args[0];
String palabraBuscada = args[1];
```

Cuando el padre inicia el proceso, le pasará dos argumentos. Por
ejemplo:

``` text
BuscaPalabra texto.txt Hola
```

En ese caso:

-   `args[0]` contiene `texto.txt`.
-   `args[1]` contiene `Hola`.

`args` es un array de cadenas de texto. No necesitas utilizar un
`Scanner` en el hijo porque los datos ya se los proporciona el padre.

#### B. Leer el fichero

``` java
String linea;

while ((linea = lector.readLine()) != null) {
    // Procesamos la línea
}
```

`readLine()` devuelve una línea cada vez. Cuando ya no quedan líneas,
devuelve `null` y el bucle termina.

#### C. Contar las apariciones

``` java
String[] palabras = linea.split("\\s+");
```

Esto separa cada línea en palabras utilizando los espacios en blanco.

Después recorremos el array y comparamos cada palabra con la que
buscamos:

``` java
if (limpia.equalsIgnoreCase(palabraBuscada)) {
    total++;
}
```

`equalsIgnoreCase()` permite que `Hola`, `HOLA` y `hola` se consideren
iguales. Este ejemplo cuenta palabras completas, no coincidencias dentro
de otras palabras: por ejemplo, buscar `sol` no cuenta `solamente`.

#### D. Devolver el resultado

``` java
System.exit(total);
```

El proceso finaliza con un código de salida. El padre podrá recuperarlo
con `waitFor()`.

**Importante:** los códigos de salida son enteros limitados en la
práctica a valores entre 0 y 255 en los sistemas habituales. Por tanto,
esta forma de devolver el número de apariciones es válida para la
versión LITE si el total no supera 255. En la versión PRO también
utilizaremos ese código para el total, con la misma limitación.

------------------------------------------------------------------------

## 3. Crear el proceso padre: `Lanzador`

Ahora necesitamos el programa que pregunta los datos y lanza un proceso
por cada palabra.

Crea otro archivo, `Lanzador.java`.

``` java
import java.io.IOException;
import java.util.Scanner;

public class Lanzador {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce el nombre del fichero: ");
        String fichero = teclado.nextLine();

        System.out.print("Introduce las palabras separadas por comas: ");
        String entrada = teclado.nextLine();

        // Separamos las palabras utilizando las comas
        String[] palabras = entrada.split(",");

        // Lanzamos un proceso por cada palabra, secuencialmente
        for (String palabra : palabras) {

            palabra = palabra.trim();

            if (palabra.isEmpty()) {
                continue;
            }

            System.out.println("\\nBuscando: " + palabra);

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "java",
                        "-cp",
                        System.getProperty("java.class.path"),
                        "BuscaPalabra",
                        fichero,
                        palabra
                );

                // El hijo muestra su salida directamente en la consola
                pb.inheritIO();

                Process proceso = pb.start();

                // Esperamos a que termine el proceso actual
                int resultado = proceso.waitFor();

                System.out.println(
                        "El proceso ha terminado. Código devuelto: "
                                + resultado
                );

            } catch (IOException e) {
                System.err.println(
                        "No se pudo iniciar el proceso: " + e.getMessage()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Se interrumpió la espera del proceso.");
                return;
            }
        }

        teclado.close();
        System.out.println("\\nBúsquedas finalizadas.");
    }
}
```

### Las partes más importantes del padre

#### A. Separar las palabras

``` java
String[] palabras = entrada.split(",");
```

Si el usuario escribe:

``` text
Hola, mundo, grande
```

Obtendrás un array con tres elementos. `trim()` elimina los espacios al
principio y al final de cada uno.

#### B. Construir el proceso

``` java
ProcessBuilder pb = new ProcessBuilder(
        "java",
        "-cp",
        System.getProperty("java.class.path"),
        "BuscaPalabra",
        fichero,
        palabra
);
```

Esta es la parte central del ejercicio.

-   `"java"` indica que vamos a ejecutar Java.
-   `"-cp"` indica que vamos a establecer el classpath.
-   `System.getProperty("java.class.path")` recupera la ruta de clases
    que utiliza el programa padre.
-   `"BuscaPalabra"` indica la clase que queremos ejecutar.
-   `fichero` y `palabra` son los dos argumentos que recibe el hijo.

#### C. Iniciar el proceso y esperar

``` java
Process proceso = pb.start();
int resultado = proceso.waitFor();
```

`start()` inicia un proceso independiente. `waitFor()` hace que el padre
espere a que ese proceso termine y devuelve su código de salida.

Esto es lo que hace que la versión LITE sea secuencial: el padre no
inicia la búsqueda siguiente hasta que termina la actual.

#### D. ¿Qué hace `inheritIO()`?

``` java
pb.inheritIO();
```

Hace que el proceso hijo herede la entrada y las salidas de la consola
del padre. Así, los mensajes de `BuscaPalabra` aparecen directamente en
la misma consola.

Es una solución sencilla para la versión LITE. No estamos leyendo
manualmente un `InputStream` ni un `OutputStream`; por eso todavía no
necesitamos trabajar con los Streams para recoger las líneas.

------------------------------------------------------------------------

## 4. Probar la versión LITE

Coloca ambos archivos en el mismo proyecto, dentro del mismo directorio
de código fuente, y ejecuta `Lanzador`.

Con el ejemplo anterior, la consola mostrará algo parecido a esto:

``` text
Introduce el nombre del fichero: texto.txt
Introduce las palabras separadas por comas: Hola, mundo, grande

Buscando: Hola
Total de apariciones de Hola: 3
El proceso ha terminado. Código devuelto: 3

Buscando: mundo
Total de apariciones de mundo: 2
El proceso ha terminado. Código devuelto: 2

Buscando: grande
Total de apariciones de grande: 1
El proceso ha terminado. Código devuelto: 1

Búsquedas finalizadas.
```

La salida exacta depende del contenido de tu fichero.

**Una diferencia importante:** en esta versión el padre no lee
directamente el total. El hijo lo imprime en pantalla y, además,
devuelve el total como código de salida. El padre recupera ese código
con `waitFor()`.

------------------------------------------------------------------------

## 5. Pasar a la versión PRO: comunicar líneas mediante Streams

Ahora vamos a hacer un cambio conceptual importante.

En lugar de que el hijo imprima el total, queremos que envíe al padre
cada línea del fichero en la que aparece la palabra. El padre mostrará
las líneas conforme las recibe y, cuando termine el hijo, mostrará el
total devuelto como código de salida.

### En la versión PRO

-   **Salida estándar del hijo (`stdout`):** transporta las líneas
    encontradas hacia el padre mediante un Stream.
-   **Código de salida:** transporta el total de apariciones cuando el
    hijo finaliza.

### Paso 1. Modificar `BuscaPalabra`

Cambia la clase para que imprima cada línea que contiene la palabra
buscada, pero que no imprima el total. El total se devolverá únicamente
mediante `System.exit(total)`.

``` java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class BuscaPalabra {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.exit(1);
        }

        String nombreFichero = args[0];
        String palabraBuscada = args[1];

        int total = 0;

        try (BufferedReader lector =
                     new BufferedReader(new FileReader(nombreFichero))) {

            String linea;

            while ((linea = lector.readLine()) != null) {

                String[] palabras = linea.split("\\s+");
                boolean encontrada = false;

                for (String palabra : palabras) {

                    String limpia = palabra.replaceAll(
                            "^[.,;:!?¡¿()\\[\\]\"']+|[.,;:!?¡¿()\\[\\]\"']+$",
                            ""
                    );

                    if (limpia.equalsIgnoreCase(palabraBuscada)) {
                        total++;
                        encontrada = true;
                    }
                }

                // Enviamos la línea al proceso padre
                if (encontrada) {
                    System.out.println(linea);
                }
            }

            // El total se devuelve como código de salida
            System.exit(total);

        } catch (IOException e) {
            System.err.println("Error al leer el fichero: "
                    + e.getMessage());
            System.exit(1);
        }
    }
}
```

La variable `encontrada` empieza en `false` al procesar cada línea. Si
la palabra aparece varias veces en esa misma línea, incrementamos el
total por cada aparición, pero enviamos la línea una sola vez.

Por ejemplo, si el fichero contiene:

``` text
Hola, hola, hola.
```

Y buscamos `hola`, el proceso contará tres apariciones, pero enviará una
sola línea al padre.

### Paso 2. Modificar `Lanzador`

Aquí ya no utilizaremos `inheritIO()`, porque necesitamos leer la salida
del hijo nosotros mismos.

``` java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Lanzador {

    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Introduce el nombre del fichero: ");
        String fichero = teclado.nextLine();

        System.out.print("Introduce las palabras separadas por comas: ");
        String entrada = teclado.nextLine();

        String[] palabras = entrada.split(",");

        for (String palabra : palabras) {

            palabra = palabra.trim();

            if (palabra.isEmpty()) {
                continue;
            }

            System.out.println("\\nBuscando: " + palabra);

            try {
                ProcessBuilder pb = new ProcessBuilder(
                        "java",
                        "-cp",
                        System.getProperty("java.class.path"),
                        "BuscaPalabra",
                        fichero,
                        palabra
                );

                Process proceso = pb.start();

                // Leemos las líneas enviadas por el hijo
                try (BufferedReader lector =
                             new BufferedReader(
                                     new InputStreamReader(
                                             proceso.getInputStream()))) {

                    String linea;

                    while ((linea = lector.readLine()) != null) {
                        System.out.println("Encontrada: " + linea);
                    }
                }

                // Esperamos al final y recuperamos el código de salida
                int total = proceso.waitFor();

                System.out.println(
                        "Total de apariciones de " + palabra
                                + ": " + total
                );

            } catch (IOException e) {
                System.err.println(
                        "Error al iniciar o leer el proceso: "
                                + e.getMessage()
                );
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Se interrumpió la espera.");
                return;
            }
        }

        teclado.close();
    }
}
```

### Entender los Streams del padre

Estas son las líneas nuevas más importantes:

``` java
BufferedReader lector = new BufferedReader(
    new InputStreamReader(proceso.getInputStream())
);
```

Desglosémoslas desde dentro hacia fuera:

-   `proceso.getInputStream()` permite al padre leer lo que el hijo
    escribe por su salida estándar.
-   `InputStreamReader` convierte los bytes recibidos en caracteres.
-   `BufferedReader` facilita la lectura del texto línea por línea
    mediante `readLine()`.

Después:

``` java
while ((linea = lector.readLine()) != null) {
    System.out.println("Encontrada: " + linea);
}
```

El padre lee las líneas a medida que llegan. Cuando el hijo cierra su
salida, `readLine()` termina devolviendo `null`.

¿Por qué esperamos después de leer?

``` java
int total = proceso.waitFor();
```

Porque queremos leer toda la salida antes de recuperar el código de
salida. Así el hijo puede seguir escribiendo mientras el padre consume
su salida, evitando que se bloquee porque el buffer de comunicación se
llene.

En esta versión también seguimos creando los procesos secuencialmente.
Solo hemos cambiado el modo de comunicación.

------------------------------------------------------------------------

## 6. ¿Qué pasa en la versión PRO ULTRA MAX?

En esta última versión, el enunciado te pide que no esperes a que
termine un proceso para lanzar el siguiente.

### Versión PRO: secuencial

1.  Iniciar el proceso para `Hola`.
2.  Esperar a que termine.
3.  Iniciar el proceso para `mundo`.
4.  Esperar a que termine.
5.  Iniciar el proceso para `grande`.

### Versión PRO ULTRA MAX: lanzar todos primero

1.  Iniciar `Hola`, `mundo` y `grande` sin esperar entre ellos.
2.  Después, recoger las salidas y los códigos de salida.

### ¿Cómo se lanzan varios procesos sin esperar?

El truco es guardar los objetos `Process` en una lista. Necesitarás
importar:

``` java
import java.util.ArrayList;
import java.util.List;
```

En el padre, primero creas los procesos y los guardas:

``` java
List<Process> procesos = new ArrayList<>();
List<String> palabrasBuscadas = new ArrayList<>();

for (String palabra : entrada.split(",")) {

    palabra = palabra.trim();

    if (palabra.isEmpty()) {
        continue;
    }

    ProcessBuilder pb = new ProcessBuilder(
            "java",
            "-cp",
            System.getProperty("java.class.path"),
            "BuscaPalabra",
            fichero,
            palabra
    );

    Process proceso = pb.start();

    procesos.add(proceso);
    palabrasBuscadas.add(palabra);
}
```

Fíjate en que aquí **no hay ningún `waitFor()` dentro del primer
bucle**. Por eso el padre no espera a que termine cada hijo antes de
lanzar el siguiente.

Después recorres la lista para leer la salida de cada proceso y
recuperar su resultado:

``` java
for (int i = 0; i < procesos.size(); i++) {

    Process proceso = procesos.get(i);
    String palabra = palabrasBuscadas.get(i);

    try (BufferedReader lector = new BufferedReader(
            new InputStreamReader(proceso.getInputStream()))) {

        String linea;

        while ((linea = lector.readLine()) != null) {
            System.out.println(
                    "[" + palabra + "] " + linea
            );
        }
    }

    int total = proceso.waitFor();

    System.out.println(
            "Total de " + palabra + ": " + total
    );
}
```

Este código permite iniciar todos los hijos antes de leer sus salidas.
Sin embargo, **no muestra las líneas en el orden global en el que las
producen los distintos procesos**: las recoge proceso por proceso.

### ¿Y qué ocurre con los buffers?

Cada proceso tiene un canal de salida con capacidad limitada. Si un hijo
escribe mucha información y el padre no la lee, ese hijo puede quedarse
bloqueado al llenarse el buffer.

En este ejemplo, el padre lee primero la salida de un hijo y después la
de los demás. Los hijos que no se están leyendo podrían quedar
bloqueados temporalmente, pero el padre acabará atendiendo sus salidas.

Para consumir realmente todas las salidas a la vez y mostrarlas conforme
llegan desde cualquier hijo, normalmente se necesita concurrencia, por
ejemplo mediante hilos o mecanismos de E/S no bloqueante. No vamos a
implementarlo ahora porque todavía no habéis dado esos conceptos.

------------------------------------------------------------------------

## 7. Qué deberías aprender de este ejercicio

Te recomiendo que domines estos puntos antes de intentar la versión
ULTRA MAX:

-   [ ] Crear un proceso hijo con `ProcessBuilder`.
-   [ ] Pasar argumentos a otra clase Java.
-   [ ] Entender qué hace `start()` y qué hace `waitFor()`.
-   [ ] Distinguir entre la salida estándar y el código de salida.
-   [ ] Leer la salida de un proceso con `getInputStream()` y
    `BufferedReader`.
-   [ ] Diferenciar entre lanzar procesos secuencialmente y lanzarlos
    todos antes de recoger sus resultados.

**Mi consejo:** implementa primero la versión LITE, ejecútala y
comprueba que cuenta correctamente las palabras. Después cambia el hijo
para enviar las líneas y modifica el padre para leer el Stream. Así
aprenderás cada concepto por separado, sin mezclarlo todavía con
concurrencia.
