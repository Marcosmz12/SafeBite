export interface Product {
    id: number;
    name: string;
    price: number;
    image: string;
    supermarketId: string;
    category: string;
  }
  
  export interface Supermarket {
    id: string;
    name: string;
    logo: string; // Emoji o URL de imagen
    color: string; // Clase de Tailwind para el color
  }