const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
const axios = require("axios");

// --- CONFIGURACIÓN FIREBASE ---
let serviceAccount;

if (process.env.FIREBASE_SERVICE_ACCOUNT) {
  serviceAccount = JSON.parse(process.env.FIREBASE_SERVICE_ACCOUNT);
} else {
  serviceAccount = require("./service_account.json");
}

if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
  });
}

const db = admin.firestore();
const app = express();

// --- CONFIGURACIÓN GENERAL ---
const PORT = process.env.PORT || 3000;
const CACHE_TTL_MS = 10 * 60 * 1000;
const OPEN_FOOD_FACTS_URL = "https://world.openfoodfacts.org/api/v2/search";

// --- CACHE ---
const cacheBusqueda = new Map();

// --- MAPAS DE SUPERMERCADOS ---
const supermercadosConfig = {
  mercadona: {
    nombre: "Mercadona",
    marcas: ["hacendado", "mercadona", "bosque verde", "deliplus"],
    tiendas: ["mercadona"],
  },
  lidl: {
    nombre: "Lidl",
    marcas: ["lidl", "milbona", "belbake", "vemondo", "fin carre", "cien"],
    tiendas: ["lidl"],
  },
  carrefour: {
    nombre: "Carrefour",
    marcas: ["carrefour"],
    tiendas: ["carrefour"],
  },
  alcampo: {
    nombre: "Alcampo",
    marcas: ["alcampo", "auchan"],
    tiendas: ["alcampo", "auchan"],
  },
};

// --- MAPA DE ALÉRGENOS ---
const mapaAlergenos = {
  gluten: [
    "gluten",
    "wheat",
    "trigo",
    "barley",
    "cebada",
    "rye",
    "centeno",
    "spelt",
    "espelta",
    "oat",
    "avena",
  ],
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
    "nata",
    "yogurt",
    "yoghurt",
    "yogur",
    "suero de leche",
  ],
  huevo: [
    "eggs",
    "egg",
    "huevo",
    "ovoproducto",
    "albumin",
    "albumina",
    "clara de huevo",
    "yema",
  ],
  frutos_secos: [
    "nuts",
    "nut",
    "almonds",
    "almond",
    "hazelnuts",
    "hazelnut",
    "walnuts",
    "walnut",
    "cashews",
    "cashew",
    "almendras",
    "almendra",
    "avellanas",
    "avellana",
    "nueces",
    "nuez",
    "anacardos",
    "anacardo",
    "pistacho",
    "pistachio",
  ],
  soja: [
    "soy",
    "soja",
    "soybeans",
    "lecitina de soja",
    "soy lecithin",
  ],
  pescado: [
    "fish",
    "pescado",
    "salmon",
    "salmón",
    "tuna",
    "atun",
    "atún",
    "cod",
    "bacalao",
    "merluza",
  ],
};

// --- FALLBACK LOCAL ---
const productosSimulados = {
  leche: [
    {
      id: "fallback-leche-1",
      nombre: "Leche Entera",
      marca: "Hacendado",
      imagen:
        "https://images.openfoodfacts.org/images/products/843/187/625/5549/front_es.33.400.jpg",
      alergenos_lista: ["milk", "lactosa"],
      super: "Mercadona",
      fuente: "fallback",
    },
    {
      id: "fallback-leche-2",
      nombre: "Leche sin Lactosa",
      marca: "Milbona",
      imagen:
        "https://images.openfoodfacts.org/images/products/20272023/front_es.6.400.jpg",
      alergenos_lista: ["milk"],
      super: "Lidl",
      fuente: "fallback",
    },
  ],
  pan: [
    {
      id: "fallback-pan-1",
      nombre: "Pan de Molde Integral",
      marca: "Hacendado",
      imagen:
        "https://images.openfoodfacts.org/images/products/848/000/082/3745/front_es.25.400.jpg",
      alergenos_lista: ["gluten"],
      super: "Mercadona",
      fuente: "fallback",
    },
    {
      id: "fallback-pan-2",
      nombre: "Pan Blanco",
      marca: "Lidl",
      imagen: "https://via.placeholder.com/300x300?text=Pan",
      alergenos_lista: ["gluten"],
      super: "Lidl",
      fuente: "fallback",
    },
  ],
  tomate: [
    {
      id: "fallback-tomate-1",
      nombre: "Tomate Triturado",
      marca: "Hacendado",
      imagen: "https://via.placeholder.com/300x300?text=Tomate",
      alergenos_lista: [],
      super: "Mercadona",
      fuente: "fallback",
    },
    {
      id: "fallback-tomate-2",
      nombre: "Tomate Frito",
      marca: "Hacendado",
      imagen: "https://via.placeholder.com/300x300?text=Tomate",
      alergenos_lista: [],
      super: "Mercadona",
      fuente: "fallback",
    },
  ],
};

