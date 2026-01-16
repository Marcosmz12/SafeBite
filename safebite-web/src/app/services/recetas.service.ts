import { Injectable, inject, Injector, runInInjectionContext } from '@angular/core'; // Añade Injector y runInInjectionContext
import { Firestore, collection, collectionData, doc, docData, query} from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';
import { where } from '@angular/fire/firestore/lite';

@Injectable({ providedIn: 'root' })
export class RecetasService {
  // Usar inject() es el estándar moderno que evita estos avisos
  private firestore = inject(Firestore);
  private injector = inject(Injector); // 1. Inyectamos el inyector

  // Haz lo mismo con getRecetas si te da aviso allí
  getRecetas(): Observable<Receta[]> {
    return runInInjectionContext(this.injector, () => {
      const recetasRef = collection(this.firestore, 'recetas');
      return collectionData(recetasRef, { idField: 'id' }) as Observable<Receta[]>;
    });
  }

  getRecetaById(id: string): Observable<Receta> {
    // 2. Envolvemos la lógica para que Firebase sepa que estamos en "zona segura"
    return runInInjectionContext(this.injector, () => {
      const recetaDocRef = doc(this.firestore, `recetas/${id}`);
      return docData(recetaDocRef, { idField: 'id' }) as Observable<Receta>;
    });
  }

  getRecetasPorAutor(userId: string): Observable<Receta[]> {
    const ref = collection(this.firestore, 'recetas');
    // Si usas query, recuerda que la referencia original debe venir de this.firestore
    const q = query(ref, where('autor_id', '==', userId));
    return collectionData(q, { idField: 'id' }) as Observable<Receta[]>;
  }
}