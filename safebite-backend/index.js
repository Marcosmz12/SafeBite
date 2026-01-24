const express = require('express');
const cors = require('cors');
const axios = require('axios'); // <--- Nueva librería para peticiones
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

// B. Recetas por autor
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

// --- RUTAS DE PRODUCTOS (MERCADONA API) ---

// E. Buscador de productos
app.get('/api/buscar-producto', async (req, res) => {
  try {
    const nombre = req.query.articulo;
    if (!nombre) return res.status(400).json({ error: 'Falta el nombre del producto' });

    console.log("Buscando en Mercadona:", nombre);

    // Añadimos Headers para que no nos bloqueen como bot
    const url = `https://tienda.mercadona.es/api/products/?query=${nombre}`;
    const respuesta = await axios.get(url, {
      headers: {
        'User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36'
      }
    });
    
    // Verificamos si hay resultados
    if (!respuesta.data || !respuesta.data.results) {
      return res.json([]);
    }

    const productos = respuesta.data.results.map(p => ({
      id: p.id,
      nombre: p.display_name,
      imagen: p.thumbnail,
      precio: p.price_instructions.unit_price,
      marca: p.brand || 'Hacendado'
    }));

    res.json(productos);

  } catch (error) { 
    // Esto nos dirá en la consola negra exactamente qué ha fallado
    console.error("Error detallado:", error.message);
    res.status(500).json({ error: 'Error al conectar con el supermercado', detalle: error.message }); 
  }
});

// F. Detalle de producto (Para ver ingredientes y alérgenos)
app.get('/api/producto-detalles/:id', async (req, res) => {
  try {
    const id = req.params.id;
    const url = `https://tienda.mercadona.es/api/products/${id}/`;
    const respuesta = await axios.get(url);
    const data = respuesta.data;

    res.json({
      id: data.id,
      nombre: data.display_name,
      ingredientes: data.ingredients,
      alergenos: data.extra_info, // Aquí suele venir "Contiene..."
      nutriscore: data.nutriscore,
      fotos: data.photos
    });
  } catch (error) {
    res.status(500).json({ error: 'Error al obtener detalles' });
  }
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

app.get('/', (req, res) => res.send('SafeBite API 🚀 - Conectada a Mercadona y Firebase'));

const PORT = 3000;
app.listen(PORT, () => console.log(`Servidor en http://localhost:${PORT}`));