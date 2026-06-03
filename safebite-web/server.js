const express = require('express');
const cors = require('cors');
const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// Ruta para que el móvil sepa que el servidor vive
app.get('/api/status', (req, res) => {
    res.json({ status: "Online", message: "Backend de SafeBite funcionando" });
});

// Ruta para el ChatBot del móvil
app.post('/api/chat', (req, res) => {
    const { message } = req.body;
    res.json({ reply: "Hola desde el backend! Has dicho: " + message });
});

app.listen(PORT, () => {
    console.log(`Servidor ejecutándose en http://localhost:${PORT}`);
});