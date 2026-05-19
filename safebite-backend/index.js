const express = require("express");
const cors = require("cors");
const admin = require("firebase-admin");
const axios = require("axios");
const path = require("path");
const productosLocales = require("./data/productos.json");

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
  soja: ["soy", "soja", "soybeans", "lecitina de soja", "soy lecithin"],
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
  marisco: [
    "shellfish",
    "crustaceans",
    "crustaceos",
    "marisco",
    "gamba",
    "gambas",
    "langostino",
    "langostinos",
    "cangrejo",
    "mejillon",
    "mejillón",
  ],
  sesamo: ["sesame", "sésamo", "sesamo", "tahini"],
  mostaza: ["mustard", "mostaza"],
};

// --- FALLBACK PEQUEÑO LOCAL ---
const productosSimulados = {
  leche: [
    {
      id: "fallback-leche-1",
      nombre: "Leche Entera",
      marca: "Hacendado",
      imagen:
        "https://images.openfoodfacts.org/images/products/843/187/625/5549/front_es.33.400.jpg",
      alergenos_lista: ["milk", "lactosa"],
      ingredientes: "Leche entera",
      super: "Mercadona",
      categoria: "lacteos",
      fuente: "fallback",
    },
    {
      id: "fallback-leche-2",
      nombre: "Leche sin Lactosa",
      marca: "Milbona",
      imagen:
        "https://images.openfoodfacts.org/images/products/20272023/front_es.6.400.jpg",
      alergenos_lista: ["milk"],
      ingredientes: "Leche sin lactosa",
      super: "Lidl",
      categoria: "lacteos",
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
      ingredientes: "Harina de trigo, agua, levadura, sal",
      super: "Mercadona",
      categoria: "pan",
      fuente: "fallback",
    },
    {
      id: "fallback-pan-2",
      nombre: "Pan Blanco",
      marca: "Lidl",
      imagen: "",
      alergenos_lista: ["gluten"],
      ingredientes: "Harina de trigo, agua, levadura, sal",
      super: "Lidl",
      categoria: "pan",
      fuente: "fallback",
    },
  ],
  tomate: [
    {
      id: "fallback-tomate-1",
      nombre: "Tomate Triturado",
      marca: "Hacendado",
      imagen: "",
      alergenos_lista: [],
      ingredientes: "Tomate",
      super: "Mercadona",
      categoria: "verduras",
      fuente: "fallback",
    },
    {
      id: "fallback-tomate-2",
      nombre: "Tomate Frito",
      marca: "Hacendado",
      imagen: "",
      alergenos_lista: [],
      ingredientes: "Tomate, aceite, sal, azúcar",
      super: "Mercadona",
      categoria: "salsas",
      fuente: "fallback",
    },
  ],
};

// --- MIDDLEWARES ---
app.use(
  cors({
    origin: true,
  }),
);

app.use(express.json());

// Servir imágenes estáticas desde el backend.
// Ejemplo: http://localhost:3000/productos/pan.png
app.use(
  "/productos",
  express.static(path.join(__dirname, "public", "productos")),
);

// --- HELPERS ---
function normalizarTexto(texto) {
  return String(texto || "")
    .toLowerCase()
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "")
    .replace(/ñ/g, "n")
    .trim();
}

function obtenerTokensTexto(texto) {
  return normalizarTexto(texto)
    .split(/[\s,.;:()_\-/]+/)
    .map((p) => p.trim())
    .filter((p) => p.length >= 2);
}

function limpiarArrayTags(tags) {
  if (!Array.isArray(tags)) return [];

  return tags
    .map((tag) => {
      return normalizarTexto(
        String(tag).replace(/^en:/, "").replace(/^es:/, "").replace(/^fr:/, ""),
      );
    })
    .filter(Boolean);
}

function obtenerNombreProducto(producto) {
  return String(
    producto.product_name_es ||
      producto.product_name ||
      producto.generic_name_es ||
      producto.generic_name ||
      "",
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
      producto.categories,
      Array.isArray(producto.categories_tags)
        ? producto.categories_tags.join(" ")
        : "",
      Array.isArray(producto.stores_tags) ? producto.stores_tags.join(" ") : "",
      Array.isArray(producto.brands_tags) ? producto.brands_tags.join(" ") : "",
    ]
      .filter(Boolean)
      .join(" "),
  );
}