// --- MIDDLEWARES ---
app.use(cors());
app.use(express.json());

// --- HELPERS ---
function normalizarTexto(texto) {
  return String(texto || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/ñ/g, "n")
    .trim();
}

function limpiarArrayTags(tags) {
  if (!Array.isArray(tags)) return [];

  return tags
    .map((tag) => {
      return normalizarTexto(String(tag).replace(/^en:/, "").replace(/^es:/, ""));
    })
    .filter(Boolean);
}

function obtenerNombreProducto(producto) {
  return (
    producto.product_name_es ||
    producto.product_name ||
    producto.generic_name_es ||
    producto.generic_name ||
    ""
  ).trim();
}

function obtenerTextoCompletoProducto(producto) {
  return normalizarTexto(
    [
      producto.product_name_es,
      producto.product_name,
      producto.generic_name_es,
      producto.generic_name,
      producto.brands,
      producto.ingredients_text_es,
      producto.ingredients_text,
      producto.categories,
      Array.isArray(producto.stores_tags) ? producto.stores_tags.join(" ") : "",
      Array.isArray(producto.brands_tags) ? producto.brands_tags.join(" ") : "",
    ]
      .filter(Boolean)
      .join(" ")
  );
}

function coincideBusqueda(producto, query) {
  const texto = obtenerTextoCompletoProducto(producto);
  const palabras = normalizarTexto(query)
    .split(/\s+/)
    .filter((palabra) => palabra.length >= 2);

  if (palabras.length === 0) return false;

  return palabras.every((palabra) => texto.includes(palabra));
}

function detectarAlergenos(producto) {
  const allergensTags = limpiarArrayTags(producto.allergens_tags);
  const tracesTags = limpiarArrayTags(producto.traces_tags);

  const ingredientes = normalizarTexto(
    [
      producto.ingredients_text_es,
      producto.ingredients_text,
      producto.allergens,
      producto.traces,
    ]
      .filter(Boolean)
      .join(" ")
  );

  const detectados = new Set();

  for (const tag of allergensTags) {
    detectados.add(tag);
  }

  for (const tag of tracesTags) {
    detectados.add(`trazas_${tag}`);
  }

  for (const [alergia, sinonimos] of Object.entries(mapaAlergenos)) {
    const aparece = sinonimos.some((sinonimo) => {
      return ingredientes.includes(normalizarTexto(sinonimo));
    });

    if (aparece) {
      detectados.add(alergia);
    }
  }

  return Array.from(detectados);
}

function puntuarPorSupermercado(producto, supermercadoKey) {
  const config = supermercadosConfig[supermercadoKey];

  if (!config) return 0;

  const marcasProducto = limpiarArrayTags(producto.brands_tags);
  const tiendasProducto = limpiarArrayTags(producto.stores_tags);
  const brandsTexto = normalizarTexto(producto.brands);
  const textoCompleto = obtenerTextoCompletoProducto(producto);

  let score = 0;

  for (const marca of config.marcas) {
    const marcaNormalizada = normalizarTexto(marca);

    if (marcasProducto.includes(marcaNormalizada)) score += 8;
    if (brandsTexto.includes(marcaNormalizada)) score += 6;
    if (textoCompleto.includes(marcaNormalizada)) score += 2;
  }

  for (const tienda of config.tiendas) {
    const tiendaNormalizada = normalizarTexto(tienda);

    if (tiendasProducto.includes(tiendaNormalizada)) score += 10;
    if (textoCompleto.includes(tiendaNormalizada)) score += 2;
  }

  return score;
}

