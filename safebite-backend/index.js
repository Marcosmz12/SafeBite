const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
const axios = require('axios');

// --- CAMBIO 1: CREDENCIALES DINÁMICAS ---
// En local usará el archivo, en Render usará la variable de entorno
let serviceAccount;
if (process.env.FIREBASE_SERVICE_ACCOUNT) {
  serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
} else {
  serviceAccount = require("./service_account.json");
}

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
});

const db = admin.firestore();
const app = express();

// --- 1. MEMORIA TEMPORAL (CACHÉ) ---
const cacheBusqueda = new Map();

// --- 2. DATOS DE EMERGENCIA (Por si la API falla o da 503) ---
const productosSimulados = {
  leche: [
    { id: "1001", nombre: "Leche Entera Calcio", marca: "Hacendado", imagen: "https://images.openfoodfacts.org/images/products/843/187/625/5549/front_es.33.400.jpg", alergenos_lista: ["lactosa"], super: "Mercadona" },
    { id: "1002", nombre: "Leche Semidesnatada", marca: "Carrefour", imagen: "https://images.openfoodfacts.org/images/products/843/187/610/0030/front_es.24.400.jpg", alergenos_lista: ["lactosa"], super: "Carrefour" },
    { id: "1003", nombre: "Leche sin Lactosa", marca: "Milbona", imagen: "https://images.openfoodfacts.org/images/products/20272023/front_es.6.400.jpg", alergenos_lista: [], super: "Lidl" }
  ],
  pan: [
    { id: "2001", nombre: "Pan de molde Integral", marca: "Hacendado", imagen: "https://images.openfoodfacts.org/images/products/848/000/082/3745/front_es.25.400.jpg", alergenos_lista: ["gluten"], super: "Mercadona" },
    { id: "2002", nombre: "Pan de molde sin corteza", marca: "Carrefour", imagen: "https://images.openfoodfacts.org/images/products/843/187/624/3201/front_es.40.400.jpg", alergenos_lista: ["gluten"], super: "Carrefour" },
    { id: "2003", nombre: "Pan Blanco", marca: "Lidl", imagen: "https://images.openfoodfacts.org/images/products/20048451/front_es.18.400.jpg", alergenos_lista: ["gluten"], super: "Lidl" }
  ],
  pizza: [
    { id: "3001", nombre: "Pizza Jamón y Queso", marca: "Hacendado", imagen: "https://images.openfoodfacts.org/images/products/848/000/026/0205/front_es.29.400.jpg", alergenos_lista: ["gluten", "lactosa"], super: "Mercadona" },
    { id: "3002", nombre: "Pizza Margarita sin Gluten", marca: "Carrefour", imagen: "https://images.openfoodfacts.org/images/products/843/187/614/3570/front_es.24.400.jpg", alergenos_lista: ["lactosa"], super: "Carrefour" }
  ]
};

// --- CAMBIO 2: CORS FLEXIBLE ---
// Permitimos tanto tu web de Firebase como localhost (para cuando tú desarrolles)
const allowedOrigins = [
  "https://safebite-d26ff.web.app",
  "http://localhost:4200" // Puerto por defecto de Angular
];

// 2. Configura CORS para permitir tu frontend
app.use(
  cors({
    origin: function (origin, callback) {
      if (!origin || allowedOrigins.includes(origin)) {
        callback(null, true);
      } else {
        callback(new Error("No permitido por CORS"));
      }
    },
    credentials: true,
  })
);

app.use((req, res, next) => {
  res.header("Access-Control-Allow-Private-Network", "true");
  next();
});

app.use(express.json());

app.post("/api/verify-recaptcha", async (req, res) => {
  const { token } = req.body;
  const secretKey = process.env.RECAPTCHA_SECRET;

  try {
    const response = await axios.post(
      `https://www.google.com/recaptcha/api/siteverify?secret=${secretKey}&response=${token}`
    );

    if (response.data.success) {
      res.json({ success: true, message: "Validación correcta" });
    } else {
      res.status(400).json({ success: false, message: "Fallo en el reCAPTCHA" });
    }
  } catch (error) {
    res.status(500).json({ error: "Error al validar con Google" });
  }
});

// --- TUS RUTAS (SE QUEDAN IGUAL) ---

