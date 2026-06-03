SafeBite - Imágenes de productos por categoría

1) Copia todos los PNG en:
   src/assets/img/productos/

2) En el HTML de la card de producto usa:

<img
  [src]="getImagenProducto(producto)"
  [alt]="producto.nombre || 'Producto'"
  (error)="onImgError($event)"
>

3) En el componente TypeScript añade:

getImagenProducto(producto: any): string {
  const imagen = String(producto.imagen || '');

  if (
    imagen &&
    !imagen.includes('via.placeholder.com') &&
    !imagen.includes('placeholder')
  ) {
    return imagen;
  }

  const categoria = String(producto.categoria || '').toLowerCase().trim();

  const mapaCategorias: Record<string, string> = {
    pan: 'assets/img/productos/pan.png',
    lacteos: 'assets/img/productos/lacteos.png',
    quesos: 'assets/img/productos/lacteos.png',
    pizzas: 'assets/img/productos/pizzas.png',
    bebidas: 'assets/img/productos/bebidas.png',
    fruta: 'assets/img/productos/fruta.png',
    verduras: 'assets/img/productos/verduras.png',
    conservas: 'assets/img/productos/conservas.png',
    congelados: 'assets/img/productos/congelados.png',
    dulces: 'assets/img/productos/dulces.png',
    chocolate: 'assets/img/productos/dulces.png',
    galletas: 'assets/img/productos/dulces.png',
    salsas: 'assets/img/productos/salsas.png',
    pasta: 'assets/img/productos/pasta.png',
    arroz: 'assets/img/productos/arroz.png',
    legumbres: 'assets/img/productos/legumbres.png',
    snacks: 'assets/img/productos/snacks.png',
    pescado: 'assets/img/productos/pescado.png',
    carne: 'assets/img/productos/carne.png',
    huevos: 'assets/img/productos/huevos.png',
    frutos_secos: 'assets/img/productos/frutos_secos.png',
    vegetal: 'assets/img/productos/vegetal.png',
    aceites: 'assets/img/productos/aceites.png',
    caldos: 'assets/img/productos/caldos.png',
    infusiones: 'assets/img/productos/bebidas.png',
    helados: 'assets/img/productos/helados.png',
    charcuteria: 'assets/img/productos/charcuteria.png',
    platos_preparados: 'assets/img/productos/platos_preparados.png',
  };

  return mapaCategorias[categoria] || 'assets/img/productos/default.png';
}

onImgError(event: Event) {
  const img = event.target as HTMLImageElement;
  img.src = 'assets/img/productos/default.png';
}
