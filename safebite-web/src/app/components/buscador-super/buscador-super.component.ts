import {
  Component,
  computed,
  signal,
  inject,
  OnInit,
  OnDestroy,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { PerfilService } from '../../services/perfil.service';
import { Auth, user } from '@angular/fire/auth';
import { SupermercadoService } from '../../services/supermercado.service';
import { LanguageService } from '../../services/language.service';
import { Subscription, finalize } from 'rxjs';

@Component({
  selector: 'app-buscador-super',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './buscador-super.component.html',
  styleUrls: ['./buscador-super.component.css'],
})
export class BuscadorSuperComponent implements OnInit, OnDestroy {
  private superService = inject(SupermercadoService);
  private perfilService = inject(PerfilService);
  private auth = inject(Auth);
  public langService = inject(LanguageService);

  user$ = user(this.auth);
  uid: string | null = null;

  commonAllergens = [
    'Gluten',
    'Lactosa',
    'Huevo',
    'Frutos Secos',
    'Soja',
    'Marisco',
    'Pescado',
  ];

  supermarkets = [
    { id: 'mercadona', name: 'Mercadona', logo: '🛒', color: '#2ecc71' },
    { id: 'carrefour', name: 'Carrefour', logo: '🔵', color: '#3498db' },
    { id: 'lidl', name: 'Lidl', logo: '🟡', color: '#f1c40f' },
    { id: 'alcampo', name: 'Alcampo', logo: '🔴', color: '#e74c3c' },
  ];

  selectedSuperId = signal<string | null>(null);
  searchQuery = signal<string>('');
  userAllergens = signal<string[]>([]);
  productosDesdeAPI = signal<any[]>([]);
  loading = signal(false);
  errorBusqueda = signal<string | null>(null);

  newAllergenInput = '';

  private searchTimeout: ReturnType<typeof setTimeout> | null = null;
  private searchSubscription: Subscription | null = null;
  private perfilSubscription: Subscription | null = null;
  private authSubscription: Subscription | null = null;
  private searchRequestId = 0;

  ngOnInit() {
    this.authSubscription = this.user$.subscribe((u) => {
      if (u) {
        this.uid = u.uid;
        this.cargarAlergiasDesdeAPI();
      } else {
        this.uid = null;
        this.userAllergens.set([]);
      }
    });
  }

  ngOnDestroy() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
    }

    this.searchSubscription?.unsubscribe();
    this.perfilSubscription?.unsubscribe();
    this.authSubscription?.unsubscribe();
  }

  cargarAlergiasDesdeAPI() {
    if (!this.uid) return;

    this.perfilSubscription?.unsubscribe();

    this.perfilSubscription = this.perfilService.getPerfil(this.uid).subscribe({
      next: (res) => {
        this.userAllergens.set(res.alergias || []);
      },
      error: (err) => {
        console.error('Error cargando alergias:', err);
        this.userAllergens.set([]);
      },
    });
  }

  seleccionarSupermercado(superId: string) {
    this.selectedSuperId.set(superId);
    this.productosDesdeAPI.set([]);
    this.errorBusqueda.set(null);
    this.ejecutarBusqueda();
  }

  actualizarBusqueda(valor: string) {
    this.searchQuery.set(valor);
    this.ejecutarBusqueda();
  }

  ejecutarBusqueda() {
    const superId = this.selectedSuperId();
    const query = this.searchQuery().trim();

    this.errorBusqueda.set(null);

    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = null;
    }

    if (query.length < 3 || !superId) {
      this.searchSubscription?.unsubscribe();
      this.searchSubscription = null;
      this.loading.set(false);
      this.productosDesdeAPI.set([]);
      return;
    }

    this.searchTimeout = setTimeout(() => {
      const currentRequestId = ++this.searchRequestId;

      this.searchSubscription?.unsubscribe();

      this.loading.set(true);
      this.errorBusqueda.set(null);

      this.searchSubscription = this.superService
        .buscar(superId, query)
        .pipe(
          finalize(() => {
            if (currentRequestId === this.searchRequestId) {
              this.loading.set(false);
            }
          }),
        )
        .subscribe({
          next: (res) => {
            if (currentRequestId !== this.searchRequestId) {
              return;
            }

            console.log('Respuesta API supermercado:', res);
            this.productosDesdeAPI.set(Array.isArray(res) ? res : []);
          },
          error: (err) => {
            if (currentRequestId !== this.searchRequestId) {
              return;
            }

            console.error('Error en la búsqueda:', err);
            this.productosDesdeAPI.set([]);
            this.errorBusqueda.set(
              'No se pudieron cargar productos. Inténtalo de nuevo.',
            );
          },
        });
    }, 500);
  }

  limpiarBusqueda() {
    if (this.searchTimeout) {
      clearTimeout(this.searchTimeout);
      this.searchTimeout = null;
    }

    this.searchSubscription?.unsubscribe();
    this.searchSubscription = null;

    this.searchQuery.set('');
    this.productosDesdeAPI.set([]);
    this.loading.set(false);
    this.errorBusqueda.set(null);
  }

  filteredProducts = computed(() => {
    const rawProducts = this.productosDesdeAPI();
    const misAlergiasUsuario = this.userAllergens().map((a) =>
      String(a).toLowerCase().trim(),
    );

    const mapaSinonimos: any = {
      gluten: ['gluten', 'wheat', 'trigo'],
      lactosa: ['milk', 'lactose', 'leche', 'lactosa', 'dairy'],
      huevo: ['eggs', 'egg', 'huevo'],
      soja: ['soy', 'soja', 'soybeans'],
      marisco: ['shellfish', 'crustaceans', 'crustaceos', 'marisco'],
      pescado: ['fish', 'pescado', 'salmon', 'salmón', 'tuna', 'atun', 'atún'],
      'frutos secos': ['nuts', 'nut', 'almendras', 'avellanas', 'nueces'],
    };

    return rawProducts.map((p) => {
      let esPeligroso = false;

      const alergenosProducto = Array.isArray(p.alergenos_lista)
        ? p.alergenos_lista.map((alg: string) =>
            String(alg).toLowerCase().trim(),
          )
        : [];

      for (const alergia of misAlergiasUsuario) {
        const palabrasClave = mapaSinonimos[alergia] || [alergia];

        const match = alergenosProducto.some((algProducto: string) =>
          palabrasClave.includes(algProducto),
        );

        if (match) {
          esPeligroso = true;
          break;
        }
      }

      return {
        ...p,
        isSafe: !esPeligroso,
      };
    });
  });

  traducirAlergeno(nombre: string): string {
    const t = this.langService.t();

    const mapa: any = {
      Gluten: t.alg_gluten,
      Lactosa: t.alg_lactosa,
      Huevo: t.alg_huevo,
      'Frutos Secos': t.alg_frutos_secos,
      Soja: t.alg_soja,
      Marisco: t.alg_marisco,
      Pescado: t.alg_pescado,
    };

    return mapa[nombre] || nombre;
  }

  toggleCommonAllergen(allergen: string) {
    if (!this.uid) return;

    const nuevaLista = this.hasAllergen(allergen)
      ? this.userAllergens().filter((a) => a !== allergen)
      : [...this.userAllergens(), allergen];

    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe({
      next: () => {
        this.userAllergens.set(nuevaLista);
      },
      error: (err) => {
        console.error('Error guardando alergias:', err);
      },
    });
  }

  hasAllergen(allergen: string): boolean {
    return this.userAllergens().includes(allergen);
  }

  borrarAlergia(alergia: string) {
    if (!this.uid) return;

    const nuevaLista = this.userAllergens().filter((a) => a !== alergia);

    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe({
      next: () => {
        this.userAllergens.set(nuevaLista);
      },
      error: (err) => {
        console.error('Error borrando alergia:', err);
      },
    });
  }

  async agregarAlergia() {
    const alergia = this.newAllergenInput.trim();

    if (!alergia || !this.uid) return;

    const yaExiste = this.userAllergens().some(
      (a) => a.toLowerCase().trim() === alergia.toLowerCase(),
    );

    if (yaExiste) {
      this.newAllergenInput = '';
      return;
    }

    const nuevaLista = [...this.userAllergens(), alergia];

    this.perfilService.guardarAlergias(this.uid, nuevaLista).subscribe({
      next: () => {
        this.userAllergens.set(nuevaLista);
        this.newAllergenInput = '';
      },
      error: (err) => {
        console.error('Error agregando alergia:', err);
      },
    });
  }
}