function obtenerPalabrasQuery(query) {
  return obtenerTokensTexto(query);
}

function coincideBusquedaMapeado(producto, query, modo = "producto") {
  const palabrasQuery = obtenerPalabrasQuery(query);

  if (palabrasQuery.length === 0) return false;

  if (modo === "ingredientes") {
    const textoIngredientes = normalizarTexto(
      [
        producto.ingredientes,
        Array.isArray(producto.alergenos_lista)
          ? producto.alergenos_lista.join(" ")
          : "",
      ]
        .filter(Boolean)
        .join(" "),
    );

    return palabrasQuery.every((palabra) =>
      textoIngredientes.includes(palabra),
    );
  }

  const tokensProducto = obtenerTokensTexto(
    [producto.nombre, producto.marca, producto.categoria]
      .filter(Boolean)
      .join(" "),
  );

  return palabrasQuery.every((palabra) => {
    return tokensProducto.some((token) => token.startsWith(palabra));
  });
}

function detectarAlergenos(producto) {
  const detectados = new Set();

  const tags = [
    ...(producto.allergens_tags || []),
    ...(producto.traces_tags || []),
  ].map((tag) =>
    normalizarTexto(
      String(tag).replace(/^en:/, "").replace(/^es:/, "").replace(/^fr:/, ""),
    ),
  );

  tags.forEach((tag) => {
    if (tag) detectados.add(tag);
  });

  const ingredientes = normalizarTexto(
    `${producto.ingredients_text_es || ""} ${producto.ingredients_text || ""}`,
  );

  for (const [alergia, sinonimos] of Object.entries(mapaAlergenos)) {
    const encontrado = sinonimos.some((sinonimo) => {
      return ingredientes.includes(normalizarTexto(sinonimo));
    });

    if (encontrado) {
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
  const storesTexto = normalizarTexto(producto.stores);
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
    if (storesTexto.includes(tiendaNormalizada)) score += 8;
    if (textoCompleto.includes(tiendaNormalizada)) score += 2;
  }

  return score;
}

function cryptoRandomId() {
  return `tmp-${Date.now()}-${Math.random().toString(16).slice(2)}`;
}

function mapearProducto(producto, supermercadoKey) {
  const nombreProducto = obtenerNombreProducto(producto);

  const imagen =
    producto.image_front_url ||
    producto.image_url ||
    producto.selected_images?.front?.display?.es ||
    producto.selected_images?.front?.display?.en ||
    producto.selected_images?.front?.small?.es ||
    producto.selected_images?.front?.small?.en ||
    "";

  return {
    id: producto.code || producto._id || cryptoRandomId(),
    nombre: nombreProducto,
    marca:
      producto.brands ||
      supermercadosConfig[supermercadoKey]?.nombre ||
      supermercadoKey,
    imagen,
    alergenos_lista: detectarAlergenos(producto),
    ingredientes:
      producto.ingredients_text_es || producto.ingredients_text || "",
    super:
      supermercadosConfig[supermercadoKey]?.nombre ||
      supermercadoKey.charAt(0).toUpperCase() + supermercadoKey.slice(1),
    categoria: producto.categories || "",
    fuente: "openfoodfacts",
    scoreSupermercado: puntuarPorSupermercado(producto, supermercadoKey),
  };
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

function obtenerImagenLocalPorCategoria(categoria) {
  const categoriaNormalizada = normalizarTexto(categoria);

  const mapaCategorias = {
    pan: "/productos/pan.png",
    panes: "/productos/pan.png",

    lacteos: "/productos/lacteos.png",
    lacteos_y_derivados: "/productos/lacteos.png",
    quesos: "/productos/lacteos.png",
    leche: "/productos/lacteos.png",
    yogures: "/productos/lacteos.png",

    pizzas: "/productos/pizzas.png",
    pizza: "/productos/pizzas.png",

    bebidas: "/productos/bebidas.png",
    bebida: "/productos/bebidas.png",
    refrescos: "/productos/bebidas.png",
    zumos: "/productos/bebidas.png",
    infusiones: "/productos/bebidas.png",

    fruta: "/productos/fruta.png",
    frutas: "/productos/fruta.png",

    verduras: "/productos/verduras.png",
    verdura: "/productos/verduras.png",
    hortalizas: "/productos/verduras.png",

    conservas: "/productos/conservas.png",
    conserva: "/productos/conservas.png",

    congelados: "/productos/congelados.png",
    congelado: "/productos/congelados.png",

    dulces: "/productos/dulces.png",
    dulce: "/productos/dulces.png",
    chocolate: "/productos/dulces.png",
    chocolates: "/productos/dulces.png",
    galletas: "/productos/dulces.png",
    bolleria: "/productos/dulces.png",

    salsas: "/productos/salsas.png",
    salsa: "/productos/salsas.png",

    pasta: "/productos/pasta.png",
    pastas: "/productos/pasta.png",

    arroz: "/productos/arroz.png",
    arroces: "/productos/arroz.png",

    legumbres: "/productos/legumbres.png",
    legumbre: "/productos/legumbres.png",

    snacks: "/productos/snacks.png",
    snack: "/productos/snacks.png",
    aperitivos: "/productos/snacks.png",

    pescado: "/productos/pescado.png",
    pescados: "/productos/pescado.png",

    carne: "/productos/carne.png",
    carnes: "/productos/carne.png",

    huevos: "/productos/huevos.png",
    huevo: "/productos/huevos.png",

    frutos_secos: "/productos/frutos_secos.png",
    frutossecos: "/productos/frutos_secos.png",

    vegetal: "/productos/vegetal.png",
    vegano: "/productos/vegetal.png",
    vegetariano: "/productos/vegetal.png",

    aceites: "/productos/aceites.png",
    aceite: "/productos/aceites.png",

    caldos: "/productos/caldos.png",
    caldo: "/productos/caldos.png",
    sopas: "/productos/caldos.png",
    sopa: "/productos/caldos.png",

    helados: "/productos/helados.png",
    helado: "/productos/helados.png",

    charcuteria: "/productos/charcuteria.png",
    embutidos: "/productos/charcuteria.png",

    platos_preparados: "/productos/platos_preparados.png",
    platospreparados: "/productos/platos_preparados.png",
    preparados: "/productos/platos_preparados.png",
  };

  return mapaCategorias[categoriaNormalizada] || "/productos/default.png";
}

function esImagenNoValida(imagen) {
  const img = String(imagen || "")
    .trim()
    .toLowerCase();

  return (
    !img ||
    img.includes("via.placeholder.com") ||
    img.includes("placeholder") ||
    img === "null" ||
    img === "undefined"
  );
}

function convertirRutaImagenAUrlAbsoluta(imagen, req) {
  if (!req) return imagen;

  if (!imagen.startsWith("/")) {
    return imagen;
  }

  return `${req.protocol}://${req.get("host")}${imagen}`;
}

function normalizarProductoSalida(
  producto,
  supermercadoKey,
  fuenteDefecto = "local",
  req = null,
) {
  const categoria = producto.categoria || "";
  let imagen = producto.imagen || "";

  if (esImagenNoValida(imagen)) {
    imagen = obtenerImagenLocalPorCategoria(categoria);
  }

  imagen = convertirRutaImagenAUrlAbsoluta(imagen, req);

  return {
    id: producto.id || cryptoRandomId(),
    nombre: producto.nombre || "",
    marca: producto.marca || "",
    imagen,
    alergenos_lista: Array.isArray(producto.alergenos_lista)
      ? producto.alergenos_lista
      : [],
    ingredientes: producto.ingredientes || "",
    super: producto.super || supermercadosConfig[supermercadoKey]?.nombre || "",
    categoria,
    fuente: producto.fuente || fuenteDefecto,
  };
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

  const nombreSuper =
    supermercadosConfig[supermercadoKey]?.nombre || supermercadoKey;

  return fallback
    .filter((producto) => {
      return normalizarTexto(producto.super) === normalizarTexto(nombreSuper);
    })
    .map((producto) => ({
      ...producto,
      alergenos_lista: Array.isArray(producto.alergenos_lista)
        ? producto.alergenos_lista
        : [],
      ingredientes: producto.ingredientes || "",
      scoreSupermercado: 999,
    }));
}

function buscarEnProductosLocales(query, supermercadoKey, modo = "producto") {
  const nombreSuper =
    supermercadosConfig[supermercadoKey]?.nombre || supermercadoKey;

  const palabrasQuery = obtenerPalabrasQuery(query);

  if (palabrasQuery.length === 0) return [];

  return productosLocales
    .filter((producto) => {
      return normalizarTexto(producto.super) === normalizarTexto(nombreSuper);
    })
    .filter((producto) => {
      if (modo === "ingredientes") {
        const textoIngredientes = normalizarTexto(
          [
            producto.ingredientes,
            Array.isArray(producto.alergenos_lista)
              ? producto.alergenos_lista.join(" ")
              : "",
          ]
            .filter(Boolean)
            .join(" "),
        );

        return palabrasQuery.every((palabra) =>
          textoIngredientes.includes(palabra),
        );
      }

      const tokensProducto = obtenerTokensTexto(
        [producto.nombre, producto.marca, producto.categoria]
          .filter(Boolean)
          .join(" "),
      );

      return palabrasQuery.every((palabra) => {
        return tokensProducto.some((token) => token.startsWith(palabra));
      });
    })
    .map((producto) => ({
      ...producto,
      alergenos_lista: Array.isArray(producto.alergenos_lista)
        ? producto.alergenos_lista
        : [],
      ingredientes: producto.ingredientes || "",
      scoreSupermercado: 1000,
      fuente: producto.fuente || "local",
    }));
}

function limitarDuplicados(productos) {
  const vistos = new Set();

  return productos.filter((producto) => {
    if (!producto) return false;

    const nombre = String(producto.nombre || "").trim();
    const marca = String(producto.marca || "").trim();
    const superNombre = String(producto.super || "").trim();

    if (!nombre || nombre.toLowerCase() === "producto") {
      return false;
    }

    const key = normalizarTexto(`${superNombre}-${nombre}-${marca}`);

    if (vistos.has(key)) {
      return false;
    }

    vistos.add(key);
    return true;
  });
}

function prepararProductosFinales(
  productos,
  supermercadoKey,
  limite = 30,
  req = null,
) {
  return limitarDuplicados(productos)
    .sort((a, b) => {
      return (b.scoreSupermercado || 0) - (a.scoreSupermercado || 0);
    })
    .slice(0, limite)
    .map(({ scoreSupermercado, ...producto }) => {
      return normalizarProductoSalida(
        producto,
        supermercadoKey,
        producto.fuente,
        req,
      );
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
    timeout: 15000,
  });

  return Array.isArray(response.data?.products) ? response.data.products : [];
}

// --- RUTAS ---
app.get("/", (req, res) => {
  res.send("SafeBite API 🚀 Online");
});

// Ruta rápida para comprobar que las imágenes están servidas.
app.get("/api/health/images", (req, res) => {
  return res.json({
    ok: true,
    ejemploDefault: `${req.protocol}://${req.get("host")}/productos/default.png`,
    ejemploPan: `${req.protocol}://${req.get("host")}/productos/pan.png`,
  });
});

// --- RECETAS ---
app.get("/api/recetas", async (req, res) => {
  try {
    const snapshot = await db.collection("recetas").get();

    const recetas = snapshot.docs.map((doc) => ({
      id: doc.id,
      ...doc.data(),
    }));

    return res.json(recetas);
  } catch (error) {
    console.error("Error obteniendo recetas:", error.message);

    return res.status(500).json({
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

    return res.json(doc.data());
  } catch (error) {
    console.error("Error obteniendo perfil:", error.message);

    return res.status(500).json({
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

    return res.json({
      success: true,
    });
  } catch (error) {
    console.error("Error guardando contacto:", error.message);

    return res.status(500).json({
      error: "Error guardando contacto",
      detalle: error.message,
    });
  }
});

// --- SUPERMERCADOS ---
app.get("/api/supermercado/:nombre", async (req, res) => {
  const supermercadoKey = normalizarTexto(req.params.nombre);
  const query = String(req.query.q || "").trim();

  const modo = normalizarTexto(req.query.modo || "producto");
  const modoBusqueda = modo === "ingredientes" ? "ingredientes" : "producto";

  if (!query) {
    return res.status(400).json({
      error: "Falta query",
    });
  }

  if (!supermercadosConfig[supermercadoKey]) {
    return res.status(400).json({
      error: "Supermercado no soportado",
      supermercadosDisponibles: Object.keys(supermercadosConfig),
    });
  }

  const llaveCache = `${supermercadoKey}-${modoBusqueda}-${normalizarTexto(
    query,
  )}`;

  const cache = obtenerCache(llaveCache);

  if (cache) {
    console.log(`[SafeBite] Cache hit: ${llaveCache}`);
    return res.json(cache);
  }

  try {
    console.log(
      `[SafeBite] Buscando "${query}" en Open Food Facts para ${supermercadoKey}. Modo: ${modoBusqueda}`,
    );

    const productosApi = await buscarEnOpenFoodFacts(query);

    console.log(
      `[SafeBite] Open Food Facts devuelve ${productosApi.length} productos brutos`,
    );

    const productosMapeados = productosApi
      .map((producto) => mapearProducto(producto, supermercadoKey))
      .filter((producto) => {
        return (
          producto.nombre &&
          producto.nombre.trim().length > 2 &&
          producto.nombre.toLowerCase() !== "producto"
        );
      });

    const productosQueCoinciden = productosMapeados.filter((producto) => {
      return coincideBusquedaMapeado(producto, query, modoBusqueda);
    });

    const productosDelSuper = productosQueCoinciden.filter((producto) => {
      return producto.scoreSupermercado > 0;
    });

    const baseFinal =
      productosDelSuper.length > 0 ? productosDelSuper : productosQueCoinciden;

    const productosFinales = prepararProductosFinales(
      baseFinal,
      supermercadoKey,
      30,
      req,
    );

    console.log(
      `[SafeBite] Productos Open Food Facts finales para "${query}" en ${supermercadoKey}: ${productosFinales.length}`,
    );

    if (productosFinales.length > 0) {
      guardarCache(llaveCache, productosFinales);
      return res.json(productosFinales);
    }

    console.warn(
      `[SafeBite] Open Food Facts respondió, pero sin resultados útiles. Buscando en productos.json`,
    );

    const productosLocalJson = buscarEnProductosLocales(
      query,
      supermercadoKey,
      modoBusqueda,
    );

    const productosLocalesFinales = prepararProductosFinales(
      productosLocalJson,
      supermercadoKey,
      30,
      req,
    );

    if (productosLocalesFinales.length > 0) {
      console.log(
        `[SafeBite] Productos locales usados por falta de resultados útiles: ${productosLocalesFinales.length}`,
      );

      guardarCache(llaveCache, productosLocalesFinales);
      return res.json(productosLocalesFinales);
    }

    const fallback = obtenerFallback(query, supermercadoKey);
    const fallbackFinal = prepararProductosFinales(
      fallback,
      supermercadoKey,
      30,
      req,
    );

    console.log(
      `[SafeBite] Fallback pequeño usado por falta de resultados útiles: ${fallbackFinal.length}`,
    );

    guardarCache(llaveCache, fallbackFinal);
    return res.json(fallbackFinal);
  } catch (error) {
    const status = error.response?.status || "sin status";

    console.warn(
      `[SafeBite] Error consultando Open Food Facts. Status: ${status}. Usando productos.json local. Detalle: ${error.message}`,
    );

    const productosLocalJson = buscarEnProductosLocales(
      query,
      supermercadoKey,
      modoBusqueda,
    );

    const productosLocalesFinales = prepararProductosFinales(
      productosLocalJson,
      supermercadoKey,
      30,
      req,
    );

    if (productosLocalesFinales.length > 0) {
      console.log(
        `[SafeBite] Productos locales usados por fallo externo: ${productosLocalesFinales.length}`,
      );

      guardarCache(llaveCache, productosLocalesFinales);
      return res.json(productosLocalesFinales);
    }

    const fallback = obtenerFallback(query, supermercadoKey);
    const fallbackFinal = prepararProductosFinales(
      fallback,
      supermercadoKey,
      30,
      req,
    );

    console.log(
      `[SafeBite] Fallback pequeño usado por fallo externo: ${fallbackFinal.length}`,
    );

    guardarCache(llaveCache, fallbackFinal);
    return res.json(fallbackFinal);
  }
});

// --- ARRANQUE ---
app.listen(PORT, () => {
  console.log(`[SafeBite] Servidor activo en puerto ${PORT}`);
});
