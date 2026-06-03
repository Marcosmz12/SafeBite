# DOCUMENTACION SAFEBITE

## 1. Motor de búsqueda e inteligencia de alimentos

### Concepto
Una de las partes más importantes de SafeBite es el motor de búsqueda de productos. Esta función permite que el usuario pueda buscar alimentos relacionados con distintos supermercados y consultar información útil sobre ellos, especialmente desde el punto de vista de la seguridad alimentaria.

La idea no era hacer un buscador simple que solo comparase texto, sino algo un poco más completo. El sistema tiene que recibir información de una fuente externa, prepararla, limpiarla, filtrarla y mostrar resultados que tengan sentido para el usuario. Además, debe intentar evitar resultados incorrectos o poco relacionados con la búsqueda.

Por ejemplo, si una persona busca un producto concreto, la aplicación no debería devolver cualquier alimento que contenga una palabra parecida, sino productos que realmente se ajusten a lo que se ha escrito. También se ha tenido en cuenta que una API externa puede fallar, por lo que la aplicación necesita seguir funcionando aunque esa fuente de datos no esté disponible en un momento determinado.

### Implementación técnica
Para obtener información de productos, SafeBite utiliza la API de Open Food Facts. Desde el backend se realizan peticiones HTTP utilizando `axios`, indicando diferentes parámetros como el término de búsqueda, el número de productos que se quieren obtener, los campos necesarios y el país.

Se intenta pedir solo la información que realmente se va a utilizar, como el nombre del producto, la marca, los ingredientes, los alérgenos, las imágenes, las categorías o las tiendas asociadas. Esto ayuda a que la respuesta sea más ligera y más fácil de tratar desde el servidor.

La llamada a Open Food Facts está separada en una función propia. Esto me parece importante porque, si en el futuro quisiera cambiar la API o añadir otra fuente de productos, no tendría que modificar toda la lógica del buscador, sino solamente esa parte concreta.

Uno de los problemas que apareció durante el desarrollo fue que la API externa a veces podía fallar o devolver errores, como el error 503 Service Unavailable. Para evitar que la aplicación se quedara sin funcionar, se añadió un sistema de respaldo utilizando un archivo JSON local. Este archivo contiene productos con una estructura parecida a la que devuelve la API, de forma que el frontend puede seguir trabajando igual aunque los datos vengan de otro origen.

También se implementó una normalización de textos. Esto consiste en convertir las palabras a minúsculas, eliminar acentos, limpiar prefijos como `en:` o `es:` y quitar caracteres especiales. Gracias a esto, búsquedas como “salmón” y “salmon” pueden tratarse de forma parecida.

Al principio, la búsqueda funcionaba con `includes`, pero esto provocaba algunos falsos positivos. Por ejemplo, al buscar “pan”, podían aparecer productos como “empanadillas” o “mazapán”, porque esas palabras contienen las letras “pan”. Para mejorarlo, se dividió el texto en palabras o tokens usando una expresión regular:

`split(/[\s,.;:()_\-/]+/)`

Después, el sistema compara las palabras de la búsqueda con el inicio de los tokens reales del producto. Así, “pan” puede coincidir con “Pan de molde” o “Pan sin gluten”, pero no con “empanadillas”.

También se añadió un sistema de puntuación o scoring. Esto sirve para dar más importancia a los productos que tienen más relación con el supermercado seleccionado. Por ejemplo, si el usuario busca en Mercadona, se priorizan marcas como Hacendado, Deliplus o Bosque Verde. En el caso de Lidl, se pueden priorizar marcas como Milbona, Belbake, Vemondo o Fin Carré.

Por último, se añadió una caché en memoria utilizando un `Map` con tiempo de vida limitado. Si el usuario repite una búsqueda en poco tiempo, el servidor puede devolver el resultado guardado sin tener que volver a llamar a la API externa.

