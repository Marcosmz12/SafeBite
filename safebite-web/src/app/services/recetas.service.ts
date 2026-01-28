import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http'; // Importamos el cliente HTTP
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';

@Injectable({ providedIn: 'root' })
export class RecetasService {
  private http = inject(HttpClient);
  
  // La dirección de tu servidor Node.js
  private apiUrl = 'https://safebite-7dgr.onrender.com/';

  // 1. Obtener todas las recetas
  getRecetas(): Observable<Receta[]> {
    return this.http.get<Receta[]>(this.apiUrl);
  }

  // 2. Obtener una receta por su ID
  getRecetaById(id: string): Observable<Receta> {
    return this.http.get<Receta>(`${this.apiUrl}/${id}`);
  }

  // 3. Obtener recetas de un autor específico
  getRecetasPorAutor(userId: string): Observable<Receta[]> {
    // Mira bien esta URL: /api/recetas/autor/ID
    return this.http.get<Receta[]>(`http://localhost:3000/api/recetas/autor/${userId}`);
  }
}