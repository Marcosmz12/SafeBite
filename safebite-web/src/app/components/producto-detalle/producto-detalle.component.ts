import { Component, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Producto } from '../../models/producto.interface'; // Ajusta la ruta si es necesario

@Component({
  selector: 'app-producto-detalle',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './producto-detalle.component.html',
  styleUrls: ['./producto-detalle.component.css']
})
export class ProductoDetalleComponent {
  // Estos son los datos que le pasaremos desde fuera
  @Input({ required: true }) producto!: Producto;
  @Input() alergiasUsuario: string[] = [];

  // Calculamos si el producto es seguro "al vuelo"
  esSeguro = computed(() => {
    if (!this.producto.alergenos_lista || this.alergiasUsuario.length === 0) return true;

    const misAlergias = this.alergiasUsuario.map(a => a.toLowerCase().trim());
    
    // Comprobamos si algún alérgeno del producto coincide con las alergias del usuario
    const hayConflicto = this.producto.alergenos_lista.some(algProd => 
      misAlergias.includes(algProd.toLowerCase().trim())
    );

    return !hayConflicto;
  });

  // Función para poner el color según el súper
  getColorSuper(superId: string): string {
    const colores: any = {
      mercadona: '#2ecc71',
      carrefour: '#3498db',
      lidl: '#f1c40f',
      alcampo: '#e74c3c'
    };
    return colores[superId.toLowerCase()] || '#95a5a6';
  }
}