DOCUMENTACIÓN TÉCNICA DETALLADA: SafeBite

1. Identidad del Proyecto

SafeBite es una plataforma móvil avanzada diseñada para mitigar los riesgos
asociados a la seguridad alimentaria. Su objetivo es proporcionar a los usuarios
con alergias, intolerancias o dietas específicas (vegano, sin gluten, sin
lactosa) una herramienta de consulta instantánea basada en datos masivos (Big
Data) e Inteligencia Artificial.

2. Especificaciones del Stack Tecnológico

- Lenguaje de Programación: Kotlin 2.0 (JVM Target 11).
- Framework de UI: Jetpack Compose (Material Design 3).
- Gestión de Estado: mutableStateOf, remember, y ViewModel (MVVM).
- Backend & Persistencia:
    - Firebase Auth: Gestión de identidades y tokens de sesión.
    - Cloud Firestore: Base de datos NoSQL para perfiles de usuario y
      metadatos.
    - Firebase Analytics: Monitorización del comportamiento del usuario.
- Networking (Comunicación con APIs):
    - Retrofit 2: Cliente REST para consumo de OpenFoodFacts API.
    - Gson: Serialización y de-serialización de JSON.
    - OkHttp: Intercepción de logs y gestión de peticiones HTTP.
- Inteligencia Artificial: SDK de Google Generative AI (Modelo Gemini 1.5
  Flash).
- Hardware e Imagen:
    - CameraX: API de cámara para dispositivos Android.
    - Google ML Kit: Motor de reconocimiento de códigos de barras (EAN-13).
    - Coil: Carga de imágenes asíncronas con gestión de caché.

3. Arquitectura del Sistema

La aplicación utiliza una Arquitectura en Capas (N-Tier Architecture) basada en
MVVM:

1.  Capa de Presentación (UI): Funciones @Composable que reaccionan a los
    estados del ViewModel.
2.  Capa de Negocio (ViewModel): Procesa la lógica, maneja corrutinas
    (viewModelScope) y expone estados reactivos.
3.  Capa de Datos (Repository): Punto único de acceso a los datos. Implementa la
    lógica de "Single Source of Truth", decidiendo entre la API de OpenFoodFacts
    o la persistencia de Firestore.

4. Desglose Detallado de Módulos

A. Módulo de Autenticación y Perfil

- Registro Dinámico: Al crear una cuenta, el sistema no solo registra las
  credenciales, sino que inicializa un documento en Firestore con el UID del
  usuario para almacenar preferencias futuras.
- Gestión de Perfil: Permite la actualización de campos críticos
  (Email/Password) en Firebase Auth sincronizadamente con los datos
  descriptivos en Firestore mediante SetOptions.merge().

B. Módulo de Búsqueda y Filtrado Inteligente

- Motor de Búsqueda: Realiza peticiones a la API de OpenFoodFacts utilizando
  parámetros de búsqueda difusa.
- Algoritmo de Filtrado: Implementa filtros por etiquetas (Tags). Por ejemplo,
  al seleccionar "Sin Gluten", la app inyecta el parámetro
  tag_0=en:gluten-free en la URL de consulta.
- Nutri-Score: Visualización de la calificación nutricional (A-E) extraída de
  los metadatos del producto.

C. Módulo de Asistente IA (SafeBite Bot)

