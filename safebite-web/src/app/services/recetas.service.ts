import { Injectable } from '@angular/core';
import { Firestore, collection, collectionData } from '@angular/fire/firestore';
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';

@Injectable({
  providedIn: 'root'
})
export class RecetasService {
  constructor(private firestore: Firestore) {}

  // Trae todas las recetas de la colección
  getRecetas(): Observable<Receta[]> {
    const recetasRef = collection(this.firestore, 'recetas');
    return collectionData(recetasRef, { idField: 'id' }) as Observable<Receta[]>;
  }
}