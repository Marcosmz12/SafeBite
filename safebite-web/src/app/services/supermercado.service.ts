import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class SupermercadoService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/supermercado`;

  buscar(superNombre: string, query: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/${superNombre.toLowerCase()}?q=${query}`);
  }
}