- Procesamiento de Lenguaje Natural (NLP): Utiliza Gemini para interpretar
  consultas complejas del usuario (ej: "¿Puedo comer esto si tengo alergia a
  los frutos secos?").
- Instrucción de Sistema: El modelo tiene un "prompt" de sistema restrictivo
  que asegura que las respuestas se limiten exclusivamente al dominio de la
  nutrición y seguridad alimentaria.

D. Módulo de Escaneo (Computer Vision)

- Reconocimiento de Barcodes: Utiliza el análisis de imágenes en tiempo real
  de ML Kit. Una vez detectado el código, se realiza una consulta automática
  al endpoint api/v0/product/{barcode}.json.
- Historial de Escaneo: Cada producto escaneado con éxito se añade a una
  sub-colección en Firestore, permitiendo la consulta offline de productos
  vistos anteriormente.

5. Seguridad y Privacidad

- Reglas de Firestore: Configuración de reglas de seguridad que impiden que un
  usuario lea o escriba datos de otro (request.auth.uid == userId).
- Ofuscación: Uso de buildConfigField en Gradle para manejar llaves de API
  sensibles, evitando que queden expuestas en el código fuente
  (local.properties).

---------------------------------------------------------------------------------------------

DETAILED TECHNICAL DOCUMENTATION: SafeBite

1. Project Identity

SafeBite is an advanced mobile platform designed to mitigate food safety risks.
Its goal is to provide users with allergies, intolerances, or specific diets
(Vegan, Gluten-Free, Lactose-Free) with an instant consultation tool based on
Big Data and Artificial Intelligence.

2. Tech Stack Specifications

- Language: Kotlin 2.0 (JVM Target 11).
- UI Framework: Jetpack Compose (Material Design 3).
- State Management: mutableStateOf, remember, and ViewModel (MVVM).
- Backend & Persistence:
    - Firebase Auth: Identity management and session tokens.
    - Cloud Firestore: NoSQL database for user profiles and metadata.
    - Firebase Analytics: User behavior monitoring.
- Networking:
    - Retrofit 2: REST client for OpenFoodFacts API consumption.
    - Gson: JSON serialization and de-serialization.
    - OkHttp: Log interception and HTTP request management.
- Artificial Intelligence: Google Generative AI SDK (Gemini 1.5 Flash Model).
- Hardware & Imaging:
    - CameraX: Camera API for Android devices.
    - Google ML Kit: Barcode recognition engine (EAN-13).
    - Coil: Asynchronous image loading with cache management.

3. System Architecture

The application uses a Layered Architecture (N-Tier) based on MVVM:

1.  Presentation Layer (UI): @Composable functions that react to ViewModel
    states.
2.  Business Layer (ViewModel): Processes logic, handles coroutines
    (viewModelScope), and exposes reactive states.
3.  Data Layer (Repository): Single point of entry for data. Implements the
    "Single Source of Truth" logic, deciding between the OpenFoodFacts API or
    Firestore persistence.

4. Detailed Module Breakdown

A. Authentication & Profile Module

- Dynamic Registration: When creating an account, the system not only
  registers credentials but also initializes a Firestore document with the
  user's UID to store future preferences.
- Profile Management: Allows updating critical fields (Email/Password) in
  Firebase Auth synchronized with descriptive data in Firestore using
  SetOptions.merge().

B. Search & Smart Filtering Module

- Search Engine: Performs requests to the OpenFoodFacts API using fuzzy search
  parameters.
- Filtering Algorithm: Implements tag-based filters. For example, selecting
  "Gluten-Free" injects the tag_0=en:gluten-free parameter into the query URL.
- Nutri-Score: Visual display of the nutritional rating (A-E) extracted from
  product metadata.

C. AI Assistant Module (SafeBite Bot)

- Natural Language Processing (NLP): Uses Gemini to interpret complex user
  queries (e.g., "Can I eat this if I have a nut allergy?").
- System Instruction: The model has a restrictive system prompt ensuring
  responses are strictly limited to the domain of nutrition and food safety.

D. Scanning Module (Computer Vision)

- Barcode Recognition: Uses real-time image analysis from ML Kit. Once a code
  is detected, an automatic request is made to the
  api/v0/product/{barcode}.json endpoint.
- Scan History: Each successfully scanned product is added to a Firestore
  sub-collection, allowing offline viewing of previously seen products.

5. Security and Privacy

- Firestore Rules: Security rules configuration that prevents one user from
  reading or writing another's data (request.auth.uid == userId).
- Obfuscation: Use of buildConfigField in Gradle to handle sensitive API keys,
  preventing them from being exposed in the source code (local.properties).