app.get("/api/recetas", async (req, res) => {
  try {
    const snapshot = await db.collection("recetas").get();
    const recetas = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
    res.json(recetas);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.get("/api/recetas/autor/:userId", async (req, res) => {
  try {
    const userId = req.params.userId;
    const snapshot = await db
      .collection("recetas")
      .where("autor_id", "==", userId)
      .get();
    const recetas = snapshot.docs.map((doc) => ({ id: doc.id, ...doc.data() }));
    res.json(recetas);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.get("/api/recetas/:id", async (req, res) => {
  try {
    const doc = await db.collection("recetas").doc(req.params.id).get();
    if (!doc.exists) return res.status(404).json({ message: "No existe" });
    res.json({ id: doc.id, ...doc.data() });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post("/api/recetas", async (req, res) => {
  try {
    const docRef = await db.collection("recetas").add(req.body);
    res.status(201).json({ id: docRef.id });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.get("/api/perfil/:uid", async (req, res) => {
  try {
    const doc = await db.collection("perfiles").doc(req.params.uid).get();
    res.json(doc.exists ? doc.data() : { alergias: [], recetasSubidas: 0 });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post("/api/perfil/:uid", async (req, res) => {
  try {
    await db
      .collection("perfiles")
      .doc(req.params.uid)
      .set(req.body, { merge: true });
    res.json({ message: "Perfil ok" });
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

app.post("/api/contacto", async (req, res) => {
  try {
    const nuevoMensaje = req.body;
    
    // Guardamos el mensaje en una nueva colección llamada 'mensajes_contacto'
    // Añadimos una marca de tiempo para saber cuándo se envió
    await db.collection("mensajes_contacto").add({
      ...nuevoMensaje,
      fecha_envio: new Date().toISOString()
    });

    res.json({ success: true, message: "Mensaje recibido y guardado en Firebase 🚀" });
  } catch (error) {
    console.error("Error en contacto:", error.message);
    res.status(500).json({ error: "No se pudo guardar el mensaje" });
  }
});

// --- RUTA ÚNICA DEFINITIVA: BUSCADOR DE PRODUCTOS ---
app.get("/api/supermercado/:nombre", async (req, res) => {
  const { nombre } = req.params;
  const query = req.query.q?.toLowerCase().trim();

  if (!query) return res.status(400).json({ error: "Falta el término de búsqueda" });

  const llaveCache = `${nombre}-${query}`;
  if (cacheBusqueda.has(llaveCache)) {
    return res.json(cacheBusqueda.get(llaveCache));
  }

  try {
    let brandFilter = "";
    if (nombre === 'mercadona') brandFilter = 'hacendado';
    else if (nombre === 'lidl') brandFilter = 'belbake,milbona,lidl';
    else if (nombre === 'carrefour') brandFilter = 'carrefour';
    else if (nombre === 'alcampo') brandFilter = 'auchan,alcampo';

    // 1. Pedimos 50 productos (más cantidad para poder filtrar nosotros)
    const url = `https://world.openfoodfacts.org/api/v2/search?search_terms=${encodeURIComponent(query)}&brands_tags=${brandFilter}&fields=code,product_name,brands,image_url,allergens_tags&page_size=50`;

    const response = await axios.get(url, {
      headers: { 'User-Agent': 'SafeBite-App-Final-v8' },
      timeout: 10000 
    });

    if (response.data.products && response.data.products.length > 0) {
      // 2. FILTRO MANUAL: Solo nos quedamos con los que tienen la palabra buscada en el nombre
      // Esto eliminará el guacamole, el kefir y demás cosas que no son lo que buscas.
      const productosValidos = response.data.products
        .map(p => ({
          id: p.code,
          nombre: p.product_name || "",
          marca: p.brands || nombre.toUpperCase(),
          imagen: p.image_url || "https://via.placeholder.com/150?text=SafeBite",
          alergenos_lista: p.allergens_tags 
            ? p.allergens_tags.map(a => a.replace('en:', '').replace(/-/g, ' ')) 
            : [],
          super: nombre.charAt(0).toUpperCase() + nombre.slice(1)
        }))
        .filter(p => p.nombre.toLowerCase().includes(query)); // <--- LA MAGIA ESTÁ AQUÍ

      cacheBusqueda.set(llaveCache, productosValidos);
      return res.json(productosValidos);
    }

    throw new Error("Sin resultados");

  } catch (error) {
    // PLAN B: Datos simulados si internet falla
    let fallbackData = [];
    if (query.includes('leche')) fallbackData = productosSimulados.leche;
    else if (query.includes('pan')) fallbackData = productosSimulados.pan;
    
    let dataFiltrada = fallbackData.filter(p => p.super.toLowerCase() === nombre.toLowerCase());
    return res.json(dataFiltrada.length > 0 ? dataFiltrada : fallbackData);
  }
});

app.get("/", (req, res) => res.send("SafeBite API 🚀 Corriendo perfectamente"));

// --- CAMBIO 3: PUERTO DINÁMICO (OBLIGATORIO) ---
// Render te asigna un puerto al azar, no puedes dejar el 3000 fijo
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor activo en puerto ${PORT}`));