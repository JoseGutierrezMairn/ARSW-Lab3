
  
  
### Escuela Colombiana de Ingeniería
### Arquitecturas de Software – ARSW


## Laboratorio – Programación concurrente, condiciones de carrera y sincronización de hilos - Caso Inmortales

### Descripción
Este laboratorio tiene como fin que el estudiante conozca y aplique conceptos propios de la programación concurrente, además de estrategias que eviten condiciones de carrera.
### Dependencias:

* [Ejercicio Introducción al paralelismo - Hilos - BlackList Search](https://github.com/ARSW-ECI-beta/PARALLELISM-JAVA_THREADS-INTRODUCTION_BLACKLISTSEARCH)
#### Parte I – Antes de terminar la clase.

Control de hilos con wait/notify. Productor/consumidor.

1. Revise el funcionamiento del programa y ejecútelo. Mientras esto ocurren, ejecute jVisualVM y revise el consumo de CPU del proceso correspondiente. A qué se debe este consumo?, cual es la clase responsable?
~~~
Como se aprecia el consumo de cpu es alto, esto se debe a que la clase consumidor  
no tiene ninguna instrucción de esperar al productor para consumir,  
mientras que el productor agrega un producto y espera un segundo  
lo que implica que el consumidor estará siempre intentando consumir  
de la lista todo el tiempo, es decir que una vez el productor  
agrega algo a la lista, el consumidor antes de que el productor  
pueda agregar algo más ya habrá consumido lo agregado.
~~~
![consumoAlto](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/consumoPrimerPunto.PNG?raw=true)
2. Haga los ajustes necesarios para que la solución use más eficientemente la CPU, teniendo en cuenta que -por ahora- la producción es lenta y el consumo es rápido. Verifique con JVisualVM que el consumo de CPU se reduzca.
~~~
La propuesta para usar la CPU de manera eficiente fue hacer  
que ambos hilos se esperaran, es decir, cada vez que no existan  
productos para consumir el consumidor esperará (con el método wait())  
a que el productor agregue más productos, y si ya existen productos  
el productor esperará a que el consumidor termine de consumir  
todos los productos.  
A continuación la imagen del consumo de CPU donde se evidencia  
que disminuyó, y el código con el que se logró el resultado:
~~~
![consumoMedio](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/consumoPrimerPunto2.PNG?raw=true)  

```java
public class Consumer extends Thread{   
    @Override
    public void run() {
        while (true) {
        	synchronized (queue) {
        		if (queue.size() > 0) {
                    int elem=queue.poll();
                    System.out.println("Consumer consumes "+elem);   
                    queue.notify();
                }else {
                	try {
    					queue.wait();
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                }
        	}
            
            
        }
    }
}
```  

```java
public class Producer extends Thread {
    @Override
    public void run() {
        while (true) {
        	synchronized(queue) {
        		if(queue.size() == 0) {
            		dataSeed = dataSeed + rand.nextInt(100);
                    System.out.println("Producer added " + dataSeed);
                    queue.add(dataSeed);
                    queue.notify();
            	}else {
            		try {
						queue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		
            	}
        	}
        	
            
            
           /*** try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }***/

        }
    }
}
```

3. Haga que ahora el productor produzca muy rápido, y el consumidor consuma lento. Teniendo en cuenta que el productor conoce un límite de Stock (cuantos elementos debería tener, a lo sumo en la cola), haga que dicho límite se respete. Revise el API de la colección usada como cola para ver cómo garantizar que dicho límite no se supere. Verifique que, al poner un límite pequeño para el 'stock', no haya consumo alto de CPU ni errores.
~~~
La diferencia entre el primer punto y este es considerable  
en cuanto al uso del CPU durante la ejecución del programa
a continuación se ve el consumo del CPU y la propuesta  
para llegar a hacer mejor uso del CPU en este programa: 
~~~  
![consumoBajo](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/consumoPrimerPunto3.PNG?raw=true)  
```java
public class Consumer extends Thread{
    @Override
    public void run() {
        while (true) {
        	synchronized (queue) {
        		if (queue.size() > 0) {
                    int elem=queue.poll();
                    System.out.println("Consumer consumes "+elem);   
                    queue.notify();
                    try {
                    	Thread.sleep(10);
    					//queue.wait();
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
                }
        	} 
        }
    }
}
```  

```java
public class Producer extends Thread {
    @Override
    public void run() {
        while (true) {
        	synchronized(queue) {
        		if(queue.size() == stockLimit) {
        			try {
						queue.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}else {
            		dataSeed = dataSeed + rand.nextInt(100);
                    System.out.println("Producer added " + dataSeed);
                    queue.add(dataSeed);
                    //queue.notify();
            		
            	}
        	}
        	
            
            
           /*** try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Producer.class.getName()).log(Level.SEVERE, null, ex);
            }***/

        }
    }
}
```


#### Parte II. – Antes de terminar la clase.

Teniendo en cuenta los conceptos vistos de condición de carrera y sincronización, haga una nueva versión -más eficiente- del ejercicio anterior (el buscador de listas negras). En la versión actual, cada hilo se encarga de revisar el host en la totalidad del subconjunto de servidores que le corresponde, de manera que en conjunto se están explorando la totalidad de servidores. Teniendo esto en cuenta, haga que:

- La búsqueda distribuida se detenga (deje de buscar en las listas negras restantes) y retorne la respuesta apenas, en su conjunto, los hilos hayan detectado el número de ocurrencias requerido que determina si un host es confiable o no (_BLACK_LIST_ALARM_COUNT_).
- Lo anterior, garantizando que no se den condiciones de carrera.
~~~
Se cumplió con el reto y a continuación se muestra un caso donde el hilo detuvo  
la búsqueda al verificar que ya se había encontrado que la ip estaba reportada
en las listas negras.
~~~
![blackListTest](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/blacklist.PNG?raw=true)  
#### Parte II. – Avance para la siguiente clase

Sincronización y Dead-Locks.

![](http://files.explosm.net/comics/Matt/Bummed-forever.png)

1. Revise el programa “highlander-simulator”, dispuesto en el paquete edu.eci.arsw.highlandersim. Este es un juego en el que:

	* Se tienen N jugadores inmortales.
	* Cada jugador conoce a los N-1 jugador restantes.
	* Cada jugador, permanentemente, ataca a algún otro inmortal. El que primero ataca le resta M puntos de vida a su contrincante, y aumenta en esta misma cantidad sus propios puntos de vida.
	* El juego podría nunca tener un único ganador. Lo más probable es que al final sólo queden dos, peleando indefinidamente quitando y sumando puntos de vida.

2. Revise el código e identifique cómo se implemento la funcionalidad antes indicada. Dada la intención del juego, un invariante debería ser que la sumatoria de los puntos de vida de todos los jugadores siempre sea el mismo(claro está, en un instante de tiempo en el que no esté en proceso una operación de incremento/reducción de tiempo). Para este caso, para N jugadores, cual debería ser este valor?.
~~~
Dado que el valor por defecto de los puntos de vida para todos los inmortales es de 100  
es decir que la sumatoria durante todo el juego debe ser siempre de N * 100.
~~~

3. Ejecute la aplicación y verifique cómo funcionan las opción ‘pause and check’. Se cumple el invariante?.
~~~
No se cumple la invariante, está en cambio constante y nunca cumple que la sumatoria  
sea igual a N * 100 siendo N el número de inmortales.
~~~

4. Una primera hipótesis para que se presente la condición de carrera para dicha función (pause and check), es que el programa consulta la lista cuyos valores va a imprimir, a la vez que otros hilos modifican sus valores. Para corregir esto, haga lo que sea necesario para que efectivamente, antes de imprimir los resultados actuales, se pausen todos los demás hilos. Adicionalmente, implemente la opción ‘resume’.
~~~
Las funciones "pause and check" y "resume" fueron implementadas, a continuación se muestra
como fueron implementadas respectivamente.
~~~
```java
btnPauseAndCheck.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	
       
                /*
				 * COMPLETAR
                 */
            	immortals.clear();
            	int sum = 0;
            	for (Immortal im : temp) {
            		sum += im.getHealth();
            	}
            	statisticsLabel.setText("<html>"+temp.toString()+"<br>Health sum:"+ sum);

            }
        });
```  
  
```java
btnResume.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                /**
                 * IMPLEMENTAR
                 */
            	synchronized (immortals) {
            		immortals = getPlayers(immortals, temp);
            		immortals.notify();
            	}

            }
        });
```

5. Verifique nuevamente el funcionamiento (haga clic muchas veces en el botón). Se cumple o no el invariante?.
~~~
Fue probado el invariante en varios casos, en todos fue exitoso el resultado, es decir el invariante  
se cumple como se puede apreciar en la imagen pauseAndCheckTest.
~~~
![pauseAndCheckTest](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/pauseAndCheck.PNG?raw=true)  
6. Identifique posibles regiones críticas en lo que respecta a la pelea de los inmortales. Implemente una estrategia de bloqueo que evite las condiciones de carrera. Recuerde que si usted requiere usar dos o más ‘locks’ simultáneamente, puede usar bloques sincronizados anidados:

	```java
	synchronized(locka){
		synchronized(lockb){
			…
		}
	}
	```
~~~
La solución que propongo es bloquear el bloque de código cuando se van a realizar cambios en las  
variables de los inmortales, para evitar las condiciones de carrera en el momento de cada pelea  
la propuesta se llevó a cabo en el metodo "figt()" de la clase "Inmortal".
~~~

```java
public void fight(Immortal i2) {
    	if (i2.getHealth() > 0) {
    		synchronized(this) {
    			synchronized (i2) {
    				i2.changeHealth(i2.getHealth() - defaultDamageValue);
    	    		this.health += defaultDamageValue;
    	    		updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
    			}
    		}
    	} else {
    		updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
    	}
    }
```
7. Tras implementar su estrategia, ponga a correr su programa, y ponga atención a si éste se llega a detener. Si es así, use los programas jps y jstack para identificar por qué el programa se detuvo.
~~~
Al usar JPS y JSTACK se pudo ver lo que estaba pasando, el programa se encontraba en un deadlock, lo que quiere  
decir que por alguna razón se quedaron esperandose mutuamente los hilos (Immortal) gracias a los locks propuestos  
en el punto anterior y básicamente esto sucedia cuando dos inmortales iban a atacarse simultáneamente y bloqueaban el  
bloque de código, a continuación el registro del resultado de usar los programas mencionados al inicio del texto.
~~~  

![jps&jstack](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/jstack.PNG?raw=true)  

8. Plantee una estrategia para corregir el problema antes identificado (puede revisar de nuevo las páginas 206 y 207 de _Java Concurrency in Practice_).
~~~
la propuesta para corregir el problema es usar la clase llamada *AtomicNumber*, la cual nos ofrece la funcionalidad de  
verificar un valor y cambiarlo de manera atómica, para así evitar la condición **ABA** durante la ejecución del programa,  
esto implica que la variable que se encontraba almacenando la información de la vida de los inmortales no la usamos más,
ya que usamos un objeto *AtomicNumber* que se encarga de almacenar la vida de cada inmortal, esto porque la situación  
**ABA** se presenta en el momento en el que consultamos el valor de la vida del inmortal y luego lo cambiamos, es  
por eso que sustituimos la variable "health" por "atmHealth".
A continuación la manera en la que se resolvió la condición mencionada en el método "fight" de la clase **Immortal**
~~~
```java 
public void fight(Immortal i2) {
    	int points = i2.getAtmHealth().get();
    	if (points > 0) {
    		synchronized(updateCallback) {
	    		if(i2.getAtmHealth().compareAndSet(points, points - defaultDamageValue)) {
	    			this.atmHealth.addAndGet(defaultDamageValue);
	    			//System.out.println("Atacante: "+this.getAtmHealth().get()+this.name+"\nAtacado: "+i2.getAtmHealth().get()+i2.name);
	    			
	    			updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
    			
	    		}
    		}
    		//i2.changeHealth(i2.getHealth() - defaultDamageValue);
    	} else {
    		updateCallback.processReport(this + " says:" + i2 + " is already dead!\n");
    	}
    }
```  

9. Una vez corregido el problema, rectifique que el programa siga funcionando de manera consistente cuando se ejecutan 100, 1000 o 10000 inmortales. Si en estos casos grandes se empieza a incumplir de nuevo el invariante, debe analizar lo realizado en el paso 4.
~~~
El programa fue rectificado después de aplicar la solución propuesta en el punto anterior y el invariante se sigue cumpliendo  
en los casos grandes, a continuación evidencia gráfica de que el invariante se cumple.
~~~  

![casoCienInmortales](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/casoCien.PNG?raw=true)  
![casoMilInmortales](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/casoMil.PNG?raw=true)  
![casoDiezMilInmortales](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/casoDiezMil.PNG?raw=true)  

10. Un elemento molesto para la simulación es que en cierto punto de la misma hay pocos 'inmortales' vivos realizando peleas fallidas con 'inmortales' ya muertos. Es necesario ir suprimiendo los inmortales muertos de la simulación a medida que van muriendo. Para esto:
	* Analizando el esquema de funcionamiento de la simulación, esto podría crear una condición de carrera? Implemente la funcionalidad, ejecute la simulación y observe qué problema se presenta cuando hay muchos 'inmortales' en la misma. Escriba sus conclusiones al respecto en el archivo RESPUESTAS.txt.
	```java
	public void run() {

        while (true) {
        	//System.out.println("Que haciedno");
        	synchronized(immortalsPopulation) {
        		System.out.println(immortalsPopulation.size());
        		if(immortalsPopulation.isEmpty()) {
        			try {
						immortalsPopulation.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        		Immortal im;
            	int myIndex = immortalsPopulation.indexOf(this);
            	if(this.atmHealth.get() <= 0) {
            		immortalsPopulation.remove(myIndex);
            		this.stop();
        		}
            	int nextFighterIndex = r.nextInt(immortalsPopulation.size());
            	//avoid self-fight
            	if (nextFighterIndex == myIndex) {
            		nextFighterIndex = ((nextFighterIndex + 1) % immortalsPopulation.size());
            	}
            	im = immortalsPopulation.get(nextFighterIndex);
            	this.fight(im);
            	try {
            		Thread.sleep(1);
            	} catch (InterruptedException e) {
            		e.printStackTrace();
            	}
        	}
        }
    }
	```
	* Corrija el problema anterior __SIN hacer uso de sincronización__, pues volver secuencial el acceso a la lista compartida de inmortales haría extremadamente lenta la simulación.
	~~~
	Para corregir el problema del inmortal cuando ya no tiene vida, fue agregar un método al llamado **die()** el cual simplemente mata el hilo, adicionalmente se tuvo que  
	quitar la opción de actualizar el mensaje del inmortal en **updateCallback**.  
	Así fue como se corrigió en el código.
	~~~
	```java
	public void fight(Immortal i2) {
    	int points = i2.getAtmHealth().get();
    	if (points > 0) {
    		synchronized(updateCallback) {
    			synchronized (i2) {
		    		if(i2.getAtmHealth().compareAndSet(points, points - defaultDamageValue )) {
		    			i2.die();
		    			this.atmHealth.addAndGet(defaultDamageValue);
		    			updateCallback.processReport("Fight: " + this + " vs " + i2+"\n");
		    		}
    			}
    		}
    	}
    }
	```

11. Para finalizar, implemente la opción STOP.
~~~
Para implementarlo se hizo uso de la clase JOptionPane para mostrar un mensaje al usuario avisando
que la simulación fue detenida y finalmente cerrar el programa una vez el usuario da clic en la opción  
"ok".
~~~
![stop](https://github.com/JoseGutierrezMairn/ARSW-Lab3/blob/master/img/fin.PNG?raw=true)  


<a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/"><img alt="Creative Commons License" style="border-width:0" src="https://i.creativecommons.org/l/by-nc/4.0/88x31.png" /></a><br />Este contenido hace parte del curso Arquitecturas de Software del programa de Ingeniería de Sistemas de la Escuela Colombiana de Ingeniería, y está licenciado como <a rel="license" href="http://creativecommons.org/licenses/by-nc/4.0/">Creative Commons Attribution-NonCommercial 4.0 International License</a>.
