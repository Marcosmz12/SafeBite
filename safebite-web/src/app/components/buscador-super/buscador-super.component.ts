import { Component, computed, signal, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilService } from '../../services/perfil.service'; 
import { Auth, user } from '@angular/fire/auth';
import { LanguageService } from '../../services/language.service';

@Component({
  selector: 'app-buscador-super',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './buscador-super.component.html',
  styleUrls: ['./buscador-super.component.css']
})
export class BuscadorSuperComponent implements OnInit {
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

  allProducts = signal<any[]>([
    { id: 1, name: 'Pan de Molde', brand: 'Hacendado', image: 'https://placehold.co/200?text=Pan', supermarketId: 'mercadona', allergens: ['Gluten'] },
    { id: 2, name: 'Yogur Natural', brand: 'Hacendado', image: 'https://placehold.co/200?text=Yogur', supermarketId: 'mercadona', allergens: ['Lactosa'] },
    { id: 3, name: 'Galletas María', brand: 'Cuétara', image: 'https://placehold.co/200?text=Galletas', supermarketId: 'carrefour', allergens: ['Gluten', 'Huevo'] },
    { id: 4, name: 'Arroz Blanco', brand: 'Sabroz', image: 'https://placehold.co/200?text=Arroz', supermarketId: 'lidl', allergens: [] },
  ]);

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

  filteredProducts = computed(() => {
    const superId = this.selectedSuperId();
    const query = this.searchQuery().toLowerCase();
    const myAllergens = this.userAllergens();

    return this.allProducts().filter(p => {
      const matchSuper = p.supermarketId === superId;
      const matchSearch = p.name.toLowerCase().includes(query);
      p.isSafe = !p.allergens.some((a: string) => myAllergens.includes(a));
      return matchSuper && matchSearch;
    });
  });

  // --- NUEVO: Lógica para los botones rápidos ---
  hasAllergen(allergen: string): boolean {
    return this.userAllergens().includes(allergen);
  }

  traducirAlergeno(nombre: string): string {
    const t = this.langService.t();
    
    const mapa: any = {
      'Gluten': t.alg_gluten,
      'Lactosa': t.alg_lactosa,
      'Huevo': t.alg_huevo,
      'Frutos Secos': t.alg_frutos_secos,
      'Soja': t.alg_soja,
      'Marisco': t.alg_marisco,
      'Pescado': t.alg_pescado
    };

    return mapa[nombre] || nombre;
  }

  toggleCommonAllergen(allergen: string) {
    if (!this.uid) return;
    let nuevaLista: string[];
    
    if (this.hasAllergen(allergen)) {
      nuevaLista = this.userAllergens().filter(a => a !== allergen);
    } else {
      nuevaLista = [...this.userAllergens(), allergen];
    }

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

  borrarAlergia(alergia: string) {
    if (!this.uid) return;
    const nuevaLista = this.userAllergens().filter(a => a !== alergia);
    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe(() => {
      this.userAllergens.set(nuevaLista);
    });
  }
}