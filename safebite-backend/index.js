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

// --- NUEVA RUTA: BUSCADOR DE PRODUCTOS POR SUPERMERCADO ---
app.get("/api/supermercado/:nombre", async (req, res) => {
  const { nombre } = req.params;
  const query = req.query.q;

  if (!query) return res.status(400).json({ error: "Falta el término de búsqueda" });

  try {
    // 1. Intentamos la API oficial si es Mercadona
    if (nombre.toLowerCase() === "mercadona") {
      try {
        const mercadonaUrl = `https://tienda.mercadona.es/api/products/?query=${encodeURIComponent(query)}`;
        const response = await axios.get(mercadonaUrl, {
          headers: {
            'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36',
            'Accept': 'application/json',
            'Origin': 'https://tienda.mercadona.es',
            'Referer': 'https://tienda.mercadona.es/'
          },
          timeout: 5000 // Si tarda más de 5s, saltamos al Plan B
        });

        const productos = response.data.results.map(p => ({
          id: p.id,
          nombre: p.display_name,
          marca: p.brand || "Hacendado",
          imagen: p.thumbnail,
          precio: p.price_instructions.unit_price,
          super: "Mercadona",
          fuente: "Oficial"
        }));
        return res.json(productos);

      } catch (mercaErr) {
        console.warn("API oficial de Mercadona bloqueada, usando Plan B (Open Food Facts)...");
        // No enviamos error, dejamos que el código siga hacia abajo al Plan B
      }
    }

    // 2. PLAN B: Búsqueda en Open Food Facts (Para todos, incluyendo Mercadona si falla)
    const offUrl = `https://world.openfoodfacts.org/cgi/search.pl?search_terms=${encodeURIComponent(query)}&brands=${nombre}&json=true`;
    const response = await axios.get(offUrl, {
      headers: { 'User-Agent': 'SafeBite - Web Project - https://safebite-d26ff.web.app' }
    });

    const productos = response.data.products.map(p => ({
      id: p.code,
      nombre: p.product_name || "Producto sin nombre",
      marca: p.brands || nombre,
      imagen: p.image_url || "https://via.placeholder.com/150",
      precio: "Consultar",
      super: nombre.charAt(0).toUpperCase() + nombre.slice(1),
      alergenos: p.allergens_from_ingredients || "No especificados",
      fuente: "Open Food Facts"
    }));

    res.json(productos);

  } catch (error) {
    console.error(`Error crítico buscando en ${nombre}:`, error.message);
    res.status(500).json({ error: "No se han podido cargar productos de este supermercado." });
  }
});

app.get("/", (req, res) => res.send("SafeBite API 🚀 Corriendo perfectamente"));

// --- CAMBIO 3: PUERTO DINÁMICO (OBLIGATORIO) ---
// Render te asigna un puerto al azar, no puedes dejar el 3000 fijo
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor activo en puerto ${PORT}`));