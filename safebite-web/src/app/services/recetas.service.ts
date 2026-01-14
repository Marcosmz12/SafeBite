import { Injectable, inject } from '@angular/core';
import {
  Firestore,
  collection,
  collectionData,
  doc,
  docData
} from '@angular/fire/firestore'; // <--- TODO debe venir de aquí
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';

@Injectable({
  providedIn: 'root'
})
export class RecetasService {
  private firestore = inject(Firestore); // Uso de inject() recomendado en Angular 18

  // Obtener todas las recetas
  getRecetas(): Observable<Receta[]> {
    const recetasRef = collection(this.firestore, 'recetas');
    return collectionData(recetasRef, { idField: 'id' }) as Observable<Receta[]>;
  }

  // Obtener una sola receta (Aquí estaba el error)
  getRecetaById(id: string): Observable<Receta> {
    // Creamos la referencia al documento usando el ID
    const recetaDocRef = doc(this.firestore, `recetas/${id}`);
    // Usamos docData para obtener el flujo de datos del documento
    return docData(recetaDocRef, { idField: 'id' }) as Observable<Receta>;
  }
}