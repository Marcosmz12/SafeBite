const express = require('express');
const cors = require('cors');
const admin = require('firebase-admin');
const serviceAccount = require('./service_account.json');

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount)
});

const db = admin.firestore();
const app = express();

app.use(cors()); 
app.use(express.json());

// --- RUTAS DE RECETAS ---

// A. Todas las recetas
app.get('/api/recetas', async (req, res) => {
  try {
    const snapshot = await db.collection('recetas').get();
    const recetas = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
    res.json(recetas);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// B. Recetas por autor (ESTA TE FALTABA)
app.get('/api/recetas/autor/:userId', async (req, res) => {
  try {
    const userId = req.params.userId;
    const snapshot = await db.collection('recetas').where('autor_id', '==', userId).get();
    const recetas = snapshot.docs.map(doc => ({ id: doc.id, ...doc.data() }));
    res.json(recetas);
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// C. Una receta por ID
app.get('/api/recetas/:id', async (req, res) => {
  try {
    const doc = await db.collection('recetas').doc(req.params.id).get();
    if (!doc.exists) return res.status(404).json({ message: 'No existe' });
    res.json({ id: doc.id, ...doc.data() });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// D. Crear receta
app.post('/api/recetas', async (req, res) => {
  try {
    const docRef = await db.collection('recetas').add(req.body);
    res.status(201).json({ id: docRef.id });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

// --- RUTAS DE PERFIL ---

app.get('/api/perfil/:uid', async (req, res) => {
  try {
    const doc = await db.collection('perfiles').doc(req.params.uid).get();
    res.json(doc.exists ? doc.data() : { alergias: [], recetasSubidas: 0 });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

app.post('/api/perfil/:uid', async (req, res) => {
  try {
    await db.collection('perfiles').doc(req.params.uid).set(req.body, { merge: true });
    res.json({ message: 'Perfil ok' });
  } catch (error) { res.status(500).json({ error: error.message }); }
});

app.get('/', (req, res) => res.send('SafeBite API 🚀'));

const PORT = 3000;
app.listen(PORT, () => console.log(`Servidor en http://localhost:${PORT}`));