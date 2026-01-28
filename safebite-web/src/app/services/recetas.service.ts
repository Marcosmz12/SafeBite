import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http'; 
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';

@Injectable({ providedIn: 'root' })
export class RecetasService {
  private http = inject(HttpClient);
  
  // URL base de tu backend en Render
  // Agregamos /api al final para no repetirlo en cada método
  private apiUrl = 'https://safebite-7dgr.onrender.com/api';

  // 1. Obtener todas las recetas
  getRecetas(): Observable<Receta[]> {
    // La ruta completa será: https://safebite-7dgr.onrender.com/api/recetas
    return this.http.get<Receta[]>(`${this.apiUrl}/recetas`);
  }

  // 2. Obtener una receta por su ID
  getRecetaById(id: string): Observable<Receta> {
    // La ruta completa será: https://safebite-7dgr.onrender.com/api/recetas/ID
    return this.http.get<Receta>(`${this.apiUrl}/recetas/${id}`);
  }

  // 3. Obtener recetas de un autor específico
  getRecetasPorAutor(userId: string): Observable<Receta[]> {
    // CORREGIDO: Ya no usamos localhost, usamos la variable apiUrl
    return this.http.get<Receta[]>(`${this.apiUrl}/recetas/autor/${userId}`);
  }
}