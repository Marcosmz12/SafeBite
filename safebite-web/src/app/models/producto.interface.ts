export interface Producto {
  id?: string;
  nombre: string;
  marca: string;
  precio: number;
  imagen: string;
  alergenos_lista: string[]; // Importante: debe ser un array de strings
  ingredientes?: string;
  supermercado: string;
}