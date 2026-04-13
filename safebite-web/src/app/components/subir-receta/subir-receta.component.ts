import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RecetasService } from '../../services/recetas.service';
import { Auth, user } from '@angular/fire/auth';
import { Router } from '@angular/router';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-subir-receta',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './subir-receta.component.html',
  styleUrls: ['./subir-receta.component.css']
})
export class SubirRecetaComponent {
  private recetasService = inject(RecetasService);
  private auth = inject(Auth);
  private router = inject(Router);
  public langService = inject(LanguageService);

  user$ = user(this.auth);
  selectedFile: File | null = null;
  imagePreview = signal<string | null>(null);
  isUploading = signal(false);

  getTraduccionTag(tag: string): string {
  const t = this.langService.t();
  const mapa: any = {
    'gluten': t.tag_gluten,
    'lactosa': t.tag_lactosa,
    'frutos secos': t.tag_frutos,
    'huevo': t.tag_huevo,
    'pescado': t.tag_pescado,
    'soja': t.tag_soja
  };
  return mapa[tag.toLowerCase()] || tag;
}

  getTraduccionCategoria(cat: string): string {
  const t = this.langService.t();
  const mapa: any = {
    'Entrantes': t.cat_entrantes,
    'Platos Principales': t.cat_principales,
    'Postres': t.cat_postres
  };
  return mapa[cat] || cat;
}

  // --- NUEVAS LISTAS DINÁMICAS ---
  ingredientesList = signal<string[]>([]);
  pasosList = signal<string[]>([]);

  // Variables temporales para los inputs
  nuevoIngrediente = '';
  nuevoPaso = '';

  categorias = ['Entrantes', 'Platos Principales', 'Postres'];
  alergenosDisponibles = ['gluten', 'lactosa', 'frutos secos', 'huevo', 'pescado', 'soja'];

  nuevaReceta = {
    titulo: '',
    categoria: 'Platos Principales',
    etiquetas_sin: [] as string[]
  };

  // Métodos para gestionar Ingredientes
  addIngrediente() {
    if (this.nuevoIngrediente.trim()) {
      this.ingredientesList.update(list => [...list, this.nuevoIngrediente.trim()]);
      this.nuevoIngrediente = '';
    }
  }

  removeIngrediente(index: number) {
    this.ingredientesList.update(list => list.filter((_, i) => i !== index));
  }

  // Métodos para gestionar Pasos
  addPaso() {
    if (this.nuevoPaso.trim()) {
      this.pasosList.update(list => [...list, this.nuevoPaso.trim()]);
      this.nuevoPaso = '';
    }
  }

  removePaso(index: number) {
    this.pasosList.update(list => list.filter((_, i) => i !== index));
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
      const reader = new FileReader();
      reader.onload = () => this.imagePreview.set(reader.result as string);
      reader.readAsDataURL(file);
    }
  }

  publicar() {
    if (!this.selectedFile) return alert('Por favor, selecciona una foto');
    if (this.ingredientesList().length === 0) return alert('Añade al menos un ingrediente');

    this.isUploading.set(true);

    this.recetasService.subirImagenCloudinary(this.selectedFile).subscribe({
      next: (cloudinaryRes) => {
        const urlFinal = cloudinaryRes.secure_url;

        this.user$.subscribe(u => {
          // PAYLOAD EXACTO PARA TU FIREBASE
          const payload = {
            titulo: this.nuevaReceta.titulo,
            autor_id: u?.uid,
            categoria: this.nuevaReceta.categoria,
            imagen_url: urlFinal,
            ingredientes: this.ingredientesList(), // Ya es un Array
            pasos: this.pasosList(),               // Ya es un Array
            etiquetas_sin: this.nuevaReceta.etiquetas_sin
          };

          this.recetasService.crearReceta(payload).subscribe({
            next: () => {
              this.isUploading.set(false);
              alert('¡Receta publicada!');
              this.router.navigate(['/recetas']);
            },
            error: (err) => {
              console.error(err);
              this.isUploading.set(false);
            }
          });
        });
      },
      error: (err) => {
        console.error(err);
        this.isUploading.set(false);
      }
    });
  }

  toggleEtiqueta(a: string) {
    if (this.nuevaReceta.etiquetas_sin.includes(a)) {
      this.nuevaReceta.etiquetas_sin = this.nuevaReceta.etiquetas_sin.filter(item => item !== a);
    } else {
      this.nuevaReceta.etiquetas_sin.push(a);
    }
  }
}