### Justificación
Esta solución es más completa que una búsqueda básica porque intenta resolver problemas reales que pueden aparecer en una aplicación de este tipo. La normalización ayuda a evitar errores por acentos, idiomas o formatos distintos. La tokenización mejora la precisión de la búsqueda y evita resultados que no tienen sentido.

El sistema de puntuación permite ordenar mejor los productos, mostrando primero los que tienen más relación con el supermercado elegido. Además, el fallback con el JSON local hace que la aplicación sea más resistente ante fallos externos.

### Referencias bibliográficas
*   Open Food Facts API [https://openfoodfacts.github.io/openfoodfacts-server/api/](https://openfoodfacts.github.io/openfoodfacts-server/api/)
*   Google web.dev - Offline cookbook [https://web.dev/articles/offline-cookbook](https://web.dev/articles/offline-cookbook)

---

## 2. Seguridad alimentaria y perfiles de salud

### Concepto
La seguridad alimentaria es la parte central de SafeBite. El objetivo de la aplicación no es solamente enseñar productos, sino ayudar al usuario a saber si un alimento puede ser adecuado o peligroso según sus alergias o intolerancias.

Para ello, la aplicación compara los alérgenos guardados en el perfil del usuario con la información que aparece en cada producto. Esto es importante porque los alérgenos no siempre aparecen escritos de la misma forma. Por ejemplo, un producto puede indicar “milk”, “leche”, “lactose”, “dairy”, “suero de leche” o “nata”, y todos esos términos pueden estar relacionados con la lactosa o con productos lácteos.

### Implementación técnica
En el backend se utiliza un mapa de sinónimos de alérgenos. Este mapa agrupa diferentes palabras dentro de una misma categoría. Por ejemplo, para la lactosa se pueden incluir términos como:

```json
lactosa: [
  "milk",
  "lactose",
  "leche",
  "lactosa",
  "dairy",
  "butter",
  "mantequilla",
  "cheese",
  "queso",
  "cream",
  "nata"
]
```

Gracias a este sistema, la aplicación puede detectar un posible alérgeno aunque la API no utilice exactamente la misma palabra que tiene guardada el usuario en su perfil.

Además de lactosa, también se tienen en cuenta otros grupos como gluten, huevo, frutos secos, soja, pescado, marisco, sésamo o mostaza.

La detección se hace en dos niveles. Primero se revisan los campos estructurados que vienen de Open Food Facts, como `allergens_tags` y `traces_tags`. Estos campos ya vienen etiquetados, por lo que son bastante útiles. Después, también se analiza el texto de ingredientes, aplicando normalización y buscando los sinónimos definidos en el backend.

En el frontend, el perfil del usuario se obtiene desde Firebase. Cada usuario puede tener guardadas sus alergias o intolerancias. Angular cruza esta información con los alérgenos detectados en cada producto y genera un resultado, por ejemplo una propiedad como `isSafe`, que indica si el producto puede considerarse seguro o no para ese usuario.

### Justificación
Una comparación exacta de texto no sería suficiente en este caso. Si el usuario tiene marcada “lactosa”, pero el producto indica “milk” o “dairy”, el sistema podría no detectarlo si solo compara palabras iguales.

Por eso se utiliza un mapa de sinónimos. Este sistema ayuda a reducir falsos negativos, que en una aplicación relacionada con alergias serían un problema importante. Es mejor avisar al usuario de un posible riesgo que ignorar un alérgeno porque aparece escrito de otra forma.

### Referencias bibliográficas
*   Open Food Facts - Product API [https://wiki.openfoodfacts.org/API/Read/Product](https://wiki.openfoodfacts.org/API/Read/Product)
*   EFSA - Food allergens [https://www.efsa.europa.eu/en/safe2eat/food-allergens](https://www.efsa.europa.eu/en/safe2eat/food-allergens)

---

## 3. Frontend reactivo con Angular 19

### Concepto
El frontend de SafeBite está desarrollado con Angular. La idea principal es que la interfaz sea reactiva, es decir, que los cambios se reflejen automáticamente cuando cambia el estado de la aplicación.

Por ejemplo, si el usuario cambia el idioma, modifica su perfil, escribe en el buscador o se están cargando productos, la vista debe actualizarse sin tener que manipular directamente el DOM.

Angular 19 permite trabajar con herramientas como `signal`, `computed` e `inject`, que ayudan a organizar mejor el estado de la aplicación y a hacer el código más claro.

### Implementación técnica
En SafeBite se utilizan señales para guardar estados como el supermercado seleccionado, el texto de búsqueda, los productos recibidos, los alérgenos del usuario o el estado de carga.

Por ejemplo, el término de búsqueda puede guardarse en una señal, y a partir de esa señal se pueden calcular productos filtrados mediante `computed`. Esto permite que Angular realice la actualización solo de las partes de la pantalla que dependen de esos datos.

También se incluye un sistema de traducción propio mediante un `LanguageService`. Este servicio guarda el idioma actual y proporciona los textos traducidos en español e inglés. Al usar señales computadas, los textos de la interfaz pueden cambiar automáticamente cuando el usuario cambia de idioma.

Para proteger algunas rutas, como “Perfil” o “Subir Receta”, se utiliza un `AuthGuard`. Este guard comprueba si el usuario está autenticado antes de permitir el acceso. Si no hay sesión iniciada, el usuario puede ser redirigido a la pantalla de login o a una zona pública.

La búsqueda también incluye un sistema de debounce. Esto significa que la aplicación no envía una petición al backend por cada tecla que pulsa el usuario, sino que espera un pequeño intervalo, por ejemplo 800 ms. Si el usuario sigue escribiendo, se reinicia el tiempo de espera. Esto reduce el número de peticiones y evita sobrecargar el servidor o la API externa.

### Justificación
El uso de señales ayuda a que el código sea más ordenado y fácil de mantener. En lugar de tener muchas variables sueltas o suscripciones manuales, el estado queda más controlado y las dependencias entre datos son más claras.

El debounce también es necesario porque una búsqueda sin control podría lanzar demasiadas peticiones en muy poco tiempo. Esto no sería eficiente y podría provocar errores, sobre todo si se depende de una API externa.

### Referencias bibliográficas
*   Angular - Signals [https://angular.dev/guide/signals](https://angular.dev/guide/signals)
*   Angular - CanActivate / Route Guards [https://angular.dev/api/router/CanActivate](https://angular.dev/api/router/CanActivate)

---

## 4. Experiencia de usuario UI/UX y diseño

### Concepto
La experiencia de usuario es una parte importante de SafeBite, porque la aplicación trabaja con información sensible para el usuario, como alergias e intolerancias. Por eso, no basta con que la aplicación funcione: también tiene que mostrar la información de forma clara y fácil de entender.

El usuario debe poder identificar rápidamente el producto, la marca, los alérgenos y si ese alimento es seguro o no para su perfil.

### Implementación técnica
La interfaz está organizada con componentes reutilizables, como cards de producto, botones, formularios y bloques de contenido. Cada card muestra la información principal de un producto: imagen, nombre, marca, alérgenos y estado de seguridad.

Este sistema ayuda a mantener mejor el proyecto, porque si se quiere cambiar el diseño de una card, se puede hacer en un solo componente y el cambio se aplica a todos los productos.

Para la maquetación se utilizan CSS Grid y Flexbox. Grid se usa sobre todo para organizar listados de productos o formularios en columnas, mientras que Flexbox se utiliza para alinear elementos internos, como botones, etiquetas o grupos de acciones.

La aplicación también tiene en cuenta los fallos de imágenes. Si una imagen de Open Food Facts o del JSON local no carga correctamente, Angular puede mostrar una imagen por defecto usando el evento `(error)`. Esto evita que se vean iconos rotos y mantiene la interfaz más cuidada.

También se contempla la previsualización de imágenes en apartados como recetas o subida de contenido. Esto ayuda a que el usuario pueda comprobar lo que está subiendo antes de enviarlo.

### Justificación
Una lista simple de nombres no sería suficiente para SafeBite. La aplicación necesita mostrar bastante información de forma clara, visual y ordenada. Las cards permiten agrupar los datos de cada producto y hacen que sea más fácil comparar resultados.

El diseño responsive también es importante porque la aplicación debería poder utilizarse tanto en ordenador como en tablet o móvil. Además, gestionar correctamente los errores de imagen mejora la experiencia del usuario y evita que la aplicación parezca incompleta o rota.

### Referencias bibliográficas
*   Nielsen Norman Group - Cards UI component [https://www.nngroup.com/articles/cards-component/](https://www.nngroup.com/articles/cards-component/)
*   web.dev - Responsive images [https://web.dev/learn/design/responsive-images](https://web.dev/learn/design/responsive-images)

---

## 5. Asistente virtual inteligente chatbot

### Concepto
SafeBite incluye un asistente virtual que sirve como apoyo estratégico para el usuario. Su función principal es facilitar la navegación, resolver dudas frecuentes y humanizar la experiencia digital. Este asistente no sustituye la lógica de seguridad alimentaria del motor de alérgenos, sino que actúa como una capa de guía que ayuda al usuario a interpretar alertas, localizar secciones de configuración de salud o gestionar su contenido de recetas.

### Implementación técnica
El chatbot se ha desarrollado utilizando una arquitectura de componentes Standalone y aprovecha la reactividad de Angular Signals. Visualmente, cuenta con una mascota identificativa (`hojachatbot.png`) para fortalecer el branding y la cercanía.

La innovación técnica clave en este módulo es el uso de la función `effect()` en el constructor del componente. A diferencia de un ciclo de vida tradicional (como `ngOnInit`), que solo se ejecuta una vez al cargar la página, el `effect()` monitoriza constantemente el estado de la señal de idioma proveniente del `LanguageService`.

*   **Sincronización Reactiva:** Gracias a `effect()`, el chatbot detecta en tiempo real si el usuario cambia el idioma de la aplicación (ES/EN) y reinicia automáticamente el historial de la conversación, traduciendo el saludo inicial y las opciones de respuesta de forma instantánea.
*   **Procesamiento de Lenguaje Natural (NLP) simplificado:** Se ha programado una lógica de respuesta basada en la normalización de cadenas de texto. El bot utiliza expresiones regulares y métodos de limpieza (como `normalize` y `replace`) para entender intenciones de navegación aunque el usuario escriba con tildes o variaciones (ej: "Menú" o "menu").
*   **UX Sensorial:** Para mejorar la retroalimentación, se ha integrado un efecto de escritura diferida mediante `setTimeout` y un sistema de notificaciones auditivas (sonido "pop" de baja latencia) que avisa al usuario de la llegada de una respuesta.

### Justificación
Un chatbot integrado resulta más eficiente que una sección de FAQ estática, ya que acompaña al usuario dinámicamente. La implementación de Signals y effects es una decisión arquitectónica orientada al rendimiento: evitamos el uso de Intervals o suscripciones pesadas de RxJS para vigilar cambios de estado, permitiendo que Angular gestione el renderizado de forma granular.

Además, el bot está interconectado con el Router de Angular, lo que le permite "empujar" al usuario hacia páginas críticas (como el Perfil de Alergias) tras una consulta, reduciendo la tasa de rebote y mejorando la tasa de éxito en la configuración del perfil de salud del usuario.

### Referencias bibliográficas
*   Google - Conversation Design [https://developers.google.com/assistant/conversation-design/welcome](https://developers.google.com/assistant/conversation-design/welcome)
*   Nielsen Norman Group - Chatbots and UX [https://www.nngroup.com/articles/chatbots/](https://www.nngroup.com/articles/chatbots/)

---

## 6. Accesibilidad y responsabilidad social

### Concepto
SafeBite tiene una parte social importante, ya que está pensada para ayudar a personas con alergias, intolerancias o necesidades alimentarias específicas. Por eso, la accesibilidad no debería verse como un extra añadido al final, sino como una parte necesaria del proyecto.

La aplicación debe poder usarse en distintas situaciones: personas con baja visión, daltonismo, sensibilidad a la luz, navegación mediante teclado o usuarios que necesitan una interfaz más clara.

### Implementación técnica
El proyecto incluye un panel de accesibilidad con configuración guardada en LocalStorage. Esto permite que las preferencias del usuario se mantengan entre sesiones. Por ejemplo, si una persona activa el modo oscuro o el alto contraste, la aplicación puede recordarlo la próxima vez que se abra.

El modo oscuro cambia la paleta de colores para reducir la fatiga visual, especialmente en entornos con poca luz. Esto puede hacerse mediante clases CSS globales aplicadas al documento o al contenedor principal.

También se contempla un modo para daltonismo mediante filtros CSS o ajustes de color. Aun así, no se debe depender solo del color para mostrar información importante. En una aplicación como SafeBite, donde se indican riesgos alimentarios, es necesario acompañar los colores con texto, iconos o etiquetas claras.

La navegación por teclado también es importante. Los botones y elementos interactivos deben poder recorrerse con la tecla Tab, activarse con teclado y entenderse correctamente con lectores de pantalla. Para ello se pueden usar etiquetas adecuadas y atributos como `aria-label`.

### Justificación
No sería suficiente marcar los productos seguros en verde y los peligrosos en rojo. Algunas personas pueden no distinguir bien esos colores, y otras pueden utilizar lectores de pantalla. Por eso, la información importante debe transmitirse de varias formas: color, texto, iconos y estructura visual.

Guardar las preferencias en LocalStorage también mejora la experiencia, porque evita que el usuario tenga que activar las mismas opciones cada vez que entra en la aplicación.

En este proyecto, la accesibilidad está muy relacionada con la seguridad y la responsabilidad social, porque la aplicación trata información que puede afectar directamente al bienestar del usuario.

### Referencias bibliográficas
*   W3C - WCAG 2.2 [https://www.w3.org/TR/WCAG22/](https://www.w3.org/TR/WCAG22/)
*   MDN - Window.localStorage [https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage](https://developer.mozilla.org/en-US/docs/Web/API/Window/localStorage)

---

## 7. Sistema de Internacionalización (i18n) Reactivo

### Concepto
Se puede cambiar entre español e inglés en cualquier momento. El objetivo no es solo traducir palabras, sino ofrecer una interfaz que reaccione instantáneamente al cambio de idioma sin necesidad de recargar la página ni perder el estado actual del usuario (filtros, búsquedas o mensajes del chatbot).

### Implementación técnica
Se ha implementado un `LanguageService` centralizado que utiliza las capacidades reactivas de Angular 19:

*   **Gestión de Estado con Signals:** El idioma actual se almacena en un `signal<'es' | 'en'>`, lo que permite una detección de cambios ultra-eficiente.
*   **Diccionario Computado:** Se utiliza la función `computed()` para generar un objeto de traducción dinámico. Cada vez que el Signal del idioma cambia, el objeto `t()` se recalcula automáticamente, notificando a todos los componentes que lo consumen.
*   **UI Dinámica de Selección:** El selector de idiomas en el Header utiliza un sistema de banderas circulares con filtros CSS de escala de grises. Mediante lógica condicional, la bandera del idioma activo recupera su color original mientras la otra se atenúa, mejorando la jerarquía visual.
*   **Soporte de Placeholders y Labels:** La traducción se extiende más allá de los textos fijos, aplicando Property Binding en atributos de HTML como `[placeholder]`, asegurando que los campos de búsqueda y formularios de contacto sean totalmente coherentes en ambos idiomas.

### Justificación
El uso de un servicio de traducción propio basado en Signals ofrece varias ventajas competitivas sobre librerías estándar:

*   **Rendimiento:** Angular solo actualiza los nodos de texto específicos que dependen de la señal, evitando ciclos de detección de cambios innecesarios en toda la página.
*   **Mantenibilidad:** El diccionario está centralizado en un solo archivo, lo que facilita la adición de nuevos idiomas en el futuro sin modificar la lógica de los componentes.
*   **Experiencia de Usuario (UX):** Al evitar la recarga del navegador (reload), la transición entre idiomas es fluida y no interrumpe tareas críticas, como el proceso de subida de una receta o una consulta en el chatbot.

### Referencias bibliográficas
*   Angular - Internationalization (i18n) overview: [https://angular.dev/guide/i18n](https://angular.dev/guide/i18n)
*   W3C - Internationalization Best Practices: [https://www.w3.org/International/](https://www.w3.org/International/)

---

## 8. Gestión de Archivos Multimedia (Integración con Cloudinary)

### Concepto
El almacenamiento de imágenes es un reto técnico en aplicaciones modernas, ya que guardar imágenes pesadas directamente en la base de datos ralentizaría el sistema. SafeBite utiliza una arquitectura de almacenamiento distribuido para las fotos de las recetas.

### Implementación técnica
Se ha integrado el servicio Cloudinary mediante una API REST.

1.  **Flujo de Carga:** Cuando el usuario selecciona una foto en "Subir Receta", el sistema genera una previsualización local instantánea (`imagePreview` vía `FileReader`).
2.  **Procesamiento Asíncrono:** Al pulsar "Publicar", el frontend envía el archivo binario al servidor, el cual lo sube a Cloudinary.
3.  **Persistencia de Referencia:** Una vez subida, recibimos una Secure URL (HTTPS) que es la que finalmente guardamos en Firestore.

### Justificación
Delegar los archivos a un CDN (Content Delivery Network) externo como Cloudinary garantiza que las imágenes carguen rápido en cualquier lugar del mundo y ahorra costes de almacenamiento y ancho de banda en nuestro servidor principal.

### Referencias bibliográficas
*   Cloudinary Documentation - Upload API Reference: [https://cloudinary.com/documentation/image_upload_api_reference](https://cloudinary.com/documentation/image_upload_api_reference)
*   MDN Web Docs - FileReader API: [https://developer.mozilla.org/es/docs/Web/API/FileReader](https://developer.mozilla.org/es/docs/Web/API/FileReader)
*   Google web.dev - Image Optimization: [https://web.dev/articles/image-optimization](https://web.dev/articles/image-optimization)

---

## 9. Gestión de Estado Complejo en Formularios (Signals Dinámicos)

### Concepto
A diferencia de un formulario simple (nombre, email), el formulario de "Subir Receta" requiere gestionar listas que crecen dinámicamente (ingredientes y pasos).

### Implementación técnica
Se ha utilizado el método `.update()` de los Angular Signals.

*   **Inmutabilidad:** Para añadir un elemento, usamos el patrón: `this.list.update(items => [...items, nuevoItem])`. Esto crea una nueva referencia en memoria, lo cual es el estándar de oro en rendimiento de frameworks modernos.

### Justificación
Esto permite que la interfaz reaccione al instante (por ejemplo, el botón "Publicar" se activa o desactiva solo si hay al menos un ingrediente) de forma mucho más limpia que usando los antiguos FormArrays de Angular, que son mucho más pesados y difíciles de leer.

### Referencias bibliográficas
*   Angular.dev - Angular Signals: [https://angular.dev/guide/signals](https://angular.dev/guide/signals)
*   Angular University - Angular Signals: [https://blog.angular-university.io/angular-signals/](https://blog.angular-university.io/angular-signals/)
*   MDN Web Docs - Spread Syntax (...): [https://developer.mozilla.org/es/docs/Web/JavaScript/Reference/Operators/Spread_syntax](https://developer.mozilla.org/es/docs/Web/JavaScript/Reference/Operators/Spread_syntax)