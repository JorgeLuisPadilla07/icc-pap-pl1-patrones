## Nombre: 
Jorge Luis Padilla
## Descripción del proyecto:

Esta es una aplicación en Java que simula el sistema de pedidos de una cafetería universitaria, llamado PedidoLab. El usuario crea pedidos de Café, Ensalada o Pizza y los personaliza con ingredientes, viendo el desglose y el total en tiempo real.
El proyecto demuestra dos patrones de diseño trabajando juntos:
Prototype: cada pedido nuevo es una copia independiente de un producto base guardado en un registro, con su propio ID (P-001, P-002...).
Decorator: cada ingrediente envuelve al pedido existente y suma su descripción y precio, sin crear una clase por cada combinación.

## Estructura base-plantilla del proyecto:

![alt text](evidencias/capturas/image.png)

## Capturas mockup exportada desde Figma:

![alt text](<evidencias/capturas/Sin t├¡tulo-im├ígenes-1.png>)
![alt text](<evidencias/capturas/Sin t├¡tulo-im├ígenes-1.png>)


# Pruebas del proyecto Completo:

## App recién abierta, sin crear nada: pulsa Agregar ingrediente
![alt text](src/main/java/app1/imagenes/image.png)    

## Clic en + Café dos veces
![alt text](src/main/java/app1/imagenes/cap2.png)

## Selecciona P-001, agrega Azúcar y luego Crema
![alt text](src/main/java/app1/imagenes/cap3.png)

## Clic en la fila P-002
![alt text](src/main/java/app1/imagenes/cap4.png)

## Con P-002 seleccionado, elige Queso y pulsa Agregar
![alt text](src/main/java/app1/imagenes/cap5.png)

## Con P-002, agrega Azúcar dos veces
![alt text](src/main/java/app1/imagenes/cap6.png)

## 