import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilService } from '../../services/perfil.service';

@Component({
  selector: 'app-alergias-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './alergias-manager.component.html',
  styleUrl: './alergias-manager.component.css'
})
export class AlergiasManagerComponent implements OnInit {
  @Input() uid!: string; // Recibe el ID del usuario del padre
  private perfilService = inject(PerfilService);

  // Alergias sugeridas (las básicas)
  sugerencias = ['Gluten', 'Lactosa', 'Frutos Secos', 'Huevo', 'Marisco', 'Pescado'];
  
  // Lo que el usuario tiene guardado
  misAlergias: string[] = [];
  nuevaAlergia: string = '';

  ngOnInit() {
    if (this.uid) {
      this.perfilService.getPerfil(this.uid).subscribe(perfil => {
        if (perfil && perfil.alergias) {
          this.misAlergias = perfil.alergias;
        }
      });
    }
  }

  // Añadir/Quitar sugerencias
  toggleSugerencia(item: string) {
    if (this.misAlergias.includes(item)) {
      this.misAlergias = this.misAlergias.filter(a => a !== item);
    } else {
      this.misAlergias.push(item);
    }
    this.guardar();
  }

  // Añadir una personalizada
  addPersonalizada() {
    const valor = this.nuevaAlergia.trim();
    if (valor && !this.misAlergias.includes(valor)) {
      this.misAlergias.push(valor);
      this.nuevaAlergia = ''; // Limpiar input
      this.guardar();
    }
  }

  // Quitar cualquier etiqueta
  eliminarAlergia(item: string) {
    this.misAlergias = this.misAlergias.filter(a => a !== item);
    this.guardar();
  }

  private guardar() {
    this.perfilService.guardarAlergias(this.uid, this.misAlergias);
  }
}