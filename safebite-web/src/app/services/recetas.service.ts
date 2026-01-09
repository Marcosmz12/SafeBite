import { Injectable, inject } from '@angular/core';
import { Firestore, collection, collectionData } from '@angular/fire/firestore';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecetasService {
  // Inyectamos la base de datos Firestore
  private firestore = inject(Firestore);

  // Método para obtener todas las recetas en tiempo real
  obtenerRecetas(): Observable<any[]> {
    const recetasRef = collection(this.firestore, 'recetas');
    return collectionData(recetasRef, { idField: 'id' });
  }
}