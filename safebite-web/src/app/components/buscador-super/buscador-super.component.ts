import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilService } from '../../services/perfil.service'; 
import { Auth, user } from '@angular/fire/auth';
import { SupermercadoService } from '../../services/supermercado.service';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-buscador-super',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './buscador-super.component.html',
  styleUrls: ['./buscador-super.component.css']
})
export class BuscadorSuperComponent implements OnInit {
  private superService = inject(SupermercadoService);
  private perfilService = inject(PerfilService);
  private auth = inject(Auth);
  public langService = inject(LanguageService);

  user$ = user(this.auth);
  uid: string | null = null;

  // --- NUEVO: Alérgenos básicos para selección rápida ---
  commonAllergens = ['Gluten', 'Lactosa', 'Huevo', 'Frutos Secos', 'Soja', 'Marisco', 'Pescado'];

  supermarkets = [
    { id: 'mercadona', name: 'Mercadona', logo: '🛒', color: '#2ecc71' },
    { id: 'carrefour', name: 'Carrefour', logo: '🔵', color: '#3498db' },
    { id: 'lidl', name: 'Lidl', logo: '🟡', color: '#f1c40f' },
    { id: 'alcampo', name: 'Alcampo', logo: '🔴', color: '#e74c3c' }
  ];

  selectedSuperId = signal<string | null>(null);
  searchQuery = signal<string>('');
  userAllergens = signal<string[]>([]);
  newAllergenInput = '';
  productosDesdeAPI = signal<any[]>([]);
  loading = signal(false);

  private searchTimeout: any;

  ngOnInit() {
    this.user$.subscribe(u => {
      if (u) {
        this.uid = u.uid;
        this.cargarAlergiasDesdeAPI();
      }
    });
  }

  cargarAlergiasDesdeAPI() {
    if (!this.uid) return;
    this.perfilService.getPerfil(this.uid).subscribe(res => {
      this.userAllergens.set(res.alergias || []);
    });
  }

  // --- NUEVA FUNCIÓN: Llama a tu Backend de Node ---
  ejecutarBusqueda() {
    const superId = this.selectedSuperId();
    const query = this.searchQuery();

    // Si el usuario vuelve a teclear, cancelamos el envío anterior
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    // Si borra el texto o hay menos de 3 letras, limpiamos resultados y no buscamos
    if (!superId || query.length < 3) {
      this.productosDesdeAPI.set([]);
      return;
    }

    // Esperamos 400ms a que el usuario termine de escribir
    this.searchTimeout = setTimeout(() => {
      this.loading.set(true);
      
      this.superService.buscar(superId, query).subscribe({
        next: (res) => {
          this.productosDesdeAPI.set(res);
          this.loading.set(false);
        },
        error: (err) => {
          console.error("Error en la búsqueda:", err);
          this.loading.set(false);
        }
      });
    }, 400); 
  }

  // --- LÓGICA DE SEGURIDAD MEJORADA PARA DATOS REALES ---
  filteredProducts = computed(() => {
    const rawProducts = this.productosDesdeAPI();
    const myAllergens = this.userAllergens().map(a => a.toLowerCase().trim());

    return rawProducts.map(p => {
      // Comparamos los alérgenos que trae Open Food Facts con los del usuario
      const tieneAlergenosPeligrosos = p.alergenos_lista.some((a: string) => 
        myAllergens.includes(a.toLowerCase())
      );

      return {
        ...p,
        isSafe: !tieneAlergenosPeligrosos
      };
    });
  });

  // (Tus funciones de traducirAlergeno, toggleCommonAllergen, etc., se quedan igual debajo)
  traducirAlergeno(nombre: string): string {
    const t = this.langService.t();
    const mapa: any = {
      'Gluten': t.alg_gluten, 'Lactosa': t.alg_lactosa, 'Huevo': t.alg_huevo,
      'Frutos Secos': t.alg_frutos_secos, 'Soja': t.alg_soja, 'Marisco': t.alg_marisco, 'Pescado': t.alg_pescado
    };
    return mapa[nombre] || nombre;
  }

  toggleCommonAllergen(allergen: string) {
    if (!this.uid) return;
    let nuevaLista = this.hasAllergen(allergen) 
      ? this.userAllergens().filter(a => a !== allergen)
      : [...this.userAllergens(), allergen];

    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe(() => {
      this.userAllergens.set(nuevaLista);
    });
  }

  hasAllergen(allergen: string): boolean { return this.userAllergens().includes(allergen); }

  borrarAlergia(alergia: string) {
    if (!this.uid) return;
    const nuevaLista = this.userAllergens().filter(a => a !== alergia);
    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe(() => {
      this.userAllergens.set(nuevaLista);
    });
  }

  async agregarAlergia() {
    if (!this.newAllergenInput.trim() || !this.uid) return;
    const nuevaLista = [...this.userAllergens(), this.newAllergenInput.trim()];
    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe(() => {
      this.userAllergens.set(nuevaLista);
      this.newAllergenInput = '';
    });
  }
}