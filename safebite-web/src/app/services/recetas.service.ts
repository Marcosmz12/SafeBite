import { Injectable, inject } from '@angular/core';
import { Firestore, collection, collectionData, doc, docData } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';


@Injectable({ providedIn: 'root' })
export class RecetasService {
  private firestore = inject(Firestore);

  getRecetas(): Observable<Receta[]> {
    const ref = collection(this.firestore, 'recetas');
    return collectionData(ref, { idField: 'id' }) as Observable<Receta[]>;
  }

  getRecetaById(id: string): Observable<Receta> {
    const ref = doc(this.firestore, `recetas/${id}`);
    return docData(ref, { idField: 'id' }) as Observable<Receta>;
  }
}