export interface Receta {
    etiquetas_sin: string[];
    id?: string;           // El ID que genera Firebase
    autor_id?: string;     // Quién subió la receta
    categoria: string;     // 'Entrantes', 'Postres', etc.
    imagenUrl: string;     // El link de Firebase Storage
    ingredientes: string[]; // Lista de ingredientes
    pasos: string[];        // Lista de pasos
    titulo: string;        // El nombre del plato
}