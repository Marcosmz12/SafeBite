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
  @Input() uid!: string;
  private perfilService = inject(PerfilService);

  sugerencias = ['Gluten', 'Lactosa', 'Frutos Secos', 'Huevo'];
  misAlergias: string[] = [];
  nuevaAlergia: string = '';

  ngOnInit() {
    if (this.uid) {
      // LLAMADA AL BACKEND (Node.js)
      this.perfilService.getPerfil(this.uid).subscribe(perfil => {
        if (perfil && perfil.alergias) {
          this.misAlergias = perfil.alergias;
        }
      });
    }
  }

  toggleSugerencia(item: string) {
    if (this.misAlergias.includes(item)) {
      this.misAlergias = this.misAlergias.filter(a => a !== item);
    } else {
      this.misAlergias.push(item);
    }
    this.guardar();
  }

  addPersonalizada() {
    if (this.nuevaAlergia.trim()) {
      this.misAlergias.push(this.nuevaAlergia.trim());
      this.nuevaAlergia = '';
      this.guardar();
    }
  }

  eliminarAlergia(item: string) {
    this.misAlergias = this.misAlergias.filter(a => a !== item);
    this.guardar();
  }

  private guardar() {
    // LLAMADA AL BACKEND (POST a Node.js)
    this.perfilService.guardarAlergias(this.uid, this.misAlergias).subscribe();
  }
}