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

app.get("/", (req, res) => res.send("SafeBite API 🚀 Corriendo perfectamente"));

// --- CAMBIO 3: PUERTO DINÁMICO (OBLIGATORIO) ---
// Render te asigna un puerto al azar, no puedes dejar el 3000 fijo
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => console.log(`Servidor activo en puerto ${PORT}`));