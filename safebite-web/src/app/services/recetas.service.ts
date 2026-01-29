import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Receta } from '../models/receta';

@Injectable({ providedIn: 'root' })
export class RecetasService {
  private http = inject(HttpClient);

  // URL de tu servidor en Render
  private apiUrl = 'https://safebite-7dgr.onrender.com/api';

  // --- CONFIGURACIÓN DE CLOUDINARY ---
  private cloudinaryUrl = 'https://api.cloudinary.com/v1_1/doas43jji/image/upload';
  private uploadPreset = 'recetas_preset'; // Asegúrate de que este preset sea "Unsigned" en Cloudinary

  // 1. Obtener todas las recetas
  getRecetas(): Observable<Receta[]> {
    return this.http.get<Receta[]>(`${this.apiUrl}/recetas`);
  }

  // 2. Obtener una receta por su ID
  getRecetaById(id: string): Observable<Receta> {
    return this.http.get<Receta>(`${this.apiUrl}/recetas/${id}`);
  }

  // 3. Obtener recetas de un autor específico
  getRecetasPorAutor(userId: string): Observable<Receta[]> {
    return this.http.get<Receta[]>(`${this.apiUrl}/recetas/autor/${userId}`);
  }

  // 4. SUBIR IMAGEN A CLOUDINARY (Sustituye a Firebase Storage)
  subirImagenCloudinary(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('upload_preset', this.uploadPreset);

    return this.http.post(this.cloudinaryUrl, formData);
  }

  // 5. Guardar la receta en tu base de datos de Render
  crearReceta(receta: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/recetas`, receta);
  }
}