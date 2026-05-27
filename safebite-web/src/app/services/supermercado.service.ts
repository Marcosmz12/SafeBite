import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SupermercadoService {
  private http = inject(HttpClient);
  // Esta es la URL de tu backend de Node (asegúrate de que el backend esté encendido)
  private apiUrl = 'http://localhost:3000/api/supermercado';

  // Esta función llama a la ruta: /api/supermercado/:nombre?q=termino
  buscar(superNombre: string, query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${superNombre.toLowerCase()}?q=${query}`);
  }
}