function mapearProducto(producto, supermercadoKey) {
  const nombreProducto = obtenerNombreProducto(producto);

  return {
    id: producto.code || producto._id || cryptoRandomId(),
    nombre: nombreProducto,
    marca: producto.brands || supermercadosConfig[supermercadoKey]?.nombre || supermercadoKey,
    imagen:
      producto.image_front_url ||
      producto.image_url ||
      producto.selected_images?.front?.display?.es ||
      producto.selected_images?.front?.display?.en ||
      "https://via.placeholder.com/300x300?text=SafeBite",
    alergenos_lista: detectarAlergenos(producto),
    ingredientes:
      producto.ingredients_text_es ||
      producto.ingredients_text ||
      "",
    super:
      supermercadosConfig[supermercadoKey]?.nombre ||
      supermercadoKey.charAt(0).toUpperCase() + supermercadoKey.slice(1),
    fuente: "openfoodfacts",
    scoreSupermercado: puntuarPorSupermercado(producto, supermercadoKey),
  };
}

function cryptoRandomId() {
  return `tmp-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function obtenerCache(key) {
  const entrada = cacheBusqueda.get(key);

  if (!entrada) return null;

  const caducado = Date.now() - entrada.timestamp > CACHE_TTL_MS;

  if (caducado) {
    cacheBusqueda.delete(key);
    return null;
  }

  return entrada.data;
}

function guardarCache(key, data) {
  cacheBusqueda.set(key, {
    timestamp: Date.now(),
    data,
  });
}

function obtenerFallback(query, supermercadoKey) {
  const q = normalizarTexto(query);

  let fallback = [];

  if (q.includes("leche")) {
    fallback = productosSimulados.leche;
  } else if (q.includes("pan")) {
    fallback = productosSimulados.pan;
  } else if (q.includes("tomate")) {
    fallback = productosSimulados.tomate;
  }

  const nombreSuper = supermercadosConfig[supermercadoKey]?.nombre || supermercadoKey;

  return fallback.filter((producto) => {
    return normalizarTexto(producto.super) === normalizarTexto(nombreSuper);
  });
}

function limitarDuplicados(productos) {
  const vistos = new Set();

  return productos.filter((producto) => {
    const key = normalizarTexto(`${producto.nombre}-${producto.marca}`);

    if (!producto.nombre || producto.nombre.toLowerCase() === "producto") {
      return false;
    }

    if (vistos.has(key)) {
      return false;
    }

    vistos.add(key);
    return true;
  });
}

async function buscarEnOpenFoodFacts(query) {
  const fields = [
    "code",
    "product_name",
    "product_name_es",
    "generic_name",
    "generic_name_es",
    "brands",
    "brands_tags",
    "stores",
    "stores_tags",
    "image_url",
    "image_front_url",
    "selected_images",
    "allergens",
    "allergens_tags",
    "traces",
    "traces_tags",
    "ingredients_text",
    "ingredients_text_es",
    "categories",
    "categories_tags",
    "countries_tags",
  ].join(",");

  const response = await axios.get(OPEN_FOOD_FACTS_URL, {
    params: {
      search_terms: query,
      search_simple: 1,
      action: "process",
      page_size: 80,
      fields,
      countries_tags_en: "Spain",
      sort_by: "unique_scans_n",
    },
    headers: {
      "User-Agent":
        "SafeBite/1.0 - Academic food allergy project - contact: safebite.local",
      Accept: "application/json",
    },
    timeout: 8000,
  });

  return Array.isArray(response.data?.products) ? response.data.products : [];
}

// --- RUTAS ---
app.get("/", (req, res) => {
  res.send("SafeBite API 🚀 Online");
});

// --- RECETAS ---
app.get("/api/recetas", async (req, res) => {
  try {
    const snapshot = await db.collection("recetas").get();

    const recetas = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    res.json(recetas);
  } catch (error) {
    console.error("Error obteniendo recetas:", error.message);
    res.status(500).json({
      error: "Error obteniendo recetas",
      detalle: error.message,
    });
  }
});

// --- PERFIL ---
app.get("/api/perfil/:uid", async (req, res) => {
  try {
    const { uid } = req.params;

    const doc = await db.collection("perfiles").doc(uid).get();

    if (!doc.exists) {
      return res.json({
        alergias: [],
        recetasSubidas: 0,
      });
    }

    res.json(doc.data());
  } catch (error) {
    console.error("Error obteniendo perfil:", error.message);
    res.status(500).json({
      error: "Error obteniendo perfil",
      detalle: error.message,
    });
  }
});

// --- CONTACTO ---
app.post("/api/contacto", async (req, res) => {
  try {
    await db.collection("mensajes_contacto").add({
      ...req.body,
      fecha: new Date().toISOString(),
    });

    res.json({
      success: true,
    });
  } catch (error) {
    console.error("Error guardando contacto:", error.message);
    res.status(500).json({
      error: "Error guardando contacto",
      detalle: error.message,
    });
  }
});

// --- SUPERMERCADOS ---
app.get("/api/supermercado/:nombre", async (req, res) => {
  const supermercadoKey = normalizarTexto(req.params.nombre);
  const query = String(req.query.q || "").trim();

  if (!query) {
    return res.status(400).json({
      error: "Falta query",
    });
  }

  const llaveCache = `${supermercadoKey}-${normalizarTexto(query)}`;
  const cache = obtenerCache(llaveCache);

  if (cache) {
    return res.json(cache);
  }

  try {
    console.log(`[SafeBite] Buscando "${query}" en ${supermercadoKey}`);

    const productosApi = await buscarEnOpenFoodFacts(query);

    console.log(`[SafeBite] Open Food Facts devuelve ${productosApi.length} productos brutos`);

    const productosQueCoinciden = productosApi.filter((producto) => {
      return coincideBusqueda(producto, query);
    });

    console.log(
      `[SafeBite] Tras filtrar por búsqueda quedan ${productosQueCoinciden.length}`
    );

    const productosMapeados = productosQueCoinciden
      .map((producto) => mapearProducto(producto, supermercadoKey))
      .filter((producto) => producto.nombre);

    /*
      Estrategia:
      1. Si hay productos que parecen claramente del supermercado seleccionado, priorizamos esos.
      2. Si no hay, NO devolvemos vacío; devolvemos resultados españoles relacionados.
         Esto evita el caso de "busco leche y no sale nada" por falta de tags correctos.
    */
    const productosDelSuper = productosMapeados.filter((producto) => {
      return producto.scoreSupermercado > 0;
    });

    const baseFinal =
      productosDelSuper.length > 0 ? productosDelSuper : productosMapeados;

    const productosFinales = limitarDuplicados(baseFinal)
      .sort((a, b) => {
        return b.scoreSupermercado - a.scoreSupermercado;
      })
      .slice(0, 30)
      .map(({ scoreSupermercado, ...producto }) => producto);

    if (productosFinales.length === 0) {
      console.warn(
        `[SafeBite] Sin resultados útiles para "${query}". Usando fallback.`
      );

      const fallback = obtenerFallback(query, supermercadoKey);
      guardarCache(llaveCache, fallback);

      return res.json(fallback);
    }

    guardarCache(llaveCache, productosFinales);

    return res.json(productosFinales);
  } catch (error) {
    console.warn(
      `[SafeBite] Error consultando Open Food Facts. Usando fallback: ${error.message}`
    );

    const fallback = obtenerFallback(query, supermercadoKey);
    guardarCache(llaveCache, fallback);

    return res.json(fallback);
  }
});

// --- ARRANQUE ---
app.listen(PORT, () => {
  console.log(`[SafeBite] Servidor activo en puerto ${PORT}`);
});